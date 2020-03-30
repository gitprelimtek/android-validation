package com.prelimtek.android.validation.adapter;


import androidx.databinding.ObservableField;
import android.view.View;
import android.widget.EditText;

public class FormValidationAdapter {

    public static EditText.OnFocusChangeListener handleTextFieldFocusListener(EditText textfield , final ObservableField field, final int errorMessageId) {
        //if(textfield.hasFocus()) {
            return new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus)
                        field.notifyPropertyChanged(errorMessageId);
                }
            };
       // }

    }

}
