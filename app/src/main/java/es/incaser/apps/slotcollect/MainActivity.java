package es.incaser.apps.slotcollect;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class MainActivity extends ListActivity{
    private static Cursor cur = null;
    private static EstablecimientosAdapter estabAdapter;
    DbAdapter dbAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        ReadPreferences(this);
        dbAdapter = new DbAdapter(this);
        Cursor curtmp = dbAdapter.getCursor("Select * from sqlite_master WHERE name = 'Establecimientos'");
        if (curtmp.getCount() > 0 ) {
            getDataSql();
            estabAdapter = new EstablecimientosAdapter(this);
            setListAdapter(estabAdapter);
        };
    }

    public static void ReadPreferences(Activity act){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
        //String prefAppName = getApplication().getPackageName() + "_preferences";
        //SharedPreferences pref = getSharedPreferences(prefAppName,Context.MODE_PRIVATE);
        SQLConnection.host = pref.getString("pref_sql_host","");
        SQLConnection.port = pref.getString("pref_sql_port","");
        SQLConnection.user = pref.getString("pref_sql_user","");
        SQLConnection.password = pref.getString("pref_sql_password","");
        SQLConnection.database = pref.getString("pref_sql_database","");
    }

//    private class ImportSqlData extends AsyncTask<Integer, Void, String>{
//        @Override
//        protected String doInBackground(Integer... params) {
//            adapterDB.openDB();
//            adapterDB.importRecords();
//            return "Datos importados";
//        }
//        @Override
//        protected void onPostExecute(String result){
//            Toast.makeText(getApplicationContext(),result, Toast.LENGTH_SHORT).show();
//            getDataSql();
//            setListAdapter(estabAdapter);
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_filtrar_estableciemtos) {
            //new ImportSqlData().execute(1);
            Intent intent = new Intent(this, EstablecimientosLogic.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_sync_data) {
            //new ImportSqlData().execute(1);
            SyncData syncData = new SyncData(this);
            syncData.SincronizarDatos();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void getDataSql(){
        cur = dbAdapter.getCursorBuscador("","Establecimientos","");
    }

    public static class EstablecimientosAdapter extends BaseAdapter {
        private Context myContext;
        public EstablecimientosAdapter (Context ctx){
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
                myView = myInflater.inflate(R.layout.item_establecimiento, null);
            } else {
                myView = convertView;
            }
            cur.moveToPosition(position);
            if ((cur.getColumnIndex("INC_CodigoEstablecimiento") == 53) || (cur.getColumnIndex("RazonSocial") == 53)){
                int a = 5;
            }
            TextView txtCodigoEstablecimiento = (TextView) myView.findViewById(R.id.codigoEstablecimiento);
            TextView txtEstablecimiento = (TextView) myView.findViewById(R.id.Establecimiento);

            txtCodigoEstablecimiento.setText(cur.getString(cur.getColumnIndex("INC_CodigoEstablecimiento")));
            txtEstablecimiento.setText(cur.getString(cur.getColumnIndex("RazonSocial")));
            return myView;
        }
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        //Intent myIntent = new Intent(this,input_reading.class);

        //cur.moveToPosition(position);
        //myIntent.putExtra("last_value", cur.getString(cur.getColumnIndex("last_value")));
        //myIntent.putExtra("name", cur.getString(cur.getColumnIndex("name")));
        //myIntent.putExtra("id", cur.getInt(cur.getColumnIndex("id")));
        //startActivity(myIntent);
        Toast.makeText(getApplicationContext(),"Vamos a Recaudar este establecimiento", Toast.LENGTH_LONG).show();

    }

}
