package es.incaser.apps.slotcollect;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import static es.incaser.apps.slotcollect.tools.*;

/**
 * Created by sergio on 28/09/14.
 */
public class FragmentContadoresMaquina extends Fragment {

    //Contadores Anteriores
    EditText txtSal010Ant;
    EditText txtEnt010Ant;
    EditText txtEnt020Ant;
    EditText txtSal020Ant;
    EditText txtEnt050Ant;
    EditText txtEnt100Ant;
    EditText txtSal100Ant;
    EditText txtEnt200Ant;
    EditText txtEnt500Ant;
    EditText txtEnt1000Ant;

    //Contadores Actuales
    EditText txtEnt010;
    EditText txtSal010;
    EditText txtEnt020;
    EditText txtSal020;
    EditText txtEnt050;
    EditText txtEnt100;
    EditText txtSal100;
    EditText txtEnt200;
    EditText txtEnt500;
    EditText txtEnt1000;

    //Valores teoricos
    EditText txtJugadoTeorico;
    EditText txtPremioTeorico;
    EditText txtPartidas;

    private void bindAntData(View rootView){
        txtSal010Ant = (EditText)rootView.findViewById(R.id.txtSal010Ant);
        txtEnt010Ant = (EditText)rootView.findViewById(R.id.txtEnt010Ant);
        txtEnt020Ant = (EditText)rootView.findViewById(R.id.txtEnt020Ant);
        txtSal020Ant = (EditText)rootView.findViewById(R.id.txtSal020Ant);
        txtEnt050Ant = (EditText)rootView.findViewById(R.id.txtEnt050Ant);
        txtEnt100Ant = (EditText)rootView.findViewById(R.id.txtEnt100Ant);
        txtSal100Ant = (EditText)rootView.findViewById(R.id.txtSal100Ant);
        txtEnt200Ant = (EditText)rootView.findViewById(R.id.txtEnt200Ant);
        txtEnt500Ant = (EditText)rootView.findViewById(R.id.txtEnt500Ant);
        txtEnt1000Ant = (EditText)rootView.findViewById(R.id.txtEnt1000Ant);
    }

    private void setAntData(){
        txtEnt010Ant.setText(getRecaudacion("INC_Entrada010ANT"));
        txtSal010Ant.setText(getRecaudacion("INC_Salida010ANT"));
        txtEnt020Ant.setText(getRecaudacion("INC_Entrada020ANT"));
        txtSal020Ant.setText(getRecaudacion("INC_Salida020ANT"));
        txtEnt050Ant.setText(getRecaudacion("INC_Entrada050ANT"));
        txtEnt100Ant.setText(getRecaudacion("INC_Entrada100ANT"));
        txtSal100Ant.setText(getRecaudacion("INC_Salida100ANT"));
        txtEnt200Ant.setText(getRecaudacion("INC_Entrada200ANT"));
        txtEnt500Ant.setText(getRecaudacion("INC_Entrada500ANT"));
        txtEnt1000Ant.setText(getRecaudacion("INC_Entrada1000ANT"));
    }

    private void bindActualData(View rootView) {
        txtEnt010 = (EditText)rootView.findViewById(R.id.txtEnt010);
        txtSal010 = (EditText)rootView.findViewById(R.id.txtSal010);
        txtEnt020 = (EditText)rootView.findViewById(R.id.txtEnt020);
        txtSal020 = (EditText)rootView.findViewById(R.id.txtSal020);
        txtEnt050 = (EditText)rootView.findViewById(R.id.txtEnt050);
        txtEnt100 = (EditText)rootView.findViewById(R.id.txtEnt100);
        txtSal100 = (EditText)rootView.findViewById(R.id.txtSal100);
        txtEnt200 = (EditText)rootView.findViewById(R.id.txtEnt200);
        txtEnt500 = (EditText)rootView.findViewById(R.id.txtEnt500);
        txtEnt1000 = (EditText)rootView.findViewById(R.id.txtEnt1000);

        txtJugadoTeorico = (EditText)rootView.findViewById(R.id.txtJugadoTeorico);
        txtPremioTeorico = (EditText)rootView.findViewById(R.id.txtPremioTeorico);
        txtPartidas = (EditText)rootView.findViewById(R.id.txtPartidas);

        txtEnt010.setOnFocusChangeListener(new CustomOnFocusChange());
        txtSal010.setOnFocusChangeListener(new CustomOnFocusChange());
        txtEnt020.setOnFocusChangeListener(new CustomOnFocusChange());
        txtSal020.setOnFocusChangeListener(new CustomOnFocusChange());
        txtEnt050.setOnFocusChangeListener(new CustomOnFocusChange());
        txtEnt100.setOnFocusChangeListener(new CustomOnFocusChange());
        txtSal100.setOnFocusChangeListener(new CustomOnFocusChange());
        txtEnt200.setOnFocusChangeListener(new CustomOnFocusChange());
        txtEnt500.setOnFocusChangeListener(new CustomOnFocusChange());
        txtEnt1000.setOnFocusChangeListener(new CustomOnFocusChange());
    }

