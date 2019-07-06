package io.mtini.android.tenantmanager;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import io.mtini.android.service.DataRefreshService;

import com.prelimtek.android.basecomponents.Configuration;
import com.prelimtek.android.basecomponents.dialog.DialogUtils;
import io.mtini.android.tenantmanager.dialog.LoginDialogFragment;
import io.mtini.model.AppDAO;

public class MainActivity extends AppCompatActivity implements LoginDialogFragment.LoggedInListener {

    public static String TAG = MainActivity.class.getSimpleName();

    private AppDAO dbHelper;
    private Dialog init_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        try{
        dbHelper =  AppDAO.builder().open(this);


        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
        }

        View fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Contact us", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                navigateToNextActivity();
            }
        });
        //fab.hide();

        Switch switchBtn = (Switch) findViewById(R.id.switch2);
        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton btn, boolean val) {
                Snackbar.make(btn, "Switched = "+val, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //show estates list

            }
        });


        /*
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "io.mtini.android.tenantmanager",
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        */
        //Security dialog
        displaySecurityDialog();

    }


    //TODO rename to proper name
    private void uploadRemoteData(){

        //TODO complete this service to replace code below
        Intent intent = new Intent(this, DataRefreshService.class);
        intent.putExtra(DataRefreshService.DATA_REFRESH_SERVICE_KEY,DataRefreshService.LOAD_STATE_DATA_INTENT);
        startService(intent);

        registerDataRefreshReceiver();
        Log.i(TAG,"initializing data ");

    }


    private void navigateToNextActivity(){

        dismissDialog();

        System.out.println("navigateToNextActivity called ");
        Intent intent = new Intent(this, EstateActivity.class);
        startActivity(intent);
    }

    private void showError(String message){
        if(init_dialog!=null && init_dialog.isShowing()){dismissDialog();}
        init_dialog=DialogUtils.startErrorDialog(this,message);
    }

    private void showProcessDialog(String message){
        //dismissDialog();
        if(init_dialog!=null && init_dialog.isShowing()){dismissDialog();}
        init_dialog = DialogUtils.startProgressDialog(this,message);
    }

    private void dismissDialog(){
        if(init_dialog!=null)init_dialog.dismiss();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent = null;
        switch (id) {
            case R.id.version:
                DialogUtils.startInfoDialog(this,"App Version",Configuration.configuredPreferences(this).getVersionText());
            case R.id.search:
                showError("Search has not been implemented. Coming soon!");
                return true;
            case R.id.user_settings:
                 intent = new Intent(this, UserSettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout:
                LoginDialogFragment.LoginManager.logout(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private DataRefreshCallback dataRefreshCallback = null;

    private void registerDataRefreshReceiver(){
        dataRefreshCallback = new DataRefreshCallback();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DataRefreshService.DATA_REFRESH_SERVICE_CALLBACK_ACTION);

        registerReceiver(dataRefreshCallback, intentFilter);
    }

    private void displaySecurityDialog(){

        FragmentManager fm = getFragmentManager();
        LoginDialogFragment editEstateDialogFragment = new LoginDialogFragment();
        Bundle args = new Bundle();
        //TODO pass login mechanism preference: i.e. facebook, google, local
        //args.putSerializable(EditEstateDetailsDialogFragment.ARG_EDIT_ESTATE, editEstate);
        editEstateDialogFragment.setArguments(args);
        editEstateDialogFragment.setCancelable(false);
        editEstateDialogFragment.show(fm, "fragment_login_dialog");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       try{
            unregisterReceiver(dataRefreshCallback);
       }catch(Exception e){
           Log.w(TAG,e.getMessage());
       }
    }

    @Override
    public void onLoginSuccessful() {
        showProcessDialog("Loading remote data. Please wait.");
        uploadRemoteData();
    }

    @Override
    public void onLoggedOutSuccessful() {
        showProcessDialog("Logged out.");
        onRestart();
    }

    private void showSnackBarMessage(String message){
        //Snackbar.make(getCurrentFocus(), message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    private class DataRefreshCallback extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(DataRefreshService.DATA_REFRESH_SERVICE_CALLBACK_INTENT_KEY);
            //String action = intent.getAction() ;
            String message = intent.getStringExtra(DataRefreshService.DATA_REFRESH_SERVICE_CALLBACK_MESSAGE_KEY);

            switch(type){
                case DataRefreshService.DATA_REFRESH_SERVICE_CALLBACK_SUCCESS_INTENT:
                    showSnackBarMessage(message);
                    //navigate to next
                    dismissDialog();
                    navigateToNextActivity();
                    ;break;
                case DataRefreshService.DATA_REFRESH_SERVICE_CALLBACK_ERROR_INTENT:
                    showError(message);
                    break;
            }

        }
    }

}
