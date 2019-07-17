package io.mtini.android.view;

import android.content.res.Resources;
import android.databinding.InverseMethod;
import android.util.Log;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.prelimtek.android.basecomponents.Configuration;
import com.prelimtek.android.basecomponents.ResourcesUtils;

import io.mtini.android.tenantmanager.R;
import io.mtini.model.EstateModel;
import io.mtini.model.TenantModel;

public class TextViewValueConverter {

    public static String TAG = "TextViewValueConverter";

    @InverseMethod("statusToStr")
    public static TenantModel.STATUS toStatus(String status){

        TenantModel.STATUS ret = TenantModel.STATUS.valueOf(status);

        return ret;
    }

    public static String statusToStr(TenantModel.STATUS status){

        String ret = null;

        if(status==null)
            return null;
        ret = status.name();

        return ret;
    }


    @InverseMethod("scheduleToStr")
    public static TenantModel.SCHEDULE toSchedule(String schedule){

        TenantModel.SCHEDULE ret = TenantModel.SCHEDULE.valueOf(schedule);

        return ret;
    }

    public static String scheduleToStr(TenantModel.SCHEDULE schedule){

        String ret = null;

        if(schedule==null)
            return null;
        ret = schedule.name();

        return ret;
    }

    @InverseMethod("estateTypeToStr")
    public static EstateModel.TYPE toEstateType(String typeStr){
        return EstateModel.TYPE.valueOf(typeStr);
    }

    public static String estateTypeToStr(EstateModel.TYPE type){
        String ret = null;

        if(type==null)return null;

        ret = type.name();

        return ret;
    }


    @InverseMethod("highlightBalance")
    public static BigDecimal highlightBalance(TextView view,
                                          final BigDecimal amount ){
        if(amount.longValue()<=0){
            view.setTextColor(ResourcesUtils.getColor(view,R.color.Teal_700));
        }else{
            view.setTextColor(ResourcesUtils.getColor(view,R.color.Red_700));
        }
        return amount;
    }

    @InverseMethod("toDate")
    @Deprecated
    public static Date stringToDate(TextView  view, String date){

        Date ret = null;

        DateFormat dateFormat = Configuration.configuredPreferences(view.getContext()).dateFormat;

        try {
            if( view.getText()!=null && date!=null && !view.getText().equals(date)) {
                ret = dateFormat.parse(date);
            }
        }catch(ParseException e){

            Resources resources = view.getResources();
            String errStr = "Bad data format; expected "+((SimpleDateFormat) dateFormat).toPattern();
            view.setError(errStr);
            Log.e(TAG,e.getLocalizedMessage());
        }


        return ret;
    }

    @Deprecated
    public static String toDate(TextView  view,Date date){
        DateFormat dateFormat = Configuration.configuredPreferences(view.getContext()).dateFormat;

        String ret = null;

        if(date==null)return null;
        ret = dateFormat.format(date);

        return ret;
    }

    @Deprecated
    public static String toDate(TextView  view, Long date){
        DateFormat dateFormat = Configuration.configuredPreferences(view.getContext()).dateFormat;

        String ret = null;

        if(date==null)return null;
        ret = dateFormat.format(new Date(date));

        return ret;
    }
    //TODO get this pattern from Preferences via Configuration
    //public final static String DATE_PATTERN = "yyyy/MM/dd";
    //public final static SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);


}
