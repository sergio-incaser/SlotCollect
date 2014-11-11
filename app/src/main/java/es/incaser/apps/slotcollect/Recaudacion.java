package es.incaser.apps.slotcollect;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static es.incaser.apps.slotcollect.tools.getActualHour;
import static es.incaser.apps.slotcollect.tools.getHourFractionDay;
import static es.incaser.apps.slotcollect.tools.getNumber;
import static es.incaser.apps.slotcollect.tools.getToday;
import static es.incaser.apps.slotcollect.tools.importeStr;
import static es.incaser.apps.slotcollect.tools.millis2String;
import static es.incaser.apps.slotcollect.tools.str2date;

/**
 * Created by sergio on 28/09/14.
 */
public class Recaudacion extends FragmentActivity implements ActionBar.TabListener {
    private ViewPager vPager;
    private TabsAdapter tAdapter;
    private ActionBar aBar;
    private static int NumPages = 3;
    public static DbAdapter dbAdapter;
    public static Cursor curMaquina;
    public static Cursor curRecaudacion;
    public static Cursor curCabRecaudacion;
    public static Cursor curUltimoArqueo;
    public static Cursor curSumasDesdeA;
    public static Cursor curSumasDesdeI;
    public static Cursor curUltimaRecaudacion;
    public static float valorUltimoArqueo = 0;
    public static Date fechaUltimaRecaudacion;
    public static String fechaUltimoArqueo;
    public static String codigoRecaudador;
    public static ContentValues cvRecaudacion;
    public static float porcentajeRecaudacion = 0;
    public static float porcentajeArqueo = 0;
    public static float porcentajeInstalacion = 0;


