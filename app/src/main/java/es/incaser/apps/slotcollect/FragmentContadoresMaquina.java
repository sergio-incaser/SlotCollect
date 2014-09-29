package es.incaser.apps.slotcollect;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by sergio on 28/09/14.
 */
public class FragmentContadoresMaquina extends Fragment {
    private final static String KEY_REG_TEXT = "texto";
    private static Cursor curMaquina;
    private static Cursor curRecaudacion;
    private static DbAdapter dbAdapter;
    Context myContext;

    //EditText txtSal010Ant = (EditText)rootView.findViewById(R.id.txtSal010Ant);
    EditText txtEnt010Ant;
    EditText txtEnt020Ant;
    EditText txtSal020Ant;
    EditText txtEnt050Ant;
    EditText txtEnt100Ant;
    EditText txtSal100Ant;
    //EditText txtEnt200Ant;
    EditText txtEnt500Ant;
    EditText txtEnt1000Ant;

    //Contadores Actuales
    EditText txtEnt010;
    //EditText txtSal010;
    EditText txtEnt020;
    EditText txtSal020;
    EditText txtEnt050;
    EditText txtEnt100;
    EditText txtSal100;
    //EditText txtEnt200;
    EditText txtEnt500;
    EditText txtEnt1000;

    private void bindAntData(View rootView){
        //txtSal010Ant = (EditText)rootView.findViewById(R.id.txtSal010Ant);
        txtEnt010Ant = (EditText)rootView.findViewById(R.id.txtEnt010Ant);
        txtEnt020Ant = (EditText)rootView.findViewById(R.id.txtEnt020Ant);
        txtSal020Ant = (EditText)rootView.findViewById(R.id.txtSal020Ant);
        txtEnt050Ant = (EditText)rootView.findViewById(R.id.txtEnt050Ant);
        txtEnt100Ant = (EditText)rootView.findViewById(R.id.txtEnt100Ant);
        txtSal100Ant = (EditText)rootView.findViewById(R.id.txtSal100Ant);
        //txtEnt200Ant = (EditText)rootView.findViewById(R.id.txtEnt200Ant);
        txtEnt500Ant = (EditText)rootView.findViewById(R.id.txtEnt500Ant);
        txtEnt1000Ant = (EditText)rootView.findViewById(R.id.txtEnt1000Ant);

        txtEnt010Ant.setText(curMaquina.getString(curMaquina.getColumnIndex("INC_Entrada010")));

        //TODO
        //txtSal010Ant.setText(curMaquina.getString(curMaquina.getColumnIndex("INC_Salida010")));

        txtEnt020Ant.setText(curMaquina.getString(curMaquina.getColumnIndex("INC_Entrada020")));
        txtSal020Ant.setText(curMaquina.getString(curMaquina.getColumnIndex("INC_Salida020")));
        txtEnt050Ant.setText(curMaquina.getString(curMaquina.getColumnIndex("INC_Entrada050")));
        txtEnt100Ant.setText(curMaquina.getString(curMaquina.getColumnIndex("INC_Entrada100")));
        txtSal100Ant.setText(curMaquina.getString(curMaquina.getColumnIndex("INC_Salida100")));
        //txtEnt200Ant.setText(curMaquina.getString(curMaquina.getColumnIndex("INC_Entrada200")));
        txtEnt500Ant.setText(curMaquina.getString(curMaquina.getColumnIndex("INC_Entrada500")));
        txtEnt1000Ant.setText(curMaquina.getString(curMaquina.getColumnIndex("INC_Entrada1000")));
    }

