package com.prelimtek.android.crypto.dao;

import com.prelimtek.android.basecomponents.dao.BaseDAOInterface;
import com.prelimtek.android.crypto.SecurityModel;
import com.prelimtek.android.crypto.Wallet;

import java.io.IOException;

public interface CryptoDAOInterface extends BaseDAOInterface {

    public String getPrivateKey(String id);

    public String updatePrivateKey(String id, String privateKey);

    public void updateWallet(String address, String oldAddress, SecurityModel securityModel, Wallet wallet, String action) throws IOException, Wallet.WalletException;

    public Wallet getMyWallet(String address, SecurityModel securityModel) throws IOException, Wallet.WalletException;

}
