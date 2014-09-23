package es.incaser.apps.slotcollect;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.preference.PreferenceManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import net.sourceforge.jtds.jdbc.Driver;


/**
 * Created by sergio on 5/09/14.
 */
public class SQLConnection {
    private static SQLConnection instance = null;
    public static String host;
    public static String port;
    public static String user;
    public static String password;
    public static String database;

    private static Connection connection = null;

    private SQLConnection(){}


    public static SQLConnection getInstance(){
        if (instance == null)
            instance = new SQLConnection();
        return instance;
    }

    public Connection getConnection(String host, String port, String user, String password, String database){
        try {
            if (this.host != host || this.user != user || this.password != password) {
                this.host = host;
                this.port = port;
                this.user = user;
                this.password = password;
                this.database = database;
                if ((connection != null) && !connection.isClosed()) {
                    connection.close();
                }
            }
            if(connection == null || connection.isClosed())
                connection = connectSQL();
            return connection;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Connection getConnection(){
        if(connection == null)
            connection = connectSQL();
        return connection;
    }


    private Connection connectSQL(){
        Connection conn = null;
        (new Driver()).getClass();
        try {
            String uri = "jdbc:jtds:sqlserver://" + host + ":"+ port +"/"+ database +";";
            conn = DriverManager.getConnection(uri,user,password);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public ResultSet getEstablecimientos(){
        if(connection == null)
            connection = connectSQL();
        String sql = "Select INC_CodigoEstablecimiento as id, * From INC_Establecimientos";
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            rs = statement.executeQuery(sql);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    
}
