package com.prelimtek.android.basecomponents;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;

import com.prelimtek.android.customcomponents.R;

public class Configuration {


    static{
        //TODO read config file

    }

    /** 24 hours */
    public static int expiration_24_hours = 24;

    /**
     * Will be used to as a user configuredPreferences key to track session user data.
     * */
    public static String SERVER_SIDE_PREFERENCES_TAG = "EAT_SERVER_SIDE_PREFERENCES";

    public static String USER_SCREEN_PREFERENCES_TAG = "EAT_USER_SCREEN_PREFERENCES";

    public static String preferences_jwt_key = "JWT_PREF_KEY";

    /**
     * This is used in conjuction with user configuredPreferences remote server
     * */

    public static final String apiKey_Name = "x-mtini-apikey";

    public static final String authServiceKey = "authentication_service";

    public static final String walletAddressKey = "walletAddress";

    public static final String userIdKey = "id";

    public static final String DEFAULT_AUTH_SERVICE = "None";


    //public static final String DISK_CACHE_SUBDIR = "appDiskCache";
    //public static final int  DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB

    //IMAGE CONFIGURATIONS

    public static int captureImgMaxHeight = 300;

    public static int captureImgMaxWidth  = 300;

    public static int bitmapMaxHeight = 400;

    public static int bitmapMaxWidth  = 400;

    public static int imageDialogMaxHeight = 1000;

    public static int imageDialogMaxWidth = 1000;

    public enum SUPPORTED_AUTH_SERVICE{
        none,facebook,mtini,firebase
    }

    private Configuration(){
    }

    private Context context;

    private Configuration(Context context){
        this.context = context;
        versionCode = getVersionCode(context);
        versionName = getVersionName(context);
        PreferenceManager.setDefaultValues(context.getApplicationContext(),R.xml.preferences,false);
    }

    public SharedPreferences.Editor edit() {

        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(Configuration.SERVER_SIDE_PREFERENCES_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        return editor;
        //return this;
    }

    public static SharedPreferences preferences(Context context) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(Configuration.SERVER_SIDE_PREFERENCES_TAG, Context.MODE_PRIVATE);
        return pref;
    }

    public static Configuration configuredPreferences(Context context){
        Configuration conf = new Configuration(context);
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(Configuration.SERVER_SIDE_PREFERENCES_TAG, Context.MODE_PRIVATE);

        //server-side gets
        conf.apikey  = pref.getString(Configuration.preferences_jwt_key, null);
        conf.customerId = pref.getString(userIdKey, null);
        //conf.walletJson = pref.getString(walletAddressKey,null);

        //presets
        SharedPreferences defaultPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

        conf.authService = defaultPrefs.getString(authServiceKey,DEFAULT_AUTH_SERVICE);
        conf.currencyCode = defaultPrefs.getString("base_currency","USD");
        conf.dateFormatStr = defaultPrefs.getString("date_format","yyyy/MM/dd");
        conf.dateFormat = new SimpleDateFormat(conf.dateFormatStr);
        //conf.userEmail = pref.getString("", null);
        //conf.phoneNumber = pref.getString("", null);

        Boolean sslEnabled = defaultPrefs.getBoolean("remoteServerTLSEnabled",false);
        String remoteServerPort = defaultPrefs.getString("remoteServerPort",null);
        String remoteServer = defaultPrefs.getString("remoteServer",null);

        if(sslEnabled){
            conf.remoteHostUrl="https://"+remoteServer+":"+remoteServerPort+"/";
        }else{
            conf.remoteHostUrl="http://"+remoteServer+":"+remoteServerPort+"/";
        }
        return conf;
    }

    public static int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException ex) {}
        return 0;
    }


    public static String getVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException ex) {}
        return "";
    }


    public static String getVersionText(Context context) {

        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return  ""+pi.versionName+":"+pi.versionCode;
        } catch (PackageManager.NameNotFoundException ex) {}
        return "";
    }

    public String remoteHostUrl;
    public String customerId;
    public String apikey;
    public String userEmail;
    public String phoneNumber;
    public String currencyCode;
    public String dateFormatStr;
    public SimpleDateFormat dateFormat;
    public String authService;
    public String walletJson;
    public String versionName;
    public int versionCode;

}
