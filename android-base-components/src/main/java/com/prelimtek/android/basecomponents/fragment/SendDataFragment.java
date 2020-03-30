package com.prelimtek.android.basecomponents.fragment;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prelimtek.android.basecomponents.BackupActivityInterface;
import com.prelimtek.android.basecomponents.TelephonyUtils;
import com.prelimtek.android.basecomponents.dialog.DialogUtils;
import com.prelimtek.android.customcomponents.R;


public class SendDataFragment extends Fragment {

    public static final String TAG = SendDataFragment.class.getSimpleName();

    private String dataFilePath;

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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        final String phoneNumber = null;

        final Uri dataUrl = Uri.parse(dataFilePath);

        View whatsappMessageButton = view.findViewById(R.id.whatsapp_icon_btn);
        whatsappMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**Response is sent to activity's onActivityRequest*/
                TelephonyUtils.composeWhatsappMessage(getActivity(),phoneNumber);

            }
        });

        /*View textMessageButton = view.findViewById(R.id.text_msg_btn);
        textMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TelephonyUtils.composeSMSMessageWithAttachment(getActivity(),phoneNumber,null,null,dataUrl);

            }
        });*/

        View phoneCallButton = view.findViewById(R.id.send_attachment_chooser_btn);
        phoneCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //String subject = "Data Backup";
                /**Response is sent to activity's onActivityRequest*/
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
            case TelephonyUtils.EMAIL_MESG_WITHATTCHMENT_REQ_CODE:
                if( grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    Snackbar.make(this.getView(),"Permission ACTION_SEND with Attachment granted. Please press message button again.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Log.i(TAG,"Permission ACTION_SEND with Attachment granted. ");
                }else{
                    DialogUtils.startErrorDialog(this.getActivity(),"Permission ACTION_SEND with Attachment denied. contact administrator. "+grantResults[0]);
                    Log.w(TAG,"Permission ACTION_SEND with Attachment denied. "+grantResults[0]);
                }
                break;
        }

    }

}