    public AlertDialog alertDialog;
    private boolean dialogoContestado = false;
    private boolean positiveButton = false;
    private int oldPosition = 0;
    String codigoEmpresa;
    String codigoEstablecimiento;
    String codigoMaquina;
    static FragmentContadoresMaquina fragmentContadoresMaquina;
    static FragmentImportesMaquina fragmentImportesMaquina;
    static FragmentArqueoMaquina fragmentArqueoMaquina;
    String idMaquina;
    LocationManager locManager;
    static String codigoRecaudacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_screen_recaudacion);
        Bundle bundle = getIntent().getExtras();
        idMaquina = bundle.getString("id");
        dbAdapter = new DbAdapter(this);

        cvRecaudacion = new ContentValues();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        codigoRecaudador = pref.getString("pref_recaudador", "");

        initCursors();
        if (curCabRecaudacion.moveToFirst()) {
            codigoRecaudacion = getCabeceraRecaudacion("INC_CodigoRecaudacion");
        } else {
            codigoRecaudacion = UUID.randomUUID().toString();
        }

        vPager = (ViewPager) findViewById(R.id.recaudacion_pager);
        tAdapter = new TabsAdapter(getSupportFragmentManager());
        aBar = getActionBar();
        aBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        for (String title : getResources().getStringArray(R.array.tabs_recaudacion)) {
            aBar.addTab(aBar.newTab().setText(title).setTabListener(this));
        }

        vPager.setAdapter(tAdapter);

        vPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                aBar.setSelectedNavigationItem(position);
                switch (oldPosition) {
                    case 0:
                        fragmentContadoresMaquina.save(cvRecaudacion);
                        calcData(true);
                        break;
                    case 1:
                        fragmentImportesMaquina.save(cvRecaudacion);
                        calcData(false);
                        break;
                    case 2:
                        fragmentArqueoMaquina.save(cvRecaudacion);
                        calcData(false);
                        break;
                }
                switch (position) {
                    case 1:
                        if (fragmentImportesMaquina != null) {
                            fragmentImportesMaquina.setData();
                        }
                    case 2:
                        if (fragmentArqueoMaquina != null) {
                            fragmentArqueoMaquina.setData();
                        }
                }
                oldPosition = position;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    public static void calcData(boolean brutoFlag) {
        calcTeoricos();
        if (brutoFlag) {
            calculaBruto();
        }
        calcImportes();
        calculaArqueo();
    }

    private void initCursors() {
        curMaquina = dbAdapter.getMaquina(idMaquina);
        curMaquina.moveToFirst();

        codigoEmpresa = dbAdapter.getColumnData(curMaquina, "CodigoEmpresa");
        codigoEstablecimiento = dbAdapter.getColumnData(curMaquina, "INC_CodigoEstablecimiento");
        codigoMaquina = dbAdapter.getColumnData(curMaquina, "INC_CodigoMaquina");

        curUltimoArqueo = dbAdapter.getUltimoArqueo(codigoEmpresa, codigoEstablecimiento, codigoMaquina);
        //Obtener el valor introducido en el hopper del ultimo arqueo o de la instalacion
        if (curUltimoArqueo.moveToFirst()) {
            valorUltimoArqueo = curUltimoArqueo.getFloat(curUltimoArqueo.getColumnIndex("INC_ValorArqueoTeorico"));
            fechaUltimoArqueo = curUltimoArqueo.getString(curUltimoArqueo.getColumnIndex("INC_FechaRecaudacion"));
        } else {
            valorUltimoArqueo = curMaquina.getFloat(curMaquina.getColumnIndex("INC_IntroducidoHopper"));
            fechaUltimoArqueo = curMaquina.getString(curMaquina.getColumnIndex("INC_FechaInstalacion"));
        }
        curUltimaRecaudacion = dbAdapter.getUltimaRecaudacion(codigoEmpresa, codigoEstablecimiento, codigoMaquina);
        curSumasDesdeI = dbAdapter.getSumasDesde(codigoEmpresa, codigoEstablecimiento, codigoMaquina,
                curMaquina.getString(curMaquina.getColumnIndex("INC_FechaInstalacion")));
        curSumasDesdeA = dbAdapter.getSumasDesde(codigoEmpresa, codigoEstablecimiento, codigoMaquina, fechaUltimoArqueo);

        curCabRecaudacion = dbAdapter.getCabeceraRecaudacion(codigoEmpresa, codigoEstablecimiento);
        curRecaudacion = dbAdapter.getRecaudacion(codigoEmpresa, codigoEstablecimiento, codigoMaquina);
        if (!curRecaudacion.moveToFirst()) {
            initialValues();
            //dbAdapter.insertRecord("INC_LineasRecaudacion",initialValues());
            //curRecaudacion = dbAdapter.getRecaudacion(codigoEmpresa, codigoEstablecimiento, codigoMaquina);
            //curRecaudacion.moveToFirst();

            getPositionGPS();
        } else {
            loadCvRecaudacion();
        }
    }

    private void loadCvRecaudacion() {
        String col;
        for (int i = 0; i < curRecaudacion.getColumnCount(); i++) {
            col = curRecaudacion.getColumnName(i);
            cvRecaudacion.put(col, curRecaudacion.getString(i));
        }
    }

    private void initialValues() {
        //ContentValues values = new ContentValues();
        cvRecaudacion.put("CodigoEmpresa", getColMaquina("CodigoEmpresa"));
        cvRecaudacion.put("INC_CodigoMaquina", getColMaquina("INC_CodigoMaquina"));
        cvRecaudacion.put("INC_CodigoEstablecimiento", getColMaquina("INC_CodigoEstablecimiento"));
        cvRecaudacion.put("IdDelegacion", getColMaquina("IdDelegacion"));
        //cvRecaudacion.put("INC_CodigoModelo", getColMaquina("INC_CodigoModelo"));
        cvRecaudacion.put("INC_FechaRecaudacion", getToday());
        cvRecaudacion.put("INC_HoraRecaudacion", getActualHour());
        cvRecaudacion.put("CodigoCanal", getColMaquina("CodigoCanal"));
        cvRecaudacion.put("INC_PorcentajeDistribucion", getColMaquina("INC_PorcentajeDistribucion"));

        if (curUltimaRecaudacion.moveToFirst()) {
            cvRecaudacion.put("INC_Entrada010ANT", getUltRecaudacion("INC_Entrada010"));
            cvRecaudacion.put("INC_Salida010ANT", getUltRecaudacion("INC_Salida010"));
            cvRecaudacion.put("INC_Entrada020ANT", getUltRecaudacion("INC_Entrada020"));
            cvRecaudacion.put("INC_Salida020ANT", getUltRecaudacion("INC_Salida020"));
            cvRecaudacion.put("INC_Entrada050ANT", getUltRecaudacion("INC_Entrada050"));
            cvRecaudacion.put("INC_Entrada100ANT", getUltRecaudacion("INC_Entrada100"));
            cvRecaudacion.put("INC_Salida100ANT", getUltRecaudacion("INC_Salida100"));
            cvRecaudacion.put("INC_Entrada200ANT", getUltRecaudacion("INC_Entrada200"));
            cvRecaudacion.put("INC_Entrada500ANT", getUltRecaudacion("INC_Entrada500"));
            cvRecaudacion.put("INC_Entrada1000ANT", getUltRecaudacion("INC_Entrada1000"));
        } else {
            cvRecaudacion.put("INC_Entrada010ANT", getColMaquina("INC_Entrada010"));
            cvRecaudacion.put("INC_Salida010ANT", getColMaquina("INC_Salida010"));
            cvRecaudacion.put("INC_Entrada020ANT", getColMaquina("INC_Entrada020"));
            cvRecaudacion.put("INC_Salida020ANT", getColMaquina("INC_Salida020"));
            cvRecaudacion.put("INC_Entrada050ANT", getColMaquina("INC_Entrada050"));
            cvRecaudacion.put("INC_Entrada100ANT", getColMaquina("INC_Entrada100"));
            cvRecaudacion.put("INC_Salida100ANT", getColMaquina("INC_Salida100"));
            cvRecaudacion.put("INC_Entrada200ANT", getColMaquina("INC_Entrada200"));
            cvRecaudacion.put("INC_Entrada500ANT", getColMaquina("INC_Entrada500"));
            cvRecaudacion.put("INC_Entrada1000ANT", getColMaquina("INC_Entrada1000"));
        }

        cvRecaudacion.put("INC_Entrada010", cvRecaudacion.getAsString("INC_Entrada010ANT"));
        cvRecaudacion.put("INC_Salida010", cvRecaudacion.getAsString("INC_Salida010ANT"));
        cvRecaudacion.put("INC_Entrada020", cvRecaudacion.getAsString("INC_Entrada020ANT"));
        cvRecaudacion.put("INC_Salida020", cvRecaudacion.getAsString("INC_Salida020ANT"));
        cvRecaudacion.put("INC_Entrada050", cvRecaudacion.getAsString("INC_Entrada050ANT"));
        cvRecaudacion.put("INC_Entrada100", cvRecaudacion.getAsString("INC_Entrada100ANT"));
        cvRecaudacion.put("INC_Salida100", cvRecaudacion.getAsString("INC_Salida100ANT"));
        cvRecaudacion.put("INC_Entrada200", cvRecaudacion.getAsString("INC_Entrada200ANT"));
        cvRecaudacion.put("INC_Entrada500", cvRecaudacion.getAsString("INC_Entrada500ANT"));
        cvRecaudacion.put("INC_Entrada1000", cvRecaudacion.getAsString("INC_Entrada1000ANT"));

        cvRecaudacion.put("INC_CodigoInstalacion", getColMaquina("INC_CodigoInstalacion"));
        cvRecaudacion.put("INC_CodigoRecaudacion", codigoRecaudacion);
        cvRecaudacion.put("INC_CodigoRecaudador", codigoRecaudador);

        //Date now = Calendar.getInstance().getTime();
        Date now = str2date(getToday(), "yyyy-MM-dd");
        int semanas = Math.round((now.getTime() - str2date(getColMaquina("INC_FechaInstalacion")).getTime())
                / (86400000 * 7));

        float importeRetencion;
        float recuperaEmpresa;
        float recuperaEstablecimiento;

        if (curSumasDesdeI.moveToFirst()) {
            importeRetencion = ((getFloatMaquina("INC_RetencionFija") * semanas)
                    + getFloatMaquina("INC_RetencionPendiente")
                    - curSumasDesdeI.getFloat(curSumasDesdeI.getColumnIndex("SumaImporteRetencion")));
            recuperaEmpresa = (getFloatMaquina("INC_CargaHopperEmpresa")
                    + curSumasDesdeI.getFloat(curSumasDesdeI.getColumnIndex("SumaCargaHopperEmpresa"))
                    - curSumasDesdeI.getFloat(curSumasDesdeI.getColumnIndex("SumaRecuperaCargaEmpresa")));

            recuperaEstablecimiento = (getFloatMaquina("INC_CargeHopperEstablecimiento")
                    + curSumasDesdeI.getFloat(curSumasDesdeI.getColumnIndex("SumaCargaHopperEstablecimiento"))
                    - curSumasDesdeI.getFloat(curSumasDesdeI.getColumnIndex("SumaRecuperaCargaEstablecimiento")));
        } else {
            importeRetencion = ((getFloatMaquina("INC_RetencionFija") * semanas)
                    + getFloatMaquina("INC_RetencionPendiente"));
            recuperaEmpresa = (getFloatMaquina("INC_CargeHopperEmpresa"));
            recuperaEstablecimiento = (getFloatMaquina("INC_CargeHopperEstablecimiento"));
        }
        cvRecaudacion.put("INC_ImporteRetencion", importeRetencion);
        cvRecaudacion.put("INC_RecuperaCargaEmpresa", recuperaEmpresa);
        cvRecaudacion.put("INC_RecuperaCargaEstablecimiento", recuperaEstablecimiento);
    }

    private String getColMaquina(String col) {
        return curMaquina.getString(curMaquina.getColumnIndex(col));
    }

    ;

    private String getUltRecaudacion(String col) {
        return curUltimaRecaudacion.getString(curUltimaRecaudacion.getColumnIndex(col));
    }

    ;

    private static float getFloatMaquina(String col) {
        return curMaquina.getFloat(curMaquina.getColumnIndex(col));
    }

    ;

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        vPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    public class TabsAdapter extends FragmentPagerAdapter {

        public TabsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index) {
            if (index < 3) {
                switch (index) {
                    case 0:
                        fragmentContadoresMaquina = new FragmentContadoresMaquina();
                        return fragmentContadoresMaquina;
                    case 1:
                        fragmentImportesMaquina = new FragmentImportesMaquina();
                        return fragmentImportesMaquina;
                    case 2:
                        fragmentArqueoMaquina = new FragmentArqueoMaquina();
                        return fragmentArqueoMaquina;
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

    }

    public static String getColCurRecaudacion(String col) {
        return curRecaudacion.getString(curRecaudacion.getColumnIndex(col));
    }

    public static String getRecaudacion(String col) {
        String res = cvRecaudacion.getAsString(col);
        if (res == null) {
            res = "";
        }
        return res;
    }

    public static Integer getIntRecaudacion(String col) {
        return cvRecaudacion.getAsInteger(col);
    }

    public static boolean getBoolRecaudacion(String col) {
        Boolean res = cvRecaudacion.getAsBoolean(col);
        if (res == null) {
            res = false;
        }
        return res;
    }

    public static Float getFloatRecaudacion(String col) {
        Float res = cvRecaudacion.getAsFloat(col);
        if (res == null) {
            res = (float) 0;
        }
        return res;
    }

    public static String getRecaudacionImporte(String col) {
        return importeStr(getRecaudacion(col));
    }

    public static String getCabeceraRecaudacion(String col) {
        return curCabRecaudacion.getString(curCabRecaudacion.getColumnIndex(col));
    }


    private ContentValues computedValuesCabRecaudacion() {
        ContentValues cv = new ContentValues();
        //Map<String, String> dicRelLineasCabecera = dbAdapter.getDicRelLineasCabecera();
        Cursor curTotales = dbAdapter.getTotalesRecaudacion(codigoEmpresa,
                getRecaudacion("INC_CodigoEstablecimiento"),
                getRecaudacion("INC_FechaRecaudacion"));
        if (curTotales.moveToFirst()) {
            List<String> listColLineas = Arrays.asList(curRecaudacion.getColumnNames());
            for (String col : curCabRecaudacion.getColumnNames()) {
                if (listColLineas.contains(col)) {
                    if (col.equals("INC_CodigoRecaudacion")) {
                        cv.put("INC_CodigoRecaudacion", codigoRecaudacion);
                    } else {
                        cv.put(col, curRecaudacion.getString(curRecaudacion.getColumnIndex(col)));
                    }
                }
            }
            cv.remove("id");
            List<String> listColTotales = Arrays.asList(curTotales.getColumnNames());
            for (String col : curCabRecaudacion.getColumnNames()) {
                if (listColTotales.contains(col)) {
                    cv.put(col, curTotales.getString(curTotales.getColumnIndex(col)));
                }
            }
            cv.put("INC_MaquinasInstaladas", dbAdapter.getMaquinasEstablecimiento(codigoEmpresa, codigoEstablecimiento).getCount());
        }
        return cv;
    }

    public void guardarRecaudacion(boolean printable) {
        //Modificamos el campo printable a true en las lineas y si no hay cabecera la creamos
        cvRecaudacion.put("printable", printable);

        if (curRecaudacion.moveToFirst()) {
            int res = dbAdapter.updateRecord("INC_LineasRecaudacion", cvRecaudacion, "id=?",
                    new String[]{getColCurRecaudacion("id")});
        } else {
            dbAdapter.insertRecord("INC_LineasRecaudacion", cvRecaudacion);
        }
        curRecaudacion = dbAdapter.getRecaudacion(codigoEmpresa, codigoEstablecimiento, codigoMaquina);
        curRecaudacion.moveToFirst();

        if (printable) {
            if (!curCabRecaudacion.moveToFirst()) {
                //Si no existe la cabcera de recaudacion la creamos
                ContentValues cv = computedValuesCabRecaudacion();
                if (cv.size() > 0) {
                    Long id = dbAdapter.insertRecord("INC_CabeceraRecaudacion", computedValuesCabRecaudacion());
                }
            } else {
                dbAdapter.updateRecord("INC_CabeceraRecaudacion",
                        computedValuesCabRecaudacion(), "id=?",
                        new String[]{getCabeceraRecaudacion("id")});
            }
        }
    }


    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.w("onLocationChanged", "Latitud: " + location.getLatitude());
            ContentValues cv = new ContentValues();
            cv.put("CodigoEmpresa", codigoEmpresa);
            cv.put("INC_CodigoEstablecimiento", codigoEstablecimiento);
            cv.put("INC_FechaRecaudacion", getRecaudacion("INC_FechaRecaudacion"));
            cv.put("INC_HoraRecaudacion", getRecaudacion("INC_HoraRecaudacion"));
            cv.put("INC_FechaLocalizacion", millis2String(location.getTime()));
            cv.put("INC_HoraLocalizacion", getHourFractionDay(location.getTime()));
            cv.put("INC_Latitud", location.getLatitude());
            cv.put("INC_Longitud", location.getLongitude());

            dbAdapter.insertRecord("INC_Localizaciones", cv);
            locManager.removeUpdates(locListener);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };

    private ContentValues getPositionGPS() {
        ContentValues cv = new ContentValues();

        locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria req = new Criteria();
        req.setAccuracy(Criteria.ACCURACY_COARSE);

        //Mejor proveedor por criterio
        String mejorProviderCrit = locManager.getBestProvider(req, true);
        LocationProvider provider = locManager.getProvider(mejorProviderCrit);

        if (mejorProviderCrit == null) {
            return cv;
        }

        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mostrarAvisoGpsDeshabilitado();
        }

        Location lastLocation = locManager.getLastKnownLocation(mejorProviderCrit);

        locManager.requestLocationUpdates(
                mejorProviderCrit, 30000, 0, locListener);

        return cv;
    }

    private void mostrarAvisoGpsDeshabilitado() {
        Toast.makeText(this, "GPS desactivado", Toast.LENGTH_SHORT);
    }

    public void mostrarDialogo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //fragmentContadoresMaquina.saveRecaudacion();
        //fragmentImportesMaquina.saveRecaudacion();
        //fragmentArqueoMaquina.saveRecaudacion();

        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialogoContestado = true;
                positiveButton = true;
