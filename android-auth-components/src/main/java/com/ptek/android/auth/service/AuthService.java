package com.ptek.android.auth.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;
import android.util.Log;

import com.prelimtek.android.basecomponents.Configuration;
import com.prelimtek.android.basecomponents.dao.BaseDAOFactory;
import com.prelimtek.android.basecomponents.dao.RemoteDAOListener;
import com.prelimtek.utils.crypto.JWTManager;
import com.prelimtek.utils.crypto.Wallet;
import com.ptek.android.auth.AuthLocalDAOInterface;
import com.ptek.android.auth.AuthModel;
import com.ptek.android.auth.AuthRemoteDAOInterface;
import com.ptek.android.auth.component.PtekAuthLoginFragment;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

import io.mtini.proto.RequestResponseProtos;

public class AuthService extends IntentService {

    public static String TAG = AuthService.class.getSimpleName();

    BaseDAOFactory daoFactory = null;
    public AuthService(BaseDAOFactory _daoFactory){
        this();
        daoFactory = _daoFactory;
    }

    private  AuthService(){
        super("Ptek Auth Intent Service");
    }

    public static final String VERIFY_LOCAL_TOKEN_EXPIRATION_INTENT = "PtekAuth_CheckLocalTokenExpiration";
    public static final String AUTHENTICATE_TOKEN_AS_EMAIL_TEXT_INTENT = "PtekAuth_EmailTextAuthentication";
    public static final String REQUEST_EMAIL_TEXT_TOKEN_INTENT = "PtekAuth_RequestEmailTextToken";
    public static final String RETRIEVE_MY_LOCAL_ENCRYPTED_WALLET_INTENT ="PtekAuth_RetrieveMyLocalEncryptedWalletIntent";
    public static final String UPDATE_MY_WALLET_INTENT = "PtekAuth_UpdateMyWalletIntent";
    public static final String CREATE_NEW_WALLET_INTENT = "PtekAuth_CreateNewWalletIntent";
    public static final String LOGOUT_INTENT = "PtekAuth_LogOutIntent";
    
    public static final String SECURITY_MODEL_KEY = "AppAuthModelKey";
    public static final String DEPRECATED_SECURITY_MODEL_KEY = "DeprecatedAppAuthModelKey";
    public static final String WALLET_KEY = "MyWalletKey";
    public static final String SECURITY_SERVICE_KEY = "PtekAuth_SecurityServiceKey";
    public static final String SECURITY_SERVICE_CALLBACK_MESSAGE_KEY = "SecurityMessageKey";
    public static final String SECURITY_SERVICE_CALLBACK_INTENT_KEY = "PtekAuth_SecurityCallbackIntentKey";

    public static final String SECURITY_SERVICE_CALLBACK_ACTION = "PtekAuth_SecurityCallbackAction";

    public static final String SECURITY_SERVICE_CALLBACK_ERROR_INTENT = "PtekAuth_SecurityErrorIntent";
    public static final String SECURITY_SERVICE_CALLBACK_SUCCESS_INTENT = "PtekAuth_SecuritySuccessIntent";
    public static final String SECURITY_SERVICE_CALLBACK_VERIFY_TOKEN_INTENT = "PtekAuth_SecurityVerifyTokenAfterRequestOkIntent";
    public static final String SECURITY_SERVICE_CALLBACK_CREATE_NEW_WALLET_INTENT ="PtekAuth_SecurityCreateNewWalletDueToNullIntent";
    public static final String SECURITY_SERVICE_CALLBACK_UNENCRYPT_WALLET_INTENT = "PtekAuth_SecurityUnencyptWalletIntent";

    AuthLocalDAOInterface<AuthModel,Wallet> localDao;
    AuthRemoteDAOInterface<AuthModel,Wallet> remoteDao;
    //Dialog init_dialog;


    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getBaseContext();

