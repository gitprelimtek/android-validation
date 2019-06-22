package io.mtini.android.tenantmanager;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;

import com.prelimtek.android.basecomponents.fragment.SettingsFragment;

public class UserSettingsActivity extends PreferenceActivity  {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        System.out.println(" UserSettingsActivity called : ");

        super.onCreate(savedInstanceState);

        this.getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

    }



}
