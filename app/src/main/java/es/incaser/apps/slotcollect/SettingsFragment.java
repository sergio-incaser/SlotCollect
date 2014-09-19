package es.incaser.apps.slotcollect;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by sergio on 19/09/14.
 */

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}