package es.incaser.apps.slotcollect;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static es.incaser.apps.slotcollect.tools.*;

/**
 * Created by sergio on 28/09/14.
 */
public class Recaudacion extends FragmentActivity implements ActionBar.TabListener{
    private ViewPager vPager;
    private TabsAdapter tAdapter;
    private ActionBar aBar;
    private static int NumPages=3;
    public static  DbAdapter dbAdapter;
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

    public AlertDialog alertDialog;
    private boolean dialogoContestado = false;
    String codigoEmpresa;
    String codigoEstablecimiento;
    String codigoMaquina;
    FragmentContadoresMaquina fragmentContadoresMaquina;
    FragmentImportesMaquina fragmentImportesMaquina;
    FragmentArqueoMaquina fragmentArqueoMaquina;
    public static boolean isModified = false;
    public static int oldPagePosition = -1;
    String idMaquina;
    LocationManager locManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_screen_recaudacion);
        Bundle bundle = getIntent().getExtras();
        idMaquina = bundle.getString("id");
        dbAdapter = new DbAdapter(this);
        initCursors();

        vPager = (ViewPager)findViewById(R.id.recaudacion_pager);
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
                if (Recaudacion.isModified) {
                    fragmentContadoresMaquina.saveRecaudacion();
                    fragmentImportesMaquina.saveRecaudacion();
                    fragmentArqueoMaquina.saveRecaudacion();

                    curRecaudacion = dbAdapter.getRecaudacion(codigoEmpresa, codigoEstablecimiento, codigoMaquina);
                    curRecaudacion.moveToFirst();

                    fragmentArqueoMaquina.calculaArqueo();

                    switch (position) {
                        case 0:
                            break;
                        case 1:
                            break;
                        case 2:
                            break;
                    }
                }
                Recaudacion.isModified = false;
                oldPagePosition = position;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                String a="2";
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                String a="2";
            }
        });
    }

    private void initCursors(){
        curMaquina = dbAdapter.getMaquina(idMaquina);
        curMaquina.moveToFirst();

        codigoEmpresa = dbAdapter.getColumnData(curMaquina, "CodigoEmpresa");
        codigoEstablecimiento = dbAdapter.getColumnData(curMaquina, "INC_CodigoEstablecimiento");
        codigoMaquina = dbAdapter.getColumnData(curMaquina, "INC_CodigoMaquina");

        curUltimoArqueo = dbAdapter.getUltimoArqueo(codigoEmpresa, codigoEstablecimiento,codigoMaquina);
        //Obtener el valor introducido en el hopper del ultimo arqueo o de la instalacion
        if (curUltimoArqueo.moveToFirst()) {
            valorUltimoArqueo = curUltimoArqueo.getFloat(curUltimoArqueo.getColumnIndex("INC_ValorArqueoTeorico"));
            fechaUltimoArqueo = curUltimoArqueo.getString(curUltimoArqueo.getColumnIndex("INC_FechaRecaudacion"));
        } else {
            valorUltimoArqueo = curMaquina.getFloat(curMaquina.getColumnIndex("INC_IntroducidoHopper"));
            fechaUltimoArqueo = curMaquina.getString(curMaquina.getColumnIndex("INC_FechaInstalacion"));
        }
        curUltimaRecaudacion = dbAdapter.getUltimaRecaudacion(codigoEmpresa, codigoEstablecimiento,codigoMaquina);
        curSumasDesdeI = dbAdapter.getSumasDesde(codigoEmpresa, codigoEstablecimiento, codigoMaquina,
                            curMaquina.getString(curMaquina.getColumnIndex("INC_FechaInstalacion")));
        curSumasDesdeA = dbAdapter.getSumasDesde(codigoEmpresa, codigoEstablecimiento,codigoMaquina, fechaUltimoArqueo);

        curCabRecaudacion = dbAdapter.getCabeceraRecaudacion(codigoEmpresa,codigoEstablecimiento);
        curRecaudacion = dbAdapter.getRecaudacion(codigoEmpresa, codigoEstablecimiento, codigoMaquina);
        if (!curRecaudacion.moveToFirst()){
            dbAdapter.insertRecord("INC_LineasRecaudacion",initialValues());
            curRecaudacion = dbAdapter.getRecaudacion(codigoEmpresa, codigoEstablecimiento, codigoMaquina);
            curRecaudacion.moveToFirst();
            getPositionGPS(getRecaudacion("id"));
        }
    }
    private ContentValues initialValues(){
        ContentValues values = new ContentValues();
        values.put("CodigoEmpresa", getColMaquina("CodigoEmpresa"));
        values.put("INC_CodigoMaquina", getColMaquina("INC_CodigoMaquina"));
        values.put("INC_CodigoEstablecimiento", getColMaquina("INC_CodigoEstablecimiento"));
        values.put("IdDelegacion", getColMaquina("IdDelegacion"));
        //values.put("INC_CodigoModelo", getColMaquina("INC_CodigoModelo"));
        values.put("INC_FechaRecaudacion", getToday());
        values.put("INC_HoraRecaudacion", getActualHour());
        values.put("CodigoCanal", getColMaquina("CodigoCanal"));
        values.put("INC_PorcentajeDistribucion", getColMaquina("INC_PorcentajeDistribucion"));

        values.put("INC_Entrada010ANT", getColMaquina("INC_Entrada010"));
        values.put("INC_Entrada010", getColMaquina("INC_Entrada010"));
        values.put("INC_Salida010ANT", getColMaquina("INC_Salida010"));
        values.put("INC_Salida010", getColMaquina("INC_Salida010"));
        values.put("INC_Entrada020ANT", getColMaquina("INC_Entrada020"));
        values.put("INC_Entrada020", getColMaquina("INC_Entrada020"));
        values.put("INC_Salida020ANT", getColMaquina("INC_Salida020"));
        values.put("INC_Salida020", getColMaquina("INC_Salida020"));
        values.put("INC_Entrada050ANT", getColMaquina("INC_Entrada050"));
        values.put("INC_Entrada050", getColMaquina("INC_Entrada050"));
        values.put("INC_Entrada100ANT", getColMaquina("INC_Entrada100"));
        values.put("INC_Entrada100", getColMaquina("INC_Entrada100"));
        values.put("INC_Salida100ANT", getColMaquina("INC_Salida100"));
        values.put("INC_Salida100", getColMaquina("INC_Salida100"));
        values.put("INC_Entrada200ANT", getColMaquina("INC_Entrada200"));
        values.put("INC_Entrada200", getColMaquina("INC_Entrada200"));
        values.put("INC_Entrada500ANT", getColMaquina("INC_Entrada500"));
        values.put("INC_Entrada500", getColMaquina("INC_Entrada500"));
        values.put("INC_Entrada1000ANT", getColMaquina("INC_Entrada1000"));
        values.put("INC_Entrada1000", getColMaquina("INC_Entrada1000"));

        //Date now = Calendar.getInstance().getTime();
        Date now = str2date(getToday(),"yyyy-MM-dd");
        int semanas = Math.round((now.getTime() - str2date(getColMaquina("INC_FechaInstalacion")).getTime())
                        / (86400000 * 7));

        float importeRetencion;
        float recuperaEmpresa;
        float recuperaEstablecimiento;

        if (curSumasDesdeI.moveToFirst()){
            importeRetencion = ((getFloatMaquina("INC_RetencionFija") * semanas)
                    + getFloatMaquina("INC_RetencionPendiente")
                    - curSumasDesdeI.getFloat(curSumasDesdeI.getColumnIndex("SumaImporteRetencion")));
            recuperaEmpresa = (getFloatMaquina("INC_CargaHopperEmpresa")
                    + curSumasDesdeI.getFloat(curSumasDesdeI.getColumnIndex("SumaCargaHopperEmpresa"))
                    - curSumasDesdeI.getFloat(curSumasDesdeI.getColumnIndex("SumaRecuperaCargaEmpresa")));

            recuperaEstablecimiento = (getFloatMaquina("INC_CargeHopperEstablecimiento")
                    + curSumasDesdeI.getFloat(curSumasDesdeI.getColumnIndex("SumaCargaHopperEstablecimiento"))
                    - curSumasDesdeI.getFloat(curSumasDesdeI.getColumnIndex("SumaRecuperaCargaEstablecimiento")));
        }else {
            importeRetencion = ((getFloatMaquina("INC_RetencionFija") * semanas)
                    + getFloatMaquina("INC_RetencionPendiente"));
            recuperaEmpresa = (getFloatMaquina("INC_CargeHopperEmpresa"));
            recuperaEstablecimiento = (getFloatMaquina("INC_CargeHopperEstablecimiento"));
        }
        values.put("INC_ImporteRetencion", importeRetencion);
        values.put("INC_RecuperaCargaEmpresa", recuperaEmpresa);
        values.put("INC_RecuperaCargaEstablecimiento", recuperaEstablecimiento);

        return values;
    }

    private String getColMaquina(String col){
        return curMaquina.getString(curMaquina.getColumnIndex(col));
    };

    private float getFloatMaquina(String col){
        return curMaquina.getFloat(curMaquina.getColumnIndex(col));
    };

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
            if(index < 3) {
                switch(index) {
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

    public static String getRecaudacion(String columna){
        return curRecaudacion.getString(curRecaudacion.getColumnIndex(columna));
    }

    public static String getRecaudacionImporte(String columna){
        return importeStr(getRecaudacion(columna));
    }

    public static String getCabeceraRecaudacion(String columna){
        return curCabRecaudacion.getString(curCabRecaudacion.getColumnIndex(columna));
    }
    private ContentValues initialValuesCabRecaudacion(){
        ContentValues values = new ContentValues();
        values.put("CodigoEmpresa", codigoEmpresa);
        values.put("INC_CodigoEstablecimiento", getColMaquina("INC_CodigoEstablecimiento"));
        values.put("IdDelegacion", getColMaquina("IdDelegacion"));
        values.put("CodigoCanal", getColMaquina("CodigoCanal"));
        values.put("INC_FechaRecaudacion", getToday());
        values.put("INC_HoraRecaudacion", getActualHour());
        //Se crea la cabcera con al menos una linea asi que hay que calcular tambien totales
        values.putAll(computedValuesCabRecaudacion());
        return values;
    }

    private ContentValues computedValuesCabRecaudacion(){
        ContentValues cv = new ContentValues();
        //Map<String, String> dicRelLineasCabecera = dbAdapter.getDicRelLineasCabecera();
        Cursor curTotales = dbAdapter.getTotalesRecaudacion(codigoEmpresa,
                                        getRecaudacion("INC_CodigoEstablecimiento"),
                                        getRecaudacion("INC_FechaRecaudacion"));
        if (curTotales.moveToFirst()){
            List<String> listColLineas = Arrays.asList(curRecaudacion.getColumnNames());
            for (String col: curCabRecaudacion.getColumnNames()){
                if (listColLineas.contains(col)) {
                    cv.put(col, curRecaudacion.getString(curRecaudacion.getColumnIndex(col)));
                }
            }
            cv.remove("id");
            List<String> listColTotales = Arrays.asList(curTotales.getColumnNames());
            for (String col: curCabRecaudacion.getColumnNames()){
                if (listColTotales.contains(col)) {
                    cv.put(col, curTotales.getString(curTotales.getColumnIndex(col)));
                }
            }
            byte[] blob = new byte[16];
            new Random().nextBytes(blob);
            cv.put("INC_CodigoRecaudacion",blob);
        }
        return cv;
    }

    public void validarRecaudacion(){
        //Modificamos el campo printable a true en las lineas y si no hay cabecera la creamos
        ContentValues cvLineas = new ContentValues();
        cvLineas.put("printable", true);
        int res = dbAdapter.updateRecord("INC_LineasRecaudacion",cvLineas,"id=?",
                new String[]{getRecaudacion("id")});
        if (!curCabRecaudacion.moveToFirst()){
            //Si no existe la cabcera de recaudacion la creamos
            Long id = dbAdapter.insertRecord("INC_CabeceraRecaudacion",computedValuesCabRecaudacion());
        }else{
            dbAdapter.updateRecord("INC_CabeceraRecaudacion",
                                computedValuesCabRecaudacion(),"id=?",
                                            new String[]{getCabeceraRecaudacion("id")});
        }
    }


    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.w("onLocationChanged", "Latitud: " + location.getLatitude());
            ContentValues cv = new ContentValues();
            cv.put("CodigoEmpresa",codigoEmpresa);
            cv.put("INC_CodigoEstablecimiento",codigoEstablecimiento);
            cv.put("INC_FechaRecaudacion",getRecaudacion("INC_FechaRecaudacion"));
            cv.put("INC_HoraRecaudacion",getRecaudacion("INC_HoraRecaudacion"));
            cv.put("INC_FechaLocalizacion",location.getTime());
            cv.put("INC_Latitud",location.getLatitude());
            cv.put("INC_Longitud",location.getLongitude());

            dbAdapter.insertRecord("INC_Localizaciones",cv);
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

    private ContentValues getPositionGPS(String id){
        ContentValues cv = new ContentValues();

        locManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        Criteria req = new Criteria();
        req.setAccuracy(Criteria.ACCURACY_COARSE);

        //Mejor proveedor por criterio
        String mejorProviderCrit = locManager.getBestProvider(req, true);
        LocationProvider provider = locManager.getProvider(mejorProviderCrit);

        if (mejorProviderCrit == null){
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

    private void mostrarAvisoGpsDeshabilitado(){
        Toast.makeText(this, "GPS desactivado",Toast.LENGTH_SHORT);
    }

    public void mostrarDialogo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        fragmentContadoresMaquina.saveRecaudacion();
        fragmentImportesMaquina.saveRecaudacion();
        //fragmentArqueoMaquina.saveRecaudacion();

        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialogoContestado = true;
                validarRecaudacion();
                Recaudacion.this.onBackPressed();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialogoContestado = true;
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
        if (!dialogoContestado){
            mostrarDialogo();
        }else{
            super.onBackPressed();
        }
    }
}
