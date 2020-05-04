package com.prelimtek.android.basecomponents.dao;

import android.content.Context;
import android.content.SharedPreferences;

public abstract class BaseDAOFactory <T> implements BaseDAOInterface {

    public enum TYPE{LOCAL,REMOTE,BOTH};

    public static final String DATA_ACCESS_FACTORY_CLASSNAME="DATA_ACCESS_FACTORY_CLASS";
    public static final String DATA_ACCESS_PREFERENCES="DATA_ACCESS_PREFERENCES";


    protected Context context = null;

    /**
     * Assumes a DAOFactory has been registered ahead of time using registerInSharedPreference method below.
     * */
    public static BaseDAOFactory instance(Context context) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

            SharedPreferences prefs = context.getSharedPreferences(DATA_ACCESS_PREFERENCES,Context.MODE_PRIVATE);
            String className = prefs.getString(DATA_ACCESS_FACTORY_CLASSNAME,null);

            Class<? extends BaseDAOFactory> ret = null;
            if(className!=null){
                ret = (Class<? extends BaseDAOFactory>)Class.forName(className);
            }else{
                //exception ?
                return null;
            }

            return ret.newInstance();
    }

    /**
     * This should be called in MainActivity to register this DAOFactory
     * */
    public static void registerInSharedPreference(Context context, Class<? extends BaseDAOFactory> _class) {
        SharedPreferences prefs = context.getSharedPreferences(DATA_ACCESS_PREFERENCES,Context.MODE_PRIVATE);
        prefs.edit().putString(DATA_ACCESS_FACTORY_CLASSNAME,_class.getName());
    }

    public abstract T  open(TYPE type) throws Exception;

    public abstract  BaseDAOInterface getLocalDao();

    public abstract  BaseDAOInterface getRemoteDao();

}
