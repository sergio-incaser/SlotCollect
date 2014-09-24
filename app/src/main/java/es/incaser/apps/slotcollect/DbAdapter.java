package es.incaser.apps.slotcollect;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.SyncStateContract;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import net.sourceforge.jtds.jdbc.ColInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class DbAdapter extends SQLiteOpenHelper{
	private static final String DATABASE_NAME = "SlotCollect";
	private static final int DATABASE_VER = 15;
    private static Connection conSQL;
    private SQLiteDatabase db;
    private static Context ctx;
    public static String[][] QUERY_LIST = {
            //Tablas a importar
            {"Establecimientos","SELECT * FROM VIS_INC_EstablecARecaudar"},
            {"Maquinas","SELECT * FROM VIS_INC_MaquinasInstaladas"},
            {"Prestamos","SELECT * FROM INC_PrestamosEstablecimiento"},
            {"UltimaRecaudacion","SELECT * FROM VIS_INC_UltimaRecaudacion"},
            //Fin Tablas a importar
            {"INC_RecaudacionesPDA","SELECT * FROM INC_RecaudacionesPDA"},
    };
    public static int tablesToImport = 4; // Modificar en caso de añadir mas tablas
    public static int tablesToExport = 5; // Exportar tablas a partir de este indice
    private SQLConnection sqlConnection;

	public DbAdapter(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VER);
		ctx = context;
        openDB();
	}

    @Override
    protected void finalize() throws Throwable {
        //closeDB();
        super.finalize();
    }

    private class GetDBConnection extends AsyncTask<Integer, Void, String>{
        @Override
        protected String doInBackground(Integer... params) {
            sqlConnection = SQLConnection.getInstance();
            ResultSet rs;
            ResultSetMetaData rsmd;
            String colname;
            String coltype;
            String columnsSql = "";
            String createSql = "";

            for (String[] query : QUERY_LIST) {
                columnsSql = "";
                try {
                    rs = sqlConnection.getResultset(query[1] + " WHERE 1=2");
                    rsmd = rs.getMetaData();
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        colname = rsmd.getColumnName(i);
                        coltype = rsmd.getColumnTypeName(i);
                        columnsSql += ", '" + colname + "' " + coltype;
                    }
                    createSql = "CREATE TABLE " + query[0] + " ('id' INTEGER PRIMARY KEY AUTOINCREMENT" + columnsSql + ");";
                    db.execSQL(createSql);
                } catch (java.sql.SQLException e) {
                    e.printStackTrace();
                }
            }
            return "OK";
        }

        @Override
        protected void onPostExecute(String result){
            Toast.makeText(ctx,result, Toast.LENGTH_LONG).show();

        }
    }


    @Override
	public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        //SQLConnection conInstance = SQLConnection.getInstance();
        new GetDBConnection().execute(1);
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        Log.w("SlotCollet", "Actualizando base de datos" + newVersion);
        for (String[] query : QUERY_LIST) {
            db.execSQL("DROP TABLE IF EXISTS " + query[0]);
        }
        onCreate(db);
    }

	public void emptyTables(){
        for (String[] query:QUERY_LIST) {
            db.execSQL("DROP TABLE IF EXISTS " + query[0]);
        }
	}
	public void openDB() {
		if (db == null){
			db = this.getWritableDatabase();
		}
	}
	public void closeDB() {
		if (db != null){
			db.close();
		}
	}

    public Cursor getCursorBuscador(String textSearch, String tableSearch, String order){
        textSearch = textSearch.replace("'", "''");
        String[] fields = new String[]{"*" ,"id  AS _id", "RazonSocial AS item"};
        String where = "";
        String[] selectionArgs = new String[]{};
        String orderBy ="id";
        String table = tableSearch.toString();

        if (order.length() > 0)	orderBy = order;

        if (textSearch.length()>0){
            selectionArgs = new String[]{"%" + textSearch +"%"};
            if (where.length()>0) {
                where +=" AND ";
            }
            where += "name LIKE ?";
        }
        return db.query(table, fields, where, selectionArgs, "", "", orderBy);
    }

    public Cursor getTable(String tableName){
        return db.query(tableName,new String[]{"*"},"",new String[]{},"","","");
    }
    public Cursor getTable(String tableName, Integer limit){
        return db.query(tableName,new String[]{"*"},"",new String[]{},"","","",limit.toString());
    }
    public Cursor getCursor(String query){
        return db.rawQuery(query, new String[]{});
    }

    public int recordCount(String tableName){
        Cursor cursor = db.rawQuery("SELECT count() FROM " + tableName, new String[]{});
        if (cursor != null) {
            return cursor.getCount();
        } else{
            return 0;
        }
    }

    public long insertRecord(String tableName, ContentValues values){
        return db.insert(tableName, null, values);
    };

}
