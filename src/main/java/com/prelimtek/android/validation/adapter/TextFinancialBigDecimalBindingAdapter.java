package com.prelimtek.android.validation.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import android.icu.text.DecimalFormat;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.regex.Pattern;

import com.prelimtek.android.basecomponents.Configuration;
import com.prelimtek.android.basecomponents.ResourcesUtils;
import com.prelimtek.android.validation.R;

/**
 *
 * TODO deal with data formatting issue when adding decimal!
 *
 * */
public class TextFinancialBigDecimalBindingAdapter {
    //TODO remove?
    public static final String regex = "(([1-9]+\\.?\\d*)|([0]\\.\\d*)|[0])";
    public static final Pattern pattern = Pattern.compile(regex);
    private static NumberFormat moneyFormat = NumberFormat.getCurrencyInstance();

    @Deprecated
    public static NumberFormat getMoneyFormat(Context context){

        String currencyCode = getPreferenceCurrency(context);

        NumberFormat moneyFormat = NumberFormat.getCurrencyInstance();

        moneyFormat.setCurrency(Currency.getInstance(currencyCode));

        return moneyFormat;
    }

    public static String getPreferenceCurrency(Context context){
        /*SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String currencyCode = prefs.getString("base_currency","USD");
        return currencyCode;*/
        return Configuration.configuredPreferences(context).currencyCode;
    }

    public static void setPreferenceCurrency(Context context,String currencycode){
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        Configuration.configuredPreferences(context).currencyCode = currencycode;//prefs.edit().putString("base_currency", currencycode).apply();
    }


    @BindingAdapter(value = {"currencyValue","currencyCode","currencyHighlight"} , requireAll = false)
    public static String setCurrencyValue(TextView view,final BigDecimal amount,final String currencyCode , boolean currencyHighlight ){
        String ret = null;
        if(amount !=null ) {

            moneyFormat = NumberFormat.getCurrencyInstance();
            Currency currency = null;

            //if(currencyCode==null
            //        || !Currency.getAvailableCurrencies().contains(Currency.getInstance(currencyCode)
            //)){
            if(currencyCode==null){
                view.setError("Invalid currency code '"+currencyCode+"'");
                currency = Currency.getInstance(getPreferenceCurrency(view.getContext()));
            }else{
                System.out.println("currencyCode = "+currencyCode);
                currency=Currency.getInstance(currencyCode);
            }

            moneyFormat.setCurrency(currency);

            ret = moneyFormat.format(amount);
            if (amount != null && view != null) {
                view.setText(ret);

                if(currencyHighlight){
                    if (amount.longValue() <= 0) {
                        view.setTextColor(ResourcesUtils.getColor(view, R.color.Teal_700));
                    } else {
                        view.setTextColor(ResourcesUtils.getColor(view, R.color.Red_700));
                    }
                }
            }
        }

        return ret;
    }

    //@Deprecated
    @InverseBindingAdapter(attribute="currencyValue")
    public static BigDecimal getCurrencyValue(TextView view){
        BigDecimal ret = null;
        String strVal =  view.getText()==null?null:view.getText().toString();
        if(strVal != null) {
            try {
                ret = new BigDecimal(moneyFormat.parse(strVal).byteValue());
            } catch (Throwable e) {
                e.printStackTrace();
                view.setError("Invalid data format");
            }
        }

        return ret;
    }

    //@Deprecated
    @InverseBindingAdapter(attribute="currencyCode")
    public static String getCurrencyCode1(TextView view){
        BigDecimal ret = null;
        String strVal =  null;
        if(view.getText()!=null) {
            strVal = view.getText().toString();

            //if(Currency.getAvailableCurrencies().contains(Currency.getInstance(strVal))){
            //    view.setError("Invalid Currency code '"+strVal+"'");
            //}

        }

        return strVal;
    }
    @InverseBindingAdapter(attribute = "currencyHighlight")
    public static boolean getCurrencyHighlight(TextView view){
        return true;
    }


    public static String setBindCurrencyValue(TextView view,
                                              String currencyCode, final BigDecimal amount ){
        String ret = null;
        if(amount !=null && currencyCode!=null ) {
            moneyFormat.setCurrency(Currency.getInstance(currencyCode));
            ret = moneyFormat.format(amount);
            if (view != null) {
                view.setText(ret);
            }
        }

        return ret;
    }
    @Deprecated
    @BindingAdapter({"bindCurrency"})
    public static String setBindCurrencyValue(TextView view,
                                              final BigDecimal amount ){
        String ret = null;
        if(amount !=null ) {
            ret = getMoneyFormat(view.getContext()).format(amount);
            if (amount != null && view != null) {
                view.setText(ret);
            }
        }

        return ret;
    }

