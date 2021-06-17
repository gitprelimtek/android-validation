package com.prelimtek.utils.crypto.dao;

import java.io.IOException;
import java.io.Serializable;

public interface CryptoDAOInterface  extends Serializable {

     public interface LocalCryptoDAO <T1,T2>extends Serializable{
         String getPrivateKey(String id);

         String updatePrivateKey(String id, String privateKey);

         void updateWallet(String address, String oldAddress, T1 securityModel, T2 wallet, String action) throws IOException;

         T2 getMyWallet(String address, T1 securityModel) throws IOException;


     }

     public interface RemoteCryptoDAO<T1,T2>extends Serializable{

         T2 retrieveMyWallet(String address, T1 sModel,  String jwt) throws Exception ;

         boolean updateWallet(String address, String oldAddress, T1 sModel, T2 wallet, String action,  String jwt) throws Exception;

         T1 requestRemoteCustomerDetails(T1 user,String jwt) throws Exception;
     }
}
