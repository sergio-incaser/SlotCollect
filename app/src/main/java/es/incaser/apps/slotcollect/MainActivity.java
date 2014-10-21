package es.incaser.apps.slotcollect;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static android.provider.Settings.Global.getString;


public class MainActivity extends Activity{
    private static Cursor cur = null;
    private static EstablecimientosAdapter estabAdapter;
    DbAdapter dbAdapter;
    ListView lvEstablecimientos;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvEstablecimientos = (ListView) findViewById(R.id.lv_establecimientos);

        lvEstablecimientos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(view.getContext(),DetallesEstablecimiento.class);
                cur.moveToPosition(position);
                myIntent.putExtra("id", cur.getString(cur.getColumnIndex("id")));
                startActivity(myIntent);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        ReadPreferences(this);
        if (SQLConnection.host != ""){
            bindData();
        }
    }
    public void bindData(){
        dbAdapter = new DbAdapter(this);
        Cursor curtmp = dbAdapter.getCursor("Select * from sqlite_master WHERE name = 'Establecimientos'");
        if (curtmp.getCount() > 0 ) {
            getDataSql();
            estabAdapter = new EstablecimientosAdapter(this);
            lvEstablecimientos.setAdapter(estabAdapter);
            //setListAdapter(estabAdapter);
        };
    }

    public static void ReadPreferences(Activity act){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
        SQLConnection.host = pref.getString("pref_sql_host","");
        SQLConnection.port = pref.getString("pref_sql_port","");
        SQLConnection.user = pref.getString("pref_sql_user","");
        SQLConnection.password = pref.getString("pref_sql_password","");
        SQLConnection.database = pref.getString("pref_sql_database","");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= 3){
                    getDataSql(newText.toString());
                    estabAdapter.notifyDataSetChanged();
                }else if(newText.length()==0){
                    getDataSql();
                    estabAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
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
//            SyncData syncData = new SyncData(this);
//            syncData.SincronizarDatos();
            sincronizarDatos();
            return true;
        }
        if (id == R.id.action_test) {
            //new ImportSqlData().execute(1);
            SyncData syncData = new SyncData(this);
            syncData.test();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void getDataSql(){
        cur = dbAdapter.getCursorBuscador("","Establecimientos","");
    }

    private void getDataSql(String text){
        cur = dbAdapter.getCursorBuscador(text,"Establecimientos","");
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

            TextView txtEstablecimiento = (TextView) myView.findViewById(R.id.Establecimiento);
            TextView txtCodigoEstablecimiento = (TextView) myView.findViewById(R.id.codigoEstablecimiento);
            TextView txtDireccion = (TextView) myView.findViewById(R.id.domicilioEstablecimiento);
            TextView txtMunicipio = (TextView) myView.findViewById(R.id.municipioEstablecimiento);
            TextView txtTelefono = (TextView) myView.findViewById(R.id.telefonoEstablecimiento);

            txtEstablecimiento.setText(getEstablecimiento("RazonSocial"));
            txtCodigoEstablecimiento.setText("("+getEstablecimiento("INC_CodigoEstablecimiento")+")");
            txtDireccion.setText(getEstablecimiento("Domicilio"));
            txtMunicipio.setText(getEstablecimiento("Municipio"));
            txtTelefono.setText(getEstablecimiento("Telefono"));
            return myView;
        }
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent myIntent = new Intent(this,DetallesEstablecimiento.class);

        cur.moveToPosition(position);
        myIntent.putExtra("id", cur.getString(cur.getColumnIndex("id")));
        startActivity(myIntent);
    }

    private static String getEstablecimiento(String column){
        return cur.getString(cur.getColumnIndex(column));
    }
    public void sincronizarDatos(){
        new Synchronize().execute(1);
    }

    private class Synchronize extends AsyncTask<Integer, Void, String> {
        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setIndeterminate(false);
            progressDialog.setTitle("Proceso de sincronización");
            progressDialog.setMax(100);
            progressDialog.show();
            progressDialog.setMessage("Sincronizando datos");
        }
        @Override
        protected String doInBackground(Integer... params) {
            SyncData syncData = new SyncData(MainActivity.this);
            syncData.conSQL = new SQLConnection();
            if (SQLConnection.connection == null) {
                return "errorSQLconnection";
            }
            if (syncData.exportRecords() >= 0){
                syncData.importRecords();
                return "Datos Sincronizados";
            }else {
                return "ERROR EN LA SINCRONIZACION";
            }
        }

        @Override
        protected void onPostExecute(String result){
            if (result == "errorSQLconnection"){
                result = MainActivity.this.getString(R.string.errorSQLconnection);
            }
            Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            bindData();
        }
    }

}