        try {
            daoFactory =  (BaseDAOFactory)daoFactory.open(context);
            localDao = (AuthLocalDAOInterface<AuthModel,Wallet>)daoFactory.getLocalDao();
            remoteDao = (AuthRemoteDAOInterface<AuthModel,Wallet>)daoFactory.getRemoteDao();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String intentId = intent.getStringExtra(SECURITY_SERVICE_KEY);
        AuthModel sModel = (AuthModel)intent.getSerializableExtra(SECURITY_MODEL_KEY);
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
    private void requestEmailTextToken(final AuthModel sModel) {
        RemoteDAOListener listener = new
                RemoteDAOListener<RequestResponseProtos.RequestResponse.Response>() {
                    @Override
                    public void onRequestComplete(RequestResponseProtos.RequestResponse.Response response) {
                        try {
                            if(response==null || response.getJsonResponse()==null)
                                throw new AuthRemoteDAOInterface.RemoteAuthException("An server side error occurred. Remote service not reachable.");


                            JSONObject json = new JSONObject(response.getJsonResponse());
                            if(json.has("result")) {
                                String result = json.getString("result");
                                if (result.equalsIgnoreCase("sent")) {

                                    prepTokenVerificationUI(sModel);

                                }
                            }else if(json.has("error")){
                                showError(json.getString("error"));
                            }

                        } catch (Exception e) {
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
            remoteDao.requestEmailTextToken(sModel,listener);
        } catch (Throwable e) {
            Log.e(TAG,e.getMessage(),e);
            showError("Token could not be generated. Please try again.");
        }
    }


    private void prepTokenVerificationUI(final AuthModel smodel) {

        Intent intent = new Intent();
        intent.setAction(SECURITY_SERVICE_CALLBACK_ACTION);
        intent.putExtra(SECURITY_SERVICE_CALLBACK_INTENT_KEY, SECURITY_SERVICE_CALLBACK_VERIFY_TOKEN_INTENT);
        intent.putExtra(SECURITY_SERVICE_CALLBACK_MESSAGE_KEY,"success");
        intent.putExtra(SECURITY_MODEL_KEY,smodel);
        sendBroadcast(intent);

    }


    private void prepCreateNewWalletUI(final AuthModel smodel) {

        Intent intent = new Intent();
        intent.setAction(SECURITY_SERVICE_CALLBACK_ACTION);
        intent.putExtra(SECURITY_SERVICE_CALLBACK_INTENT_KEY, SECURITY_SERVICE_CALLBACK_CREATE_NEW_WALLET_INTENT);
        intent.putExtra(SECURITY_SERVICE_CALLBACK_MESSAGE_KEY,"create wallet");
        intent.putExtra(SECURITY_MODEL_KEY,smodel);
        sendBroadcast(intent);

    }

    private void prepUnencryptWalletUI(AuthModel sModel, Wallet wallet) {

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
    private void authenticateTokenEmailText(AuthModel sModel) {

        try {

            String address = Wallet.generateWalletAddress(sModel);

            String signingKey = remoteDao.retrieveServerSigner(sModel);

            String jwt = createJWT( sModel.getEmail(),sModel.getToken(), signingKey,5000);

            //dbHelper.getRemoteDao().setAPIKeyToPrefs(jwt);
            //Thread.sleep(2000);//wait 1 seconds for jwt to write

            Wallet wallet = null;

            try {

                 wallet = remoteDao.retrieveMyWallet(address, sModel, jwt);//retrieveMyPrivateKey(userId, token);
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



    private void verifyTokenExpiration(AuthModel sModel, Wallet wallet) {

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


    private Wallet createNewWallet(AuthModel sModel) {

        Wallet wallet = new Wallet<>(sModel.getEmailHash(),sModel.getPhoneNumberHash(),null);

        try {

            String address = Wallet.generateWalletAddress(sModel);

            localDao.updateWallet(address,null,sModel,wallet,"add");

            wallet.encrypPrivateKeyHex(sModel.getPassword());

            String signingKey = remoteDao.retrieveServerSigner(sModel);

            String jwt = createJWT( sModel.getEmail(),address, signingKey,5000);

            remoteDao.updateWallet(address,null, sModel, wallet, "add", jwt);

            sModel.setWalletAddress(address);
            showSuccess(sModel,wallet,"A new wallet was created successfully.");

        } catch (Exception e) {
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
                .putString(PtekAuthLoginFragment.ARG_EAT_LOGIN_PHONENUMBER, null)
                .putString(PtekAuthLoginFragment.ARG_EAT_LOGIN_EMAIL, null)
                .putString(Configuration.authServiceKey, null
                ).commit();
    }


    private void updateMyWallet(AuthModel newSModel,Intent intent){
        try {
            AuthModel oldSModel = (AuthModel) intent.getSerializableExtra(DEPRECATED_SECURITY_MODEL_KEY);
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

    private Wallet updateMyWallet(AuthModel newModel,AuthModel oldModel, Wallet oldWallet) throws Exception {

        Wallet newWallet = null;
        if(newModel==null)throw new Exception("unexpected security error. AuthModel is null.");

        if(newModel !=null && oldWallet!=null) {
            if (newModel.hashCode() == oldModel.hashCode()) {
                //return oldWallet;
            }

            String address = Wallet.generateWalletAddress(newModel);

            String oldAddress = Wallet.generateWalletAddress(oldModel);

            newWallet = oldWallet.updateWallet(newModel.getPassword(), oldModel.getPassword(), false);

            remoteDao.updateWallet(address,oldAddress, newModel, newWallet, "update");

            if(newWallet.isEncrypted()){
                newWallet.decryptPrivateKeyHex(newModel.getPassword());
            }

            localDao.updateWallet(address,oldAddress, newModel, newWallet, "update");

        }else {
            throw new Exception("Update cannot be performed on a Null Wallet object or Null security object.");
        }

        return newWallet;
    }


    private Wallet retrieveMyLocalWallet(AuthModel securityModel) {

        String address = null;
        Wallet wallet = null;
        try {
            address = Wallet.generateWalletAddress(securityModel);


             wallet = localDao.getMyWallet(address, securityModel);
        } catch (Wallet.WalletException | IOException e) {
            Log.e(TAG, e.getMessage(),e);
        }

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

    public  void showSuccess(AuthModel sModel, Wallet wallet ,String message){
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
