package com.prelimtek.android.basecomponents;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class AppNetworkStatus {

    private static final String TAG = AppNetworkStatus.class.getSimpleName();
    private static Context context = null;
    private static AppNetworkStatus instance = new AppNetworkStatus();
    private AppNetworkStatus(){}

    public static AppNetworkStatus getInstance(Context _context){
            context = _context.getApplicationContext();
            return instance;
    }

    public boolean isOnline() {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
        if (nwInfo != null && nwInfo.isConnectedOrConnecting()) {
            Log.i(TAG,"network is ok!");
            return true;
        }

        Log.i(TAG,"network is NOT ok!");
        return false;
    }

    public void networkIsRequired(boolean required){
        if(required && !isOnline()){
            redirectToNoNetworkPage();
        }
    }

    public void redirectToNoNetworkPage(){

        Intent intent = new Intent(context,NoNetworkActivity.class);
        context.startActivity(intent);

    }

    public void restartApplication(){
        Context baseContext = context.getApplicationContext();
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.CUPCAKE) {
            intent = baseContext.getPackageManager()
                    .getLaunchIntentForPackage( baseContext.getPackageName() );
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        baseContext.startActivity(intent);
    }

}
