package es.incaser.apps.slotcollect;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import static es.incaser.apps.slotcollect.tools.importeStr;


public class TotalesRecaudacion extends Activity {
    private static Cursor curTotalRecaudacion;
    private TextView txtTotalRecaudacion;
    private TextView txtTotalRetencion;
    private TextView txtTotalNeto;
    private TextView txtTotalEstablecimiento;
    private TextView txtTotalNetoMasRetencion;
    private TextView txtTotalRecuperaCarga;
    private TextView txtTotalRecuperaPrestamo;
    private TextView txtTotalSaldo;
    private TextView txtMaquinasRecaudadas;
    DbAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_totales_recaudacion);
        bindData();
        setData();
    }

    private void bindData(){
        //Enlazamos los controles a las variables
        txtTotalRecaudacion = (TextView) findViewById(R.id.tv_totalRecaudadoAll);
        txtTotalRetencion = (TextView) findViewById(R.id.tv_totalRetencionAll);
        txtTotalNeto = (TextView) findViewById(R.id.tv_totalNetoAll);
        txtTotalEstablecimiento = (TextView) findViewById(R.id.tv_totalEstablecimientoAll);
        txtTotalNetoMasRetencion = (TextView) findViewById(R.id.tv_totalNetoMasRetencionAll);
        txtTotalRecuperaCarga = (TextView) findViewById(R.id.tv_totalRecuperaCargaAll);
        txtTotalRecuperaPrestamo = (TextView) findViewById(R.id.tv_totalRecuperaPrestamoAll);
        txtTotalSaldo = (TextView) findViewById(R.id.tv_totalSaldoAll);
        txtMaquinasRecaudadas = (TextView) findViewById(R.id.tv_totalMaquinasRecaudadasAll);
    }

    private void setData(){
        //Asignamos los datos a las variables del cursor obtenido
        dbAdapter = new DbAdapter(this);
        curTotalRecaudacion = dbAdapter.getTotalRecaudadoAll();

        if(curTotalRecaudacion.moveToFirst()){
            txtTotalRecaudacion.setText(getTotalRecaudadoImporte("INC_TotalRecaudacion"));
            txtTotalRetencion.setText(getTotalRecaudadoImporte("INC_TotalRetencion"));
            txtTotalNeto.setText(getTotalRecaudadoImporte("INC_TotalNeto"));
            txtTotalEstablecimiento.setText(getTotalRecaudadoImporte("INC_TotalEstablecimiento"));
            txtTotalNetoMasRetencion.setText(getTotalRecaudadoImporte("INC_TotalNetoMasRetencion"));
            txtTotalRecuperaCarga.setText(getTotalRecaudadoImporte("INC_TotalRecuperaCarga"));
            txtTotalRecuperaPrestamo.setText(getTotalRecaudadoImporte("INC_TotalRecuperaPrestamo"));
            txtTotalSaldo.setText(getTotalRecaudadoImporte("INC_TotalSaldo"));
            txtMaquinasRecaudadas.setText(getTotalRecaudado("INC_MaquinasRecaudadas"));
        }
    }

    private static String getTotalRecaudado(String col) {
        return curTotalRecaudacion.getString(curTotalRecaudacion.getColumnIndex(col));
    }
    private static String getTotalRecaudadoImporte(String col) {
        return importeStr(getTotalRecaudado(col));
    }
}
