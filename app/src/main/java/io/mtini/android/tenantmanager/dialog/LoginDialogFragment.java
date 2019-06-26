package io.mtini.android.tenantmanager.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.google.gson.Gson;

import org.bitcoinj.core.ECKey;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;

import com.prelimtek.android.basecomponents.Configuration;
import io.mtini.android.service.SecurityService;
import io.mtini.android.tenantmanager.R;
import io.mtini.android.tenantmanager.UserSettingsActivity;

import com.prelimtek.android.basecomponents.dialog.DialogUtils;
import com.prelimtek.utils.crypto.Wallet;

import io.mtini.model.AppDAO;
import io.mtini.model.RemoteDAO;
import io.mtini.model.SecurityModel;

import com.prelimtek.utils.crypto.JWTManager;
import io.mtini.android.view.component.EATLoginButton;
import io.mtini.android.view.component.EATLoginFragment;

public class LoginDialogFragment extends EATLoginFragment implements EATLoginFragment.EATLoginInterface {

    public enum FACEBOOK_ATTR{ email, user_mobile_phone, public_profile,user_birthday,user_friends};

    public interface LoggedInListener {

        public void onLoginSuccessful();
        public void onLoggedOutSuccessful();

    }
    public static final String TAG = LoginDialogFragment.class.getSimpleName();

    private AppDAO dbHelper;
    private Dialog minorDialog;

    private EATLoginFragment eatLoginFrag;
    private EATLoginFragment.LogInCallBack eatLoginCallBackReceiever;

