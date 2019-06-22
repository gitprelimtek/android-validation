package io.mtini.android.service;

import android.app.Dialog;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import java.sql.SQLException;

import io.mtini.model.AppDAO;
import io.mtini.model.EstateModel;
import io.mtini.model.TenantModel;

//TODO revisit this to allow background data synchronization processes. Or is Async task preferable?
public class DataRefreshService extends IntentService {
    public static String TAG = DataRefreshService.class.getSimpleName();

    public DataRefreshService(){
        super("Data Synchronization Service");
    }

    public static final String LOAD_STATE_DATA_INTENT = "StateDataLoadIntent";
    public static final String LOAD_IMAGE_DATA_INTENT = "ImageDataLoadIntent";

    public static final String TENANT_METHODS_INTENT = "TenantMethodsIntent";
    public static final String ESTATE_METHODS_INTENT = "EstateMethodsIntent";
    public static final String METHOD_ADD_ACTION = "MethodAddAction";
    public static final String METHOD_UPDATE_ACTION = "MethodEditAction";
    public static final String METHOD_DELETE_ACTION = "MethodDeleteAction";
    public static final String METHOD_OBJECT_ATTR_KEY = "MethodObjectAttrKey";

    public static final String DATA_REFRESH_SERVICE_KEY = "DataRefreshService";
    public static final String DATA_REFRESH_SERVICE_CALLBACK_ACTION = "DataRefreshCallback";
    public static final String DATA_REFRESH_SERVICE_CALLBACK_INTENT_KEY = "DataRefreshCallbackIntent";
    public static final String DATA_REFRESH_SERVICE_CALLBACK_ERROR_INTENT = "DataRefreshErrorIntent";
    public static final String DATA_REFRESH_SERVICE_CALLBACK_SUCCESS_INTENT = "DataRefreshSuccessIntent";
    public static final String DATA_REFRESH_SERVICE_CALLBACK_MESSAGE_KEY = "Message";

    private AppDAO dbHelper;
    private Dialog init_dialog;

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();//getBaseContext();

        try {
            dbHelper =  AppDAO.builder().open(context);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String intentId = intent.getStringExtra(DATA_REFRESH_SERVICE_KEY);
        switch (intentId){
            case LOAD_STATE_DATA_INTENT:loadStateData();break;
            case LOAD_IMAGE_DATA_INTENT:loadImageData();break;
            case TENANT_METHODS_INTENT:tenantMethods(intent);break;
            case ESTATE_METHODS_INTENT:estateMethods(intent);break;
        }

    }

    //TODO make dbHelper calls granular and handle exceptions
    private void tenantMethods(Intent intent) {

        TenantModel tenant = (TenantModel)intent.getSerializableExtra(METHOD_OBJECT_ATTR_KEY);
        if(tenant == null){}
        String action = intent.getAction();

        switch (action){
            case METHOD_ADD_ACTION:
                dbHelper.addTenant(tenant,null);
                ;break;
            case METHOD_UPDATE_ACTION:
                dbHelper.updateTenant(tenant);
                ;break;
            case METHOD_DELETE_ACTION:
                dbHelper.deleteTenant(tenant,null);
                ;break;
        }

    }

    //TODO make dbHelper calls granular and handle exceptions
    private void estateMethods(Intent intent) {
        EstateModel estate = (EstateModel)intent.getSerializableExtra(METHOD_OBJECT_ATTR_KEY);
        String action = intent.getAction();

        switch (action){
            case METHOD_ADD_ACTION:
                dbHelper.addEstate(estate);
                ;break;
            case METHOD_UPDATE_ACTION:
                dbHelper.updateEstate(estate);
                ;break;
            case METHOD_DELETE_ACTION:
                dbHelper.deleteEstate(estate);
                ;break;
        }
    }


    private void loadImageData() {
    }

    private void loadStateData() {
        try {
            //DROP TABLE
            dbHelper.getLocalDao().dropTables();
            dbHelper.uploadData();
            showSuccess("Data loaded");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
            showError(e.getMessage());
        }
    }

    private void showError(String message){
        Intent intent = new Intent();
        intent.setAction(DATA_REFRESH_SERVICE_CALLBACK_ACTION);
        intent.putExtra(DATA_REFRESH_SERVICE_CALLBACK_INTENT_KEY,DATA_REFRESH_SERVICE_CALLBACK_ERROR_INTENT);
        intent.putExtra(DATA_REFRESH_SERVICE_CALLBACK_MESSAGE_KEY,message);
        sendBroadcast(intent);
    }

    private void showSuccess(String message){
        Intent intent = new Intent();
        intent.setAction(DATA_REFRESH_SERVICE_CALLBACK_ACTION);
        intent.putExtra(DATA_REFRESH_SERVICE_CALLBACK_INTENT_KEY,DATA_REFRESH_SERVICE_CALLBACK_SUCCESS_INTENT);
        intent.putExtra(DATA_REFRESH_SERVICE_CALLBACK_MESSAGE_KEY,message);

        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //dbHelper.close(); //TODO evaluate because this caused another process to throw a db closed exception. Not pooled.
    }
}
