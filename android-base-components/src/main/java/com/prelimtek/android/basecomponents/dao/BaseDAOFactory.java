package com.prelimtek.android.basecomponents.dao;

public abstract class BaseDAOFactory implements BaseDAOInterface {

    public abstract  BaseDAOInterface getLocalDao();

    public abstract  BaseDAOInterface getRemoteDao();

}
