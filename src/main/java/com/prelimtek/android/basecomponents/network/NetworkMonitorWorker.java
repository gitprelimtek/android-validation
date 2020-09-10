package com.prelimtek.android.basecomponents.network;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.prelimtek.android.basecomponents.AppNetworkStatus;

/**
 * Purpose of this class is to monitor network availability and type.
 * This is called periodically or once as needed. Depending on listener configurations,
 * worker will report and or disable certain features until network speed is optimal.
 * */
public class NetworkMonitorWorker extends Worker {

    public static final String TAG = NetworkMonitorWorker.class.getName();

    AppNetworkStatus networkStatus;
    public NetworkMonitorWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParameters) {
        super(context, workerParameters);
        networkStatus = AppNetworkStatus.getInstance(context);

    }

    @NonNull
    @Override
    public Result doWork() {

        Log.i(TAG,": doWork triggered ###########");

        //get conf is network required?
        boolean networkRequired = false;//Configuration.;
        if(networkRequired && !networkStatus.isOnline()){

            networkStatus.networkIsRequired(networkRequired);

           return Result.failure();
       }

       return Result.success();
    }

    @Override
    public void onStopped() {
        super.onStopped();
        Log.i(TAG,": doWork onStopped!!!!");
    }

}
