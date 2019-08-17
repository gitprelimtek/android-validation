package io.mtini.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.prelimtek.android.basecomponents.Configuration;
import com.prelimtek.utils.crypto.Wallet;

import org.bitcoinj.wallet.Protos;
import org.json.JSONObject;

import io.mtini.android.view.component.EATLoginFragment;

public abstract class AbstractDAO {

    String host;
    String apiKey_Name;
    Context context ;
    Configuration config;

    AbstractDAO() {}
    public AbstractDAO(Context _context) {
        context =_context;
        config =Configuration.configuredPreferences(_context);
        //host = Configuration.remotehost ;
        host=config.remoteHostUrl;
        apiKey_Name =Configuration.apiKey_Name;
    }

    //TODO this should be created upon signin and stored in temporary cache. Therefore this should be a retrieve from cache
    public String retrieveAPIKeyFromPrefs() throws AbstractDAOException {
        //SharedPreferences pref = context.getApplicationContext().getSharedPreferences(Configuration.SERVER_SIDE_PREFERENCES_TAG, Context.MODE_PRIVATE);
        //String apikey  = pref.getString(Configuration.preferences_jwt_key, null);
        String apikey = config.apikey;
        if(apikey==null)throw new AbstractDAOException("Apikey cannot be null");
        return apikey;
    }

    public void setAPIKeyToPrefs(String apikey){
        //SharedPreferences pref = context.getApplicationContext().getSharedPreferences(Configuration.SERVER_SIDE_PREFERENCES_TAG, Context.MODE_PRIVATE);

        config.edit()
                .putString(Configuration.preferences_jwt_key, apikey)
                .commit();
    }

    public Wallet getMyAuthedWallet() {


        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(Configuration.SERVER_SIDE_PREFERENCES_TAG, Context.MODE_PRIVATE);
        String address = pref.getString(Configuration.walletAddressKey,null);

        String json = pref.getString(address,null);
        if(json==null)return null;
        return new Gson().fromJson(json,Wallet.class);
    }

    public  static class AbstractDAOException extends Exception{
        public AbstractDAOException(String s){
            super(s);
        }
    }

}
