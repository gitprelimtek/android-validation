package com.prelimtek.android.basecomponents.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.widget.ProgressBar;

import com.prelimtek.android.customcomponents.R;


public class DialogUtils {

    public static Dialog startProgressDialog(Context context){
        return startProgressDialog(context,"Loading. Please wait...");
    }

    public static void startProgressDialogRunnable(final Activity context, final String message){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startProgressDialog(context,message);
            }
        });
    }

    public static Dialog startProgressDialog(Context context, String message) {
        ProgressBar bar = new ProgressBar(context);
        //bar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(bar);
        builder.setMessage(message);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;
    }

    public static void startErrorDialogRunnable( Activity activity, final String message){
        final Context context = activity.getBaseContext();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startErrorDialog(context,message);
            }
        });
    }

    public static AlertDialog startErrorDialog(Context context, String message){
        if(Looper.myLooper()==null)Looper.prepare();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        //TODO add icon dialogBuilder.setIcon()
        dialogBuilder.setMessage(message)
                .setTitle(R.string.error_message);
        dialogBuilder.setPositiveButton(R.string.ok,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });


        AlertDialog errorDialog = dialogBuilder.create();
        errorDialog.setCanceledOnTouchOutside(true);
        errorDialog.show();
        return errorDialog;
    }

    public static AlertDialog startInfoDialog(Context context, CharSequence title, String message){
        if(Looper.myLooper()==null)Looper.prepare();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        //TODO add icon dialogBuilder.setIcon()
        dialogBuilder.setMessage(message);
        if(title!=null)
            dialogBuilder.setTitle(title);
        dialogBuilder.setPositiveButton(R.string.ok,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });


        AlertDialog errorDialog = dialogBuilder.create();
        errorDialog.setCanceledOnTouchOutside(true);
        errorDialog.show();
        return errorDialog;
    }

}
