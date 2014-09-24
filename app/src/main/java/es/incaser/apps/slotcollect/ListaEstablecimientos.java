package es.incaser.apps.slotcollect;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import es.incaser.apps.slotcollect.DbAdapter;
import es.incaser.apps.slotcollect.R;

/**
 * Created by sergio on 23/09/14.
 */
public class ListaEstablecimientos extends ListActivity {
    private static Cursor cursorEstablecimientos = null;
    private static EstablecimientosAdapter estabAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DbAdapter myDbAdapter = new DbAdapter(this);
        myDbAdapter.openDB();

        getDataSql();
        estabAdapter = new EstablecimientosAdapter(this);
        setListAdapter(estabAdapter);
    }

    private void getDataSql(){
        cursorEstablecimientos = new DbAdapter(this).getCursorBuscador("","Establecimientos","");
    }

    public static class EstablecimientosAdapter extends BaseAdapter{
        private Context myContext;
        public EstablecimientosAdapter (Context ctx){
            myContext = ctx;
        }

        @Override
        public int getCount() {
            return cursorEstablecimientos.getCount();
        }

        @Override
        public Object getItem(int i) {
            cursorEstablecimientos.moveToPosition(i);
            return cursorEstablecimientos;
        }

        @Override
        public long getItemId(int i) {
            cursorEstablecimientos.moveToPosition(i);
            return cursorEstablecimientos.getInt(i);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View myView = null;

            if (convertView == null) {
                LayoutInflater myInflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                myView = myInflater.inflate(R.layout.item_establecimiento, null);
            } else {
                myView = convertView;
            }
            cursorEstablecimientos.moveToPosition(position);

            TextView txtCodigoEstablecimiento = (TextView) myView.findViewById(R.id.codigoEstablecimiento);
            TextView txtEstablecimiento = (TextView) myView.findViewById(R.id.Establecimiento);
            return myView;
        }
    }
}
