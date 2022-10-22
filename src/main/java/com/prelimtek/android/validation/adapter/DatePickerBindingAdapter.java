package com.prelimtek.android.validation.adapter;

import android.app.DatePickerDialog;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.prelimtek.android.basecomponents.Configuration;

public class DatePickerBindingAdapter {

    private final static String TAG = DatePickerBindingAdapter.class.getSimpleName();
    //public static final String DATE_PATTERN = "yyyy/MM/dd";
    //public final static SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);

    /**
     * bindDate setter for Date Object
     * **/
    @BindingAdapter("bindDate")
    public static void setDateValue(TextView view,
                                            final Date newDate ){

            DateFormat dateFormat = Configuration.configuredPreferences(view.getContext()).dateFormat;
            if(newDate!=null) {
                String newDateStr = dateFormat.format(newDate);
                view.setText(newDateStr);
            }

    }

    /**
     * bindDate getter for Date Object
     * **/
    @InverseBindingAdapter(attribute="bindDate")
    public static Date getDateTextValue(TextView view){
        DateFormat dateFormat = Configuration.configuredPreferences(view.getContext()).dateFormat;
        Date newDate = null;
        if(view.getText()!=null) {
            String newDateStr = view.getText().toString();
            try {
                newDate = dateFormat.parse(newDateStr);
            } catch (ParseException e) {
                view.setError("Invalid date format. Expected "+((SimpleDateFormat) dateFormat).toPattern());
            }

        }
        return newDate;
    }

    /**
     * bindDate setter for Long Object
     * **/
    @BindingAdapter("bindDate")
    public static void setDateValue(TextView view,
                                    final Long newDate ){
        DateFormat dateFormat = Configuration.configuredPreferences(view.getContext()).dateFormat;
        if(newDate!=null) {
            String newDateStr = dateFormat.format(new Date(newDate));
            view.setText(newDateStr);
        }

    }

    /**
     * bindDate getter for Long Object
     * **/
    @InverseBindingAdapter(attribute="bindDate")
    public static Long getLongDateTextValue(TextView view){
        DateFormat dateFormat = Configuration.configuredPreferences(view.getContext()).dateFormat;
        Date newDate = null;
        if(view.getText()!=null) {
            String newDateStr = view.getText().toString();
            try {
                newDate = dateFormat.parse(newDateStr);
            } catch (ParseException e) {
                view.setError("Invalid date format. Expected "+((SimpleDateFormat) dateFormat).toPattern());
            }

        }
        return newDate.getTime();
    }


    @BindingAdapter(value = "bindDateAttrChanged")
    public static void setChangeListener(TextView view, final InverseBindingListener listener) {
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
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listener.onChange();
                        }
                    }, 500);
                }
            });
        }
    }

    public static void onClickDate( View view, final Date currentDate) {
        Log.d(TAG,"onClickDate");

        final TextView textView = (TextView)view;
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTime(currentDate==null?new Date(): currentDate);

        DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(year,month,day);

                DatePickerBindingAdapter.setDateValue(textView, calendar.getTime());

            }
        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();

    }

    public static void onClickDate( View view, final Long currentDate) {
        Log.d(TAG,"onClickDate");
        final TextView textView = (TextView)view;
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTime(currentDate==null?new Date(): new Date(currentDate));

        DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(year,month,day);

                DatePickerBindingAdapter.setDateValue(textView, calendar.getTime());

            }
        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();

    }

}
