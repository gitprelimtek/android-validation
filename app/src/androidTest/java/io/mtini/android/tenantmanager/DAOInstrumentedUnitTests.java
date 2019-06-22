package io.mtini.android.tenantmanager;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import android.support.test.InstrumentationRegistry;

import junit.framework.Assert;

import java.util.List;

import io.mtini.model.EstateModel;
import io.mtini.model.RemoteDAO;

@RunWith(AndroidJUnit4.class)
public class DAOInstrumentedUnitTests {

    RemoteDAO dbHelper = null;

    @Before
    public void init(){

        Context appContext = InstrumentationRegistry.getTargetContext();

        dbHelper = RemoteDAO
                .builder(appContext);

        try {
           // dbHelper.open();
        } catch (Exception e) {
           e.printStackTrace();
        }

    }


    @Test
    public void testRetrieveEstateList() throws Exception {

        List<EstateModel> estates = dbHelper.getMyEstateList();

        Assert.assertTrue(estates!=null);

        System.out.println(((List) estates).size());

    }

    @Test
    public void testInsertNewData(){

    }

    @After
    public void cleanup(){
        //delete all added data.
    }
}
