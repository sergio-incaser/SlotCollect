package es.incaser.apps.slotcollect;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;

import static es.incaser.apps.slotcollect.tools.*;
import static es.incaser.apps.slotcollect.tools.importeStr;

/**
 * Created by sergio on 28/09/14.
 */
public class FragmentArqueoMaquina extends Fragment {
    private CheckBox chkArqueoRealizado;
    private EditText txtTipoArqueo;
    private EditText txtArqueoTeorico;
    private EditText txtCargaHopper;
    private EditText txtCargaRecEmpresa;
    private EditText txtCargaRecEstablecimiento;
    private EditText txtDiferenciaRecaudacion;
    private EditText txtPorcPremio;
    private EditText txtDiferenciaArqueo;
    private EditText txtPorcArqueo;
    private EditText txtDiferenciaInstalacion;
    private EditText txtPorcInstalacion;
    private EditText txtDiasEfectivosUR;
    private EditText txtDiasNaturalesUR;
    private EditText txtMediaDiaria;

    private void bindArqueoData(View rootView) {
        chkArqueoRealizado = (CheckBox) rootView.findViewById(R.id.chArqueoRealizado);
        txtTipoArqueo = (EditText)rootView.findViewById(R.id.txtTipoArqueo);
        txtArqueoTeorico = (EditText)rootView.findViewById(R.id.txtArqueoTeorico);
        txtCargaHopper = (EditText)rootView.findViewById(R.id.txtCargaHopper);
        txtCargaRecEmpresa = (EditText)rootView.findViewById(R.id.txtCargaRecEmpresa);
        txtCargaRecEstablecimiento = (EditText)rootView.findViewById(R.id.txtCargaRecEstablecimiento);
        txtDiferenciaRecaudacion = (EditText)rootView.findViewById(R.id.txtDiferenciaRecaudacion);
        txtPorcPremio = (EditText)rootView.findViewById(R.id.txtPorcPremio);
        txtDiferenciaArqueo = (EditText)rootView.findViewById(R.id.txtDiferenciaArqueo);
        txtPorcArqueo = (EditText)rootView.findViewById(R.id.txtPorcArqueo);
        txtDiferenciaInstalacion = (EditText)rootView.findViewById(R.id.txtDiferenciaInstalacion);
        txtPorcInstalacion = (EditText)rootView.findViewById(R.id.txtPorcInstalacion);
        txtDiasEfectivosUR = (EditText)rootView.findViewById(R.id.txtDiasEfectivosUR);
        txtDiasNaturalesUR = (EditText)rootView.findViewById(R.id.txtDiasNaturalesUR);
        txtMediaDiaria = (EditText)rootView.findViewById(R.id.txtMediaDiaria);
    }

    public void setData(){
        chkArqueoRealizado.setChecked(Recaudacion.getBoolRecaudacion("INC_ArqueoRealizado"));
        txtTipoArqueo.setText(Recaudacion.getRecaudacion("INC_TipoArqueo"));
        txtArqueoTeorico.setText(Recaudacion.getRecaudacionImporte("INC_ValorArqueoTeorico"));
        txtCargaHopper.setText(Recaudacion.getRecaudacionImporte("INC_IntroducidoHopper"));
        txtCargaRecEmpresa.setText(Recaudacion.getRecaudacionImporte("INC_CargaHopperEmpresa"));
        txtCargaRecEstablecimiento.setText(Recaudacion.getRecaudacionImporte("INC_CargaHopperEstablecimiento"));
        txtDiferenciaRecaudacion.setText(Recaudacion.getRecaudacionImporte("INC_DiferenciaRecaudacion"));
        //txtPorcPremio.setText(getRecaudacionImporte("INC_ImporteRetencion"));
        txtDiferenciaArqueo.setText(Recaudacion.getRecaudacionImporte("INC_DiferenciaArqueo"));
        //txtPorcArqueo.setText(getRecaudacionImporte("INC_ImporteEstablecimiento"));
        txtDiferenciaInstalacion.setText(Recaudacion.getRecaudacionImporte("INC_DiferenciaInstalacion"));
        //txtPorcInstalacion.setText(getRecaudacionImporte("INC_ImporteNeto"));

        txtDiasEfectivosUR.setText(Recaudacion.getRecaudacionImporte("INC_DiasEfectivosUR"));
        txtDiasNaturalesUR.setText(Recaudacion.getRecaudacionImporte("INC_DiasNaturalesUR"));
        txtMediaDiaria.setText(Recaudacion.getRecaudacionImporte("INC_MediaDiaria"));
    };

    protected void save(ContentValues cv){
        cv.put("INC_ArqueoRealizado", chkArqueoRealizado.isChecked());
        cv.put("INC_TipoArqueo", txtTipoArqueo.getText().toString());
        cv.put("INC_ValorArqueoTeorico", getNumber(txtArqueoTeorico));
        cv.put("INC_IntroducidoHopper", getNumber(txtCargaHopper));
        cv.put("INC_CargaHopperEmpresa", getNumber(txtCargaRecEmpresa));
        cv.put("INC_CargaHopperEstablecimiento", getNumber(txtCargaRecEstablecimiento));
        cv.put("INC_DiferenciaRecaudacion", getNumber(txtDiferenciaRecaudacion));
        cv.put("INC_DiferenciaArqueo", getNumber(txtDiferenciaArqueo));
        cv.put("INC_DiferenciaInstalacion", getNumber(txtDiferenciaInstalacion));
        cv.put("INC_DiasEfectivosUR", getNumber(txtDiasEfectivosUR));
        cv.put("INC_DiasNaturalesUR", getNumber(txtDiasNaturalesUR));
        cv.put("INC_MediaDiaria", getNumber(txtMediaDiaria));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflamos la Vista que se debe mostrar en pantalla.
        View rootView = inflater.inflate(R.layout.fragment_slide_page_arqueo, container,
                false);
        bindArqueoData(rootView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        setData();
        calculaPorcentajes();
    }

    private String getRecaudacion(String col){
        return Recaudacion.getRecaudacion(col);
    }

    private Float getFloatRecaudacion(String col){
        return getNumber(getRecaudacion(col));
    }

    private void calculaPorcentajes(){
        txtPorcPremio.setText(importeStr(Recaudacion.porcentajeRecaudacion));
        txtPorcArqueo.setText(importeStr(Recaudacion.porcentajeArqueo));
        txtPorcInstalacion.setText(importeStr(Recaudacion.porcentajeInstalacion));
    }

}
