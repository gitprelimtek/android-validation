package com.prelimtek.android.basecomponents;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.prelimtek.android.basecomponents.dialog.DialogUtils;

public class TelephonyUtils {

    public static final String TAG = "TelephonyUtilsTAG";

    public static final int PHONE_CALL_REQ_CODE = 11111;
    public static final int TEXT_MESG_REQ_CODE = 11112;
    public static final int WHATSAPP_MESG_REQ_CODE = 11113;
    public static final int EMAIL_MESG_WITHATTCHMENT_REQ_CODE = 11114;
    public static final String EMAIL_MESG_WITHATTCHMENT_CHOOSER = "drive/email/sms with attachment";

    /**Response is sent to activity's onActivityRequest*/
    @Deprecated
    public static void makePhoneCall1(Activity context, String phoneNumber){
        //if (Build.VERSION.SDK_INT > 22) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CALL_PHONE}, PHONE_CALL_REQ_CODE);
            Log.w(TAG,"Requesting Permission CALL_PHONE .");
            return;
        }


        try{
            //intent call
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:+" + phoneNumber));
            context.startActivityForResult(callIntent,PHONE_CALL_REQ_CODE);//a response is sent to onRequestPermissionsResult mthd
        }catch(Exception e){
            Log.e(TAG,"Make phone call failed.",e);
            DialogUtils.startErrorDialog(context,"Make phone call failed.");
        }
        //}
    }
    /**Response is sent to activity's onActivityRequest*/
    @Deprecated
    public static void composeSMSMessage1(Activity context, String phoneNumber){

        //if (Build.VERSION.SDK_INT > 22) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.SEND_SMS}, TEXT_MESG_REQ_CODE);
            Log.w(TAG,"Requesting Permission SEND_SMS .");
            return;
        }

        try{
            Intent callIntent = new Intent(Intent.ACTION_VIEW);
            callIntent.setData(Uri.parse("sms:"+phoneNumber));
            context.startActivityForResult(callIntent,TEXT_MESG_REQ_CODE );//a response is sent to onRequestPermissionsResult mthd
        }catch(Exception e){
            Log.e(TAG,"Compose sms failed.",e);
            DialogUtils.startErrorDialog(context,"Compose SMS failed.");
        }
        //}

    }
    /**Response is sent to activity's onActivityRequest*/
    @Deprecated
    public static void composeWhatsappMessage1(Activity context, String phoneNumber){
        //intent call
        //if (Build.VERSION.SDK_INT > 22) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.SEND_SMS}, WHATSAPP_MESG_REQ_CODE);
            Log.w(TAG,"Requesting Permission SEND_SMS .");
            return;
        }

        try {
            Intent whatsappIntent = new Intent(Intent.ACTION_SENDTO);
            whatsappIntent.setData(Uri.parse("smsto:" + phoneNumber));
            whatsappIntent.setPackage("com.whatsapp");
            //context.startActivity(Intent.createChooser(whatsappIntent,""));
            context.startActivityForResult(whatsappIntent, WHATSAPP_MESG_REQ_CODE);//a response is sent to onRequestPermissionsResult mthd
        }catch(ActivityNotFoundException e){
            Log.e(TAG,"App not found .",e);
            DialogUtils.startErrorDialog(context,"Please install Whatsapp app and try again.");
        }
        // }
    }

    /**Response is sent to activity's onActivityRequest*/
    public static void makePhoneCall(Activity context, String phoneNumber){

        try{
            //intent call
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:+" + phoneNumber));
            context.startActivityForResult(callIntent,PHONE_CALL_REQ_CODE);//a response is sent to onRequestPermissionsResult mthd
        }catch(Exception e){
            Log.e(TAG,"Make phone call failed.",e);
            DialogUtils.startErrorDialog(context,"Make phone call failed.");
        }
        //}
    }

    /**Response is sent to activity's onActivityRequest*/
    public static void composeSMSMessage(Activity context, String phoneNumber){

        try{
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
            smsIntent.setData(Uri.parse("smsto:" + phoneNumber));
            context.startActivityForResult(smsIntent,TEXT_MESG_REQ_CODE );//a response is sent to onRequestPermissionsResult mthd
        }catch(Exception e){
            Log.e(TAG,"Compose sms failed.",e);
            DialogUtils.startErrorDialog(context,"Compose SMS failed.");
        }
        //}

    }

    /**Response is sent to activity's onActivityRequest*/
    public static void composeWhatsappMessage(Activity context, String phoneNumber){

        try {
            Intent whatsappIntent = new Intent(Intent.ACTION_SENDTO);
            whatsappIntent.setData(Uri.parse("smsto:" + phoneNumber));
            whatsappIntent.setPackage("com.whatsapp");
            //context.startActivity(Intent.createChooser(whatsappIntent,""));
            context.startActivityForResult(whatsappIntent, WHATSAPP_MESG_REQ_CODE);//a response is sent to onRequestPermissionsResult mthd
        }catch(ActivityNotFoundException e){
            Log.e(TAG,"App not found .",e);
            DialogUtils.startErrorDialog(context,"Please install Whatsapp app and try again.");
        }

    }

    /**Response is sent to activity's onActivityRequest*/
    public static void composeEmailMessageWithAttachment(Activity context, String[] email, String cc, String subject,String body, Uri attachment){

        try{

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL,email);
            emailIntent.putExtra(Intent.EXTRA_CC,cc);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT,subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT,body);
            emailIntent.putExtra(Intent.EXTRA_STREAM,attachment);
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            context.startActivityForResult(Intent.createChooser(emailIntent,EMAIL_MESG_WITHATTCHMENT_CHOOSER),EMAIL_MESG_WITHATTCHMENT_REQ_CODE );//a response is sent to onRequestPermissionsResult mthd
        }catch(Exception e){
            Log.e(TAG,"Compose email failed.",e);
            DialogUtils.startErrorDialog(context,"Compose email failed.");
        }

    }

    /**Response is sent to activity's onActivityRequest*/
    public static void composeSMSMessageWithAttachment(Activity context, String phoneNumber, String subject,String body, Uri attachment){

        try{
            Intent smsSendIntent = new Intent(Intent.ACTION_SEND);
            smsSendIntent.setType("text/plain");
            if(phoneNumber!=null)
            smsSendIntent.putExtra(Intent.EXTRA_PHONE_NUMBER,phoneNumber);
            if(subject!=null)
            smsSendIntent.putExtra(Intent.EXTRA_SUBJECT,subject);
            if(body!=null)
            smsSendIntent.putExtra(Intent.EXTRA_TEXT,body);
            if(attachment!=null)
            smsSendIntent.putExtra(Intent.EXTRA_STREAM,attachment);
            smsSendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            context.startActivityForResult(Intent.createChooser(smsSendIntent,EMAIL_MESG_WITHATTCHMENT_CHOOSER) ,EMAIL_MESG_WITHATTCHMENT_REQ_CODE);//a response is sent to onRequestPermissionsResult mthd
        }catch(Exception e){
            Log.e(TAG,"Compose email failed.",e);
            DialogUtils.startErrorDialog(context,"Compose email failed.");
        }

    }

}

