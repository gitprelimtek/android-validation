package com.prelimtek.utils.crypto.dao;

import java.io.IOException;

public interface CryptoDAOInterface<T1,T2>  {

    public String getPrivateKey(String id);

    public String updatePrivateKey(String id, String privateKey);

    public void updateWallet(String address, String oldAddress, T1 securityModel, T2 wallet, String action) throws IOException;

    public T2 getMyWallet(String address, T1 securityModel) throws IOException;

}