    @Override
    public void onAttach(Context context) {
        System.out.println("onAttach ...............");
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("onCreate ...............");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.ThemeOverlay_AppCompat_Dialog );
        try {
            dbHelper =  AppDAO.builder().open(this.getActivity());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        //eatLoginFrag = new EATLoginFragment();
        //eatLoginCallBackReceiever = new EATLoginFragment.LogInCallBack();
        //eatLoginCallBackReceiever = eatLoginFrag.registerSecurityServiceReceiver(getActivity());
        eatLoginCallBackReceiever = registerSecurityServiceReceiver(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        System.out.println("onCreateView ...............");
        View view = inflater.inflate(R.layout.login_dialogfragment_layout,container,false);

        View settingsButton = view.findViewById(R.id.settings_button);
        settingsButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserSettingsActivity.class);
                startActivity(intent);
            }});
        /*
        //TODO assume login configuredPreferences to decide on which login mechanism.
        if (savedInstanceState != null) {
            imagesModel = (ImagesModel) savedInstanceState.getSerializable(ARG_SELECTED_MODEL_IMAGE);
        } else if (getArguments() != null) {
            imagesModel = (ImagesModel) getArguments().getSerializable(ARG_SELECTED_MODEL_IMAGE);
        }
        */

        //register receiver used by all
        //IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction(SecurityService.SECURITY_SERVICE_CALLBACK_ACTION);
        //getActivity().registerReceiver(localLoginEATReceiver, intentFilter);

        createFaceBookLogin(view);

        createEATLogin(view);

        return view;
    }



    @Override
    public void onResume() {
        System.out.println("onResume ...............");
        super.onResume();
        Activity context = getActivity();
        String confAuthService = Configuration.configuredPreferences(context).authService;

        boolean fbActivated = confAuthService == null || Configuration.SUPPORTED_AUTH_SERVICE.facebook.name().equalsIgnoreCase(confAuthService) || Configuration.SUPPORTED_AUTH_SERVICE.none.name().equalsIgnoreCase(confAuthService);
        fbLoginButton.setActivated(fbActivated);
        fbLoginButton.setEnabled(fbActivated);
        System.out.println("onResume FBLogin ...............activate="+fbActivated);
        boolean eatActivated = confAuthService == null || Configuration.SUPPORTED_AUTH_SERVICE.mtini.name().equalsIgnoreCase(confAuthService) || Configuration.SUPPORTED_AUTH_SERVICE.none.name().equalsIgnoreCase(confAuthService);
        eatLoginButton.setActivated(eatActivated);
        eatLoginButton.setEnabled(eatActivated);
        System.out.println("onResume EATLogin ...............activate="+eatActivated);

        Configuration.SUPPORTED_AUTH_SERVICE service = Configuration.SUPPORTED_AUTH_SERVICE.valueOf(Configuration.configuredPreferences(this.getActivity()).authService.toLowerCase());
        SharedPreferences confPrefs = Configuration.preferences(context);
        String userName = confPrefs.getString(Configuration.userIdKey,null);
        String email = confPrefs.getString(EATLoginFragment.ARG_EAT_LOGIN_EMAIL,null);
        String phoneNumber = confPrefs.getString(EATLoginFragment.ARG_EAT_LOGIN_PHONENUMBER,null);
        SecurityModel sModel = new SecurityModel();
        sModel.setUserName(userName);
        sModel.setPhoneNumber(phoneNumber);
        sModel.setEmail(email);
        boolean loggedIn = false;

        switch(service){
            case facebook:

                AccessToken curAccessToken = AccessToken.getCurrentAccessToken();

                    if(isLoggedIn(curAccessToken)){
                        try {

                            loggedIn = LoginManager.checkIsLogged(context,sModel);

                        } catch (Wallet.WalletException e) {
                            Log.e(TAG,e.getMessage(),e);
                            loggedIn = false;
                        }finally{
                            if(!loggedIn){
                                //do nothing
                                makeFaceBookGraphRequest(curAccessToken);
                            }
                        }
                    }



                break;
            case mtini:
                //TODO use jwtmanager
                //call service
                //get wallet from configuredPreferences
                //should be decrypted already

                try {
                    loggedIn = LoginManager.checkIsLogged(context,sModel);
                } catch (Wallet.WalletException e) {
                   Log.e(TAG,e.getMessage(),e);
                   //do nothing
                }


                break;
            case none:
                    //log all others out!
                    //
                ;
                break;
            default:
        }

        updateAuthentication(loggedIn);

    }


    @Deprecated
    private void getMyLocalEncryptedWallet(SecurityModel smodel) {

        Intent intent = new Intent(this.getActivity(), SecurityService.class);
        intent.putExtra(SecurityService.SECURITY_SERVICE_KEY,SecurityService.RETRIEVE_MY_LOCAL_ENCRYPTED_WALLET_INTENT);
        intent.putExtra(SecurityService.SECURITY_MODEL_KEY,smodel);
        getActivity().startService(intent);

    }

    LoginButton fbLoginButton;
    EATLoginButton eatLoginButton;

    CallbackManager fbCallbackManager;
    private void createFaceBookLogin(View view){
        System.out.println("createFaceBookLogin ...............activated");

        fbCallbackManager = CallbackManager.Factory.create();

        fbLoginButton = (LoginButton) view.findViewById(R.id.fb_login_button);
        fbLoginButton.setReadPermissions(
                Arrays.asList(
                        FACEBOOK_ATTR.email.name(),
                        //FACEBOOK_ATTR.user_mobile_phone.name(),
                        FACEBOOK_ATTR.public_profile.name(),
                        FACEBOOK_ATTR.user_friends.name()
                )
        );
        // If you are using in a fragment, call loginButton.setFragment(this);
        //fbLoginButton.setActivated(activated);
        //fbLoginButton.setEnabled(activated);
        fbLoginButton.setFragment(this);
        // Callback registration
        fbLoginButton.registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                final AccessToken accessToken = loginResult.getAccessToken();
                if(isLoggedIn(accessToken)){

                    makeFaceBookGraphRequest(accessToken);

                    /*
                    //retrieve or create wallet
                    SecurityModel sModel = new SecurityModel();
                    //sModel.setPhoneNumber(accessToken.getUserId());
                    sModel.setEmail(accessToken.getUserId());
                    sModel.setAuthService(Configuration.SUPPORTED_AUTH_SERVICE.facebook);
                    sModel.setToken(accessToken.getToken());

                    writeMessage(accessToken.getUserId() +" is logged in");
                    updateAuthentication(true);

                    */
                }else{
                    writeMessage("Login failed.");
                }

            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                exception.printStackTrace();
                writeMessage("Login failed with error: "+exception.getMessage());
            }
        });

    }

    private void makeFaceBookGraphRequest(final AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        Log.v("LoginActivity",graphResponse==null?null:graphResponse.toString());

                        if(jsonObject==null ){

                            Log.e("LoginActivity",graphResponse==null?null:graphResponse.toString());

                            return;
                        }

                        try {

                            String name = jsonObject.getString("name");
                            String id = jsonObject.getString("id");
                            String email = jsonObject.getString("email");
                            //String bday = jsonObject.getString("birthday");
                            //String phone = jsonObject.getString("phone");

                            SecurityModel sModel = new SecurityModel();
                            //sModel.setPhoneNumber(accessToken.getUserId());
                            sModel.setEmail(email);
                            sModel.setUserName(id);
                            //sModel.setUserName(email);
                            sModel.setAuthService(Configuration.SUPPORTED_AUTH_SERVICE.facebook);
                            sModel.setToken(accessToken.getToken());

                            //registerSecurityServiceReceiver();
                            //IntentFilter intentFilter = new IntentFilter();
                            //intentFilter.addAction(SecurityService.SECURITY_SERVICE_CALLBACK_ACTION);
                            //getActivity().registerReceiver(eatFragLogInCallBack, intentFilter);
                            EATLoginFragment.showProcessDialog(getActivity(),"Begin retrieving facebook user wallet.");
                            EATLoginFragment.verifySecurityToken(getActivity(),sModel);
                            writeMessage(name +" is logged in");
                            //updateAuthentication(true);

                        }catch(JSONException e){
                            Log.e(TAG,e.getLocalizedMessage(),e);
                        }

                    }
                }
        );
        Bundle params = new Bundle();
        params.putString("fields","id,name,email,gender,birthday");//phone causing error
        request.setParameters(params);
        request.executeAsync();
    }


    private BroadcastReceiver localLoginEATReceiver =   new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(SecurityService.SECURITY_SERVICE_CALLBACK_INTENT_KEY);
            String action = intent.getAction() ;
            String message = intent.getStringExtra(SecurityService.SECURITY_SERVICE_CALLBACK_MESSAGE_KEY);
            SecurityModel sModel = null;
            Wallet wallet = null;
            switch(type) {
                case SecurityService.SECURITY_SERVICE_CALLBACK_SUCCESS_INTENT:
                    sModel = (SecurityModel) intent.getSerializableExtra(SecurityService.SECURITY_MODEL_KEY);
                    wallet = (Wallet) intent.getSerializableExtra(SecurityService.WALLET_KEY);
                    onWalletDecrypted(sModel,wallet);
                    //showSuccess(sModel, wallet, message);
                    ;

                    break;
                case SecurityService.SECURITY_SERVICE_CALLBACK_ERROR_INTENT:
                    showError( message);
                    break;
            }

        }
    };

    private void createEATLogin(View view) {
         System.out.println("createEATLogin ...............");

        eatLoginButton = (EATLoginButton) view.findViewById(R.id.eat_login_button);
        eatLoginButton.setParentFragment(this);
        //eatLoginButton.setActivated(activated);
        //eatLoginButton.setEnabled(activated);
        /*localLoginEATReceiver  =  new BroadcastReceiver(){

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String type = intent.getStringExtra(SecurityService.SECURITY_SERVICE_CALLBACK_INTENT_KEY);
                        String action = intent.getAction() ;
                        String message = intent.getStringExtra(SecurityService.SECURITY_SERVICE_CALLBACK_MESSAGE_KEY);
                        SecurityModel sModel = null;
                        Wallet wallet = null;
                        switch(type) {
                            case SecurityService.SECURITY_SERVICE_CALLBACK_SUCCESS_INTENT:
                                sModel = (SecurityModel) intent.getSerializableExtra(SecurityService.SECURITY_MODEL_KEY);
                                wallet = (Wallet) intent.getSerializableExtra(SecurityService.WALLET_KEY);
                                onWalletDecrypted(sModel,wallet);
                                //showSuccess(sModel, wallet, message);
                                ;

                                break;
                            case SecurityService.SECURITY_SERVICE_CALLBACK_ERROR_INTENT:
                                showError( message);
                                break;
                        }

                        }
                };*/

        //TODO set callback on this
    }



    private void writeMessage(String message){
        TextView view = getView().findViewById(R.id.security_message_details);
        view.setText(message);
    }

    public void updateAuthentication(Boolean loggedIn){

        if(loggedIn) {
            LoggedInListener listener = (LoggedInListener) getActivity();
            listener.onLoginSuccessful();
            dismiss();
        }else{
            //DO nothing
            //LoggedInListener listener = (LoggedInListener) getActivity();
            //listener.onLoginSuccessful();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("onActivityResult ...............");
        fbCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        System.out.println("onDestroy ...............");
        //getActivity().unregisterReceiver(localLoginEATReceiver);
        getActivity().unregisterReceiver(eatLoginCallBackReceiever);
        super.onDestroy();
    }

    @Override
    public void onWalletDecrypted(SecurityModel sModel, Wallet wallet) {

        //persist wallet
        //get private key
        //create jwt and persist
        try {
            if(wallet.isEncrypted()){
                wallet.decryptPrivateKeyHex(sModel.getPassword());
                //persist?
            }

            if (!wallet.isEncrypted()) {
                String jwt = null;
                int trial = 3;
                do {
                    try {
                        Thread.sleep(10000);
                        jwt = dbHelper.getRemoteDao().retrieveAPIKeyFromPrefs();
                        if (jwt != null) break;
                    } catch (Exception e) {
                        Log.w(TAG, e.getMessage(), e);
                    }
                } while (--trial > 0);

                if(jwt==null){

                    String signingKey = dbHelper.getRemoteDao().retrieveServerSigner(sModel);
                    jwt = createJWT(wallet.getPrivateKeyHex(), sModel.getEmail(), signingKey);
                }

                String id = sModel.getEmail()==null || sModel.getEmail().isEmpty()?
                        sModel.getPhoneNumber() : sModel.getEmail();

                String address = Wallet.generateWalletAddress(sModel);
                Configuration.SUPPORTED_AUTH_SERVICE authService = sModel.getAuthService();

                boolean loggedin = LoginManager.login(getActivity(),id,jwt,address,sModel,wallet,authService);

                updateAuthentication(loggedin);
            }else{
                throw new Exception("Expected a decrypted wallet. This one is locked.");
            }
        }catch(Exception e){
            dismissMessageDialog();
            Log.e(TAG,e.getMessage(),e);
            showError(e.getMessage());
        }


    }


    private void setTokenToSession(AccessToken accessToken, JSONObject object) throws RemoteDAO.RemoteDAOException {

        //get signin key from server
        String id = accessToken.getUserId();
        String token = accessToken.getToken();

        SharedPreferences pref = Configuration.preferences(getActivity().getApplicationContext());//.getSharedPreferences(Configuration.SERVER_SIDE_PREFERENCES_TAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        //editor.clear();//just incase there was old data
        //editor.apply();//just incase
        String userEmail = null;
        String name = null;
        String rawData = null;
        if(object!=null) {
            userEmail = object.optString("email", "null");
            //String userGender = object.optString("gender");
            String userMobile = object.optString("mobile_phone");
            //String userBirthdate = object.optString("birthday");
            name = object.optString("name");
            rawData = object.toString();
            editor.putString("email", userEmail);
            editor.putString("name", name);
        }

        editor.putString("id", id);
        editor.putString("rawData", rawData);
        editor.commit();
        /*
        SecurityModel sModel = new SecurityModel();
        sModel.setEmail(userEmail);
        sModel.setUserName(id);
        sModel.setToken(token);
        sModel.setAuthService(Configuration.SUPPORTED_AUTH_SERVICE.facebook);
        //sModel.setPassword();

        String signingKey = dbHelper.getRemoteDao().retrieveServerSigner(sModel);

        String userPrivateKey = retrieveMyPrivateKey(id, token);

        String jwt = createJWT(userPrivateKey,name,signingKey);

        editor
                .putString(Configuration.preferences_jwt_key, jwt)
                .putString(Configuration.authServiceKey, Configuration.SUPPORTED_AUTH_SERVICE.facebook.name())
                .putString(Configuration.userIdKey, userEmail)
                .commit();

        //boolean loggedin = LoginManager.login(getActivity(),id,jwt,address,sModel,wallet,Configuration.SUPPORTED_AUTH_SERVICE.facebook);

        updateAuthentication(loggedin);
        */

    }


    private String createJWT(String id, String subject , String signingKey){

        JWTManager mn =  new JWTManager(signingKey);
        long expiration = JWTManager.incrementDate( new Date(),Configuration.expiration_24_hours).getTime();
        String jwt =  mn.createJWT( id,"mtini", subject, expiration );
        //System.out.println("jwt : "+jwt);
        Log.i(TAG,jwt);

        return jwt;
    }


    /**
     *
     * Uses bitcoin signing for sawtooth persistence verification.
     * Query db and if db is empty create an entry.
     *
     * */
    private String retrieveMyPrivateKey(String id, String token) {

        String privateKeyHex =  dbHelper.getPrivateKey(id);

        if(privateKeyHex==null) {
            ECKey privateKey = new ECKey(new SecureRandom());
            privateKeyHex = privateKey.getPrivateKeyAsHex();
            privateKeyHex = dbHelper.updatePrivateKey(id, privateKeyHex);
        }

        return privateKeyHex;
    }

    private boolean isLoggedIn(AccessToken accessToken) {
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        Log.i(TAG,"FB isLoggedIn = "+isLoggedIn);
        System.out.println("FB isLoggedIn = "+isLoggedIn);

        if(accessToken!=null) {
            try {
                setTokenToSession(accessToken, null);
            } catch (RemoteDAO.RemoteDAOException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
                isLoggedIn = false;
            }
        }
        return isLoggedIn;
    }

    private void showError(String message){
        dismissDialog();
        try {
            minorDialog = DialogUtils.startErrorDialog(getActivity(), message);
        }catch(Exception e){
            Log.e(TAG, e.getMessage(),e);
        }
    }

    private void showProcessDialog(String message){
        //dismissDialog();
        try {
            minorDialog = DialogUtils.startProgressDialog(getActivity(),message);
        }catch(Exception e){
            Log.e(TAG, e.getMessage(),e);
        }
    }

    private void dismissDialog(){
        if(minorDialog !=null) minorDialog.dismiss();
    }

    public static class LoginManager{
        public static boolean login(Context context, String id, String jwt, String address, SecurityModel sModel, Wallet wallet,Configuration.SUPPORTED_AUTH_SERVICE authService){
            String walletJson = new Gson().toJson(wallet);
            Configuration.configuredPreferences(context)
                    .edit()
                    .putString(Configuration.preferences_jwt_key, jwt)
                    .putString(Configuration.userIdKey,id )
                    .putString(Configuration.walletAddressKey, address)
                    .putString(EATLoginFragment.ARG_EAT_LOGIN_PHONENUMBER, sModel.getPhoneNumber())
                    .putString(EATLoginFragment.ARG_EAT_LOGIN_EMAIL, sModel.getEmail())
                    .putString(Configuration.authServiceKey, authService.name())
                    .putString(address,walletJson)
                    .commit();
            return true;
        }

        public static boolean logout(Context context){
            Configuration.configuredPreferences(context)
                    .edit()
                    .putString(Configuration.preferences_jwt_key, null)
                    .putString(Configuration.userIdKey,null )
                    .putString(Configuration.walletAddressKey, null)
                    .putString(EATLoginFragment.ARG_EAT_LOGIN_PHONENUMBER, null)
                    .putString(EATLoginFragment.ARG_EAT_LOGIN_EMAIL, null)
                    .putString(Configuration.authServiceKey, Configuration.SUPPORTED_AUTH_SERVICE.none.name()
                    ).commit();
            return true;
        }

        public static boolean checkIsLogged(Context context, SecurityModel sModel) throws Wallet.WalletException {
            //find jwt and wallet else false
            String calcAddress = Wallet.generateWalletAddress(sModel);
            //Assume it hasnt expired. Server will check.
            String jwt = Configuration.preferences(context).getString(Configuration.preferences_jwt_key, null);
            String address = Configuration.preferences(context).getString(Configuration.walletAddressKey, null);
            String walletJson = Configuration.preferences(context).getString(address, null);

            if(calcAddress.equalsIgnoreCase(address) && walletJson!=null){
                return true;
            }
            return false;
        }
    }
}
