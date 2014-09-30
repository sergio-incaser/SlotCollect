package es.incaser.apps.slotcollect;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowId;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by sergio on 28/09/14.
 */
public class FragmentImportesMaquina extends Fragment{
    //private static Cursor curMaquina;
    private static Cursor curRecaudacion;
    private static DbAdapter dbAdapter;

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

        txtBruto.setOnFocusChangeListener(new CustomOnFocusChange());
        txtFallos.setOnFocusChangeListener(new CustomOnFocusChange());
        txtRecCargaEmpresa.setOnFocusChangeListener(new CustomOnFocusChange());
        txtRecCargaEstablecimiento.setOnFocusChangeListener(new CustomOnFocusChange());
        txtImporteVarios.setOnFocusChangeListener(new CustomOnFocusChange());
        txtImporteRetencion.setOnFocusChangeListener(new CustomOnFocusChange());
        txtPorcentajeDistribucion.setOnFocusChangeListener(new CustomOnFocusChange());
        txtImporteEstablecimiento.setOnFocusChangeListener(new CustomOnFocusChange());
    }

    private void saveRecaudacion(){
        ContentValues values = new ContentValues();

        values.put("INC_Bruto", getNumber(txtBruto));
        values.put("INC_Fallos", getNumber(txtFallos));
        values.put("INC_RecuperaCargaEmpresa", getNumber(txtRecCargaEmpresa));
        values.put("INC_RecuperaCargaEstablecimiento", getNumber(txtRecCargaEstablecimiento));
        values.put("INC_ImporteRecaudacion", getNumber(txtImporteRecaudacion));
        values.put("INC_ImporteVarios", getNumber(txtImporteVarios));
        values.put("INC_PorcentajeDistribucion", getNumber(txtPorcentajeDistribucion));
        values.put("INC_ImporteEstablecimiento", getNumber(txtImporteEstablecimiento));
        values.put("INC_ImporteNeto", getNumber(txtImporteNeto));

        int numRecords = ScreenSlidePagerRecaudacion.dbAdapter.updateRecord("INC_RecaudacionesPDA", values,
                "CodigoEmpresa=? AND INC_CodigoMaquina=?",
                new String[]{getRecaudacion("CodigoEmpresa"), getRecaudacion("INC_CodigoMaquina")});
    }

    @Override
    public void onDestroy() {
        saveRecaudacion();
        super.onDestroy();
    }


    private String getRecaudacion(String columna){
        return curRecaudacion.getString(curRecaudacion.getColumnIndex(columna));
    }

    private String getRecaudacionImporte(String columna){
        return importeStr(getRecaudacion(columna));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_slide_page_importes, container,false);
        curRecaudacion = ScreenSlidePagerRecaudacion.curRecaudacion;
        bindRecaudacionData(rootView);
        return rootView;
    }

    private class CustomOnFocusChange implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View view, boolean b) {
            if (! b){
                if (view instanceof EditText){
                    EditText txt = ((EditText) view);
                    txt.setText(importeStr(txt));
                }
                calcImportes();
            }
        }
    }

    private float getNumber(String text){
        if (text == null){
            return 0;
        }else if (text.length() == 0){
            return 0;
        }else if (text.matches(".*\\\\D+.*")){
            return 0;
        }else {
            //TODO Controlar separador de miles
            return Float.valueOf(text.replace(",","."));
        }
    }
    private float getNumber(EditText txt){
        return getNumber(txt.getText().toString());
    }

    private String importeStr(Float importe){
        DecimalFormat nf = new DecimalFormat();
        nf.applyPattern("#0.00");
        return nf.format(importe);
    }

    private String importeStr(String importe){
        return importeStr(getNumber(importe));
    }

    private String importeStr(EditText txt){
        return importeStr(txt.getText().toString());
    }

    private void calcImportes(){

        Float importeRecaudacion = (getNumber(txtBruto)
                                    - getNumber(txtFallos)
                                    - getNumber(txtRecCargaEmpresa)
                                    - getNumber(txtRecCargaEstablecimiento));
        txtImporteRecaudacion.setText(importeStr(importeRecaudacion));
        Float importeReparto = (importeRecaudacion
                                - getNumber(txtImporteVarios)
                                - getNumber(txtImporteRetencion));

        Float neto = importeReparto - getNumber(txtImporteEstablecimiento);
        txtImporteNeto.setText(importeStr(neto));
    }

    public static float val(String str) {
        StringBuilder validStr = new StringBuilder();
        boolean seenDot = false;   // when this is true, dots are not allowed
        boolean seenDigit = false; // when this is true, signs are not allowed
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '.' && !seenDot) {
                seenDot = true;
                validStr.append(c);
            } else if ((c == '-' || c == '+') && !seenDigit) {
                validStr.append(c);
            } else if (Character.isDigit(c)) {
                seenDigit = true;
                validStr.append(c);
            } else if (Character.isWhitespace(c)) {
                // just skip over whitespace
                continue;
            } else {
                // invalid character
                break;
            }
        }
        return Float.parseFloat(validStr.toString());
    }
}
