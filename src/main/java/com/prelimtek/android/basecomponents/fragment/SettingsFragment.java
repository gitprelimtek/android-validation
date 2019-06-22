package com.prelimtek.android.basecomponents.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import java.util.Map;
import java.util.Set;

import com.prelimtek.android.customcomponents.R;

public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    private SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        System.out.println(" SettingsFragment called : ");

        super.onCreate(savedInstanceState);

        // Load the configuredPreferences from an XML resource
        this.addPreferencesFromResource(R.xml.preferences);

        sp = this.getPreferenceScreen().getSharedPreferences();


    }


    @Override
    public void onResume(){
        super.onResume();
        sp.registerOnSharedPreferenceChangeListener(this);
        // TODO update configuredPreferences by scrolling through each and calling onSharedPreferenceChanged
        Map<String,?> prefMap = sp.getAll();
        if(prefMap!=null && !prefMap.isEmpty()){
            Set<String> keys = prefMap.keySet();
            for(String s : keys){
                updatePreference(s);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sp.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePreference(key);
    }

    private void updatePreference(String key){

        Preference pref = findPreference(key);

        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            listPref.setSummary( listPref.getEntry() );

            return;
        }

        if (pref instanceof EditTextPreference){
            EditTextPreference editPref =  (EditTextPreference) pref;
            editPref.setSummary(editPref.getText());

            return;
        }

        if (pref instanceof CheckBoxPreference){
            CheckBoxPreference checkPref =  (CheckBoxPreference) pref;
            checkPref.setSummary(checkPref.isChecked() ? "True" : "False");

            return;
        }

        if (pref instanceof SwitchPreference){
            SwitchPreference switchPref =  (SwitchPreference) pref;
            switchPref.setSummary(switchPref.isChecked() ? "True" : "False");

            return;
        }

        //case KEY_PREF_LANGUAGE:
        //LocaleHelper.setLocale(getContext(), ((ListPreference) pref).getEntry());
        //getActivity().recreate(); // necessary here because this Activity is currently running and thus a recreate() in onResume() would be too late
        //break;

    }


}
