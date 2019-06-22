package io.mtini.android.view.component;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;

import com.prelimtek.android.basecomponents.Configuration;

import io.mtini.android.service.SecurityService;
import io.mtini.android.tenantmanager.R;
import io.mtini.android.tenantmanager.databinding.LoginEatPhoneEmailDialogfragmentLayoutBinding;
import com.prelimtek.android.basecomponents.dialog.DialogUtils;

import com.prelimtek.android.crypto.BitcoinCryptoUtils;
import com.prelimtek.android.crypto.Wallet;

import com.prelimtek.android.crypto.SecurityModel;
import com.prelimtek.android.crypto.JWTManager;

public class EATLoginFragment extends DialogFragment {

    public interface EATLoginInterface {
        public void onWalletDecrypted(SecurityModel sModel,Wallet wallet);
    }

    static String TAG = Class.class.getSimpleName();

    public static String ARG_EAT_LOGIN_USERNAME="username";
    public static String ARG_EAT_LOGIN_PASSWORD="password";

    public static String ARG_EAT_LOGIN_EMAIL="email";
    public static String ARG_EAT_LOGIN_PHONENUMBER="phoneNumber";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //TODO review this , i.e username password usage
        String username= null,password= null,email= null,phoneNumber = null;
        if (savedInstanceState != null) {
            username = savedInstanceState.getString(ARG_EAT_LOGIN_USERNAME);
            password = savedInstanceState.getString(ARG_EAT_LOGIN_PASSWORD);
            email = savedInstanceState.getString(ARG_EAT_LOGIN_EMAIL);
            phoneNumber = savedInstanceState.getString(ARG_EAT_LOGIN_PHONENUMBER);
        }else if(getArguments()!=null) {
            username = getArguments().getString(ARG_EAT_LOGIN_USERNAME);
            password = getArguments().getString(ARG_EAT_LOGIN_PASSWORD);
            email = getArguments().getString(ARG_EAT_LOGIN_EMAIL);
            phoneNumber = getArguments().getString(ARG_EAT_LOGIN_PHONENUMBER);
        }

        SecurityModel securityModel = new SecurityModel();
        securityModel.setEmail(email);
        securityModel.setPassword(password);
        securityModel.setPhoneNumber(phoneNumber);
        securityModel.setUserName(username);
        //securityModel.setToken();//TODO get this from preferrences
        //TODO if token expired do something

        LoginEatPhoneEmailDialogfragmentLayoutBinding binding = DataBindingUtil
                .inflate(inflater, R.layout.login_eat_phone_email_dialogfragment_layout,container,false)
                ;
        binding.setSecurity(securityModel);

        View view = binding.getRoot();

        binding.emailText.setFocusableInTouchMode(true);
        binding.emailText.requestFocus();