//                guardarRecaudacion(true);
                Recaudacion.this.onBackPressed();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialogoContestado = true;
                positiveButton = false;
                if (curRecaudacion.moveToFirst()) {
                    dbAdapter.deleteRecaudacion(getColCurRecaudacion("id"));
                }
                if (curCabRecaudacion.moveToFirst()) {
                    ContentValues cv = computedValuesCabRecaudacion();
                    if (cv.size() > 0) {
                        dbAdapter.updateRecord("INC_CabeceraRecaudacion",
                                cv, "id=?",
                                new String[]{getCabeceraRecaudacion("id")});
                    } else {
                        dbAdapter.deleteCabRecaudacion(getCabeceraRecaudacion("id"));
                    }
                    ;
                }
                ;
                Recaudacion.this.onBackPressed();
            }
        });

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("Quiere validar la recaudación")
                .setTitle("Recaudaciones");

        // 3. Get the AlertDialog from create()
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!dialogoContestado) {
            mostrarDialogo();
        } else {
            super.onBackPressed();
        }
    }

    public static void calcTeoricos() {
        float precioPartida = getFloatMaquina("INC_PrecioPartida");
        float jugadoTeorico = 0;
        float premioTeorico = 0;
        float importeBruto = 0;
        Integer partidas = 0;

        //Calculo jugado teorico

        jugadoTeorico += 0.10 * (getIntRecaudacion("INC_Entrada010") - getIntRecaudacion("INC_Entrada010ANT"));
        jugadoTeorico += 0.20 * (getIntRecaudacion("INC_Entrada020") - getIntRecaudacion("INC_Entrada020ANT"));
        jugadoTeorico += 0.50 * (getIntRecaudacion("INC_Entrada050") - getIntRecaudacion("INC_Entrada050ANT"));
        jugadoTeorico += 1.00 * (getIntRecaudacion("INC_Entrada100") - getIntRecaudacion("INC_Entrada100ANT"));
        jugadoTeorico += 2.00 * (getIntRecaudacion("INC_Entrada200") - getIntRecaudacion("INC_Entrada200ANT"));
        cvRecaudacion.put("INC_JugadoTeorico", jugadoTeorico);

        //Calculo premio teorico
        premioTeorico += 0.10 * (getIntRecaudacion("INC_Salida010") - getIntRecaudacion("INC_Salida010ANT"));
        premioTeorico += 0.20 * (getIntRecaudacion("INC_Salida020") - getIntRecaudacion("INC_Salida020ANT"));
        premioTeorico += 1.00 * (getIntRecaudacion("INC_Salida100") - getIntRecaudacion("INC_Salida100ANT"));
        cvRecaudacion.put("INC_PremioTeorico", premioTeorico);

        //Calculo partidas
        partidas = Math.round(jugadoTeorico / precioPartida);
        cvRecaudacion.put("INC_Partidas", partidas);
    }

    public static void calculaBruto() {
        float importeBruto = getFloatRecaudacion("INC_JugadoTeorico") - getFloatRecaudacion("INC_PremioTeorico");
        cvRecaudacion.put("INC_Bruto", importeBruto);
    }

    public static void calcImportes() {
        //TODO Leer de cursor instalacion
        float redondeo = (float) getNumber(curMaquina.getString(
                curMaquina.getColumnIndex("INC_Redondeo")));
        boolean aFavorEmpresa = (curMaquina.getInt(
                curMaquina.getColumnIndex("INC_AFavorEmpresa")) != 0);
        float importeRecaudacion = (getFloatRecaudacion("INC_Bruto")
                - getFloatRecaudacion("INC_Fallos")
                - getFloatRecaudacion("INC_RecuperaCargaEmpresa")
                - getFloatRecaudacion("INC_RecuperaCargaEstablecimiento"));
        cvRecaudacion.put("INC_ImporteRecaudacion", importeRecaudacion);

        float importeReparto = (importeRecaudacion
                - getFloatRecaudacion("INC_ImporteVarios")
                - getFloatRecaudacion("INC_ImporteRetencion"));

        float monedas = importeReparto / redondeo;
        float monedasEst = monedas * getFloatRecaudacion("INC_PorcentajeDistribucion") / 100;
        if (aFavorEmpresa) {
            monedasEst = (int) monedasEst;
        } else {
            // Restamos a las monedas totales, las monedas que corresponderian a la empresa truncadas
            monedasEst = monedas - ((int) (monedas - monedasEst));
        }
        float estab = monedasEst * redondeo;
        cvRecaudacion.put("INC_ImporteEstablecimiento", estab);
        cvRecaudacion.put("INC_ImporteNeto", (importeReparto - estab));
        //calculaArqueo();
    }

    public static void calculaArqueo() {
        long diasNaturales = 0;
        float valorArqueoteorico = 0;
        float diferenciaRecaudacion = 0;
        float diferenciaArqueo = 0;
        float diferenciaInstalacion = 0;

        if (getBoolRecaudacion("INC_ArqueoRealizado")) {
            diferenciaRecaudacion = (getFloatRecaudacion("INC_Bruto")
                    - getFloatRecaudacion("INC_JugadoTeorico")
                    + getFloatRecaudacion("INC_PremioTeorico")
                    + getFloatRecaudacion("INC_ValorArqueoTeorico")
                    - valorUltimoArqueo);
        } else {
            diferenciaRecaudacion = (getFloatRecaudacion("INC_Bruto")
                    - getFloatRecaudacion("INC_JugadoTeorico")
                    + getFloatRecaudacion("INC_PremioTeorico"));
        }
        cvRecaudacion.put("INC_DiferenciaRecaudacion", diferenciaRecaudacion);

        porcentajeRecaudacion = (getFloatRecaudacion("INC_PremioTeorico")
                / getFloatRecaudacion("INC_JugadoTeorico") * 100);
//        fragmentArqueoMaquina.txtPorcPremio.setText(importeStr(porcentajeRecaudacion));

        if (curUltimaRecaudacion.moveToFirst()) {
            fechaUltimaRecaudacion = str2date(curUltimaRecaudacion.getString(
                    curUltimaRecaudacion.getColumnIndex("INC_FechaRecaudacion")));
        } else {
            fechaUltimaRecaudacion = str2date(curMaquina.getString(curMaquina.getColumnIndex("INC_FechaInstalacion")));
        }
        Date now = str2date(getToday(), "yyyy-MM-dd");
        diasNaturales = now.getTime() - fechaUltimaRecaudacion.getTime();
        diasNaturales = diasNaturales / 86400000;
        if (diasNaturales == 0) {
            diasNaturales += 1;
        }
        ;

        cvRecaudacion.put("INC_DiasNaturalesUR", Math.round(diasNaturales));
        cvRecaudacion.put("INC_DiasEfectivosUR", Math.round(diasNaturales));
        cvRecaudacion.put("INC_MediaDiaria", getFloatRecaudacion("INC_Bruto") / diasNaturales);

        if (curSumasDesdeI.moveToFirst()) {
            diferenciaInstalacion = diferenciaRecaudacion
                    + curSumasDesdeI.getFloat(curSumasDesdeI.getColumnIndex("SumaBruto"))
                    - curSumasDesdeI.getFloat(curSumasDesdeI.getColumnIndex("SumaJugadoTeorico"))
                    + curSumasDesdeI.getFloat(curSumasDesdeI.getColumnIndex("SumaPremioTeorico"));

            porcentajeInstalacion = (curSumasDesdeI.getFloat(curSumasDesdeI.getColumnIndex("SumaPremioTeorico"))
                    + getFloatRecaudacion("INC_PremioTeorico"))
                    / (curSumasDesdeI.getFloat(curSumasDesdeI.getColumnIndex("SumaJugadoTeorico"))
                    + getFloatRecaudacion("INC_JugadoTeorico")) * 100;
        } else {
            diferenciaInstalacion = diferenciaRecaudacion;
            porcentajeInstalacion = porcentajeRecaudacion;
        }
        cvRecaudacion.put("INC_DiferenciaInstalacion", diferenciaInstalacion);
//        fragmentArqueoMaquina.txtPorcInstalacion.setText(importeStr(porcentajeInstalacion));


        if (Recaudacion.curSumasDesdeA.moveToFirst()) {
            diferenciaArqueo = diferenciaRecaudacion
                    + curSumasDesdeA.getFloat(curSumasDesdeA.getColumnIndex("SumaBruto"))
                    - curSumasDesdeA.getFloat(curSumasDesdeA.getColumnIndex("SumaJugadoTeorico"))
                    + curSumasDesdeA.getFloat(curSumasDesdeA.getColumnIndex("SumaPremioTeorico"));

            porcentajeArqueo = (curSumasDesdeA.getFloat(curSumasDesdeA.getColumnIndex("SumaPremioTeorico"))
                    + getFloatRecaudacion("INC_PremioTeorico"))
                    / (curSumasDesdeA.getFloat(curSumasDesdeA.getColumnIndex("SumaJugadoTeorico"))
                    + getFloatRecaudacion("INC_JugadoTeorico")) * 100;
        } else {
            diferenciaArqueo = diferenciaInstalacion;
            porcentajeArqueo = porcentajeInstalacion;
        }
        cvRecaudacion.put("INC_DiferenciaArqueo", diferenciaArqueo);
//        fragmentArqueoMaquina.txtPorcArqueo.setText(importeStr(porcentajeArqueo));

        if (!getBoolRecaudacion("INC_ArqueoRealizado")) {
            valorArqueoteorico = valorUltimoArqueo - diferenciaArqueo;
            cvRecaudacion.put("INC_ValorArqueoTeorico", valorArqueoteorico);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        guardarRecaudacion(positiveButton);
    }
}
