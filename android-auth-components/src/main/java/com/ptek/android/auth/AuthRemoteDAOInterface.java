package com.ptek.android.auth;

import androidx.annotation.Nullable;

import com.prelimtek.android.basecomponents.dao.RemoteDAOListener;
import com.prelimtek.utils.crypto.dao.CryptoDAOInterface;

import java.io.IOException;

public interface AuthRemoteDAOInterface<T1,T2> extends CryptoDAOInterface.RemoteDAO<T1,T2> {

    //crypto
    //Wallet retrieveMyWallet(String address, AuthModel sModel, @Nullable  String ... jwt) throws RemoteAuthException ;
    //crypto
    //boolean updateWallet(String address, String oldAddress, AuthModel sModel, Wallet wallet, String action, @Nullable String ... jwt) throws RemoteAuthException ;
    /**Explicitly expect RemoteDAOListener*/
    void requestEmailTextToken(T1 sModel, RemoteDAOListener<?> listener) throws RemoteAuthException;

    String retrieveServerSigner(T1 sModel) throws RemoteAuthException;

    public class RemoteAuthException extends IOException {
        public RemoteAuthException(String s){super(s);}
        public RemoteAuthException(Throwable e){super(e);}
    }



}
