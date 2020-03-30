package com.ptek.android.auth;

import com.prelimtek.utils.crypto.dao.CryptoDAOInterface;

public interface AuthLocalDAOInterface <T1,T2>extends CryptoDAOInterface.LocalDAO<T1,T2> {

     //T2 retrieveMyWallet(String address, T1 sModel, @Nullable String... jwt) throws LocalAuthException ;

     //void updateWallet(String address, String oldAddress, T1 sModel, T2 wallet, String action) throws Exception ;

    //T2 getMyWallet(String address, T1 securityModel)throws LocalAuthException;

    public class LocalAuthException extends Exception{
        public LocalAuthException(String s){super(s);}
        public LocalAuthException(Throwable e){super(e);}
    }
}
