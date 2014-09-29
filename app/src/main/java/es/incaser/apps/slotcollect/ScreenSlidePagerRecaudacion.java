package es.incaser.apps.slotcollect;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_screen_recaudacion);
        Bundle bundle = getIntent().getExtras();
        String idMaquina = bundle.getString("id");
        dbAdapter = new DbAdapter(this);
        curMaquina = dbAdapter.getMaquina(idMaquina);
        curMaquina.moveToFirst();

        String codigoEmpresa = dbAdapter.getColumnData(curMaquina, "CodigoEmpresa");
        String codigoMaquina = dbAdapter.getColumnData(curMaquina, "INC_CodigoMaquina");
        curRecaudacion = dbAdapter.getRecaudacion(codigoEmpresa, codigoMaquina);

        if (curRecaudacion.getCount() == 0){
            ContentValues values = new ContentValues();
            values.put("CodigoEmpresa", codigoEmpresa);
            values.put("INC_CodigoMaquina", codigoMaquina);
            values.put("INC_CodigoEstablecimiento", dbAdapter.getColumnData(curMaquina,"INC_CodigoEstablecimiento"));
            dbAdapter.insertRecord("INC_RecaudacionesPDA",values);
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
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        vPager.setCurrentItem(tab.getPosition());
        //Toast.makeText(getBaseContext(), "Tab Seleccionado: " + tab.getText(), Toast.LENGTH_SHORT).show();
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
                //aBar.selectTab(aBar.getTabAt(index));
                switch(index) {
                    case 0:
                        //return FragmentContadoresMaquina.newInstance("Texto de la pestaña nº 1.", curMaquina);
                        return new FragmentContadoresMaquina();
                    case 1:
                        return FragmentImportesMaquina.newInstance("Texto de la pestaña nº 2.");
                    case 2:
                        return new FragmentArqueoMaquina();
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

    }
}
