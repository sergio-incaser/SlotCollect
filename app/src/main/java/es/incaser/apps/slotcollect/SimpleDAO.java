package es.incaser.apps.slotcollect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by sergio on 5/09/14.
 */

public class SimpleDAO {
    Connection conn = SQLConnection.getInstance().getConnection();

    public ResultSet construirLector(String query) {
        ResultSet result = null;
        try {
            Statement statement = null;
            statement = conn.createStatement();
            result = statement.executeQuery(query);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public ResultSet getResultQuery(String query) {
        ResultSet result = null;
        try {
            Statement statement = null;
            statement = conn.createStatement();
            result = statement.executeQuery(query);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


}
