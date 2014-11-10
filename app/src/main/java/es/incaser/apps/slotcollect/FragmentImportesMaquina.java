package es.incaser.apps.slotcollect;

import android.app.Activity;
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
public class FragmentImportesMaquina extends Fragment{
    private EditText txtBruto;
    private EditText txtFallos;
    private EditText txtRecCargaEmpresa;
    private EditText txtRecCargaEstablecimiento;
    private EditText txtImporteRecaudacion;
    private EditText txtImporteVarios;
    private EditText txtImporteRetencion;
    private EditText txtPorcentajeDistribucion;
    private EditText txtImporteEstablecimiento;
    private EditText txtImporteNeto;

    private void bindRecaudacionData(View rootView) {
        txtBruto = (EditText)rootView.findViewById(R.id.txtBruto);
        txtFallos = (EditText)rootView.findViewById(R.id.txtFallos);
        txtRecCargaEmpresa = (EditText)rootView.findViewById(R.id.txtRecCargaEmpresa);
        txtRecCargaEstablecimiento = (EditText)rootView.findViewById(R.id.txtRecCargaEstablecimiento);
        txtImporteRecaudacion = (EditText)rootView.findViewById(R.id.txtImporteRecaudacion);
        txtImporteVarios = (EditText)rootView.findViewById(R.id.txtImporteVarios);
        txtImporteRetencion = (EditText)rootView.findViewById(R.id.txtImporteRetencion);
        txtPorcentajeDistribucion = (EditText)rootView.findViewById(R.id.txtPorcentajeDistribucion);
        txtImporteEstablecimiento = (EditText)rootView.findViewById(R.id.txtImporteEstablecimiento);
        txtImporteNeto = (EditText)rootView.findViewById(R.id.txtImporteNeto);

        txtBruto.setOnFocusChangeListener(new CustomOnFocusChange());
        txtFallos.setOnFocusChangeListener(new CustomOnFocusChange());
        txtRecCargaEmpresa.setOnFocusChangeListener(new CustomOnFocusChange());
        txtRecCargaEstablecimiento.setOnFocusChangeListener(new CustomOnFocusChange());
        txtImporteVarios.setOnFocusChangeListener(new CustomOnFocusChange());
        txtImporteRetencion.setOnFocusChangeListener(new CustomOnFocusChange());
        txtPorcentajeDistribucion.setOnFocusChangeListener(new CustomOnFocusChange());
        txtImporteEstablecimiento.setOnFocusChangeListener(new CustomOnFocusChange());
    }

    public void setData(){
        txtBruto.setText(getRecaudacionImporte("INC_Bruto"));
        txtFallos.setText(getRecaudacionImporte("INC_Fallos"));
        txtRecCargaEmpresa.setText(getRecaudacionImporte("INC_RecuperaCargaEmpresa"));
        txtRecCargaEstablecimiento.setText(getRecaudacionImporte("INC_RecuperaCargaEstablecimiento"));
        txtImporteRecaudacion.setText(getRecaudacionImporte("INC_ImporteRecaudacion"));
        txtImporteVarios.setText(getRecaudacionImporte("INC_ImporteVarios"));
        txtImporteRetencion.setText(getRecaudacionImporte("INC_ImporteRetencion"));
        txtPorcentajeDistribucion.setText(getRecaudacionImporte("INC_PorcentajeDistribucion"));
        txtImporteEstablecimiento.setText(getRecaudacionImporte("INC_ImporteEstablecimiento"));
        txtImporteNeto.setText(getRecaudacionImporte("INC_ImporteNeto"));
    };

    public void save(ContentValues cv){
        cv.put("INC_Bruto", getNumber(txtBruto));
        cv.put("INC_Fallos", getNumber(txtFallos));
        cv.put("INC_RecuperaCargaEmpresa", getNumber(txtRecCargaEmpresa));
        cv.put("INC_RecuperaCargaEstablecimiento", getNumber(txtRecCargaEstablecimiento));
        cv.put("INC_ImporteRecaudacion", getNumber(txtImporteRecaudacion));
        cv.put("INC_ImporteVarios", getNumber(txtImporteVarios));
        cv.put("INC_ImporteRetencion", getNumber(txtImporteRetencion));
        cv.put("INC_PorcentajeDistribucion", getNumber(txtPorcentajeDistribucion));
        cv.put("INC_ImporteEstablecimiento", getNumber(txtImporteEstablecimiento));
        cv.put("INC_ImporteNeto", getNumber(txtImporteNeto));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_slide_page_importes, container,false);
        bindRecaudacionData(rootView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        setData();
    }

    private String getRecaudacion(String col){
        return Recaudacion.cvRecaudacion.getAsString(col);
    }

    private String getRecaudacionImporte(String col){
        return importeStr(getRecaudacion(col));
    }

    private class CustomOnFocusChange implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View view, boolean b) {
            if (! b){
                save(Recaudacion.cvRecaudacion);
                Recaudacion.calcData();
                //Recaudacion.calcImportes();
                setData();
            }
        }
    }
}
