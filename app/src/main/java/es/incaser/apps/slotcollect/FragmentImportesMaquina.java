package es.incaser.apps.slotcollect;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sergio on 28/09/14.
 */
public class FragmentImportesMaquina extends Fragment {
    private final static String KEY_REG_TEXT = "texto";

    public static FragmentImportesMaquina newInstance(String text) {
        FragmentImportesMaquina frag = new FragmentImportesMaquina();

        Bundle args = frag.getArguments();
        if (args == null)
            args = new Bundle();

        args.putString(KEY_REG_TEXT, text);

        frag.setArguments(args);
        String a ="0";
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        // Inflamos la Vista que se debe mostrar en pantalla.
        View rootView = inflater.inflate(R.layout.fragment_slide_page_importes, container,
                false);
        // Creamos instancia del TextView.
        //TextView tvEnt10 =  (TextView) rootView.findViewById(R.id.tv);
        // Recogemos el texto que guardamos al crear el Fragment.
        //String text = getArguments().getString(KEY_REG_TEXT);
        // Mostramos el texto en el TextView.
        //tvText.setText(text);

        // Devolvemos la vista para que se muestre en pantalla.
        return rootView;
    }
}
