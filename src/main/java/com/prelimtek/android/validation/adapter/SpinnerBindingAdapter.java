package com.prelimtek.android.validation.adapter;


import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;

import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Spinner;
import android.widget.SpinnerAdapter;




public final class SpinnerBindingAdapter {

    private SpinnerBindingAdapter() {}

    //SETTER
    @SuppressWarnings("unchecked")
    @BindingAdapter(value = {"selectedSpinnerValue", "selectedSpinnerValueAttrChanged"}, requireAll = false)
    public static void bindSpinnerData(Spinner pAppCompatSpinner, String newSelectedValue, final InverseBindingListener newTextAttrChanged) {

        pAppCompatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(newTextAttrChanged!=null)
                    newTextAttrChanged.onChange();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if (newSelectedValue != null && pAppCompatSpinner.getAdapter() instanceof ArrayAdapter ) {
            int pos = ((ArrayAdapter<String>) pAppCompatSpinner.getAdapter()).getPosition(newSelectedValue);
            pAppCompatSpinner.setSelection(pos, true);
        }
    }

    //GETTER
    @InverseBindingAdapter(attribute = "selectedSpinnerValue", event = "selectedSpinnerValueAttrChanged")
    public static String captureSelectedValue(Spinner pAppCompatSpinner) {
        return (String) pAppCompatSpinner.getSelectedItem();
    }

}

