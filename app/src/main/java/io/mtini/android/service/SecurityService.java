package io.mtini.android.service;

import android.app.Dialog;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.Date;

import com.prelimtek.android.basecomponents.Configuration;

import com.prelimtek.android.crypto.Wallet;
import io.mtini.model.AppDAO;
import io.mtini.model.RemoteDAO;
import io.mtini.model.RemoteDAOListener;
import com.prelimtek.android.crypto.SecurityModel;
import io.mtini.proto.EATRequestResponseProtos;
import com.prelimtek.android.crypto.JWTManager;
import io.mtini.android.view.component.EATLoginFragment;

public class SecurityService  extends IntentService {

    public static String TAG = SecurityService.class.getSimpleName();

    public SecurityService(){
        super("EAT Intent Security Service");
    }

    public static final String VERIFY_LOCAL_TOKEN_EXPIRATION_INTENT = "EAT_CheckLocalTokenExpiration";
    public static final String AUTHENTICATE_TOKEN_AS_EMAIL_TEXT_INTENT = "EAT_EmailTextAuthentication";
    public static final String REQUEST_EMAIL_TEXT_TOKEN_INTENT = "EAT_RequestEmailTextToken";
    public static final String RETRIEVE_MY_LOCAL_ENCRYPTED_WALLET_INTENT ="EAT_RetrieveMyLocalEncryptedWalletIntent";
    public static final String UPDATE_MY_WALLET_INTENT = "EAT_UpdateMyWalletIntent";
    public static final String CREATE_NEW_WALLET_INTENT = "EAT_CreateNewWalletIntent";
    public static final String LOGOUT_INTENT = "EAT_LogOutIntent";
    
    public static final String SECURITY_MODEL_KEY = "AppSecurityModelKey";
    public static final String DEPRECATED_SECURITY_MODEL_KEY = "DeprecatedAppSecurityModelKey";
    public static final String WALLET_KEY = "MyWalletKey";
    public static final String SECURITY_SERVICE_KEY = "EAT_SecurityServiceKey";
    public static final String SECURITY_SERVICE_CALLBACK_MESSAGE_KEY = "SecurityMessageKey";
    public static final String SECURITY_SERVICE_CALLBACK_INTENT_KEY = "EAT_SecurityCallbackIntentKey";

    public static final String SECURITY_SERVICE_CALLBACK_ACTION = "EAT_SecurityCallbackAction";

    public static final String SECURITY_SERVICE_CALLBACK_ERROR_INTENT = "EAT_SecurityErrorIntent";
    public static final String SECURITY_SERVICE_CALLBACK_SUCCESS_INTENT = "EAT_SecuritySuccessIntent";
    public static final String SECURITY_SERVICE_CALLBACK_VERIFY_TOKEN_INTENT = "EAT_SecurityVerifyTokenAfterRequestOkIntent";
    public static final String SECURITY_SERVICE_CALLBACK_CREATE_NEW_WALLET_INTENT ="EAT_SecurityCreateNewWalletDueToNullIntent";
    public static final String SECURITY_SERVICE_CALLBACK_UNENCRYPT_WALLET_INTENT = "EAT_SecurityUnencyptWalletIntent";

    private AppDAO dbHelper;
    private Dialog init_dialog;

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getBaseContext();

        try {
            dbHelper =  AppDAO.builder().open(context);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String intentId = intent.getStringExtra(SECURITY_SERVICE_KEY);
        SecurityModel sModel = (SecurityModel)intent.getSerializableExtra(SECURITY_MODEL_KEY);
        if(sModel==null){
            showError("Security model has not been set. Please login!");
            return;
        }

        //this.getBaseContext().

        switch (intentId){
            case REQUEST_EMAIL_TEXT_TOKEN_INTENT:
                requestEmailTextToken(sModel);
                break;
            case AUTHENTICATE_TOKEN_AS_EMAIL_TEXT_INTENT:
                authenticateTokenEmailText(sModel);
                break;
            case VERIFY_LOCAL_TOKEN_EXPIRATION_INTENT:
                verifyTokenExpiration(sModel,null);
                break;
            case RETRIEVE_MY_LOCAL_ENCRYPTED_WALLET_INTENT:
                retrieveMyLocalWallet(sModel);
                break;
            case UPDATE_MY_WALLET_INTENT:
                updateMyWallet(sModel,intent);
                break;
            case CREATE_NEW_WALLET_INTENT:
                createNewWallet(sModel);
                //updateMyWallet(sModel,intent);
                break;
            case LOGOUT_INTENT:
                logout();
                //updateMyWallet(sModel,intent);
                break;
        }

    }




