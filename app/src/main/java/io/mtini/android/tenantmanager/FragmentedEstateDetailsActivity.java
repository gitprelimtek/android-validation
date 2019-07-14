package io.mtini.android.tenantmanager;


import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.prelimtek.android.validation.adapter.TextFinancialBigDecimalBindingAdapter;
import com.prelimtek.android.basecomponents.dialog.DialogUtils;
import io.mtini.android.tenantmanager.dialog.EditTenantDetailsDialogFragment;
import com.prelimtek.android.picha.view.ImageHandlingDialogFragment;
import io.mtini.android.tenantmanager.dialog.PaymentHandlingDialogFragment;
import io.mtini.android.tenantmanager.fragments.TenantDetailsFragment;
import io.mtini.android.tenantmanager.fragments.TenantListFragment;
import io.mtini.android.view.TextViewValueConverter;
import io.mtini.model.AppDAO;
import io.mtini.model.EstateModel;
import com.prelimtek.android.picha.ImagesModel;
import io.mtini.model.TenantModel;

/**
 * The list and details fragment function differently from estate list and details
 * because of the use of fragments here instead of activity as the list.
 * Therefore incorporation of images list may be slightly different.
 * */
public class FragmentedEstateDetailsActivity extends AppCompatActivity//FragmentActivity
        implements TenantListFragment.OnTenantSelectedListener , EditTenantDetailsDialogFragment.OnEditTenantListener,
        TenantDetailsFragment.OnDeleteTenantListener,
        ImageHandlingDialogFragment.OnImageEditedModelListener, PaymentHandlingDialogFragment.OnProcessPaymentListener {

    public static String  TAG = Class.class.getSimpleName();

    public static String INTENT_TENANT_ESTATE_MODEL_KEY = "estate";

    boolean twinView = false;

    AppDAO dbHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("onCreate .....");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_tenant_list );
        try {
            dbHelper =  AppDAO.builder().open(this);

        } catch (Exception e) {
            Log.e(TAG,e.getLocalizedMessage());
        }

        if (findViewById( R.id.fragment_tenant_list ) != null) {
            if (savedInstanceState != null) {
                return;//?? why ??
            }

            TenantListFragment tenantListFragment = new TenantListFragment();
            tenantListFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_tenant_list, tenantListFragment).commit();

        }

        final EstateModel estate = (EstateModel)getIntent().getSerializableExtra(INTENT_TENANT_ESTATE_MODEL_KEY);

        View addButton = findViewById(R.id.add_tenant_button );
        addButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                TenantModel newTenant = TenantModel.create(estate);

                FragmentManager fm = getFragmentManager();
                EditTenantDetailsDialogFragment editNameDialogFragment = new EditTenantDetailsDialogFragment();
                Bundle args = new Bundle();
                args.putSerializable(EditTenantDetailsDialogFragment.ARG_EDIT_TENANT, newTenant);
                editNameDialogFragment.setArguments(args);
                editNameDialogFragment.setCancelable(false);
                editNameDialogFragment.show(fm, "fragment_edit_tenant");
                //replace edit button with add button

            }
        });





    }

    @Override
    protected void onResume() {
        System.out.println("Onresume .....");
        super.onResume();

        EstateModel estate = (EstateModel)getIntent().getSerializableExtra(INTENT_TENANT_ESTATE_MODEL_KEY);
        //set balance value
        TextView balancetextView = findViewById(R.id.list_balance_txt_view);
        List<TenantModel> tenantList = dbHelper.getTenantList(estate);
        BigDecimal balance = new BigDecimal(0);
        for(TenantModel tenant : tenantList ){
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
        }*/


        //update Text view for onscreen debugging
        String estateName = estate.getName();
        int count = tenantList.size();
        //estate.setTenantCount(count);
        TextView estateSizeTxtView = (TextView)findViewById(R.id.textEstateCount);
        estateSizeTxtView.setText(estateName+" has "+count+" tenants.");

    }

    @Override
    public void onTenantSelected(TenantModel tenant) {

        String[] imageArr = dbHelper.getImageIdList(tenant.getId());
        ImagesModel imageModel = new ImagesModel(tenant.getId(), Arrays.asList(imageArr));

        navigateToDetails(tenant,imageModel);

    }


    /**
     * If newtenant == oldtenant ;
     *      show timed dialog with "No changes"
     *      do nothing
     * else
     *      show wait dialog "Wait. Updating."
     *      call db
     *      update list
     *      //change fragment title to "update review"
     *      navigate to list ??
     *
     *      */
    @Override
    public void onTenantEdited(TenantModel newtenant,TenantModel oldtenant) {
        System.out.println("onTenantEdited .....");
        //TODO use hash equals in model
        if(newtenant.toString().equalsIgnoreCase(oldtenant.toString())){
            Toast.makeText(this, "No changes detected.", Toast.LENGTH_SHORT).show();

        }else {
            Dialog progress = DialogUtils.startProgressDialog(this);
            //onTenantSelected(newtenant);
            updateDB(newtenant);
            //refreshInstanceState();
            //navigateToDetails(newtenant);
            onTenantSelected(newtenant);
            progress.dismiss();
            Toast.makeText(this, newtenant.getName()+" info has been updated.", Toast.LENGTH_SHORT).show();

        }

        /**
         * Testing, the hope is that super.onResume calls activity's onResume()
         * */
        onResume();//

    }

    @Override
    public void onTenantDeleted (TenantModel tenant) {
        if(tenant==null || tenant.getId()==null){
            Toast.makeText(this, "No changes detected.", Toast.LENGTH_SHORT).show();

        }else {

            Dialog progress = DialogUtils.startProgressDialog(this);
            //onTenantSelected(newtenant);
            //dbHelper.getImageIdList(estate.getId());
            dbHelper.deleteTenant(tenant,null);

            progress.dismiss();
            Toast.makeText(this, tenant.getName()+" info has been delete.", Toast.LENGTH_SHORT).show();

            //restart estate list
            Intent intent = new Intent(this, EstateActivity.class);
            startActivity(intent);
            //navigateToList();
        }
    }

    private void updateDB(TenantModel tenant){

        if(dbHelper.getTenantById(tenant.getId()+"")!=null)
            dbHelper.updateTenant(tenant);
        else
            dbHelper.addTenant(tenant,null);

    }

    //TODO remove
    private void navigateToList(){

        FragmentManager fgmtMgr = getFragmentManager();

        Fragment oldFragment = fgmtMgr.findFragmentById(R.id.fragment_tenant_list);

        Fragment detailsFragment = fgmtMgr.findFragmentById(R.id.fragment_tenant_detail);

        FragmentTransaction transaction = fgmtMgr.beginTransaction();

        Fragment newFragment = new TenantListFragment();

        if(detailsFragment!=null
                && detailsFragment.isVisible()){
            transaction.detach(detailsFragment);
        }

        if(oldFragment != null && oldFragment instanceof TenantListFragment){

            transaction.replace(R.id.fragment_tenant_list, newFragment);

        }else{

            transaction.add(R.id.fragment_tenant_list, newFragment);

        }

        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void navigateToDetails(TenantModel tenant,ImagesModel imageModel){
        FragmentManager fgmtMgr = getFragmentManager();
        FragmentTransaction transaction = fgmtMgr.beginTransaction();
        //if List is present , remove
        //TenantDetailsFragment tenantFrag = (TenantDetailsFragment)
        //       fgmtMgr.findFragmentById(R.id.fragment_tenant_detail);

        TenantListFragment tenantListFrag = (TenantListFragment)
                fgmtMgr.findFragmentById(R.id.fragment_tenant_list);


        if(tenantListFrag!=null){
            transaction.detach(tenantListFrag);
        }

            TenantDetailsFragment newFragment = new TenantDetailsFragment();
            Intent intent = getIntent();
            Bundle args = intent.getExtras();
            //Bundle args = new Bundle();
            args.putSerializable(TenantDetailsFragment.ARG_SELECTED_TENANT, tenant);
            args.putSerializable(TenantDetailsFragment.ARG_SELECTED_TENANT_IMAGES, imageModel);
            newFragment.setArguments(args);

            transaction.replace(R.id.fragment_tenant_detail, newFragment);
            /*
            if(twinView){

                transaction.replace(R.id.fragment_tenant_detail, newFragment);

            }else{

                transaction.replace(R.id.fragment_tenant_list, newFragment);
                transaction.addToBackStack(null);

            }*/




        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        System.out.println("onBackPressed .....");
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        }

    }


    //IMAGES methods --- START

    private void updateDB(ImagesModel images,ImagesModel oldImages){
        //AppDAOInterface dbHelper = new AppDAO(this);

        try {

            String modelId = oldImages.getModelId();

            List<String> commonList = Lists.newArrayList(images.getImageNames());
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

    /**
     * The difference between edit image and editdetails is that images have to be commited to
     * db to save memory usage, Therefore upon image update, force the activity to refresh as
     * opposed to calling the listener/callback. TODO review this process in the future! Changed!
     * */
    @Override
    public void onImageModelEdited(ImagesModel newImages, ImagesModel oldImages) {
        System.out.println("onImageModelEdited .....");
        if(newImages.equals(oldImages)){
            Toast.makeText(this, "No changes detected.", Toast.LENGTH_SHORT).show();

        }else {
            Dialog progress = DialogUtils.startProgressDialog(this);
            //TODO verify successful update by returning Result or null
            updateDB(newImages,oldImages);

            //Refresh current activity's state
            //TODO assume onstart() is called after setting these arguments
            //refreshInstanceState();

            //navigateToDetails();
            TenantModel curTenant = dbHelper.getTenantById(newImages.getModelId());
            //onTenantSelected(curTenant);
            navigateToDetails(curTenant,newImages);

            progress.dismiss();
            Toast.makeText(this, " Images have been updated.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentSubmitted(TenantModel tenant, TenantModel oldTenant) {
        System.out.println("onPaymentSubmitted .....");

        tenant.calculateStatus();
        onTenantEdited(tenant,oldTenant);
        tenant.resetRentValue();
    }

    //TODO remove
    private void refreshInstanceState() {
        EstateModel original_estate = (EstateModel)getIntent().getSerializableExtra(INTENT_TENANT_ESTATE_MODEL_KEY);
        EstateModel renewedEstate = dbHelper.getEstateById(original_estate.getId());
        Intent intent = getIntent();
        intent.putExtra(FragmentedEstateDetailsActivity.INTENT_TENANT_ESTATE_MODEL_KEY,renewedEstate);
        startActivity(intent);
    }


    ///IMAGES methods -- END

}
