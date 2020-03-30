package com.ptek.android.auth.service;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Broadcast receiver to monitor device sms and update an input text field.
 * */
public class TwoFactorAuthTokenSMSReceiver extends BroadcastReceiver {

    public TwoFactorAuthTokenSMSReceiver(){super();
        Log.i(TAG,"Instantiate TwoFactorAuthTokenSMSReceiver tokenField instantiated ? "+(tokenField==null?"false":"true"));

    }

    public TwoFactorAuthTokenSMSReceiver(EditText tokenField){super();
        Log.i(TAG,"Instantiate TwoFactorAuthTokenSMSReceiver tokenField instantiated ? "+(tokenField==null?"false":"true"));
        this.tokenField = tokenField;
    }

    private static final String TAG = TwoFactorAuthTokenSMSReceiver.class.getSimpleName();
    private static final String pdu_type = "pdus";
    private EditText tokenField;
    @SuppressLint("NewApi")
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG,"Message received....begin processing. tokenField instantiated ? "+(tokenField==null?"false":"true"));
        Log.i(TAG,intent.getAction());
        Toast.makeText(context, "Something was received...begin processing", Toast.LENGTH_LONG).show();

        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs;
        String mesgStr;
        String format = bundle.getString("format");
        Object[] pdus = (Object[])bundle.get(pdu_type);

        if(pdus!=null){

            boolean isMversion=(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M);
            msgs = new SmsMessage[pdus.length];

            for(int i = 0 ; i < msgs.length; i ++) {
                if (isMversion) {
                    msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i],format);
                } else {
                    msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                }

                if(tokenField!=null)
                    tokenField.setText(msgs[i].getOriginatingAddress()+" : "+msgs[i].getMessageBody());
                //expects a message from ptek structure according to
                //match type and break loop;
                Toast.makeText(context, msgs[i].getOriginatingAddress()+" : "+msgs[i].getMessageBody(), Toast.LENGTH_LONG).show();
            }
        }

    }




}
