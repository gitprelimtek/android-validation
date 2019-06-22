package io.mtini.android.tenantmanager.dialog.tasks;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;

import com.prelimtek.android.basecomponents.dialog.DialogUtils;

/**
 * NOTE: Still under testing.
 * */
public class DialogTasks extends AsyncTask<DialogTasks.DialogType,Void,Dialog> {

    public DialogTasks(Context _context, CharSequence _title, CharSequence _message){
        context = _context;
        title = _title;
        message = _message;
    }

    Context context;
    CharSequence title,message;
    Dialog dialog = null;

    @Override
    protected Dialog doInBackground(DialogType... dialogTypes) {
        if(Looper.myLooper()==null || !Looper.myLooper().isCurrentThread())
            Looper.prepare();
        DialogType type = dialogTypes[0];
        switch(type){
            case alert:
                dialog = DialogUtils.startErrorDialog(context,message.toString());
                break;
            case image:

                ;
                break;
            case progress:
                dialog = DialogUtils.startProgressDialog(context,message.toString());
                break;
            default:;
        }

        dialog.setTitle(title);

        if(dialog instanceof AlertDialog){
            ((AlertDialog)dialog).setMessage(message);
        } else if(dialog instanceof ProgressDialog){
            ((ProgressDialog)dialog).setMessage(message);
        };

        return dialog;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(Dialog dialog) {

        //dialog.dismiss();
    }

    public enum DialogType{alert,progress,image}





}
