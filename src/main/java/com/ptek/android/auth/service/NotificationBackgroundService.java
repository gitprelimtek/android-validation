package com.ptek.android.auth.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * This should be a long running service that listens for backend notifications/messages
 * and updates a user interface.
*/
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NotificationBackgroundService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
