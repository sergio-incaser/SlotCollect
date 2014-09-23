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
	private static final int DATABASE_VER = 10;
    private static Connection conSQL;
    public static SQLiteDatabase db;
    private static Context ctx;
    private static String[][] QUERY_LIST = {
            //Tablas a importar
            {"Establecimientos","SELECT DISTINCT \n" +
                    "                     IdDelegacion, CodigoCanal, INC_CodigoEstablecimiento, RazonSocial, INC_CodigoRecaudador, INC_ZonaRecaudacion, Domicilio, CodigoPostal, Municipio, Telefono, Telefono2, \n" +
                    "                      Email, PersonaContacto\n" +
                    "FROM         Vis_INC_UltimasRecaudaciones\n" +
                    "WHERE INC_CodigoEstablecimiento>'100000'"},
            {"Maquinas","SELECT TOP 1 * FROM VIS_INC_MaquinasInstaladas"},
            {"Prestamos","SELECT TOP 1 * FROM INC_PrestamosEstablecimiento"},
            {"UltimaRecaudacion","SELECT TOP 1 * FROM VIS_INC_UltimaRecaudacion"},
            //Fin Tablas a importar
            {"Recaudaciones","SELECT TOP 1 * FROM INC_RecaudacionesPDA"},
    };
    private static int tablesToImport = 4; // Modificar en caso de añadir mas tablas

	public DbAdapter(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VER);
		ctx = context;
	}

    private class GetDBConnection extends AsyncTask<Integer, Void, String>{
        @Override
        protected String doInBackground(Integer... params) {
            conSQL = SQLConnection.getInstance().getConnection();
            Statement statement = null;
            try {
                statement = conSQL.createStatement();
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }

            ResultSet rs;
            ResultSetMetaData rsmd;
            String colname;
            String coltype;
            String columnsSql = "";
            String createSql = "";

            for (String[] query : QUERY_LIST) {
                columnsSql = "";
                try {
                    rs = statement.executeQuery(query[1]);
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


    public int importRecords() {
        conSQL = SQLConnection.getInstance().getConnection();
        Statement statement = null;
        ResultSet rs;
        try {
            statement = conSQL.createStatement();
            for (int i = 0; i < tablesToImport; i++){
                rs = statement.executeQuery(String.valueOf(QUERY_LIST[i][1]));
                copyRecords(rs, QUERY_LIST[i][0]);
//                while (rs.next()){
//                    copyRecords(rs, QUERY_LIST[i][0]);
//                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }



        return 0;
    }


    public static int copyRecords(ResultSet source, String target) {
        ResultSetMetaData RSmd;
        ContentValues values = new ContentValues();
        ArrayList<String> columnList = new ArrayList();
        try {
            RSmd = source.getMetaData();
            String[] args = new String[]{target};
            //Cursor cur = db.rawQuery("SELECT * FROM Establecimientos",new String[]{});
            Cursor cur = db.rawQuery("SELECT * FROM " + target + " LIMIT 1 ", null);
            String[] localColumns = cur.getColumnNames();
            for (int i = 1; i <= RSmd.getColumnCount(); i++) {
                if (Arrays.asList(localColumns).contains(RSmd.getColumnName(i))) {
                    columnList.add(RSmd.getColumnName(i));
                }
            }
            while(source.next()){
                for (String col : columnList) {
                //for (int i = 1; i <= RSmd.getColumnCount(); i++) {
                    values.put(col, source.getString(col));
                }
                db.insert(target, "",values);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int copyRecords(Cursor source, String tableSource, ResultSet target) {
        ResultSetMetaData RSmd;
        ContentValues values = new ContentValues();
        List<String> columnList = null;
        Integer colInt;
        try {
            RSmd = target.getMetaData();
            String[] args = {tableSource};
            String[] localColumns = db.rawQuery("SELECT * FROM ? LIMIT 1 ", args).getColumnNames();
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
    public static Cursor getCursorBuscador(String textSearch, String tableSearch, String order){
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
}
