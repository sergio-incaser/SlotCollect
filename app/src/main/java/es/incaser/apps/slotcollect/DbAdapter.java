package es.incaser.apps.slotcollect;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;



public class DbAdapter extends SQLiteOpenHelper{
	private static final String DATABASE_NAME = "SlotCollect";
	private static final int DATABASE_VER = 10;
    private static Connection conSQL;
    private SQLiteDatabase db;
    private static Context ctx;
    public static String[][] QUERY_LIST = {
            //Tablas a importar
            {"Establecimientos","SELECT * FROM VIS_INC_EstablecARecaudar"},
            {"Maquinas","SELECT * FROM VIS_INC_MaquinasInstaladas"},
            {"Prestamos","SELECT * FROM INC_PrestamosEstablecimiento"},
            {"RecaudacionesAnteriores","SELECT * FROM VIS_INC_RecaudaInstalaActivas"},
            //Fin Tablas a importar
            {"INC_CabeceraRecaudacion","SELECT * FROM INC_CabeceraRecaudacion"},
            {"INC_LineasRecaudacion","SELECT * FROM INC_LineasRecaudacion"},
            {"INC_RecuperacionesPrestamo","SELECT * FROM INC_RecuperacionesPrestamo"},
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
        closeDB();
        super.finalize();
    }

    private class GetDBConnection extends AsyncTask<Integer, Void, String>{
        @Override
        protected String doInBackground(Integer... params) {
            sqlConnection = new SQLConnection();
            if (SQLConnection.connection == null) {
                db.setVersion(db.getVersion()-1);
                return "errorSQLconnection";
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
            return "Base de datos actualizada";
        }

        @Override
        protected void onPostExecute(String result){
            if (result == "errorSQLconnection"){
                result = ctx.getString(R.string.errorSQLconnection);
            }
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
        Log.w("SlotCollet", "Actualizando base de datos version:" + newVersion);
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
    public Cursor getEstablecimiento(String id){
        Cursor cur = db.query("Establecimientos",new String[]{"*"},"id=?",new String[]{id},"","","");
        cur.moveToFirst();
        return cur;
    }
    public Cursor getMaquinasEstablecimiento(String codigoEmpresa, String codigoEstablecimiento){
        return db.query("Maquinas",new String[]{"*"},"CodigoEmpresa=? AND INC_CodigoEstablecimiento=?",
                        new String[]{codigoEmpresa, codigoEstablecimiento},"","","");
    }
    public Cursor getMaquina(String id){
        return db.query("Maquinas",new String[]{"*"},"id=?",new String[]{id},"","","");
    }
    public Cursor getRecaudacion(String codigoEmpresa, String codigoMaquina){
        return db.query("INC_LineasRecaudacion",new String[]{"*"},"CodigoEmpresa=? AND INC_CodigoMaquina=?",
                new String[]{codigoEmpresa, codigoMaquina},"","","");
    }
    public Cursor getCabeceraRecaudacion(String codigoEmpresa, String codigoEstablecimiento){
        return db.query("INC_CabeceraRecaudacion",new String[]{"*"},"CodigoEmpresa=? AND INC_CodigoEstablecimiento=?",
                new String[]{codigoEmpresa, codigoEstablecimiento},"","","");
    }

    public Cursor getUltimaRecaudacion(String codigoEmpresa, String codigoMaquina){
        String order = "INC_FechaRecaudacion DESC, INC_HoraRecaudacion DESC";
        return db.query("RecaudacionesAnteriores",new String[]{"*"},"CodigoEmpresa=? AND INC_CodigoMaquina=?",
                new String[]{codigoEmpresa, codigoMaquina},"","",order,"1");
    }

    public Cursor getPrestamosEstablecimiento(String id){
        return db.query("Prestamos",new String[]{"*"},"id=?",new String[]{id},"","","");
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

    public int updateRecord(String table, ContentValues values, String whereClause, String[] whereArgs){
        return db.update(table, values, whereClause, whereArgs);
    };

    public String getColumnData(Cursor cur, String column){
        return cur.getString(cur.getColumnIndex(column));
    };
}
