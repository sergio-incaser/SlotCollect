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
    private static Cursor curRecaudacion;
    private static Cursor curMaquina;
    private static DbAdapter dbAdapter;

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

    private void setArqueoData(){
        chkArqueoRealizado.setChecked(curRecaudacion.getInt(curRecaudacion.getColumnIndex("INC_ArqueoRealizado"))!= 0);
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

    protected void saveRecaudacion(){
        ContentValues values = new ContentValues();

        values.put("INC_ArqueoRealizado", chkArqueoRealizado.isChecked());
        values.put("INC_TipoArqueo", txtTipoArqueo.getText().toString());
        values.put("INC_ValorArqueoTeorico", getNumber(txtArqueoTeorico));
        values.put("INC_IntroducidoHopper", getNumber(txtCargaHopper));
        values.put("INC_CargaHopperEmpresa", getNumber(txtCargaRecEmpresa));
        values.put("INC_CargaHopperEstablecimiento", getNumber(txtCargaRecEstablecimiento));
        values.put("INC_DiferenciaRecaudacion", getNumber(txtDiferenciaRecaudacion));
        values.put("INC_DiferenciaArqueo", getNumber(txtDiferenciaArqueo));
        values.put("INC_DiferenciaInstalacion", getNumber(txtDiferenciaInstalacion));
        values.put("INC_DiasEfectivosUR", getNumber(txtDiasEfectivosUR));
        values.put("INC_DiasNaturalesUR", getNumber(txtDiasNaturalesUR));
        values.put("INC_MediaDiaria", getNumber(txtMediaDiaria));

        int numRecords = Recaudacion.dbAdapter.updateRecord("INC_LineasRecaudacion", values,
                "id=?",
                new String[]{Recaudacion.getRecaudacion("id")});
        Recaudacion.isModified = false;
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
        curMaquina = Recaudacion.curMaquina;
        curRecaudacion = Recaudacion.curRecaudacion;
        dbAdapter = Recaudacion.dbAdapter;
        setArqueoData();
    }

    @Override
    public void onPause() {
        saveRecaudacion();
        super.onPause();
    }

    protected void calculaArqueo() {
        long diasNaturales = 0;
        float valorArqueoteorico = 0;
        float diferenciaRecaudacion = 0;
        float diferenciaArqueo = 0;
        float diferenciaInstalacion = 0;
        float porcentajeRecaudacion = 0;
        float porcentajeArqueo = 0;
        float porcentajeInstalacion = 0;

        curRecaudacion = Recaudacion.curRecaudacion;

        if (chkArqueoRealizado.isChecked()){
            diferenciaRecaudacion = (getRecaudacionFloat("INC_Bruto")
                    - getRecaudacionFloat("INC_JugadoTeorico")
                    + getRecaudacionFloat("INC_PremioTeorico")
                    + getNumber(txtArqueoTeorico)
                    - Recaudacion.valorUltimoArqueo);
        } else {
            diferenciaRecaudacion = (getRecaudacionFloat("INC_Bruto")
                    - getRecaudacionFloat("INC_JugadoTeorico")
                    + getRecaudacionFloat("INC_PremioTeorico"));
        }

        txtDiferenciaRecaudacion.setText(importeStr(diferenciaRecaudacion));

        porcentajeRecaudacion = (getRecaudacionFloat("INC_PremioTeorico")
                / getRecaudacionFloat("INC_JugadoTeorico") * 100);
        txtPorcPremio.setText(importeStr(porcentajeRecaudacion));

        if (Recaudacion.curUltimaRecaudacion.moveToFirst()){
            Recaudacion.fechaUltimaRecaudacion = str2date(Recaudacion.curUltimaRecaudacion.getString(
                    Recaudacion.curUltimaRecaudacion.getColumnIndex("INC_FechaRecaudacion")));
        }else{
            Recaudacion.fechaUltimaRecaudacion = str2date(curMaquina.getString(curMaquina.getColumnIndex("INC_FechaInstalacion")));
        }
        Date now = str2date(getToday(),"yyyy-MM-dd");
        diasNaturales = now.getTime() - Recaudacion.fechaUltimaRecaudacion.getTime();
        diasNaturales = diasNaturales / 86400000;
        if (diasNaturales == 0){
            diasNaturales += 1;
        };

        txtDiasNaturalesUR.setText(enteroStr(Math.round(diasNaturales)));
        txtDiasEfectivosUR.setText(txtDiasNaturalesUR.getText().toString());
        txtMediaDiaria.setText(importeStr(getRecaudacionFloat("INC_Bruto") / diasNaturales));

        if (Recaudacion.curSumasDesdeI.moveToFirst()) {
            diferenciaInstalacion = diferenciaRecaudacion
                    + Recaudacion.curSumasDesdeI.getFloat(Recaudacion.curSumasDesdeI.getColumnIndex("SumaBruto"))
                    - Recaudacion.curSumasDesdeI.getFloat(Recaudacion.curSumasDesdeI.getColumnIndex("SumaJugadoTeorico"))
                    + Recaudacion.curSumasDesdeI.getFloat(Recaudacion.curSumasDesdeI.getColumnIndex("SumaPremioTeorico"));

            porcentajeInstalacion = (Recaudacion.curSumasDesdeI.getFloat(Recaudacion.curSumasDesdeI.getColumnIndex("SumaPremioTeorico"))
                    + getRecaudacionFloat("INC_PremioTeorico"))
                    / (Recaudacion.curSumasDesdeI.getFloat(Recaudacion.curSumasDesdeI.getColumnIndex("SumaJugadoTeorico"))
                    + getRecaudacionFloat("INC_JugadoTeorico")) * 100;
        }else{
            diferenciaInstalacion = diferenciaRecaudacion;
            porcentajeInstalacion = porcentajeRecaudacion;
        }
        txtDiferenciaInstalacion.setText(importeStr(diferenciaInstalacion));
        txtPorcInstalacion.setText(importeStr(porcentajeInstalacion));



        if (Recaudacion.curSumasDesdeA.moveToFirst()){
            diferenciaArqueo = diferenciaRecaudacion
                    + Recaudacion.curSumasDesdeA.getFloat(Recaudacion.curSumasDesdeA.getColumnIndex("SumaBruto"))
                    - Recaudacion.curSumasDesdeA.getFloat(Recaudacion.curSumasDesdeA.getColumnIndex("SumaJugadoTeorico"))
                    + Recaudacion.curSumasDesdeA.getFloat(Recaudacion.curSumasDesdeA.getColumnIndex("SumaPremioTeorico"));

            porcentajeArqueo = (Recaudacion.curSumasDesdeA.getFloat(Recaudacion.curSumasDesdeA.getColumnIndex("SumaPremioTeorico"))
                    + getRecaudacionFloat("INC_PremioTeorico"))
                    / (Recaudacion.curSumasDesdeA.getFloat(Recaudacion.curSumasDesdeA.getColumnIndex("SumaJugadoTeorico"))
                    + getRecaudacionFloat("INC_JugadoTeorico")) * 100;
        }else {
            diferenciaArqueo = diferenciaInstalacion;
            porcentajeArqueo = porcentajeInstalacion;
        }

        txtDiferenciaArqueo.setText(importeStr(diferenciaArqueo));
        txtPorcArqueo.setText(importeStr(porcentajeArqueo));

        if (! chkArqueoRealizado.isChecked()){
            valorArqueoteorico = Recaudacion.valorUltimoArqueo - diferenciaArqueo;
            txtArqueoTeorico.setText(importeStr(valorArqueoteorico));
        }

        Recaudacion.isModified = true;
    }
    private String getRecaudacion(String col){
        return curRecaudacion.getString(curRecaudacion.getColumnIndex(col));
    }

    private Float getRecaudacionFloat(String col){
        return getNumber(getRecaudacion(col));
    }
}
