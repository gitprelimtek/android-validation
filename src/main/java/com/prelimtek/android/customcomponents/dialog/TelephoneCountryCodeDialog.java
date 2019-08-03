package com.prelimtek.android.customcomponents.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.prelimtek.android.basecomponents.Configuration;
import com.prelimtek.android.customcomponents.R;

public class TelephoneCountryCodeDialog extends AlertDialog {

    public TelephoneCountryCodeDialog(Context context, final OnTelephoneCountryCodeSetListener onTelephoneCountryCodeSetListener, String prefixCountryCode, String suffixPhoneNumber) {

        super(context);

        final Context themeContext = getContext();
        final LayoutInflater inflater = LayoutInflater.from(themeContext);
        final View view = inflater.inflate(R.layout.custom_telephone_countrycode_dialog, null);

        setView(view);

        final Spinner spinner = (Spinner)view.findViewById(R.id.prefix_country_code_spinner);

        //
        prefixCountryCode = prefixCountryCode==null?getCountryCodePreference():prefixCountryCode;

        if(prefixCountryCode!=null){
            //set value
            int position = findSpinnerPosition( spinner, prefixCountryCode );
            spinner.setSelection(position);
        }

        final EditText sufTelEditText = (EditText)view.findViewById(R.id.suffix_telephone_textView);
        if(suffixPhoneNumber!=null){
            sufTelEditText.setText(suffixPhoneNumber);
        }

        final TextView phoneNumberTextView = (TextView)view.findViewById(R.id.selectedTelephoneTextView);

        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //spinner.drop
                componentValuesChanged(spinner, sufTelEditText, phoneNumberTextView);
            }
        };

        spinner.setOnFocusChangeListener(onFocusChangeListener);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                componentValuesChanged(spinner, sufTelEditText, phoneNumberTextView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sufTelEditText.setOnFocusChangeListener(onFocusChangeListener);

        sufTelEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        componentValuesChanged(spinner, sufTelEditText, phoneNumberTextView);
                    }
                },500);
            }
        });


        DialogInterface.OnClickListener clickListener =
        new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which == BUTTON_POSITIVE){
                    //set to calling view
                    if(onTelephoneCountryCodeSetListener!=null){
                        CharSequence setPhoneValue = phoneNumberTextView.getText();
                        onTelephoneCountryCodeSetListener.onTelephoneNumberSet(setPhoneValue);

                    }
                }

                dialog.dismiss();

            }
        };
        setButton(BUTTON_POSITIVE, themeContext.getString(R.string.ok), clickListener);
        setButton(BUTTON_NEGATIVE, themeContext.getString(R.string.cancel), clickListener);
    }

    private int findSpinnerPosition(Spinner spinner, String prefixCountryCode) {
        SpinnerAdapter adapter = spinner.getAdapter();
        int count = adapter.getCount();
        for(int i = 0 ; i < count ; i++){
            if(adapter.getItem(i).toString().contains(prefixCountryCode)){
                return i;
            }
        }

        return 0;
    }

    private void componentValuesChanged(Spinner spinner, EditText editText, TextView textView){

        String spinnerVal = spinner.getSelectedItem().toString();
        addCountryCodePreference(spinnerVal);
        //add this val to countrycode preference
        String[] countryCodeArr = spinnerVal.split(",");

        String countryCode = countryCodeArr[0];
        String countryInitials = countryCodeArr[1];//TODO use this to retrieve flag
        String phoneText = editText.getText().toString();

        //textView.setText(String.join(" ",countryCode,phoneText));
        textView.setText(countryCode+" "+phoneText);
    }

    public void addCountryCodePreference(String val){
        Configuration.preferences(getContext()).edit().putString(COUNTRY_CODE_PREFERENCE_KEY,val).commit();
    }

    public String getCountryCodePreference(){
        return Configuration.preferences(getContext()).getString(COUNTRY_CODE_PREFERENCE_KEY,null);
    }

    public static final String COUNTRY_CODE_PREFERENCE_KEY = "CountryCodePreferenceKey";


    public interface OnTelephoneCountryCodeSetListener {
        public void onTelephoneNumberSet(CharSequence value);
    }

}