package com.prelimtek.android.validation.adapter;

import android.view.View;
import android.widget.TextView;

import com.prelimtek.android.customcomponents.dialog.TelephoneCountryCodeDialog;
import com.prelimtek.android.validation.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelephoneBindingAdapter {

    public static void onClickPhoneEdit(View view, final String phoneNumber) {

        final TextView textView = (TextView)view;
        String prefixCountryCode = null;
        String suffixPhoneNumber=null;

        String patternStr = view.getContext().getString(R.string.phone_with_countrycode_3group_match_pattern);
        Pattern pattern = Pattern.compile(patternStr,Pattern.CASE_INSENSITIVE);

        if(phoneNumber!=null) {
            Matcher m = pattern.matcher(phoneNumber);

            if (m.find()) {
                prefixCountryCode = m.group(1);
                suffixPhoneNumber = m.group(3);
            }
        }

        TelephoneCountryCodeDialog telephoneCountryCodeDialog  = new TelephoneCountryCodeDialog(
                view.getContext(),
                new TelephoneCountryCodeDialog.OnTelephoneCountryCodeSetListener() {
                    @Override
                    public void onTelephoneNumberSet(CharSequence value) {
                        textView.setText(value);
                    }
                },
                prefixCountryCode,
                suffixPhoneNumber
        );

        telephoneCountryCodeDialog.setCancelable(false);
        telephoneCountryCodeDialog.show();
    }

}
