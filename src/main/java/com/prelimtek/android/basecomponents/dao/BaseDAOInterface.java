package com.prelimtek.android.basecomponents.dao;

import android.content.Context;

public interface BaseDAOInterface {

    public BaseDAOInterface open(Context context)throws Exception;

    public BaseDAOInterface open()throws Exception;

    public void setContext(Context context);

    public void close();
}