    private void setData(){
        txtEnt010.setText(getRecaudacion("INC_Entrada010"));
        txtSal010.setText(getRecaudacion("INC_Salida010"));
        txtEnt020.setText(getRecaudacion("INC_Entrada020"));
        txtSal020.setText(getRecaudacion("INC_Salida020"));
        txtEnt050.setText(getRecaudacion("INC_Entrada050"));
        txtEnt100.setText(getRecaudacion("INC_Entrada100"));
        txtSal100.setText(getRecaudacion("INC_Salida100"));
        txtEnt200.setText(getRecaudacion("INC_Entrada200"));
        txtEnt500.setText(getRecaudacion("INC_Entrada500"));
        txtEnt1000.setText(getRecaudacion("INC_Entrada1000"));

        txtJugadoTeorico.setText(getRecaudacion("INC_JugadoTeorico"));
        txtPremioTeorico.setText(getRecaudacion("INC_PremioTeorico"));
        txtPartidas.setText(getRecaudacion("INC_Partidas"));
    };
    
    private String getRecaudacion(String columna){
        return Recaudacion.cvRecaudacion.getAsString(columna);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflamos la Vista que se debe mostrar en pantalla.
        View rootView = inflater.inflate(R.layout.fragment_slide_page_contadores, container,
                false);
        bindAntData(rootView);
        bindActualData(rootView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        setAntData();
        setData();
    }

    protected void save(ContentValues cv){


        cv.put("INC_Entrada010Ant", txtEnt010Ant.getText().toString());
        cv.put("INC_Entrada020Ant", txtEnt020Ant.getText().toString());
        cv.put("INC_Entrada050Ant", txtEnt050Ant.getText().toString());
        cv.put("INC_Entrada100Ant", txtEnt100Ant.getText().toString());
        cv.put("INC_Entrada200Ant", txtEnt200Ant.getText().toString());
        cv.put("INC_Entrada500Ant", txtEnt500Ant.getText().toString());
        cv.put("INC_Entrada1000Ant", txtEnt1000Ant.getText().toString());
        cv.put("INC_Salida010Ant", txtSal010Ant.getText().toString());
        cv.put("INC_Salida020Ant", txtSal020Ant.getText().toString());
        cv.put("INC_Salida100Ant", txtSal100Ant.getText().toString());

        cv.put("INC_Entrada010", txtEnt010.getText().toString());
        cv.put("INC_Entrada020", txtEnt020.getText().toString());
        cv.put("INC_Entrada050", txtEnt050.getText().toString());
        cv.put("INC_Entrada100", txtEnt100.getText().toString());
        cv.put("INC_Entrada200", txtEnt200.getText().toString());
        cv.put("INC_Entrada500", txtEnt500.getText().toString());
        cv.put("INC_Entrada1000", txtEnt1000.getText().toString());
        cv.put("INC_Salida010", txtSal010.getText().toString());
        cv.put("INC_Salida020", txtSal020.getText().toString());
        cv.put("INC_Salida100", txtSal100.getText().toString());

        cv.put("INC_JugadoTeorico", txtJugadoTeorico.getText().toString());
        cv.put("INC_PremioTeorico", txtPremioTeorico.getText().toString());
        cv.put("INC_Partidas", txtPartidas.getText().toString());
    }

    private class CustomOnFocusChange implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View view, boolean b) {
            if (! b){
                save(Recaudacion.cvRecaudacion);
                Recaudacion.calcData();
                //Recaudacion.calcTeoricos();
                setData();
            }
        }
    }
}
