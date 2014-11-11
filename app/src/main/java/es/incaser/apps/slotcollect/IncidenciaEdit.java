package es.incaser.apps.slotcollect;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import static es.incaser.apps.slotcollect.tools.getToday;


public class IncidenciaEdit extends Activity {
    static DbAdapter dbAdapter;
    String codigoEmpresa;
    String codigoEstablecimiento;
    String codigoMaquina;
    EditText txtTipo;
    EditText txtFecha;
    EditText txtDescricion;
    private static String codigoRecaudador;
    private Cursor curIncidencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incidencia_edit);

        Bundle bundle = getIntent().getExtras();
        codigoEmpresa = bundle.getString("codigoEmpresa");
        codigoEstablecimiento = bundle.getString("codigoEstablecimiento");
        codigoMaquina = bundle.getString("codigoMaquina");

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        codigoRecaudador = pref.getString("pref_recaudador", "");
        dbAdapter = new DbAdapter(this);

        bindData();
        getData();
        setData();
    }

    private void getData() {
        curIncidencia = dbAdapter.getIncidencias(codigoEmpresa,
                codigoEstablecimiento, codigoMaquina, true);
        if (!curIncidencia.moveToFirst()) {
            long id = dbAdapter.insertRecord("INC_Incidencias", initialValues());
            curIncidencia = dbAdapter.getIncidencia(id);
            curIncidencia.moveToFirst();
        }
    }

    private String getIncidencia(String columna) {
        return curIncidencia.getString(curIncidencia.getColumnIndex(columna));
    }

    private void bindData() {
        txtTipo = (EditText) findViewById(R.id.txt_TipoIncidencia);
        txtFecha = (EditText) findViewById(R.id.txt_FechaIncidencia);
        txtDescricion = (EditText) findViewById(R.id.txt_DescripcionIncidencia);
    }

    private void setData() {
        txtTipo.setText(getIncidencia("INC_TipoIncidencia"));
        txtFecha.setText(getIncidencia("Fecha"));
        txtDescricion.setText(getIncidencia("Descripcion"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.incidencia_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    private ContentValues initialValues() {
        ContentValues cv = new ContentValues();
        cv.put("CodigoEmpresa", codigoEmpresa);
        cv.put("INC_CodigoEstablecimiento", codigoEstablecimiento);
        cv.put("INC_CodigoMaquina", codigoMaquina);
        cv.put("INC_TipoIncidencia", "AV");
        cv.put("Fecha", getToday());
        cv.put("Descripcion", "");
        cv.put("INC_CodigoRecaudador", codigoRecaudador);
        cv.put("INC_PendienteSync", -1);
        return cv;
    }

    private void saveIncidencia() {
        ContentValues cv = new ContentValues();
        cv.put("INC_TipoIncidencia", txtTipo.getText().toString());
        //cv.put("Fecha", txtFecha.getText().toString());
        cv.put("Descripcion", txtDescricion.getText().toString());

        int numRecords = dbAdapter.updateRecord("INC_Incidencias", cv,
                "id=?",
                new String[]{getIncidencia("id")});
    }

    @Override
    protected void onPause() {
        saveIncidencia();
        super.onPause();
    }
}
