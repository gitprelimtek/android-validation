package com.ptek.android.auth;

import android.support.annotation.Nullable;

import com.prelimtek.utils.crypto.Wallet;
import com.prelimtek.utils.crypto.dao.CryptoDAOInterface;

public interface AuthRemoteDAOInterface<T1,T2> extends CryptoDAOInterface.RemoteDAO<T1,T2> {

    //crypto
    //Wallet retrieveMyWallet(String address, AuthModel sModel, @Nullable  String ... jwt) throws RemoteAuthException ;
    //crypto
    //boolean updateWallet(String address, String oldAddress, AuthModel sModel, Wallet wallet, String action, @Nullable String ... jwt) throws RemoteAuthException ;

    void requestEmailTextToken(T1 sModel, Object listener) throws RemoteAuthException;

    String retrieveServerSigner(T1 sModel) throws RemoteAuthException;

    public class RemoteAuthException extends Exception{
        public RemoteAuthException(String s){super(s);}
        public RemoteAuthException(Throwable e){super(e);}
    }



}
