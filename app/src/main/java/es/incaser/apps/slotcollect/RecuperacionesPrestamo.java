package es.incaser.apps.slotcollect;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class RecuperacionesPrestamo extends ListActivity{
    static Cursor curDevoluciones;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setListAdapter();
    }

    public static class PrestamosAdapter extends BaseAdapter {
        private Context myContext;

        public PrestamosAdapter(Context ctx){
            myContext = ctx;
        }

        @Override
        public int getCount() {
            return curDevoluciones.getCount();
        }

        @Override
        public Object getItem(int i) {
            curDevoluciones.moveToPosition(i);
            return curDevoluciones;
        }

        @Override
        public long getItemId(int i) {
            curDevoluciones.moveToPosition(i);
            return curDevoluciones.getLong(curDevoluciones.getColumnIndex("id"));
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
            curDevoluciones.moveToPosition(position);

            TextView tvConcepto = (TextView) myView.findViewById(R.id.tvConceptoPrestamo);
            tvConcepto.setText(getDevolucion("id"));

            return myView;
        }
    }

    private static String getDevolucion(String columna){
        return curDevoluciones.getString(curDevoluciones.getColumnIndex(columna));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.devoluciones_prestamo, menu);
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
}
