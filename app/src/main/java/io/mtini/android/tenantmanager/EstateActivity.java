package io.mtini.android.tenantmanager;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import io.mtini.android.adaptor.EstateListBindingAdapter;
import io.mtini.android.tenantmanager.dialog.EditEstateDetailsDialogFragment;
import io.mtini.android.tenantmanager.dialog.LoginDialogFragment;
import io.mtini.model.AppDAO;
import io.mtini.model.AppDAOInterface;
import io.mtini.model.EstateModel;
import com.prelimtek.android.picha.ImagesModel;

//TODO remove and also remove view activity_estates.xml - has been replaced with EstateDetailsActivity
public class EstateActivity extends AppCompatActivity
        implements EditEstateDetailsDialogFragment.OnEditEstateListener
{

    public static String TAG = Class.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_estates);

        AppDAOInterface dbHelper = null;
        try {
            dbHelper = AppDAO.builder().open(this);
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }

        ListView listView = (ListView) findViewById(R.id.estateslistView);

        ObservableList<EstateModel> estates = new ObservableArrayList<EstateModel>();
        estates.addAll(dbHelper.getMyEstateList());

        int count = estates.size();
        TextView estateSizeTxtView = (TextView)findViewById(R.id.textEstateCount);
        estateSizeTxtView.setText("You manage "+count+" estate"+(count>1?"s.":"."));


        ////START NB this works fine
        final AppDAOInterface inDbHelper = dbHelper;
        EstateListBindingAdapter dataAdapter = new EstateListBindingAdapter(this,
                estates,
                new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(EstateActivity.this, EstateDetailsActivity.class);
                                EstateModel tag = (EstateModel)v.getTag();
                                String[] imageIds = inDbHelper.getImageIdList(tag.getId());
                                ImagesModel imagesModel = new ImagesModel(tag.getId(), Arrays.asList(imageIds));

                                intent.putExtra(EstateDetailsActivity.INTENT_ESTATE_MODEL_KEY,tag);
                                intent.putExtra(EstateDetailsActivity.INTENT_IMAGES_MODEL_KEY,imagesModel);
                                startActivity(intent);

                    }
                });

        listView.setAdapter(dataAdapter);
        ////END


        ///START add Estate button

        View addEstateButton = findViewById(R.id.add_estate_button );
        addEstateButton.setOnClickListener(new View.OnClickListener(){
            final EstateModel newEstate = EstateModel.create();//new EstateModel(UUID.randomUUID().toString(),"","",null);
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                EditEstateDetailsDialogFragment editEstateDialogFragment = new EditEstateDetailsDialogFragment();
                Bundle args = new Bundle();
                args.putSerializable(EditEstateDetailsDialogFragment.ARG_EDIT_ESTATE, newEstate);
                editEstateDialogFragment.setArguments(args);
               //editEstateDialogFragment.show(fm, "fragment_new_estate");
                editEstateDialogFragment.setCancelable(false);
                FragmentTransaction transaction =  fm.beginTransaction();
                transaction.add(editEstateDialogFragment,"fragment_new_estate");
                transaction.commit();
            }
        });
        ///END

    }


    ///TESTING
    @Override
    public void onEstateEdited(EstateModel newEstate, EstateModel oldEstate) {
        if(newEstate.hashCode() == oldEstate.hashCode()){
            Toast.makeText(this, "No changes detected.", Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(this, newEstate.getName()+" info has been updated.", Toast.LENGTH_SHORT).show();
            //ProgressDialog progress = DialogUtils.startProgressDialog(this);
            //onTenantSelected(newtenant);
            updateDB(newEstate);
            viewEstateDetailsDialog(newEstate);

            //progress.dismiss();
        }
    }

    private AppDAOInterface getDBHelper(){

        AppDAOInterface dbHelper = null;

        try {
            dbHelper = AppDAO.builder().open(this);
        } catch (Exception e) {

            Log.e(TAG,e.getLocalizedMessage());
        }

        return dbHelper;
    }

    private void updateDB(EstateModel estate){

        AppDAOInterface dbHelper = getDBHelper();

        if(dbHelper.getEstateById(estate.getId()+"")!=null)
            dbHelper.updateEstate(estate);
        else
            dbHelper.addEstate(estate);

    }

    private void viewEstateDetailsDialog(EstateModel estate){

        /*
        FragmentManager fm = getFragmentManager();
        EditEstateDetailsDialogFragment editEstateDialogFragment = new EditEstateDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(EditEstateDetailsDialogFragment.ARG_EDIT_ESTATE, estate);
        editEstateDialogFragment.setArguments(args);
        editEstateDialogFragment.show(fm, "fragment_edit_estate");
        */
        Intent intent = getIntent();//new Intent(EstateActivity.this, EstateDetailsActivity.class);
        intent.putExtra("estate",estate);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.home:
                return true;
            case R.id.user_settings:
                Intent intent = new Intent(this, UserSettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout:
                LoginDialogFragment.LoginManager.logout(this);
                //intent = this.getParentActivityIntent();
                //intent = new Intent(this, MainActivity.class);
                //startActivity(intent);
                this.onNavigateUpFromChild(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
