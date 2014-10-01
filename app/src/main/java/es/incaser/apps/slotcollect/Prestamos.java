package es.incaser.apps.slotcollect;

import android.app.Activity;
import android.content.Context;
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


public class Prestamos extends Activity {
    String idEstablecimiento = "";
    static Cursor curPrestamos;
    DbAdapter dbAdapter;
    PrestamosAdapter prestamosAdapter;

    ListView lvPrestamos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestamos);

        Bundle bundle = getIntent().getExtras();
        idEstablecimiento = bundle.getString("id");
        dbAdapter = new DbAdapter(this);
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        curPrestamos = dbAdapter.getPrestamosEstablecimiento(idEstablecimiento);
        bindData();
    }
    public void bindData(){
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

            TextView tvConcepto = (TextView) myView.findViewById(R.id.tvConceptoPrestamo);
            TextView tvCodigoPrestamo = (TextView) myView.findViewById(R.id.tvCodigoPrestamo);
            TextView tvFecha = (TextView) myView.findViewById(R.id.tvFechaPrestamo);
            TextView tvImporteLiquido = (TextView) myView.findViewById(R.id.tvImporteLiquidoPrestamo);
            TextView tvSaldoResto = (TextView) myView.findViewById(R.id.tvSaldoRestoPrestamo);

            tvConcepto.setText(getPrestamo("id"));
            tvCodigoPrestamo.setText(getPrestamo("INC_CodigoPrestamo"));
            tvFecha.setText(getPrestamo("INC_FechaPrestamo"));
            tvImporteLiquido.setText(getPrestamo("ImporteLiquido"));
            tvSaldoResto.setText(getPrestamo("INC_SaldoResto"));

            return myView;
        }
    }
}