    private void bindActualData(View rootView) {
        txtEnt010 = (EditText)rootView.findViewById(R.id.txtEnt010);
        //txtSal010 = (EditText)rootView.findViewById(R.id.txtSal010);
        txtEnt020 = (EditText)rootView.findViewById(R.id.txtEnt020);
        txtSal020 = (EditText)rootView.findViewById(R.id.txtSal020);
        txtEnt050 = (EditText)rootView.findViewById(R.id.txtEnt050);
        txtEnt100 = (EditText)rootView.findViewById(R.id.txtEnt100);
        txtSal100 = (EditText)rootView.findViewById(R.id.txtSal100);
        //txtEnt200 = (EditText)rootView.findViewById(R.id.txtEnt200);
        txtEnt500 = (EditText)rootView.findViewById(R.id.txtEnt500);
        txtEnt1000 = (EditText)rootView.findViewById(R.id.txtEnt1000);

        txtEnt010.setText(curRecaudacion.getString(curRecaudacion.getColumnIndex("INC_Entrada010")));
        //txtSal010.setText(curRecaudacion.getString(curRecaudacion.getColumnIndex("INC_Salida010")));
        txtEnt020.setText(curRecaudacion.getString(curRecaudacion.getColumnIndex("INC_Entrada020")));
        txtSal020.setText(curRecaudacion.getString(curRecaudacion.getColumnIndex("INC_Salida020")));
        txtEnt050.setText(curRecaudacion.getString(curRecaudacion.getColumnIndex("INC_Entrada050")));
        txtEnt100.setText(curRecaudacion.getString(curRecaudacion.getColumnIndex("INC_Entrada100")));
        txtSal100.setText(curRecaudacion.getString(curRecaudacion.getColumnIndex("INC_Salida100")));
        //txtEnt200.setText(curRecaudacion.getString(curRecaudacion.getColumnIndex("INC_Entrada200")));
        txtEnt500.setText(curRecaudacion.getString(curRecaudacion.getColumnIndex("INC_Entrada500")));
        txtEnt1000.setText(curRecaudacion.getString(curRecaudacion.getColumnIndex("INC_Entrada1000")));
    }

    private String getMaquina(String columna){
        return curMaquina.getString(curMaquina.getColumnIndex(columna));
    }
    private String getRecaudacion(String columna){
        return curRecaudacion.getString(curRecaudacion.getColumnIndex(columna));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflamos la Vista que se debe mostrar en pantalla.
        View rootView = inflater.inflate(R.layout.fragment_slide_page_contadores, container,
                false);

        curMaquina = ScreenSlidePagerRecaudacion.curMaquina;
        bindAntData(rootView);

        curRecaudacion = ScreenSlidePagerRecaudacion.curRecaudacion;
        bindActualData(rootView);
        return rootView;
    }

    private void saveRecaudacion(){
        ContentValues values = new ContentValues();
        values.put("INC_Entrada010Ant", txtEnt010Ant.getText().toString());
        values.put("INC_Entrada020Ant", txtEnt020Ant.getText().toString());
        values.put("INC_Entrada050Ant", txtEnt050Ant.getText().toString());
        values.put("INC_Entrada100Ant", txtEnt100Ant.getText().toString());
        //values.put("INC_Entrada200Ant", txtEnt200Ant.getText().toString());
        values.put("INC_Entrada500Ant", txtEnt500Ant.getText().toString());
        values.put("INC_Entrada1000Ant", txtEnt1000Ant.getText().toString());
        values.put("INC_Salida020Ant", txtSal020Ant.getText().toString());
        values.put("INC_Salida100Ant", txtSal100Ant.getText().toString());

        values.put("INC_Entrada010", txtEnt010.getText().toString());
        values.put("INC_Entrada020", txtEnt020.getText().toString());
        values.put("INC_Entrada050", txtEnt050.getText().toString());
        values.put("INC_Entrada100", txtEnt100.getText().toString());
        //values.put("INC_Entrada200", txtEnt200.getText().toString());
        values.put("INC_Entrada500", txtEnt500.getText().toString());
        values.put("INC_Entrada1000", txtEnt1000.getText().toString());
        values.put("INC_Salida020", txtSal020.getText().toString());
        values.put("INC_Salida100", txtSal100.getText().toString());
        values.put("INC_Salida020", txtSal020.getText().toString());

        int numRecords = ScreenSlidePagerRecaudacion.dbAdapter.updateRecord("INC_RecaudacionesPDA", values,
                "CodigoEmpresa=? AND INC_CodigoMaquina=?",
                new String[]{getRecaudacion("CodigoEmpresa"), getRecaudacion("INC_CodigoMaquina")});
    }

    @Override
    public void onDestroy() {
        saveRecaudacion();
        super.onDestroy();
    }
}
