package es.incaser.apps.slotcollect;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import static es.incaser.apps.slotcollect.tools.*;


public class IncidenciaEdit extends Activity {
    static DbAdapter dbAdapter;
    String codigoEmpresa;
    String codigoEstablecimiento;
    String codigoMaquina;
    EditText txtTipo;
    EditText txtFecha;
    EditText txtDescricion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incidencia_edit);

        txtTipo = (EditText) findViewById(R.id.txt_TipoIncidencia);
        txtFecha = (EditText) findViewById(R.id.txt_FechaIncidencia);
        txtDescricion = (EditText) findViewById(R.id.txt_DescripcionIncidencia);

        Bundle bundle = getIntent().getExtras();
        codigoEmpresa = bundle.getString("codigoEmpresa");
        codigoEstablecimiento = bundle.getString("codigoEstablecimiento");
        codigoMaquina = bundle.getString("codigoMaquina");

        txtFecha.setText(getToday("dd-MM-yyyy"));

        dbAdapter = new DbAdapter(this);
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createIncidencia(){
        ContentValues cv = new ContentValues();
        cv.put("CodigoEmpresa", codigoEmpresa);
        cv.put("INC_CodigoEstablecimiento", codigoEstablecimiento);
        cv.put("INC_CodigoMaquina", codigoMaquina);
        cv.put("INC_TipoIncidencia", txtTipo.getText().toString());
        cv.put("Fecha", txtFecha.getText().toString());
        cv.put("Descripcion", txtDescricion.getText().toString());

        if (dbAdapter.insertRecord("INC_Incidencias", cv) != -1){
            Toast.makeText(this, "Se ha guardado la incidencia", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Error al guardar",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        createIncidencia();
        super.onPause();
    }
}