    @InverseBindingAdapter(attribute="bindCurrency")
    public static BigDecimal getBindCurrencyValue(TextView view){
        BigDecimal ret = null;

        if(view.getText()!=null) {
            String strVal =  view.getText().toString();
            try {
                //ret = new BigDecimal(getMoneyFormat(view.getContext()).parse(strVal).byteValue());
                ret = new BigDecimal(moneyFormat.parse(strVal).byteValue());
            } catch (Throwable e) {
                e.printStackTrace();
                view.setError("Invalid data format");
            }
        }

        return ret;
    }

    //setter
    @BindingAdapter({"bindFinancial"})
    public static void setFinancialValue(TextView view, BigDecimal amount ){

        amount = amount==null?new BigDecimal(0.00):amount;
        //if(amount !=null) {

        String strVal = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            df.setMinimumFractionDigits(2);
            df.setGroupingUsed(false);
            strVal=df.format(amount);
        }else{
            strVal = NumberFormat.getNumberInstance(java.util.Locale.US).format(amount);
            //strVal = amount.toPlainString();
        }

        //String strVal = amount.toPlainString();
        if (view.getText() != null && !view.getText().toString().equals(strVal)) {
            //view.setText(moneyFormat.format(amount));
            view.setText(strVal);
        }
        //}
    }

    //getter
    @InverseBindingAdapter(attribute="bindFinancial")
    public static BigDecimal getFinancialValue(TextView view){

        BigDecimal ret = new BigDecimal(0.00);
        String strVal =  view.getText()==null?null:view.getText().toString();
        if(strVal != null) {
            //TODO verify matching patter to suite financial
            //TODO find a way to keep old valid value in pojo if text is invalid
            //Matcher matcher = pattern.matcher(strVal);
            //if (matcher.matches()) {
            try {
                //ret = new BigDecimal(moneyFormat.parse(strVal).byteValue());//new BigDecimal(matcher.group(0));
                ret = new BigDecimal(strVal);
            } catch (Throwable e) {
                e.printStackTrace();
                view.setError("Invalid data format");
            }
        }

        return ret;
    }

    //TODO find a way to pass this delay value as an attribute or value
    @BindingAdapter({"bindFinancialAttrChanged"})
    public static void setListener(final TextView view, final InverseBindingListener listener) {

        if (listener != null) {
            view.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {

                    TextWatcher watcher = this;
                    view.removeTextChangedListener(watcher);
                    if (!editable.toString().isEmpty()){
                        int len = editable.length();
                        String s = editable.toString();
                        int pos = s.indexOf(".");
                        s = s.replace(".","");
                        len = s.length();
                        s=len>2?s.substring(0,len-2)+"."+s.substring(len-2,len):"."+s;
                        editable.clear();
                        editable.append(s);
                    }
                    view.addTextChangedListener(watcher);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            listener.onChange();
                        }
                    },1000);

                }
            });

        }

    }

    @BindingAdapter({"currencyValueAttrChanged"})
    public static void setCurrencyValueListener(final TextView view, final InverseBindingListener listener) {
        setListener(view, listener);
    }


    @InverseBindingAdapter(attribute="bindCurrencyCode")
    public static String getCurrencyCode(TextView view){

        Context context = view.getContext();
        String strVal = getPreferenceCurrency(context);
        //if(view!=null)view.setText(strVal);

        return strVal;
    }

    @BindingAdapter("bindCurrencyCode")
    public static void setCurrencyCode(TextView view, String s){
        Context context = view.getContext();
        String strVal = getPreferenceCurrency(context);
        if(view!=null)view.setText(strVal);
    }

    @BindingAdapter(value = "bindCurrencyCodeAttrChanged")
    public static void setCurrencyCodeListener(final TextView view, final InverseBindingListener listener) {
        if (listener != null) {
            view.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listener.onChange();
                        }
                    },500);
                }
            });
        }
    }



    @BindingAdapter(value = {"currencyCodeSpinnerDescr","currencyCodeSpinnerDescrAttrChanged"}, requireAll = false)
    public static void currencyCodeToDesc(Spinner spinner, String code, final InverseBindingListener newTextAttrChanged) {

        String[] val = spinner.getResources().getStringArray(R.array.currency_names);
        String[] arr = spinner.getResources().getStringArray(R.array.currency_codes);
        for(int i = 0 ; i < arr.length; i++){
            if(arr[i].equalsIgnoreCase(code)) {
                setPreferenceCurrency(spinner.getContext(), code);
                spinner.setSelection(i);
                break;
            }
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(newTextAttrChanged!=null)
                    newTextAttrChanged.onChange();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    @InverseBindingAdapter(attribute = "currencyCodeSpinnerDescr", event = "currencyCodeSpinnerDescrAttrChanged")
    public static String currencyDescToCode(Spinner spinner) {
        String ret = null;

        int pos = spinner.getSelectedItemPosition();
        if ( pos>=0 ) {
            String[] arr = spinner.getResources().getStringArray(R.array.currency_codes);
            ret = arr[pos];
            if(ret!=null && !ret.isEmpty())
                setPreferenceCurrency(spinner.getContext(), ret);

        };

        return ret;
    }


}
