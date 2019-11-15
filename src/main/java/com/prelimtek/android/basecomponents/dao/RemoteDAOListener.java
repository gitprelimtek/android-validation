package com.prelimtek.android.basecomponents.dao;

public interface RemoteDAOListener <T>{

    void onRequestComplete(T response);

    void onError(Throwable e);

}
