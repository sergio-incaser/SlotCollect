package es.incaser.apps.slotcollect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class Incidencias extends Activity {
    static Cursor cur;
    DbAdapter dbAdapter;
    private static IncidenciasAdapter incidenciasAdapter;
    ListView lvIncidencias;
    String codigoEmpresa;
    String codigoEstablecimiento;
    String codigoMaquina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incidencias);
        Bundle bundle = getIntent().getExtras();
        codigoEmpresa = bundle.getString("codigoEmpresa");
        codigoEstablecimiento = bundle.getString("codigoEstablecimiento");
        codigoMaquina = bundle.getString("codigoMaquina");


        lvIncidencias = (ListView) findViewById(R.id.lv_incidencias);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindData();
    }

    public void bindData(){
        dbAdapter = new DbAdapter(this);
        cur = dbAdapter.getIncidencias(codigoEmpresa, codigoEstablecimiento, codigoMaquina);
        if (cur.moveToFirst()) {
            incidenciasAdapter = new IncidenciasAdapter(this);
            lvIncidencias.setAdapter(incidenciasAdapter);
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.incidencias, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent myIntent = new Intent(this,IncidenciaEdit.class);
            myIntent.putExtra("codigoEmpresa", codigoEmpresa);
            myIntent.putExtra("codigoEstablecimiento", codigoEstablecimiento);
            myIntent.putExtra("codigoMaquina", codigoMaquina);
            startActivity(myIntent);
            return true;
        }
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class IncidenciasAdapter extends BaseAdapter {
        private Context myContext;

        public IncidenciasAdapter (Context ctx){
            myContext = ctx;
        }

        @Override
        public int getCount() {
            return cur.getCount();
        }

        @Override
        public Object getItem(int i) {
            cur.moveToPosition(i);
            return cur;
        }

        @Override
        public long getItemId(int i) {
            cur.moveToPosition(i);
            return cur.getInt(cur.getColumnIndex("id"));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View myView = null;

            if (convertView == null) {
                LayoutInflater myInflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                myView = myInflater.inflate(R.layout.item_incidencia, null);
            } else {
                myView = convertView;
            }
            cur.moveToPosition(position);

            TextView txtFechaIncidencia = (TextView) myView.findViewById(R.id.tv_fechaIncidencia);
            TextView txtDescripcion = (TextView) myView.findViewById(R.id.tv_descripcionIncidencia);

            txtFechaIncidencia.setText(getIncidencia("Fecha"));
            txtDescripcion.setText(getIncidencia("Descripcion"));
            return myView;
        }
    }

    private static String getIncidencia(String col){
        return cur.getString(cur.getColumnIndex(col));
    }
}
