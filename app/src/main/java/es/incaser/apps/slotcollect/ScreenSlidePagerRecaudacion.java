package es.incaser.apps.slotcollect;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static es.incaser.apps.slotcollect.tools.*;

/**
 * Created by sergio on 28/09/14.
 */
public class ScreenSlidePagerRecaudacion extends FragmentActivity implements ActionBar.TabListener{
    private ViewPager vPager;
    private TabsAdapter tAdapter;
    private ActionBar aBar;
    private static int NumPages=3;
    public static  DbAdapter dbAdapter;
    public static Cursor curMaquina;
    public static Cursor curRecaudacion;
    public static Cursor curCabRecaudacion;
    public AlertDialog alertDialog;
    private boolean dialogoContestado = false;
    String codigoEmpresa;
    String codigoMaquina;
    FragmentContadoresMaquina fragmentContadoresMaquina;
    FragmentImportesMaquina fragmentImportesMaquina;
    FragmentArqueoMaquina fragmentArqueoMaquina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_screen_recaudacion);
        Bundle bundle = getIntent().getExtras();
        String idMaquina = bundle.getString("id");
        dbAdapter = new DbAdapter(this);
        curMaquina = dbAdapter.getMaquina(idMaquina);
        curMaquina.moveToFirst();

        codigoEmpresa = dbAdapter.getColumnData(curMaquina, "CodigoEmpresa");
        codigoMaquina = dbAdapter.getColumnData(curMaquina, "INC_CodigoMaquina");

        curCabRecaudacion = dbAdapter.getCabeceraRecaudacion(codigoEmpresa,getColMaquina("INC_CodigoEstablecimiento"));

        curRecaudacion = dbAdapter.getRecaudacion(codigoEmpresa, codigoMaquina);

        if (curRecaudacion.getCount() == 0){
            dbAdapter.insertRecord("INC_LineasRecaudacion",initialValues());
            curRecaudacion = dbAdapter.getRecaudacion(codigoEmpresa, codigoMaquina);
        }
        curRecaudacion.moveToFirst();

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
                switch(position) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        fragmentArqueoMaquina.calculaArqueo();
                        break;
                }
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

        return values;
    }

    private String getColMaquina(String col){
      return curMaquina.getString(curMaquina.getColumnIndex(col));
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
            dbAdapter.insertRecord("INC_CabeceraRecaudacion",computedValuesCabRecaudacion());
        }else{
            dbAdapter.updateRecord("INC_CabeceraRecaudacion",
                                computedValuesCabRecaudacion(),"id=?",
                                            new String[]{getCabeceraRecaudacion("id")});
        }
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
                ScreenSlidePagerRecaudacion.this.onBackPressed();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialogoContestado = true;
                ScreenSlidePagerRecaudacion.this.onBackPressed();
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
