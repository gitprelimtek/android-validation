package com.prelimtek.utils.crypto;

import java.io.Serializable;

public interface UserInterface  extends Serializable,Cloneable{

    public String getUserName();
    public String getEmail();
    public String getPhoneNumber();

}
