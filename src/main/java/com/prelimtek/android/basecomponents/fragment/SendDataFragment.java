package com.prelimtek.android.basecomponents.fragment;


import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prelimtek.android.basecomponents.BackupActivityInterface;
import com.prelimtek.android.basecomponents.TelephonyUtils;
import com.prelimtek.android.basecomponents.dialog.DialogUtils;
import com.prelimtek.android.customcomponents.R;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class SendDataFragment extends Fragment {

    public static final String TAG = SendDataFragment.class.getSimpleName();

    String dataFilePath;
    public interface SendDataFragmentInterface{
        void onDataSentSuccess();
        void onDataSentError();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dataFilePath= getArguments().getString(BackupActivityInterface.FILE_PATH_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.choose_send_method_layout, container, false);
    }

    /*@Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        View button =  view.findViewById(R.id.whatsapp_icon_btn );
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //do something before completing

                completeProcess();

            }
        });

    }*/

    public void completeProcess(){
        System.out.println("Completing process......");
        //getActivity().onNavigateUpFromChild(getActivity());
        ((SendDataFragmentInterface)getActivity()).onDataSentSuccess();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final String phoneNumber = null;

        final Uri dataUrl = Uri.parse(dataFilePath);

        View whatsappMessageButton = view.findViewById(R.id.whatsapp_icon_btn);
        whatsappMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TelephonyUtils.composeWhatsappMessage(getActivity(),phoneNumber);

            }
        });

        View textMessageButton = view.findViewById(R.id.text_msg_btn);
        textMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TelephonyUtils.composeSMSMessageWithAttachment(getActivity(),phoneNumber,null,null,dataUrl);

            }
        });

        View phoneCallButton = view.findViewById(R.id.make_phonecall_btn);
        phoneCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                //TelephonyUtils.makePhoneCall(getActivity(),phoneNumber);
                TelephonyUtils.composeEmailMessageWithAttachment(getActivity(),null,null,null,null,dataUrl);

            }
        });



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case TelephonyUtils.PHONE_CALL_REQ_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Snackbar.make(this.getView(),"Permission CALL_PHONE granted. Please press call button again.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Log.i(TAG,"Permission CALL_PHONE granted. ");

                }else{
                    DialogUtils.startErrorDialog(this.getActivity(),"Permission CALL_PHONE denied. contact administrator. ");
                    Log.w(TAG,"Permission CALL_PHONE denied. ");

                }
                break;
            case TelephonyUtils.TEXT_MESG_REQ_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Snackbar.make(this.getView(),"Permission SEND_SMS granted. Please press message button again.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Log.i(TAG,"Permission SEND_SMS granted. ");
                }else{
                    DialogUtils.startErrorDialog(this.getActivity(),"Permission SEND_SMS denied. contact administrator. ");
                    Log.w(TAG,"Permission SEND_SMS denied. ");

                }
                break;
            case TelephonyUtils.WHATSAPP_MESG_REQ_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Snackbar.make(this.getView(),"Permission SEND_SMS granted. Please press message button again.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Log.i(TAG,"Permission SEND_SMS granted. ");
                }else{
                    DialogUtils.startErrorDialog(this.getActivity(),"Permission SEND_SMS denied. contact administrator. "+grantResults[0]);
                    Log.w(TAG,"Permission SEND_SMS denied. "+grantResults[0]);

                }
                break;

        }

    }

}
