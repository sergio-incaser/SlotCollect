package es.incaser.apps.slotcollect;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by sergio on 5/09/14.
 */
public class PersonaDAO extends SimpleDAO {
    private static PersonaDAO instance = null;

    private PersonaDAO() {
    }

    public static PersonaDAO getInstance() {
        if (instance == null)
            instance = new PersonaDAO();
        return instance;
    }

    public String getNombrePersona(int id) {
        String result = "";
        String query = "Select razonsocial from Clientes WHERE CodigoCliente='1' AND CodigoEmpresa=1";
        ResultSet rs = super.construirLector(query);
        try {
            ResultSetMetaData rsmd = rs.getMetaData();

            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                String colname = rsmd.getColumnName(i);
                String coltype = rsmd.getColumnTypeName(i);
                result = colname + coltype;
            }
            ;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}