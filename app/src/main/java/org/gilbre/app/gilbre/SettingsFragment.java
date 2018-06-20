package org.gilbre.app.gilbre;

import android.os.Bundle;
//import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * Created by root on 12/15/17.
 */

public class SettingsFragment extends PreferenceFragmentCompat {

    public void onCreatePreferences(Bundle bundle, String s) {
        // Load the Preferences from the XML file
        addPreferencesFromResource(R.xml.preferences);
    }

}
