package es.incaser.apps.slotcollect;

import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by sergio on 2/10/14.
 */
public class tools {
    public static float getNumber(String text){
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
    public static float getNumber(EditText txt){
        return getNumber(txt.getText().toString());
    }

    public static String importeStr(Float importe){
        DecimalFormat nf = new DecimalFormat();
        nf.applyPattern("#0.00");
        return nf.format(importe);
    }

    public static String importeStr(String importe){
        return importeStr(getNumber(importe));
    }

    public static String getToday(){
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
    public static String getActualHour(){
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        return sdf.format(date);
    }

}
