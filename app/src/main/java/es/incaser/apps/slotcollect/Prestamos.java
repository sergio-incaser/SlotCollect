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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import static es.incaser.apps.slotcollect.tools.*;


public class Prestamos extends Activity {
    String codigoEstablecimiento = "";
    static Cursor curPrestamos;
    DbAdapter dbAdapter;
    PrestamosAdapter prestamosAdapter;
    String codigoRecaudacion;

    ListView lvPrestamos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestamos);
        lvPrestamos = (ListView) findViewById(R.id.lv_prestamos);

        Bundle bundle = getIntent().getExtras();
        codigoEstablecimiento = bundle.getString("codigoEstablecimiento");
        codigoRecaudacion = bundle.getString("codigoRecaudacion");
        dbAdapter = new DbAdapter(this);

        lvPrestamos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO abrir activity de las devoluciones del prestamo
                Intent myIntent = new Intent(view.getContext(),RecuperacionPrestamoEdit.class);
                //Paso solo el codigo del prestamo para cargarlo en la otra activity
                myIntent.putExtra("codigoEmpresa", getPrestamo("CodigoEmpresa"));
                myIntent.putExtra("codigoPrestamo", getPrestamo("INC_CodigoPrestamo"));
                myIntent.putExtra("codigoRecaudacion", codigoRecaudacion);
                startActivity(myIntent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.prestamos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindData();
    }
    public void bindData(){
        curPrestamos = dbAdapter.getPrestamosEstablecimiento(codigoEstablecimiento);
        curPrestamos.moveToFirst();
        prestamosAdapter= new PrestamosAdapter(this);
        lvPrestamos.setAdapter(prestamosAdapter);
    }

    private static String getPrestamo(String columna){
        return curPrestamos.getString(curPrestamos.getColumnIndex(columna));
    }

    public static class PrestamosAdapter extends BaseAdapter{
        private Context myContext;

        public PrestamosAdapter(Context ctx){
            myContext = ctx;
        }

        @Override
        public int getCount() {
            return curPrestamos.getCount();
        }

        @Override
        public Object getItem(int i) {
            curPrestamos.moveToPosition(i);
            return curPrestamos;
        }

        @Override
        public long getItemId(int i) {
            curPrestamos.moveToPosition(i);
            return curPrestamos.getLong(curPrestamos.getColumnIndex("id"));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View myView = null;

            if (convertView == null) {
                LayoutInflater myInflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                myView = myInflater.inflate(R.layout.item_prestamo, null);
            } else {
                myView = convertView;
            }
            curPrestamos.moveToPosition(position);

            TextView tvConcepto = (TextView) myView.findViewById(R.id.tv_ConceptoPrestamo);
            TextView tvCodigoPrestamo = (TextView) myView.findViewById(R.id.tv_CodigoPrestamo);
            TextView tvFecha = (TextView) myView.findViewById(R.id.tv_FechaPrestamo);
            TextView tvImporteLiquido = (TextView) myView.findViewById(R.id.tv_ImporteLiquidoPrestamo);
            TextView tvSaldoResto = (TextView) myView.findViewById(R.id.tv_SaldoRestoPrestamo);

            tvConcepto.setText(getPrestamo("INC_CodigoConceptoPrestamo"));
            tvCodigoPrestamo.setText(" ("+getPrestamo("INC_CodigoPrestamo")+") ");
            tvFecha.setText(dateStr2str(getPrestamo("INC_FechaPrestamo")));
            tvImporteLiquido.setText(importeStr(getPrestamo("ImporteLiquido")));
            tvSaldoResto.setText(importeStr(getPrestamo("INC_SaldoResto")));

            return myView;
        }
    }
}