    /**
     * This is the sign in method which requires a valid email/phone
     * to which a message containing a token/code is sent.
     * Responds with a userId based on email/phonenumber and a BigInt
     * which is a privatekey generator*/
    private void requestEmailTextToken(final SecurityModel sModel) {
        RemoteDAOListener listener = new
                RemoteDAOListener() {
                    @Override
                    public void onRequestComplete(EATRequestResponseProtos.EATRequestResponse.Response response) {
                        try {
                            if(response==null || response.getJsonResponse()==null)
                                throw new RemoteDAO.RemoteDAOException("An server side error occurred. Remote service not reachable.");


                            JSONObject json = new JSONObject(response.getJsonResponse());
                            if(json.has("result")) {
                                String result = json.getString("result");
                                if (result.equalsIgnoreCase("sent")) {

                                    prepTokenVerificationUI(sModel);

                                }
                            }else if(json.has("error")){
                                showError(json.getString("error"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (RemoteDAO.RemoteDAOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,e.getMessage(),e);
                        showError("Token could not be generated. Please try again.");
                        //DialogUtils.startErrorDialog(context,e.getMessage());
                    }
                };

        try {
            dbHelper.getRemoteDao().requestEmailTextToken(sModel,listener);
        } catch (RemoteDAO.RemoteDAOException e) {
            Log.e(TAG,e.getMessage(),e);
            showError("Token could not be generated. Please try again.");
        }
    }


    private void prepTokenVerificationUI(final SecurityModel smodel) {

        Intent intent = new Intent();
        intent.setAction(SECURITY_SERVICE_CALLBACK_ACTION);
        intent.putExtra(SECURITY_SERVICE_CALLBACK_INTENT_KEY, SECURITY_SERVICE_CALLBACK_VERIFY_TOKEN_INTENT);
        intent.putExtra(SECURITY_SERVICE_CALLBACK_MESSAGE_KEY,"success");
        intent.putExtra(SECURITY_MODEL_KEY,smodel);
        sendBroadcast(intent);

    }


    private void prepCreateNewWalletUI(final SecurityModel smodel) {

        Intent intent = new Intent();
        intent.setAction(SECURITY_SERVICE_CALLBACK_ACTION);
        intent.putExtra(SECURITY_SERVICE_CALLBACK_INTENT_KEY, SECURITY_SERVICE_CALLBACK_CREATE_NEW_WALLET_INTENT);
        intent.putExtra(SECURITY_SERVICE_CALLBACK_MESSAGE_KEY,"create wallet");
        intent.putExtra(SECURITY_MODEL_KEY,smodel);
        sendBroadcast(intent);

    }

    private void prepUnencryptWalletUI(SecurityModel sModel, Wallet wallet) {

        Intent intent = new Intent();
        intent.setAction(SECURITY_SERVICE_CALLBACK_ACTION);
        intent.putExtra(SECURITY_SERVICE_CALLBACK_INTENT_KEY, SECURITY_SERVICE_CALLBACK_UNENCRYPT_WALLET_INTENT);
        intent.putExtra(SECURITY_SERVICE_CALLBACK_MESSAGE_KEY,"unencrypt wallet");
        intent.putExtra(SECURITY_MODEL_KEY,sModel);
        intent.putExtra(WALLET_KEY,wallet);
        sendBroadcast(intent);

    }

    /**
     * Token received from calling requestEmailTextToken() is passed to the
     * server using a combination of email/phonenumber and token in order
     * to receive an expirable security/api key.
     * */
    private void authenticateTokenEmailText(SecurityModel sModel) {

        try {

            String address = Wallet.generateWalletAddress(sModel);

            String signingKey = dbHelper.getRemoteDao().retrieveServerSigner(sModel);

            String jwt = createJWT( sModel.getEmail(),sModel.getToken(), signingKey,5000);

            //dbHelper.getRemoteDao().setAPIKeyToPrefs(jwt);
            //Thread.sleep(2000);//wait 1 seconds for jwt to write

            Wallet wallet = null;

            try {

                 wallet = dbHelper.getRemoteDao().retrieveMyWallet(address, sModel, jwt);//retrieveMyPrivateKey(userId, token);
            }catch(Exception e){
                if(e.getMessage().contains("Wallet repo search returned NULL")){
                    wallet = null;
                }
                else{throw e;}
            }

            if(wallet!=null ){

                if(wallet.isEncrypted())
                    prepUnencryptWalletUI(sModel, wallet);
                else {
                    sModel.setWalletAddress(address);
                    showSuccess(sModel, wallet, "Successfully retrieved remote unencrypted wallet");
                }
            }else{

                prepCreateNewWalletUI(sModel);
            }

        } catch (Exception e) {
            //e.printStackTrace();
            Log.e(TAG,e.getMessage(),e);
            showError(e.getMessage());
        }
    }



    private void verifyTokenExpiration(SecurityModel sModel, Wallet wallet) {

        try {
            //TODO does not call backend if not available
            //JWTManager.parseJWT()
            String address = Wallet.generateWalletAddress(sModel);
            sModel.setWalletAddress(address);
            showSuccess(sModel, wallet,"Data loaded");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
            showError(e.getMessage());
        }

    }

    /*private void setTokenToSession(AccessToken accessToken, JSONObject object) throws RemoteDAO.RemoteDAOException, Wallet.WalletException {

        //get signin key from server
        String id = accessToken.getUserId();
        String token = accessToken.getToken();

        SharedPreferences pref = this.getApplicationContext().getSharedPreferences(Configuration.SERVER_SIDE_PREFERENCES_TAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        //editor.clear();//just incase there was old data
        //editor.apply();//just incase
        String userEmail = null;
        String name = null;
        String rawData = null;
        if(object!=null) {
            userEmail = object.optString("email", "null");
            //String userGender = object.optString("gender");
            //String userMobile = object.optString("mobile_phone");
            //String userBirthdate = object.optString("birthday");
            name = object.optString("name");
            rawData = object.toString();
            editor.putString("email", userEmail);
            editor.putString("name", name);
        }

        editor.putString("id", id);

        editor.putString("rawData", rawData);

        SecurityModel sModel = new SecurityModel();
        sModel.setEmail(userEmail);
        sModel.setUserName(id);
        sModel.setToken(token);

        String signingKey = dbHelper.getRemoteDao().retrieveServerSigner(sModel);

        Wallet wallet = retrieveMyWallet(sModel);
        String userPrivateKey = wallet.decryptPrivateKeyHex(sModel.getPassword());

        String jwt = createJWT(userPrivateKey,name,signingKey);

        editor.putString(Configuration.preferences_jwt_key, jwt);

        editor.commit();
    }
    private String createJWT(String id, String subject , String signingKey){
        JWTManager mn =  new JWTManager(signingKey);
        long expiration = JWTManager.incrementHours( new Date(),Configuration.expiration_24_hours ).getTime();
        String jwt =  mn.createJWT(id,"mtini",subject, expiration );

        System.out.println("jwt : "+jwt);
        Log.i(TAG,jwt);

        return jwt;
    }

    private void recreateMyWallet(SecurityModel newModel,SecurityModel oldModel, Wallet oldWallet) throws Wallet.WalletException, RemoteDAO.RemoteDAOException {

        if(newModel.hashCode()==oldModel.hashCode()){return;}

        String oldAddress = Wallet.generateWalletAddress(oldModel);

        Wallet newWallet = oldWallet.updateWallet(newModel.getPassword(),oldModel.getPassword(),true);

        String address = Wallet.generateWalletAddress(newModel);

        dbHelper.updateWallet(address,oldAddress,newModel, newWallet, "update");
    }
    */



    private Wallet createNewWallet(SecurityModel sModel) {

        Wallet wallet = new Wallet<>(sModel.getEmailHash(),sModel.getPhoneNumberHash(),null);

        try {

            String address = Wallet.generateWalletAddress(sModel);

            dbHelper.getLocalDao().updateWallet(address,null,sModel,wallet,"add");

            wallet.encrypPrivateKeyHex(sModel.getPassword());

            String signingKey = dbHelper.getRemoteDao().retrieveServerSigner(sModel);

            String jwt = createJWT( sModel.getEmail(),address, signingKey,5000);

            dbHelper.getRemoteDao().updateWallet(address,null, sModel, wallet, "add", jwt);

            sModel.setWalletAddress(address);
            showSuccess(sModel,wallet,"A new wallet was created successfully.");

        } catch (Wallet.WalletException | RemoteDAO.RemoteDAOException e) {
            Log.e(TAG,e.getMessage(),e);
            showError(e.getMessage());
        }

        return wallet;
    }

    //TODO remove because Security service is only concerned with backend authentication, delegate LoginManager to handle login and logout
    @Deprecated
    private void logout() {

        Configuration.configuredPreferences(getApplicationContext())
                .edit()
                .putString(Configuration.preferences_jwt_key, null)
                .putString(Configuration.userIdKey,null )
                .putString(Configuration.walletAddressKey, null)
                .putString(EATLoginFragment.ARG_EAT_LOGIN_PHONENUMBER, null)
                .putString(EATLoginFragment.ARG_EAT_LOGIN_EMAIL, null)
                .putString(Configuration.authServiceKey, null
                ).commit();
    }


    private void updateMyWallet(SecurityModel newSModel,Intent intent){
        try {
            SecurityModel oldSModel = (SecurityModel) intent.getSerializableExtra(DEPRECATED_SECURITY_MODEL_KEY);
            Wallet curwallet = (Wallet) intent.getSerializableExtra(WALLET_KEY);
            Wallet newWallet = updateMyWallet(newSModel, oldSModel, curwallet);

            if(newWallet==null){
                //newWallet = createNewWallet(newSModel);
                throw new Exception("Wallet update failed");
            }else{
                String address = Wallet.generateWalletAddress(newSModel);
                newSModel.setWalletAddress(address);
                showSuccess(newSModel, newWallet, "Wallet updated successfully.");
            }
        }catch(Exception e){
            Log.e(TAG,e.getMessage(),e);
            showError(e.getMessage());
        }
    }

    private Wallet updateMyWallet(SecurityModel newModel,SecurityModel oldModel, Wallet oldWallet) throws Exception {

        Wallet newWallet = null;
        if(newModel==null)throw new Exception("unexpected security error. SecurityModel is null.");

        if(newModel !=null && oldWallet!=null) {
            if (newModel.hashCode() == oldModel.hashCode()) {
                //return oldWallet;
            }

            String address = Wallet.generateWalletAddress(newModel);

            String oldAddress = Wallet.generateWalletAddress(oldModel);

            newWallet = oldWallet.updateWallet(newModel.getPassword(), oldModel.getPassword(), false);

            dbHelper.getRemoteDao().updateWallet(address,oldAddress, newModel, newWallet, "update");

            if(newWallet.isEncrypted()){
                newWallet.decryptPrivateKeyHex(newModel.getPassword());
            }

            dbHelper.getLocalDao().updateWallet(address,oldAddress, newModel, newWallet, "update");

        }else {
            throw new Exception("Update cannot be performed on a Null Wallet object or Null security object.");
        }

        return newWallet;
    }


    private Wallet retrieveMyLocalWallet(SecurityModel securityModel) {

        String address = null;
        try {
            address = Wallet.generateWalletAddress(securityModel);
        } catch (Wallet.WalletException e) {
            Log.e(TAG, e.getMessage(),e);
        }

        Wallet wallet = dbHelper.getLocalDao().getMyWallet(address, securityModel);

        if(wallet!=null) {
            securityModel.setWalletAddress(address);
            showSuccess(securityModel, wallet, "Local wallet found");
        }
        else
            showError("Local wallet not found");

        return wallet;
    }

    private String createJWT(String id, String subject , String signingKey, int expiration_milliseconds){
        JWTManager mn =  new JWTManager(signingKey);
        long expiration = JWTManager.incrementMillis(new Date(),expiration_milliseconds).getTime();
        String jwt =  mn.createJWT(id,"mtini",subject, expiration );

        //System.out.println("jwt : "+jwt);
        Log.i(TAG,jwt);

        return jwt;
    }


    public  void showError(String message){
        Intent intent = new Intent();
        intent.setAction(SECURITY_SERVICE_CALLBACK_ACTION);
        intent.putExtra(SECURITY_SERVICE_CALLBACK_INTENT_KEY, SECURITY_SERVICE_CALLBACK_ERROR_INTENT);
        intent.putExtra(SECURITY_SERVICE_CALLBACK_MESSAGE_KEY,message);
        sendBroadcast(intent);
    }

    public  void showSuccess(SecurityModel sModel, Wallet wallet ,String message){
        //set token

        Intent intent = new Intent();
        intent.setAction(SECURITY_SERVICE_CALLBACK_ACTION);
        intent.putExtra(SECURITY_SERVICE_CALLBACK_INTENT_KEY, SECURITY_SERVICE_CALLBACK_SUCCESS_INTENT);
        intent.putExtra(SECURITY_SERVICE_CALLBACK_MESSAGE_KEY,message);
        intent.putExtra(SECURITY_MODEL_KEY,sModel);
        intent.putExtra(WALLET_KEY,wallet);
        sendBroadcast(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
