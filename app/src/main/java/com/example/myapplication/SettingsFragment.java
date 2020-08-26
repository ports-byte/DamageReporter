package com.example.myapplication;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        try {
            setPreferencesFromResource(R.xml.preferences, rootKey);
            /*SharedPreferences preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
            final SwitchPreferenceCompat switchPreference1 = (SwitchPreferenceCompat) findPreference("encrypt_pdf");
            if (preferences.getBoolean("encrypt_pdf", false)) {
                switchPreference1.setChecked(false);
                Constants.encryptPDF = false;
            } else {
                Constants.encryptPDF = true;
                switchPreference1.setChecked(true);
            }*/
        } catch (Exception g) {
            Toast.makeText(null, "error" + g, Toast.LENGTH_LONG).show();
        }
    }
}
