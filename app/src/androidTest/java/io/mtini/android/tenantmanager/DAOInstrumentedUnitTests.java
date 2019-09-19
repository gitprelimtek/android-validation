package io.mtini.android.tenantmanager;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.prelimtek.android.customcomponents.NotesModel;

import java.util.List;

import io.mtini.model.EstateModel;
import io.mtini.model.AppDAO;

@RunWith(AndroidJUnit4.class)
public class DAOInstrumentedUnitTests {

    AppDAO dbHelper = null;

    @Before
    public void init() throws Exception {

        Context appContext = InstrumentationRegistry.getTargetContext();

        dbHelper = AppDAO.builder().open(appContext);

        cleanup();

        insertNewEstate(new EstateModel("id1","estate1","1st street",EstateModel.TYPE.apartment,"0708195485","note 1"));

        updateEstate(new EstateModel("id1","estate1","1st street",EstateModel.TYPE.apartment,"0708195485","note 1"));

        updateEstate(new EstateModel("id1","estate1","1st street",EstateModel.TYPE.apartment,"0708195485","note 3"));

        updateEstate(new EstateModel("id1","estate2","1st street",EstateModel.TYPE.apartment,"0708195485","note 4"));

        updateEstate(new EstateModel("id1","estate2","1st street",EstateModel.TYPE.apartment,"0708195485","note 5"));

    }

    private void insertNewEstate(EstateModel estateModel) throws Exception{

        dbHelper.getLocalDao().addEstate(estateModel);

    }

    private void updateEstate(EstateModel estateModel) throws Exception{

        dbHelper.getLocalDao().updateEstate(estateModel);

    }

    @Test
    public void testLocalRetrieveEstateList() throws Exception {

        List<EstateModel> estates = dbHelper.getLocalDao().getMyEstateList();

        assert estates!=null;

        System.out.println(((List) estates).size());

    }

    //@Test
    public void testRemoteRetrieveEstateList() throws Exception {

        List<EstateModel> estates = dbHelper.getRemoteDao().getMyEstateList();

        assert estates!=null;

        System.out.println(((List) estates).size());

    }

    @Test
    public void testLocalGetAll() throws Exception {

        List<EstateModel> estates = dbHelper.getLocalDao().getMyEstateList();
        Log.i("","estate size : "+estates.size());
        for(EstateModel estate : estates){
            List<NotesModel> notes = dbHelper.getLocalDao().getNotes(estate.getId(),null,System.currentTimeMillis(),20,0);

            Log.i("","note count : "+notes.size());
            assert notes.size() == 4;

        }


        assert estates!=null;

        System.out.println(((List) estates).size());

    }

    @Test
    public void testInsertNewData(){

    }

    @After
    public void cleanup(){

        //delete all added data.
        List<EstateModel> estates = dbHelper.getLocalDao().getMyEstateList();
        estates.forEach( e -> dbHelper.getLocalDao().deleteEstate(e) );
        /*
        for(EstateModel estate: estates){
            dbHelper.getLocalDao().deleteEstate(estate);
        }
        */

    }
}
