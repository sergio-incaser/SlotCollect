package es.incaser.apps.slotcollect;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import static es.incaser.apps.slotcollect.tools.*;


public class RecuperacionPrestamoEdit extends Activity {
    static DbAdapter dbAdapter;
    Cursor curPrestamo;
    Cursor curRecupera;
    String codigoEmpresa;
    String codigoEstablecimiento;
    String codigoPrestamo;
    String codigoRecaudacion;
    EditText txtImporte;
    EditText txtFecha;
    EditText txtComentario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperacion_prestamo_edit);
        txtFecha = (EditText) findViewById(R.id.txt_fechaRecuperacion);
        txtImporte = (EditText) findViewById(R.id.txt_importeRecuperacion);
        txtComentario = (EditText) findViewById(R.id.txt_descripcionRecuperacion);

        Bundle bundle = getIntent().getExtras();
        codigoEmpresa = bundle.getString("codigoEmpresa");
        codigoPrestamo = bundle.getString("codigoPrestamo");
        codigoRecaudacion = bundle.getString("codigoRecaudacion");

        txtFecha.setText(getToday());

        dbAdapter = new DbAdapter(this);
        curPrestamo = dbAdapter.getPrestamo(codigoPrestamo);
        curPrestamo.moveToFirst();

    }

    @Override
    protected void onStart() {
        super.onStart();
        bindData();
    }

    public void bindData(){
        curRecupera = dbAdapter.getRecuperacionesPrestamo(codigoEmpresa, codigoPrestamo);
        if (curRecupera.moveToFirst()){
            txtImporte.setText(importeStr(getRecupera("ImporteLiquido")));
            txtComentario.setText(getRecupera("INC_ComentarioRecuperacion"));
        }
    }

    private void createRecuperacion(){
        ContentValues cv = new ContentValues();
        cv.put("CodigoEmpresa", codigoEmpresa);
        cv.put("'INC_CodigoRecuperacion'", 0);
        cv.put("'INC_CodigoPrestamo'", codigoPrestamo);
        cv.put("INC_FechaRecuperacion", txtFecha.getText().toString());
        cv.put("CodigoCliente", getPrestamo("CodigoCliente"));
        cv.put("ImporteLiquido", txtImporte.getText().toString());
        cv.put("INC_ComentarioRecuperacion", txtComentario.getText().toString());
        cv.put("IdDelegacion", getPrestamo("IdDelegacion"));
        cv.put("CodigoCanal", getPrestamo("CodigoCanal"));
        cv.put("INC_CodigoRecaudacion", codigoRecaudacion);
        cv.put("Printable", true);
        cv.put("INC_IdPda", "0");

        if (curRecupera.moveToFirst()){
            dbAdapter.updateRecord("INC_RecuperacionesPrestamo", cv,"id=?",
                    new String[]{getRecupera("id")});
        }else {
            dbAdapter.insertRecord("INC_RecuperacionesPrestamo", cv);
        }
        Toast.makeText(this, "Se ha guardado la recuperación",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        createRecuperacion();
        super.onPause();
    }

    private String getPrestamo(String col){
        return curPrestamo.getString(curPrestamo.getColumnIndex(col));
    }

    private String getRecupera(String col){
        return curRecupera.getString(curRecupera.getColumnIndex(col));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.recuperacion_prestamo_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            dbAdapter.deleteRecuperacion(getRecupera("id"));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
