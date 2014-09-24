package es.incaser.apps.slotcollect;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sergio on 23/09/14.
 */
public class SyncData {
    private static DbAdapter dbAdapter;
    private Context myContext;
    private SQLConnection conSQL;

    public SyncData(Context ctx){
        myContext = ctx;
        dbAdapter = new DbAdapter(myContext);
    }

    public void SincronizarDatos(){
        new Synchronize().execute(1);
    }

    private class Synchronize extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            conSQL = new SQLConnection();
            exportRecords();
            importRecords();
            return "Datos Sincronizados";
        }
        @Override
        protected void onPostExecute(String result){
            Toast.makeText(myContext, result, Toast.LENGTH_SHORT).show();
        }
    }

    public int importRecords() {
        ResultSet resultSet;
        for (int i = 0; i < DbAdapter.tablesToImport; i++){
            resultSet = conSQL.getResultset(String.valueOf(DbAdapter.QUERY_LIST[i][1]));
            copyRecords(resultSet, DbAdapter.QUERY_LIST[i][0]);
        }
        // TODO Devolver numero de registros importados
        return 0;
    }

    public int exportRecords(){
        Cursor cursor;
        ResultSet resultSet = null;
        for (int i = DbAdapter.tablesToExport; i < DbAdapter.QUERY_LIST.length + 1; i++){
            cursor = dbAdapter.getTable(DbAdapter.QUERY_LIST[i-1][0]);
            resultSet = conSQL.getResultset("Select * FROM "+ DbAdapter.QUERY_LIST[i-1][0]);
            copyRecords(cursor, DbAdapter.QUERY_LIST[i-1][0], resultSet);
        }
        return 0;
    }

    public int copyRecords(ResultSet source, String target) {
        ResultSetMetaData RSmd;
        ContentValues values = new ContentValues();
        ArrayList<String> columnList = new ArrayList();
        try {
            RSmd = source.getMetaData();
            String[] args = new String[]{target};
            Cursor cursor = dbAdapter.getTable(target, 1);
            String[] localColumns = cursor.getColumnNames();
            for (int i = 1; i <= RSmd.getColumnCount(); i++) {
                if (Arrays.asList(localColumns).contains(RSmd.getColumnName(i))) {
                    columnList.add(RSmd.getColumnName(i));
                }
            }
            while(source.next()){
                for (String col : columnList) {
                    values.put(col, source.getString(col));
                }
                dbAdapter.insertRecord(target, values);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int copyRecords(Cursor source, String tableSource, ResultSet target) {
        ResultSetMetaData RSmd;
        ContentValues values = new ContentValues();
        List<String> columnList = null;
        Integer colInt;
        try {
            RSmd = target.getMetaData();
            String[] args = {tableSource};
            String[] localColumns = dbAdapter.getTable(tableSource, 1).getColumnNames();
            for (String colname : localColumns) {
                if (target.findColumn(colname) > 0){
                    columnList.add(colname);
                }
            }
            while(source.moveToNext()){
                target.moveToInsertRow();
                for (String col : columnList) {
                    colInt = source.getColumnIndex(col);
                    target.updateString(col, source.getString(colInt));
                }
                target.insertRow();
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
