package es.incaser.apps.slotcollect;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Created by sergio on 23/09/14.
 */
public class SyncData {
    private static DbAdapter dbAdapter;
    private Context myContext;

    public SyncData(Context ctx){
        myContext = ctx;
    }

    public void SincronizarDatos(){
        new Synchronize().execute(1);
    }

    private class Synchronize extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            dbAdapter.openDB();
            dbAdapter.importRecords();
            return "Datos Sincronizados";
        }
        @Override
        protected void onPostExecute(String result){
            Toast.makeText(myContext, result, Toast.LENGTH_SHORT).show();
            //getDataSql();
            //setListAdapter(estabAdapter);
        }
    }
}
