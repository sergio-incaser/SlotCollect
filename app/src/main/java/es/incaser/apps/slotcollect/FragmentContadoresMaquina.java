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
public class FragmentContadoresMaquina extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        // Inflamos la Vista que se debe mostrar en pantalla.
        View rootView = inflater.inflate(R.layout.fragment_slide_page_contadores, container,
                false);
        // Creamos instancia del TextView.
        //TextView tvText =  (TextView)rootView.findViewById(R.id.tvText);
        // Recogemos el texto que guardamos al crear el Fragment.
        //String text = getArguments().getString(KEY_REG_TEXT);
        // Mostramos el texto en el TextView.
        //tvText.setText(text);

        // Devolvemos la vista para que se muestre en pantalla.
        return rootView;
    }
}