        View requestTokenBtn = view.findViewById(R.id.security_request_token_btn);
        requestTokenBtn.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(final View v) {

                        LoginEatPhoneEmailDialogfragmentLayoutBinding binding  = DataBindingUtil.findBinding(v);
                        final SecurityModel sModel = binding.getSecurity();
                        //validate model
                        if(binding.emailText.getError()==null && binding.phoneText.getError()==null){

                            v.setClickable(false);

                            sModel.setUserName(sModel.getEmail());
                            sModel.setAuthService(Configuration.SUPPORTED_AUTH_SERVICE.mtini);
                            requestSecurityToken(getActivity(),sModel);
                            Log.i(TAG,"requesting token ");

                        }else{
                            Log.w(TAG,"validation failed  ");
                        }
                    }
                }
        );


        //Setup cancel button to ignore changes
        View cancelBtn = view.findViewById(R.id.security_cancel_btn );
        cancelBtn.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                }
        );

        return view;
    }



    private void showTokenVerificationUI(final SecurityModel smodel) {

        final Activity context = this.getActivity();//getApplicationContext();
        final EditText tokenText = new EditText(context);
        tokenText.setId(R.id.tokenText);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface parentDialog, int which) {
                parentDialog.dismiss();
                showProcessDialog(context,"Verifying token");
                //Activity context = ((AlertDialog)parentDialog).getOwnerActivity();//.getBaseContext();//getApplicationContext();
                //get token value
                //TextView tokenText = context.findViewById(R.id.tokenText);
                CharSequence text = tokenText.getText();
                smodel.setToken(text==null?null:text.toString());
                /*
                Runnable run = new Runnable(){
                    @Override
                    public void run() {
                        authenticateTokenEmailText(smodel);
                    }};


                context.runOnUiThread(run);
                */
                verifySecurityToken(context,smodel);

            }
        };


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        //TODO add icon dialogBuilder.setIcon()
        dialogBuilder
                .setTitle(R.string.dialog_verify_token)
                .setMessage(R.string.dialog_verify_token_msg)
                ;

        dialogBuilder.setPositiveButton(R.string.verify_token,listener);
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.setCanceledOnTouchOutside(false);

        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);


        //dialog.addContentView(tokenText,params);
        dialog.setView(tokenText);
        dialog.show();
    }

    private void showCreateNewWalletUI(final SecurityModel smodel, final String message) {

        final Activity context = this.getActivity();//getApplicationContext();

        TextView msg = new TextView(context);
        msg.setText(message);
        msg.setTextColor(Color.RED);

        final EditText passphraseText = new EditText(context);
        passphraseText.setId(R.id.tokenText);
        //passphraseText.setInputType(Type..);
        passphraseText.setHint(EditText.AUTOFILL_HINT_PASSWORD);

        final EditText passphraseText2 = new EditText(context);
        passphraseText.setHint(EditText.AUTOFILL_HINT_PASSWORD);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface parentDialog, int which) {
                parentDialog.dismiss();
                showProcessDialog(context,"Creating a new wallet for "+smodel.getUserName());

                try {
                    CharSequence text = passphraseText.getText();
                    CharSequence text2 = passphraseText2.getText();

                    System.out.println("text "+text);
                    System.out.println("text2 "+text2);

                    CharSequence hashedPass = null;
                    if(text!=null && text.length()>0){

                        if (!text.toString().equals(text2.toString())) {

                            throw new Exception("passphrases should be the same.");

                        }

                        //Wallet.generateWalletAddress()
                         hashedPass = BitcoinCryptoUtils.generatePassPhrase(text,true);

                    }

                    smodel.setPassword(hashedPass==null?null:hashedPass.toString());

                    createAndPersistWallet(context,smodel);


                }catch(Exception e){
                    dismissMessageDialog();

                    Log.w(TAG,e.getMessage(),e);
                    passphraseText.setError(e.getMessage());
                    //showErrorMessage();
                    showCreateNewWalletUI(smodel,e.getMessage());
                    return;
                }

            }
        };


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        //TODO add icon dialogBuilder.setIcon()
        dialogBuilder
                .setTitle(R.string.dialog_create_wallet)
                .setMessage(R.string.dialog_create_wallet_msg)
        ;

        dialogBuilder.setNeutralButton(R.string.create_wallet,listener);
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.setCanceledOnTouchOutside(false);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT );

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(msg,params);
        layout.addView(passphraseText,params);
        layout.addView(passphraseText2,params);
        dialog.setView(layout);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }


    private Wallet decryptMyWalletForLocalOnly(SecurityModel sModel, Wallet wallet){

        try {

            String userPrivateKey = wallet.decryptPrivateKeyHex(sModel.getPassword());

            /*
            String signingKey = dbHelper.getRemoteDao().retrieveServerSigner(sModel);
            String jwt = createJWT(userPrivateKey, sModel.getEmail(), signingKey);

            Configuration.configuredPreferences(getContext())
                    .edit()
                    .putString(Configuration.preferences_jwt_key, jwt)
              */
        }catch (Exception e){
            Log.e(TAG,e.getMessage(),e);
            return null;
        }

        return wallet;
    }

    @Deprecated
    private String createJWT(String id, String subject , String signingKey){
        JWTManager mn =  new JWTManager(signingKey);
        long expiration = JWTManager.incrementHours( new Date(),Configuration.expiration_24_hours).getTime();
        String jwt =  mn.createJWT(id,"mtini",subject, expiration );

        System.out.println("jwt : "+jwt);
        Log.i(TAG,jwt);

        return jwt;
    }

    private void showDecryptWalletUI(final SecurityModel sModel, final Wallet wallet, String message) {

        Wallet decWallet =decryptMyWalletForLocalOnly(sModel,wallet);
       if(decWallet!=null){showSuccess(sModel,decWallet,"Security Token set.");return;}

        final Activity context = this.getActivity();//getApplicationContext();

        TextView msg = new TextView(context);
        msg.setText(message);
        msg.setTextColor(Color.RED);

        final EditText passphraseText = new EditText(context);
        passphraseText.setId(R.id.tokenText);

        passphraseText.setHint(EditText.AUTOFILL_HINT_PASSWORD);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface parentDialog, int which) {
                parentDialog.dismiss();
                showProcessDialog(context ,"Decrypting wallet");

                Wallet decWallet = null;
                SecurityModel sModel2 = sModel;
                try {
                    CharSequence text = passphraseText.getText();

                    System.out.println("text "+text);

                    CharSequence hashedPass = null;
                    if(text!=null && text.length()>0){

                        //Wallet.generateWalletAddress()
                        hashedPass = BitcoinCryptoUtils.generatePassPhrase(text,true);

                    }

                    sModel2.setPassword(hashedPass==null?null:hashedPass.toString());

                    decWallet = decryptMyWalletForLocalOnly(sModel2,wallet);

                    if(decWallet==null) {
                        throw new Exception("Decryption failed. Try again.");
                    }

                    //parentDialog.dismiss();

                }catch(Exception e){
                    Log.e(TAG,e.getMessage(),e);
                    passphraseText.setError(e.getMessage());
                    //showErrorMessage();
                    dismissMessageDialog();
                    showDecryptWalletUI(sModel,wallet,e.getMessage());
                    return;
                }

                System.out.println("decWallet encryption on? "+decWallet.isEncrypted());
                showSuccess(sModel2,decWallet,"Security Token set.");

            }
        };


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        //TODO add icon dialogBuilder.setIcon()
        dialogBuilder
                .setTitle(R.string.dialog_decrypt_wallet)
                .setMessage(R.string.dialog_decrypt_wallet_msg)
        ;

        dialogBuilder.setNeutralButton(R.string.decrypt_wallet,listener);
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.setCanceledOnTouchOutside(false);


        //imageView.setOnLongClickListener(listener);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT );
        //params.setLayoutDirection(LinearLayout.VERTICAL);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(msg,params);
        layout.addView(passphraseText,params);
        dialog.setView(layout);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();

    }

    @Override
    public void onResume() {
        System.out.println("OnResume xxxxxxxxxxxxxxx");
        super.onResume();

        //show or hide certain aspects based on action.
        //1. on start - enable email field, disable token field, disable
    }

    @Override
    public void onDestroy() {
        System.out.println("onDestroy xxxxxxxxxxxxxxx");
        //getActivity().unregisterReceiver(eatFragLogInCallBack);
        super.onDestroy();
    }

    //TODO testing to remove error: "Failure saving state: EATLoginFragment{7e8968e #2 eat_login_fragment} has target not in fragment manager: LoginDialogFragment{bbb8eaf}"
    @Override
    public void onSaveInstanceState(final Bundle outState) {
        setTargetFragment(null, -1);
    }

    ////////////////////////////////////////////////////////////
    ////////////    Intents and Callback functions /////////////
    //////////// TODO move functions to LoginManager?
    ////////////////////////////////////////////////////////////

    public LogInCallBack eatFragLogInCallBack = new LogInCallBack();

    public LogInCallBack registerSecurityServiceReceiver(Activity context) {
        eatFragLogInCallBack = eatFragLogInCallBack!=null?eatFragLogInCallBack:new LogInCallBack();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SecurityService.SECURITY_SERVICE_CALLBACK_ACTION);
        context.registerReceiver(eatFragLogInCallBack, intentFilter);
        return eatFragLogInCallBack;
    }

    public static void requestSecurityToken(Activity context,SecurityModel smodel) {

        Intent intent = new Intent(context, SecurityService.class);
        intent.putExtra(SecurityService.SECURITY_SERVICE_KEY,SecurityService.REQUEST_EMAIL_TEXT_TOKEN_INTENT);
        intent.putExtra(SecurityService.SECURITY_MODEL_KEY,smodel);
        context.startService(intent);

    }


    public static void verifySecurityToken(Activity context,SecurityModel smodel) {

        Intent intent = new Intent(context, SecurityService.class);
        intent.putExtra(SecurityService.SECURITY_SERVICE_KEY,SecurityService.AUTHENTICATE_TOKEN_AS_EMAIL_TEXT_INTENT);
        intent.putExtra(SecurityService.SECURITY_MODEL_KEY,smodel);
        context.startService(intent);

    }

    public static void getMyLocalWallet(Activity context,SecurityModel smodel) {

        Intent intent = new Intent(context, SecurityService.class);
        intent.putExtra(SecurityService.SECURITY_SERVICE_KEY,SecurityService.RETRIEVE_MY_LOCAL_ENCRYPTED_WALLET_INTENT);
        intent.putExtra(SecurityService.SECURITY_MODEL_KEY,smodel);
        context.startService(intent);

    }

    public static void createAndPersistWallet(Activity context, SecurityModel smodel) {

        Intent intent = new Intent(context, SecurityService.class);
        intent.putExtra(SecurityService.SECURITY_SERVICE_KEY,SecurityService.CREATE_NEW_WALLET_INTENT);
        intent.putExtra(SecurityService.SECURITY_MODEL_KEY,smodel);
        context.startService(intent);

    }

    public class LogInCallBack extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            dismissMessageDialog();

            String type = intent.getStringExtra(SecurityService.SECURITY_SERVICE_CALLBACK_INTENT_KEY);
            String action = intent.getAction() ;
            String message = intent.getStringExtra(SecurityService.SECURITY_SERVICE_CALLBACK_MESSAGE_KEY);
            SecurityModel sModel = null;
            Wallet wallet = null;
            switch(type){
                case SecurityService.SECURITY_SERVICE_CALLBACK_SUCCESS_INTENT:
                    sModel = (SecurityModel) intent.getSerializableExtra(SecurityService.SECURITY_MODEL_KEY);
                     wallet = (Wallet) intent.getSerializableExtra(SecurityService.WALLET_KEY);
                    showSuccess(sModel, wallet, message);
                    break;
                case SecurityService.SECURITY_SERVICE_CALLBACK_ERROR_INTENT:
                    showErrorMessage(context,message);
                    break;
                case SecurityService.SECURITY_SERVICE_CALLBACK_VERIFY_TOKEN_INTENT:
                     sModel = (SecurityModel) intent.getSerializableExtra(SecurityService.SECURITY_MODEL_KEY);
                    showTokenVerificationUI(sModel);
                    break;
                case SecurityService.SECURITY_SERVICE_CALLBACK_CREATE_NEW_WALLET_INTENT:
                     sModel = (SecurityModel) intent.getSerializableExtra(SecurityService.SECURITY_MODEL_KEY);
                    showCreateNewWalletUI(sModel,message);
                    break;
                case SecurityService.SECURITY_SERVICE_CALLBACK_UNENCRYPT_WALLET_INTENT:
                    sModel = (SecurityModel) intent.getSerializableExtra(SecurityService.SECURITY_MODEL_KEY);
                     wallet = (Wallet) intent.getSerializableExtra(SecurityService.WALLET_KEY);
                    showDecryptWalletUI(sModel,wallet,"");
                    break;
            }

        }

    }

    public void showSuccess(SecurityModel sModel, Wallet wallet, String message){
        showSnackBarMessage(getView(),message);

        if(this instanceof EATLoginInterface){
            ((EATLoginInterface)this).onWalletDecrypted(sModel,wallet);
        } else if(getTargetFragment()!=null)
            ((EATLoginInterface)this.getTargetFragment()).onWalletDecrypted(sModel,wallet);
        else if(getParentFragment()!=null)
            ((EATLoginInterface)this.getParentFragment()).onWalletDecrypted(sModel,wallet);
        else if(getActivity()!=null)
            ((EATLoginInterface)this.getActivity()).onWalletDecrypted(sModel,wallet);

        dismiss();
    }


    public static Dialog init_dialog = null;
    public static void showErrorMessage(Context context, String message){
        if(init_dialog!=null && init_dialog.isShowing()){init_dialog.dismiss();}
        init_dialog=DialogUtils.startErrorDialog(context,message);
    }

    public static void dismissMessageDialog(){
        if(init_dialog!=null && init_dialog.isShowing()){
            Activity activity = init_dialog.getOwnerActivity();
            if( activity!=null && !activity.isFinishing()) {
                init_dialog.dismiss();
            }

        }
    }

    public static void showProcessDialog(final Context context, final String message){

        //DialogTasks dialogTask = new DialogTasks(context,null,message);
        //AsyncTask<DialogTasks.DialogType,Void,Dialog> task = dialogTask.execute(DialogTasks.DialogType.progress );

        try {

            //init_dialog = task.get();
            //Log.i(TAG,init_dialog.toString());
            /*
            Looper.prepareMainLooper();//epare();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    init_dialog = DialogUtils.startProgressDialog(context,message);
                }
            }).start();
            */

           init_dialog = DialogUtils.startProgressDialog(context,message);

        }catch(Exception e){
            Log.e(TAG, e.getMessage(),e);
        }
    }

    private static void showSnackBarMessage(View view, String message){
        //View view = context.findViewById(R.id.security_request_token_btn);
        if(view!=null)
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

}
