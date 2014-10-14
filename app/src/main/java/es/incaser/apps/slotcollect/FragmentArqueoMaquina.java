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

    private void bindRecaudacionData(View rootView) {
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

        chkArqueoRealizado.setChecked(curRecaudacion.getInt(curRecaudacion.getColumnIndex("INC_ArqueoRealizado"))!= 0);
        txtTipoArqueo.setText(ScreenSlidePagerRecaudacion.getRecaudacion("INC_TipoArqueo"));
        txtArqueoTeorico.setText(ScreenSlidePagerRecaudacion.getRecaudacionImporte("INC_ValorArqueoTeorico"));
        txtCargaHopper.setText(ScreenSlidePagerRecaudacion.getRecaudacionImporte("INC_IntroducidoHopper"));
        txtCargaRecEmpresa.setText(ScreenSlidePagerRecaudacion.getRecaudacionImporte("INC_CargaHopperEmpresa"));
        txtCargaRecEstablecimiento.setText(ScreenSlidePagerRecaudacion.getRecaudacionImporte("INC_CargaHopperEstablecimiento"));
        txtDiferenciaRecaudacion.setText(ScreenSlidePagerRecaudacion.getRecaudacionImporte("INC_DiferenciaRecaudacion"));
        //txtPorcPremio.setText(getRecaudacionImporte("INC_ImporteRetencion"));
        txtDiferenciaArqueo.setText(ScreenSlidePagerRecaudacion.getRecaudacionImporte("INC_DiferenciaArqueo"));
        //txtPorcArqueo.setText(getRecaudacionImporte("INC_ImporteEstablecimiento"));
        txtDiferenciaInstalacion.setText(ScreenSlidePagerRecaudacion.getRecaudacionImporte("INC_DiferenciaInstalacion"));
        //txtPorcInstalacion.setText(getRecaudacionImporte("INC_ImporteNeto"));

        txtDiasEfectivosUR.setText(ScreenSlidePagerRecaudacion.getRecaudacionImporte("INC_DiasEfectivosUR"));
        txtDiasNaturalesUR.setText(ScreenSlidePagerRecaudacion.getRecaudacionImporte("INC_DiasNaturalesUR"));
        txtMediaDiaria.setText(ScreenSlidePagerRecaudacion.getRecaudacionImporte("INC_MediaDiaria"));

//        txtBruto.setOnFocusChangeListener(new CustomOnFocusChange());
//        txtFallos.setOnFocusChangeListener(new CustomOnFocusChange());
//        txtRecCargaEmpresa.setOnFocusChangeListener(new CustomOnFocusChange());
//        txtRecCargaEstablecimiento.setOnFocusChangeListener(new CustomOnFocusChange());
//        txtImporteVarios.setOnFocusChangeListener(new CustomOnFocusChange());
//        txtImporteRetencion.setOnFocusChangeListener(new CustomOnFocusChange());
//        txtPorcentajeDistribucion.setOnFocusChangeListener(new CustomOnFocusChange());
//        txtImporteEstablecimiento.setOnFocusChangeListener(new CustomOnFocusChange());
//        txtImporteEstablecimiento.setOnFocusChangeListener(new CustomOnFocusChange());
    }

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

        int numRecords = ScreenSlidePagerRecaudacion.dbAdapter.updateRecord("INC_LineasRecaudacion", values,
                "id=?",
                new String[]{ScreenSlidePagerRecaudacion.getRecaudacion("id")});
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflamos la Vista que se debe mostrar en pantalla.
        View rootView = inflater.inflate(R.layout.fragment_slide_page_arqueo, container,
                false);
        curMaquina = ScreenSlidePagerRecaudacion.curMaquina;
        curRecaudacion = ScreenSlidePagerRecaudacion.curRecaudacion;
        dbAdapter = ScreenSlidePagerRecaudacion.dbAdapter;
        bindRecaudacionData(rootView);
        return rootView;
    }

    @Override
    public void onDestroy() {
        saveRecaudacion();
        super.onDestroy();
    }
    protected void calculaArqueo() {
        long diasNaturales = 0;
        float valorUltimoArqueo = 0;
        float valorArqueoteorico = 0;
        Date fechaUltimaRecaudacion;
        String fechaUltimoArqueo;
        float diferenciaRecaudacion = 0;
        float diferenciaArqueo = 0;
        float diferenciaInstalacion = 0;
        float porcentajeRecaudacion = 0;
        float porcentajeArqueo = 0;
        float porcentajeInstalacion = 0;

        curRecaudacion = ScreenSlidePagerRecaudacion.curRecaudacion;

        Cursor curUltimoArqueo = dbAdapter.getUltimoArqueo(getRecaudacion("CodigoEmpresa"),
                getRecaudacion("INC_CodigoEstablecimiento"),
                getRecaudacion("INC_CodigoMaquina"));

        //Obtener el valor introducido en el hopper del ultimo arqueo o de la instalacion
        if (curUltimoArqueo.moveToFirst()) {
            valorUltimoArqueo = curUltimoArqueo.getFloat(curUltimoArqueo.getColumnIndex("INC_ValorArqueoTeorico"));
            fechaUltimoArqueo = curUltimoArqueo.getString(curUltimoArqueo.getColumnIndex("INC_FechaRecaudacion"));
        } else {
            valorUltimoArqueo = curMaquina.getFloat(curMaquina.getColumnIndex("INC_IntroducidoHopper"));
            fechaUltimoArqueo = curMaquina.getString(curMaquina.getColumnIndex("INC_FechaInstalacion"));
        }
//        Ifn Lineas.INC_ArqueoRealizado=0 Then
//          Lineas.INC_DiferenciaRecaudacion= Lineas.INC_Bruto - Lineas.INC_JugadoTeorico + Lineas.INC_PremioTeorico
//        Else
//          Gosub CargarUltimoArqueo
//          Lineas.INC_DiferenciaRecaudacion= Lineas.INC_Bruto - Lineas.INC_JugadoTeorico + Lineas.INC_PremioTeorico + Lineas.INC_ValorArqueoTeorico - ValorUltimoArqueo
//        Endif
        if (chkArqueoRealizado.isChecked()){
            diferenciaRecaudacion = (getRecaudacionFloat("INC_Bruto")
                    - getRecaudacionFloat("INC_JugadoTeorico")
                    + getRecaudacionFloat("INC_PremioTeorico")
                    + getNumber(txtArqueoTeorico)
                    - valorUltimoArqueo);
        } else {
            diferenciaRecaudacion = (getRecaudacionFloat("INC_Bruto")
                    - getRecaudacionFloat("INC_JugadoTeorico")
                    + getRecaudacionFloat("INC_PremioTeorico"));
        }

        txtDiferenciaRecaudacion.setText(importeStr(diferenciaRecaudacion));

        porcentajeRecaudacion = (getRecaudacionFloat("INC_PremioTeorico")
                / getRecaudacionFloat("INC_JugadoTeorico") * 100);
        txtPorcPremio.setText(importeStr(porcentajeRecaudacion));

        Cursor curUltimaRecaudacion = dbAdapter.getUltimaRecaudacion(getRecaudacion("CodigoEmpresa"),
                                                        getRecaudacion("INC_CodigoEstablecimiento"),
                                                        getRecaudacion("INC_CodigoMaquina"));
        if (curUltimaRecaudacion.moveToFirst()){
            fechaUltimaRecaudacion = str2date(curUltimaRecaudacion.getString(
                                    curUltimaRecaudacion.getColumnIndex("INC_FechaRecaudacion")));
        }else{
            fechaUltimaRecaudacion = str2date(curMaquina.getString(curMaquina.getColumnIndex("INC_FechaInstalacion")));
        }
        Date now = str2date(getToday(),"yyyy-MM-dd");
        diasNaturales = now.getTime() - fechaUltimaRecaudacion.getTime();
        diasNaturales = diasNaturales / 86400000;

        txtDiasNaturalesUR.setText(enteroStr(Math.round(diasNaturales)));
        txtDiasEfectivosUR.setText(txtDiasNaturalesUR.getText().toString());
        txtMediaDiaria.setText(importeStr(getRecaudacionFloat("INC_Bruto") / diasNaturales));

        Cursor curSumasDesdeI = dbAdapter.getSumasDesde(getRecaudacion("CodigoEmpresa"),
                getRecaudacion("INC_CodigoEstablecimiento"),
                getRecaudacion("INC_CodigoMaquina"),
                curMaquina.getString(curMaquina.getColumnIndex("INC_FechaInstalacion")));
        if (curSumasDesdeI.moveToFirst()) {
            diferenciaInstalacion = diferenciaRecaudacion
                    + curSumasDesdeI.getFloat(curSumasDesdeI.getColumnIndex("SumaBruto"))
                    - curSumasDesdeI.getFloat(curSumasDesdeI.getColumnIndex("SumaJugadoTeorico"))
                    + curSumasDesdeI.getFloat(curSumasDesdeI.getColumnIndex("SumaPremioTeorico"));

            porcentajeInstalacion = (curSumasDesdeI.getFloat(curSumasDesdeI.getColumnIndex("SumaPremioTeorico"))
                    + getRecaudacionFloat("INC_PremioTeorico"))
                    / (curSumasDesdeI.getFloat(curSumasDesdeI.getColumnIndex("SumaJugadoTeorico"))
                    + getRecaudacionFloat("INC_JugadoTeorico")) * 100;
        }else{
            diferenciaInstalacion = diferenciaRecaudacion;
            porcentajeInstalacion = porcentajeRecaudacion;
        }
        txtDiferenciaInstalacion.setText(importeStr(diferenciaInstalacion));
        txtPorcInstalacion.setText(importeStr(porcentajeInstalacion));

        Cursor curSumasDesdeA = dbAdapter.getSumasDesde(getRecaudacion("CodigoEmpresa"),
                getRecaudacion("INC_CodigoEstablecimiento"),
                getRecaudacion("INC_CodigoMaquina"), fechaUltimoArqueo);


        if (curSumasDesdeA.moveToFirst()){
            diferenciaArqueo = diferenciaRecaudacion
                    + curSumasDesdeA.getFloat(curSumasDesdeA.getColumnIndex("SumaBruto"))
                    - curSumasDesdeA.getFloat(curSumasDesdeA.getColumnIndex("SumaJugadoTeorico"))
                    + curSumasDesdeA.getFloat(curSumasDesdeA.getColumnIndex("SumaPremioTeorico"));

            porcentajeArqueo = (curSumasDesdeA.getFloat(curSumasDesdeA.getColumnIndex("SumaPremioTeorico"))
                    + getRecaudacionFloat("INC_PremioTeorico"))
                    / (curSumasDesdeA.getFloat(curSumasDesdeA.getColumnIndex("SumaJugadoTeorico"))
                    + getRecaudacionFloat("INC_JugadoTeorico")) * 100;
        }else {
            diferenciaArqueo = diferenciaInstalacion;
            porcentajeArqueo = porcentajeInstalacion;
        }

        txtDiferenciaArqueo.setText(importeStr(diferenciaArqueo));
        txtPorcArqueo.setText(importeStr(porcentajeArqueo));

        if (! chkArqueoRealizado.isChecked()){
            valorArqueoteorico = valorUltimoArqueo - diferenciaArqueo;
            txtArqueoTeorico.setText(importeStr(valorArqueoteorico));
        }

        ScreenSlidePagerRecaudacion.isModified = true;
    }
    private String getRecaudacion(String col){
        return curRecaudacion.getString(curRecaudacion.getColumnIndex(col));
    }

    private Float getRecaudacionFloat(String col){
        return getNumber(getRecaudacion(col));
    }
}
