package pt.ipc.estgoh.ezshop.ui.main.view;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import pt.ipc.estgoh.ezshop.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_activity, rootKey);
    }
}
