package es.incaser.apps.slotcollect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class MainActivity extends Activity {
    DbAdapter adapterDB = new DbAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ReadPreferences(this);
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

    private class GetDBConnection extends AsyncTask<Integer, Void, String>{
        @Override
        protected String doInBackground(Integer... params) {

            adapterDB.openDB();
            adapterDB.importRecords();
            return "OK";
        }
        @Override
        protected void onPostExecute(String result){
//            Toast.makeText(getApplicationContext(),result, Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(),result, Toast.LENGTH_LONG).show();
        }
    }

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
        if (id == R.id.action_sql_import) {
            new GetDBConnection().execute(1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
