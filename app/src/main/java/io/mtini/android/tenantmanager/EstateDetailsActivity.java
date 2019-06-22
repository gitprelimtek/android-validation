package io.mtini.android.tenantmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.prelimtek.android.validation.adapter.TextFinancialBigDecimalBindingAdapter;
import io.mtini.android.tenantmanager.databinding.ActivityDetailsEstateBinding;
import com.prelimtek.android.basecomponents.dialog.DialogUtils;
import io.mtini.android.tenantmanager.dialog.EditEstateDetailsDialogFragment;
import com.prelimtek.android.picha.view.ImageHandlingDialogFragment;
import com.prelimtek.android.picha.view.PhotoProcUtil;
import io.mtini.android.view.TextViewValueConverter;
import io.mtini.model.AppDAO;
import io.mtini.model.EstateModel;
import com.prelimtek.android.picha.ImagesModel;
import io.mtini.model.TenantModel;

public class EstateDetailsActivity extends AppCompatActivity
        implements EditEstateDetailsDialogFragment.OnEditEstateListener , ImageHandlingDialogFragment.OnImageEditedModelListener {

    public interface OnDeleteEstateListener {
        public void onEstateDeleted(EstateModel estate);
    }

    public static String TAG = Class.class.getSimpleName();

    public static String INTENT_ESTATE_MODEL_KEY = "estate";
    public static String INTENT_IMAGES_MODEL_KEY = "estate_images";

    AppDAO dbHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details_estate);

        try{
            dbHelper =  AppDAO.builder().open(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Context currentContext = this;

        final EstateModel estate = (EstateModel)getIntent().getSerializableExtra( INTENT_ESTATE_MODEL_KEY);
        assert estate!=null;

        String estateName = estate.getName();
        List<TenantModel> tenantList = dbHelper.getTenantList(estate);

        ImagesModel estateImagesModel = null;
        if(getIntent().getSerializableExtra( INTENT_IMAGES_MODEL_KEY)!=null) {
            estateImagesModel = (ImagesModel) getIntent().getSerializableExtra(INTENT_IMAGES_MODEL_KEY);
        }else{
            //TODO remove or populate images from db else initialize
            estateImagesModel = new ImagesModel(estate.getId(), new ArrayList<String>());
        }

        int count = tenantList.size();
        //estate.setTenantCount(count);
        TextView estateSizeTxtView = (TextView)findViewById(R.id.textEstateDetails);
        estateSizeTxtView.setText(estateName+" has "+count+" tenants.");

        //This binds EstateModel to the UI as an estate object. Allows for ${estate.description} data extraction
        ActivityDetailsEstateBinding  binding = DataBindingUtil.setContentView(this,R.layout.activity_details_estate);
        binding.setEstate(estate);

        //No bind the list of Tenants using an adapter
        View viewTenantBtn = findViewById(R.id.view_tenantlist_button);
        viewTenantBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                viewEstateTenantList(estate);
            }
        });


        View editEstateButton = findViewById(R.id.edit_estate_button );
        editEstateButton.setOnClickListener(new View.OnClickListener(){
            final EstateModel editEstate = estate;
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                EditEstateDetailsDialogFragment editEstateDialogFragment = new EditEstateDetailsDialogFragment();
                Bundle args = new Bundle();
                args.putSerializable(EditEstateDetailsDialogFragment.ARG_EDIT_ESTATE, editEstate);
                editEstateDialogFragment.setArguments(args);
                editEstateDialogFragment.setCancelable(false);
                editEstateDialogFragment.show(fm, "fragment_edit_estate");
            }
        });

        View deleteEstate = findViewById(R.id.delete_estate_button );
        deleteEstate.setOnClickListener(new View.OnClickListener(){
            final EstateModel delEstate = estate;
            @Override
            public void onClick(View v) {

                //if estate has tenant show error
                //else delete
                List list = dbHelper.getTenantList(delEstate);
                if(list!=null && list.size()>0){
                    String errorMsg = "This estate contains "+list.size()+" tenants! Delete all tenants before attempting to delete.";
                    Log.w(TAG,errorMsg);
                    //navigate to tenantList ??

                    //DialogUtils.startErrorDialog(v.getContext(),"This estate contains "+list.size()+" tenants! Delete all tenants before attempting to delete.");

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(v.getContext());
                    dialogBuilder.setMessage(errorMsg)
                            .setTitle(R.string.error_message);
                    dialogBuilder.setPositiveButton(R.string.ok,new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            viewEstateTenantList(delEstate);
                        }
                    });


                    AlertDialog errorDialog = dialogBuilder.create();
                    errorDialog.setCanceledOnTouchOutside(true);
                    errorDialog.show();

                }else{

                    deleteEstate(delEstate);

                }
            }
        });


        final ImagesModel fragmentImagesModel = estateImagesModel;
        View editImages = findViewById(R.id.edit_estate_images_button );
        editImages.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                ImageHandlingDialogFragment imagesDialogFragment = new ImageHandlingDialogFragment();
                imagesDialogFragment.setDBHelper(dbHelper.getLocalDao());
                Bundle args = new Bundle();
                args.putSerializable(ImageHandlingDialogFragment.ARG_SELECTED_MODEL_IMAGE, fragmentImagesModel);
                imagesDialogFragment.setArguments(args);
                //editEstateDialogFragment.show(fm, "fragment_edit_estate_images");
                imagesDialogFragment.setCancelable(false);
                FragmentTransaction transaction =  fm.beginTransaction();
                transaction.add(imagesDialogFragment,"fragment_edit_estate_images");
                transaction.commit();

            }
        });




        //set balance value
        TextView balancetextView = findViewById(R.id.balance_txt_view);
        List<TenantModel> tenants = dbHelper.getTenantList(estate);
        BigDecimal balance = new BigDecimal(0);
        for(TenantModel tenant : tenants ){
            balance = balance.add(tenant.getBalance());
        }
        TextFinancialBigDecimalBindingAdapter.setBindCurrencyValue(balancetextView,balance);
        TextViewValueConverter.highlightBalance(balancetextView,balance);

        /*
        String balanceStr = TextFinancialBigDecimalBindingAdapter.getMoneyFormat(this).format(balance);
        balancetextView.setText(balanceStr);
        if(balance.intValue()>0){
            balancetextView.setTextColor(getResources().getColor(R.color.Red_700));
        }else{
            balancetextView.setTextColor(getResources().getColor(R.color.Teal_700));
        }
        */
    }



    //IMAGES methods --- START
    @Override
    protected void onStart() {
        super.onStart();
        ImagesModel imagesModel = (ImagesModel) getIntent().getSerializableExtra(INTENT_IMAGES_MODEL_KEY);

        PhotoProcUtil.showOrRefreshImageListFragment(imagesModel,getFragmentManager(),dbHelper,false);
    }

    @Override
    public void onEstateEdited(EstateModel newEstate, EstateModel oldEstate) {
        if(newEstate.hashCode() == oldEstate.hashCode()){
            Toast.makeText(this, "No changes detected.", Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(this, newEstate.getName()+" info has been updated.", Toast.LENGTH_SHORT).show();
            //ProgressDialog progress = DialogUtils.startProgressDialog(this);
            //onTenantSelected(newtenant);//TODO verify successful update by returning Result or null
            updateDB(newEstate);
            viewEstateDetailsDialog(newEstate);

            //progress.dismiss();
        }
    }

    private void updateDB(ImagesModel images,ImagesModel oldImages){

        try {

            String modelId = oldImages.getModelId();

           List commonList = Lists.newArrayList(images.getImageNames());
            commonList.retainAll(oldImages.getImageNames());

            List<String> toAdd = Lists.newArrayList(images.getImageNames());
            List<String> toDelete = Lists.newArrayList(oldImages.getImageNames());
            toDelete.removeAll(commonList);

        for(String imageId : toAdd){
            String encodedImage = ((AppDAO) dbHelper).getLocalDao().getImageById(imageId);
            if(encodedImage==null){
                Log.e(TAG,imageId+" image was found to be null. Expected a value!!!!!!!!!");
            }else {
                ((AppDAO) dbHelper).getRemoteDao().addImage(imageId, modelId, encodedImage);
                ((AppDAO) dbHelper).getLocalDao().updateImage(imageId,modelId);
            }
        }

        for(String imageId : toDelete){
            ((AppDAO) dbHelper).getRemoteDao().deleteImage(imageId);
            ((AppDAO) dbHelper).getLocalDao().deleteImage(imageId);
        }

       // if(dbHelper.getEstateById(images.getModelId())!=null)
            //dbHelper.updateEstate(estate);
       // else
            //dbHelper.addEstate(estate);

        } catch (Exception e) {

            Log.e(TAG,e.getLocalizedMessage());

        }finally {
            //try {
                //Thread.sleep(3000);
            //} catch (InterruptedException e) {
            //    e.printStackTrace();
            //}
            //dbHelper.close();
        }

    }
    ///IMAGES methods -- END


    private void updateDB(EstateModel estate){
       /*
       AppDAOInterface dbHelper = new AppDAO(this);
        try {
            dbHelper.open();
        } catch (Exception e) {
            Log.e(TAG,e.getLocalizedMessage());
        }
        */

         if(dbHelper.getEstateById(estate.getId())!=null)
            dbHelper.updateEstate(estate);
         else
            dbHelper.addEstate(estate);

    }

    private void viewEstateDetailsDialog(EstateModel estate){

        //FragmentManager fm = getFragmentManager();
        //EditEstateDetailsDialogFragment editEstateDialogFragment = new EditEstateDetailsDialogFragment();
        //Bundle args = new Bundle();
        //args.putSerializable(EditEstateDetailsDialogFragment.ARG_EDIT_ESTATE, estate);
        //editEstateDialogFragment.setArguments(args);
        //editEstateDialogFragment.show(fm, "fragment_edit_estate");
        Intent intent = getIntent();//new Intent(EstateActivity.this, EstateDetailsActivity.class);
        intent.putExtra(INTENT_ESTATE_MODEL_KEY,estate);
        startActivity(intent);
    }


    @Override
    public void onImageModelEdited(ImagesModel newImages, ImagesModel oldImages) {
        if(newImages.equals(oldImages)){
            Toast.makeText(this, "No changes detected.", Toast.LENGTH_SHORT).show();

        }else {

            Dialog progress = DialogUtils.startProgressDialog(this);
            //TODO verify successful update by returning Result or null
            updateDB(newImages,oldImages);

            //Refresh current activity's state
            Intent intent = getIntent();//new Intent(EstateActivity.this, EstateDetailsActivity.class);
            intent.putExtra(INTENT_IMAGES_MODEL_KEY,newImages);
            startActivity(intent);

            progress.dismiss();
            Toast.makeText(this, " Images have been updated.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteEstate(EstateModel estate) {
        if(estate==null || estate.getId()==null){
            Toast.makeText(this, "No changes detected.", Toast.LENGTH_SHORT).show();

        }else {

            Dialog progress = DialogUtils.startProgressDialog(this);
            //onTenantSelected(newtenant);
            //dbHelper.getImageIdList(estate.getId());
            dbHelper.deleteEstate(estate);

            progress.dismiss();
            Toast.makeText(this, estate.getName()+" info has been delete.", Toast.LENGTH_SHORT).show();

            //restart estate list
            Intent intent = new Intent(this, EstateActivity.class);
            startActivity(intent);
        }
    }

    private void viewEstateTenantList(EstateModel estate) {
        Intent intent = new Intent(this, FragmentedEstateDetailsActivity.class);
        //TenantModel tag = (TenantModel)v.getTag();
        //Object h = v.getHandler();
        intent.putExtra(FragmentedEstateDetailsActivity.INTENT_TENANT_ESTATE_MODEL_KEY,estate);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        dbHelper.close();
    }
}
