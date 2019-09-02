package com.prelimtek.android.validation.adapter;

import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Spinner;
import android.widget.TextView;

public final class SpinnerBindingAdapter {

    private SpinnerBindingAdapter() {}

    /**
     * This is a setter for 'selectedSpinnerValue' bindingadapter .
     * */
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

    /**
     * This is a getter for 'selectedSpinnerValue' bindingadapter .
     * */
    @InverseBindingAdapter(attribute = "selectedSpinnerValue", event = "selectedSpinnerValueAttrChanged")
    public static String captureSelectedValue(Spinner pAppCompatSpinner) {
        return (String) pAppCompatSpinner.getSelectedItem();
    }


    /**
     * Uses OnItemSelectedistener to detect dropdown changes
     * */
    @BindingAdapter(value={"onFocusValueRequired", "errorLabel","errorMessage"},requireAll=false)
    public static void setFocusListener(final Spinner view, final Object value, final TextView label, final String errorMessage){

        view.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                InputValueRequiredAdapter.ErrorHandler.clearError(label);
                if(view==null || view.toString().trim().isEmpty()){
                    String errorMsg = InputValueRequiredAdapter.ErrorHandler.clarify(label,errorMessage);
                    InputValueRequiredAdapter.ErrorHandler.setError(label,errorMsg);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if(value==null || value.toString().trim().isEmpty()){
                    String errorMsg = InputValueRequiredAdapter.ErrorHandler.clarify(label,errorMessage);
                    InputValueRequiredAdapter.ErrorHandler.setError(label,errorMsg);
                }
            }
        });


    }
}

