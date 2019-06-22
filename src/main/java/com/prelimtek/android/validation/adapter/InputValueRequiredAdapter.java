package com.prelimtek.android.validation.adapter;

import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.prelimtek.android.validation.R;



public class InputValueRequiredAdapter {

    private InputValueRequiredAdapter(){}

    /**
     * Usage <Field
     * app:validate="True"
     * app:validationRule="ruleString"
     * app:error></>
     * **/
    @BindingAdapter(value = {"required","validationRule","errorMessage","errorLabel","errorMessageAttrChanged",},requireAll = false)
    public static void setValidationRule( final TextView view,final boolean required, final String patternRule, final String errorMessage, final TextView label, final InverseBindingListener listener) {
        System.out.println("setValidationRule => "+patternRule);

        ruleValidate(required,view ,patternRule,errorMessage,label);

        view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ErrorHandler.clearError(view);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        ruleValidate(required,view ,patternRule,errorMessage,label);

                        if(listener!=null)
                            listener.onChange();

                    }
                },1000);
            }
        });

        view.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {

                        if(!hasFocus) {
                            ruleValidate( required, view, patternRule, errorMessage, label);
                            if(listener!=null)
                                listener.onChange();
                        }
                    }
                }
        );

    }


    @InverseBindingAdapter(attribute="valueRequired", event="valueRequiredAttrChanged")
    public static String getRequired(String patternRule){

        //String strVal = view.getText().toString();//getPreferenceCurrency(context);

        return patternRule;
    }

    @InverseBindingAdapter(attribute = "errorMessage", event="valueRequiredAttrChanged")
    public String getErrorMessage(String errorMessage){

        return errorMessage;
    }

    /***
     * 'valueRequired' should be used to trigger errors on create view unlike 'onFocusChangePatternValidation'
     * which should be triggered when focus changes. 'valueRequired' can be used with null-tolerant patterns. */
    @BindingAdapter(value = {"valueRequired","errorMessage","errorLabel","valueRequiredAttrChanged",},requireAll = false)
    public static void setRequired( final TextView view, final String patternRule, final String errorMessage, final TextView label,final InverseBindingListener listener) {
        System.out.println("valueRequired => "+patternRule);
            //final TextView errorLabel = label==null?view:label;
            ruleValidate(true,view ,patternRule,errorMessage,label);

            view.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    ErrorHandler.clearError(view);
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            /*if(view.getText()==null || view.getText().toString().trim().isEmpty()){
                                String errorMessage = ErrorHandler.clarify(view,null);
                                //view.setError(errorMessage);
                                ErrorHandler.setError(errorLabel,errorMessage);
                            }else if(!validatePattern(view.getText().toString(), patternRule)){
                                String errorMsg = ErrorHandler.clarify(errorLabel,errorMessage);
                                ErrorHandler.setError(errorLabel,errorMsg);
                            }else{
                                ErrorHandler.clearError(errorLabel);
                            }*/
                            ruleValidate(true,view ,patternRule,errorMessage,label);

                            if(listener!=null)
                                listener.onChange();
                        }
                    },500);
                }
            });

            view.setOnFocusChangeListener(
                    new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {

                            if(!hasFocus) {
                                ruleValidate(true,view ,patternRule,errorMessage,label);
                                if(listener!=null)
                                    listener.onChange();
                            }
                        }
                    }
            );

    }

    private static void ruleValidate( TextView view,  String patternRule,  String errorMessage,  TextView label) {
        ruleValidate(false,view,patternRule,errorMessage,label);
    }

    private static void ruleValidate( Boolean required,TextView view,  String patternRule,  String errorMessage,  TextView label){
        final TextView errorLabel = label!=null?label:view;//( 0>view.getLabelFor() ? view :(TextView) view.findViewById(view.getLabelFor()));

        if(required && (view.getText()==null || view.getText().toString().trim().isEmpty())){
             errorMessage = ErrorHandler.clarify(view,null);
            //view.setError(errorMessage);
            ErrorHandler.setError(errorLabel,errorMessage);
            ErrorHandler.setError(view,errorMessage);
        }else if(!validatePattern(view.getText().toString(), patternRule)){
            String errorMsg = ErrorHandler.clarify(errorLabel,errorMessage);
            ErrorHandler.setError(errorLabel,errorMsg);
            ErrorHandler.setError(view,errorMessage);
        }else{
            ErrorHandler.clearError(errorLabel);
            ErrorHandler.clearError(view);
        }
    }


    @BindingAdapter("onFocusChangePatternValidationAttrChanged")
    public static void setPatternFocusValidationInverse(final TextView view,final InverseBindingListener textListener){
        view.addTextChangedListener(new TextWatcher() {
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
                        //if(textListener!=null)
                            textListener.onChange();
                    }
                },500);
            }
        });
    }

    /**
     * Unlike 'valueRequired', 'onFocusChangePatternValidation' should be used with null-tolerant patterns only
     * so as not to trigger errors on non-required fields.
     * */
    @BindingAdapter(value={"onFocusChangePatternValidation","errorMessage","errorLabel"},requireAll=false)
    public static void setPatternFocusValidation(final TextView view, final String patternRule, final String errorMessage, final TextView label){
        System.out.println("patternRule = > "+ patternRule);

        if(view.getText()!=null && view.getText().toString().trim().isEmpty()){
            String errorMsg = ErrorHandler.clarify(view,errorMessage);
            //view.setError(errorMessage);
            ErrorHandler.setError(view,errorMsg);
        }


        view.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {

                        TextView textView = view;//(TextView)v;
                        TextView errorLabel = label==null?textView:label;

                        if(!hasFocus) {
                            System.out.println(v.getId()+" in focus .... validating.");
                            if(textView.getText()==null
                                    || !validatePattern(textView.getText().toString().trim(), patternRule)){
                                String errorMsg = ErrorHandler.clarify(textView,errorMessage);
                                ErrorHandler.setError(textView,errorMsg);
                                ErrorHandler.setError(errorLabel,errorMsg);
                            }else{
                                ErrorHandler.clearError(textView);
                                ErrorHandler.clearError(errorLabel);
                            }

                        }



                    }

                }
        );

    }

    //GETTER
    @InverseBindingAdapter(attribute = "onFocusChangePatternValidation", event = "onFocusChangePatternValidationAttrChanged")
    public static String captureRequiredSelectedValue(Spinner pAppCompatSpinner) {
        return (String) pAppCompatSpinner.getSelectedItem();
    }

    @InverseBindingAdapter(attribute = "onFocusChangePatternValidation", event = "onFocusChangePatternValidationAttrChanged")
    public static CharSequence getRequiredChangedTextView(TextView textView) {
        return textView.getText();
    }


    @BindingAdapter(value={"onFocusValueRequired", "errorLabel","errorMessage"},requireAll=false)
    public static void setFocusListener(final View view, final Object value, final TextView label,  final String errorMessage){


        if(value==null || value.toString().trim().isEmpty()){

            String errorMsg = ErrorHandler.clarify(label,errorMessage);
            //view.setError(errorMessage);
            ErrorHandler.setError(label,errorMsg);
        }

        view.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {

                        if(!hasFocus) {
                            ErrorHandler.clearError(label);
                            if(v==null || v.toString().trim().isEmpty()){
                                String errorMsg = ErrorHandler.clarify(label,errorMessage);
                                ErrorHandler.setError(label,errorMsg);
                            }
                        }
                    }
                }
        );

    }



    @BindingAdapter(value={"focusChangeValidation","errorMessage"},requireAll=false)
    public static void setFocusListener(final TextView view, final BaseObservable observable, final String errorMessage){
        System.out.println("Observable = > "+ observable.toString());

        if(view.getText()==null || view.getText().toString().trim().isEmpty()){
             String errorMsg = ErrorHandler.clarify(view,errorMessage);
            //view.setError(errorMessage);
            ErrorHandler.setError(view,errorMsg);
        }

        view.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        TextView textView = (TextView)v;
                        if(!hasFocus) {
                            ErrorHandler.clearError(textView);
                            if(textView.getText()==null || textView.getText().toString().trim().isEmpty()){
                                String errorMsg = ErrorHandler.clarify(textView,errorMessage);
                                ErrorHandler.setError(textView,errorMsg);
                            }
                            observable.notifyChange();
                        }
                    }
                }
        );

    }

    @BindingAdapter(value={"onTouchValidateField", "errorLabel","errorMessage"},requireAll=false)
    public static void setButtonTouchListener(final View view, final Object value, final TextView label,  final String errorMessage){

        if(value==null || value.toString().trim().isEmpty()){

            String errorMsg = ErrorHandler.clarify(label,errorMessage);
            //view.setError(errorMessage);
            ErrorHandler.setError(label,errorMsg);
        }

        view.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        ErrorHandler.clearError(label);

                        if(value==null || value.toString().trim().isEmpty()){
                                String errorMsg = ErrorHandler.clarify(label,errorMessage);
                                ErrorHandler.setError(label,errorMsg);
                            return true;
                        }
                        return false;
                    }

                }
        );

    }

    @BindingAdapter(value={"onTouchValidateField"},requireAll=false)
    public static void setViewTouchListener(View view, final TextView field1){
        setButtonTouchListener(view, field1);
    }

    @BindingAdapter(value={"validateOnTouch"},requireAll=false)
    public static void setButtonTouchListener(View button, final TextView field1){
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(field1.getText()!=null && field1.getText().toString().trim().isEmpty()){
                    String errorMsg = ErrorHandler.clarify(field1,null);
                    ErrorHandler.setError(field1,errorMsg);
                }else{
                    ErrorHandler.clearError(field1);
                    v.performClick();
                    return true;
                }
                return false;
            }
        });
    }




    /*
    //@InverseBindingAdapter(attribute = "valueRequired", event="valueRequiredAttrChanged")
    @InverseBindingAdapter(attribute = "valueRequired")
    public String valueRequiredSetter( EditText view){


        return view.getText().toString();
    }

    //@InverseBindingAdapter(attribute = "errorMessage", event="valueRequiredAttrChanged")
    @InverseBindingAdapter(attribute = "errorMessage")
    public String setter( String errorMessage){


        return errorMessage;
    }
    @BindingAdapter(value={"valueRequiredAttrChanged","errorMessageAttrChanged"},requireAll = false)
    public static void setListener(final EditText view,final TextView errorLabel, final InverseBindingListener listener) {

        if (listener != null) {
            view.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    ErrorHandler.clearError(errorLabel);
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    view.removeTextChangedListener(this);

                    if(view.getText()!=null && view.getText().toString().trim().isEmpty()){
                        String errorMessage = ErrorHandler.clarify(errorLabel,null);
                        view.setError(errorMessage);
                        ErrorHandler.setError(errorLabel,errorMessage);
                    }

                    view.addTextChangedListener(this);

                }
            });

        }
    }
    */


//email pattern = "^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}.[a-z]{2,}$"
    private static boolean validatePattern(String value, String patternStr){

        Pattern pattern = Pattern.compile(patternStr,Pattern.CASE_INSENSITIVE);

        if(value!=null) {
            Matcher m = pattern.matcher(value);
            if (m.find()) {
                return true;
            }
        }

        return false;
    }

   static class ErrorHandler{

        public static void setError(TextView view, String message){
            if(view==null)return;
            view.setTextColor(view.getResources().getColor(R.color.Red_700));
            view.setError(message);

        }
        public static void clearError(TextView view){
            if(view==null)return;
            view.setError(null);
        }

        public static String clarify(TextView view, String errorMessage){
            if(view!=null && (errorMessage==null||errorMessage.isEmpty())){
                errorMessage = view.getContext().getResources().getString(R.string.required_error_message);
            }

            return errorMessage;
        }
    }

}
