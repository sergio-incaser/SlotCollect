package es.incaser.apps.slotcollect;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.UUID;

import static es.incaser.apps.slotcollect.tools.*;


public class DetallesEstablecimiento extends Activity {
    String id = "";
    Cursor curEstablecimiento;
    static Cursor curMaquinas;
    static Cursor curCabRecaudacion;
    DbAdapter dbAdapter;
    ListView lvMaquinas;
    DetallesAdapter detallesAdapter;
    String codEstablecimiento;
    String codEmpresa;
    TextView txtTotalRecaudacion;
    TextView txtTotalEstablecimiento;
    TextView txtTotalRetencion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_establecimiento);
        lvMaquinas = (ListView) findViewById(R.id.lv_maquinas);

        txtTotalRecaudacion = (TextView) findViewById(R.id.tv_totalRecaudacion);
        txtTotalEstablecimiento = (TextView) findViewById(R.id.tv_totalEstablecimiento);
        txtTotalRetencion = (TextView) findViewById(R.id.tv_totalRetencion);

        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");
        dbAdapter = new DbAdapter(this);

        // Evento para cuando doy click en algun elemento de la lista ( ListView )
        lvMaquinas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,long id) {
            Intent myIntent = new Intent(arg1.getContext(),Recaudacion.class);
            myIntent.putExtra("id", Long.toString(id));
            startActivity(myIntent);
            }
        });

        lvMaquinas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(view.getContext(),Incidencias.class);
                myIntent.putExtra("codigoEmpresa", codEmpresa);
                myIntent.putExtra("codigoEstablecimiento", codEstablecimiento);
                myIntent.putExtra("codigoMaquina", detallesAdapter.getCodigoMaquina(position));

                startActivity(myIntent);
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        curEstablecimiento = dbAdapter.getEstablecimiento(id);
        //curEstablecimiento.moveToFirst();
        codEmpresa = getEstablecimiento("CodigoEmpresa");
        codEstablecimiento = getEstablecimiento("INC_CodigoEstablecimiento");
        bindData();
        getCabeceraRecaudacion();
    }

    public void bindData(){
        this.setTitle(getEstablecimiento("RazonSocial"));
        curMaquinas = dbAdapter.getMaquinasEstablecimiento(codEmpresa, codEstablecimiento);
        detallesAdapter = new DetallesAdapter(this);
        lvMaquinas.setAdapter(detallesAdapter);

    }

    private ContentValues initialValues(){
        ContentValues values = new ContentValues();
        values.put("CodigoEmpresa", codEmpresa);
        values.put("INC_CodigoEstablecimiento",codEstablecimiento);
        values.put("IdDelegacion", getEstablecimiento("IdDelegacion"));
        values.put("INC_FechaRecaudacion", getToday());
        values.put("INC_HoraRecaudacion", getActualHour());
        values.put("CodigoCanal", getEstablecimiento("CodigoCanal"));
        values.put("INC_MaquinasInstaladas", curMaquinas.getCount());
        values.put("INC_MaquinasRecaudadas", 0);

        return values;
    }

    private void getCabeceraRecaudacion(){
        //Intentamos obtener cursor a cabecera recaudacion. De lo contrario NO se crea
        curCabRecaudacion = dbAdapter.getCabeceraRecaudacion(codEmpresa,codEstablecimiento);
        if (curCabRecaudacion.moveToFirst()){
            writeTxtFields();
        }else{
            //TODO Campos de la pantalla a cero
        }
    }

    private String getEstablecimiento(String col){
        return curEstablecimiento.getString(curEstablecimiento.getColumnIndex(col));
    }
    private String cabeceraRecaudacion(String col){
        return curCabRecaudacion.getString(curCabRecaudacion.getColumnIndex(col));
    }
    public static class DetallesAdapter extends BaseAdapter {
        private Context myContext;

        public DetallesAdapter (Context ctx){
            myContext = ctx;
        }

        @Override
        public int getCount() {
            return curMaquinas.getCount();
        }

        @Override
        public Object getItem(int i) {
            curMaquinas.moveToPosition(i);
            return curMaquinas;
        }

        @Override
        public long getItemId(int i) {
            curMaquinas.moveToPosition(i);
            return curMaquinas.getInt(curMaquinas.getColumnIndex("id"));
        }

        public String getCodigoMaquina(int i) {
            curMaquinas.moveToPosition(i);
            return curMaquinas.getString(curMaquinas.getColumnIndex("INC_CodigoMaquina"));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View myView = null;

            if (convertView == null) {
                LayoutInflater myInflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                myView = myInflater.inflate(R.layout.item_maquina, null);
            } else {
                myView = convertView;
            }
            curMaquinas.moveToPosition(position);
            TextView txtDescripcionMaquina = (TextView) myView.findViewById(R.id.tv_descripcion_maquina);
            TextView txtCodigoMaquina = (TextView) myView.findViewById(R.id.tv_codigoMaquina);
            TextView txtFechainstalacion = (TextView) myView.findViewById(R.id.tv_fechaInstalacionMaquina);

            txtDescripcionMaquina.setText(curMaquinas.getString(curMaquinas.getColumnIndex("INC_DescripcionModelo")));
            txtCodigoMaquina.setText("("+curMaquinas.getString(curMaquinas.getColumnIndex("INC_CodigoMaquina"))+")");
            String myTimestamp = curMaquinas.getString(curMaquinas.getColumnIndex("INC_FechaInstalacion"));
            txtFechainstalacion.setText(date2str(str2date(myTimestamp)));

            return myView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detalles_establecimiento, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_prestamos) {
            Intent myIntent = new Intent(this,Prestamos.class);
            myIntent.putExtra("codigoEstablecimiento",
                    getEstablecimiento("INC_CodigoEstablecimiento"));
            if(curCabRecaudacion.moveToFirst()){
                myIntent.putExtra("codigoRecaudacion",
                        cabeceraRecaudacion("INC_CodigoRecaudacion"));
            }else {
                myIntent.putExtra("codigoRecaudacion", UUID_EMPTY);
            }
            startActivity(myIntent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void writeTxtFields(){
        txtTotalRecaudacion.setText(cabeceraRecaudacion("INC_TotalRecaudacion"));
        txtTotalEstablecimiento.setText(cabeceraRecaudacion("INC_TotalEstablecimiento"));
        txtTotalRetencion.setText(cabeceraRecaudacion("INC_TotalRetencion"));
    }
}
