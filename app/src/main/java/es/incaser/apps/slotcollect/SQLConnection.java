package es.incaser.apps.slotcollect;

import android.content.SharedPreferences;
import android.database.SQLException;

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
    private static final String URL = "jdbc:jtds:sqlserver://localhost:1433/SASD;";
    private static final String USER = "sa";
    private static final String PASS = "D";

    private static Connection connection = null;

    private SQLConnection(){}


    public static SQLConnection getInstance(){
        if (instance == null)
            instance = new SQLConnection();
        return instance;
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
            conn = DriverManager.getConnection(URL,USER,PASS);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    
}
