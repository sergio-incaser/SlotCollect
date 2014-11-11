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


public class RecuperacionesPrestamo extends Activity {
    static Cursor cur;
    static Cursor curPrestamo;
    DbAdapter dbAdapter;
    private static RecuperacionesAdapter recuperacionesAdapter;
    ListView lvRecuperaciones;
    String codigoEmpresa;
    String codigoPrestamo;
    String codigoRecaudacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperaciones_prestamo);

        Bundle bundle = getIntent().getExtras();
        codigoEmpresa = bundle.getString("codigoEmpresa");
        codigoPrestamo = bundle.getString("codigoPrestamo");
        codigoRecaudacion = bundle.getString("codigoRecaudacion");

        lvRecuperaciones = (ListView) findViewById(R.id.lv_recuperacionesPrestamo);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindData();
    }

    public void bindData() {
        dbAdapter = new DbAdapter(this);
        cur = dbAdapter.getRecuperacionesPrestamo(codigoEmpresa, codigoPrestamo);
        if (cur.moveToFirst()) {
            recuperacionesAdapter = new RecuperacionesAdapter(this);
            lvRecuperaciones.setAdapter(recuperacionesAdapter);
        }
        ;
    }


    public static class RecuperacionesAdapter extends BaseAdapter {
        private Context myContext;

        public RecuperacionesAdapter(Context ctx) {
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
            return cur.getLong(cur.getColumnIndex("id"));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View myView = null;

            if (convertView == null) {
                LayoutInflater myInflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                myView = myInflater.inflate(R.layout.item_recuperacion_prestamo, null);
            } else {
                myView = convertView;
            }
            cur.moveToPosition(position);

            TextView tvFecha = (TextView) myView.findViewById(R.id.tv_FechaRecuperacion);
            TextView tvImporte = (TextView) myView.findViewById(R.id.tv_ImporteLiquidoRecuperacion);
            TextView tvDescripcion = (TextView) myView.findViewById(R.id.tv_descripcionRecuperacion);

            tvFecha.setText(getRecuperacion("INC_FechaRecuperacion"));
            tvImporte.setText(getRecuperacion("ImporteLiquido"));
            tvDescripcion.setText(getRecuperacion("INC_ComentarioRecuperacion"));

            return myView;
        }
    }

    private static String getRecuperacion(String columna) {
        return cur.getString(cur.getColumnIndex(columna));
    }

    private static String getPrestamo(String columna) {
        return curPrestamo.getString(curPrestamo.getColumnIndex(columna));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.recuperaciones_prestamo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent myIntent = new Intent(this, RecuperacionPrestamoEdit.class);
            myIntent.putExtra("codigoEmpresa", codigoEmpresa);
            myIntent.putExtra("codigoPrestamo", codigoPrestamo);
            myIntent.putExtra("codigoRecaudacion", codigoRecaudacion);
            startActivity(myIntent);
            return true;
        }

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
