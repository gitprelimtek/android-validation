package io.mtini.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import org.bitcoinj.core.Sha256Hash;

import com.prelimtek.android.basecomponents.Configuration;

import com.prelimtek.utils.crypto.UserInterface;

import io.mtini.android.tenantmanager.BR;

public class SecurityModel extends BaseObservable implements UserInterface {

    @Bindable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPhoneNumberHash() {
        return phoneNumber==null?null:Sha256Hash.of(phoneNumber.getBytes()).toString();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        notifyPropertyChanged(BR.phoneNumber);
    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public String getEmailHash() {
        return email==null?null:Sha256Hash.of(email.getBytes()).toString();
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }

    @Bindable
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        notifyPropertyChanged(BR.userName);
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    @Bindable
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        notifyPropertyChanged(BR.token);
    }


    @Bindable
    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String address) {
        this.walletAddress = address;
        notifyPropertyChanged(BR.walletAddress);
    }

    @Bindable
    public Configuration.SUPPORTED_AUTH_SERVICE getAuthService() {
        return authService;
    }

    public void setAuthService(Configuration.SUPPORTED_AUTH_SERVICE authService) {
        this.authService = authService;
    }

    String phoneNumber,email, userName,password,token,walletAddress;

    Configuration.SUPPORTED_AUTH_SERVICE authService;

}
