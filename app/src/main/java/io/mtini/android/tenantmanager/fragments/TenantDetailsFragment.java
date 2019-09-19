package io.mtini.android.tenantmanager.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.mtini.android.tenantmanager.R;
import io.mtini.android.tenantmanager.dialog.EditTenantDetailsDialogFragment;

import com.prelimtek.android.customcomponents.TextListProcUtils;
import com.prelimtek.android.picha.view.ImageHandlingDialogFragment;
import io.mtini.android.tenantmanager.dialog.PaymentHandlingDialogFragment;
import com.prelimtek.android.picha.view.PhotoProcUtil;
import io.mtini.model.AppDAO;

import com.prelimtek.android.picha.ImagesModel;
import io.mtini.model.TenantModel;
import io.mtini.android.tenantmanager.databinding.TenantDetailLayoutBinding;

public class TenantDetailsFragment extends Fragment {

    public interface OnDeleteTenantListener {
        public void onTenantDeleted(TenantModel tenant);
    }

    public static String TAG = Class.class.getSimpleName();

    public final static String ARG_SELECTED_TENANT = "selectedTenant";
    public final static String ARG_SELECTED_TENANT_IMAGES = "selectedTenantImageModel";

    TenantModel currentTenant = null;
    ImagesModel currentImagesModel = null;
    AppDAO dbHelper = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentTenant = (TenantModel)getArguments().getSerializable(ARG_SELECTED_TENANT);
        currentImagesModel = (ImagesModel)getArguments().getSerializable(ARG_SELECTED_TENANT_IMAGES);

        try {
            dbHelper =  AppDAO.builder().open(getActivity());
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        TenantDetailLayoutBinding  binding  = DataBindingUtil.inflate(
                inflater, R.layout.tenant_detail_layout, container, false);
        View view = binding.getRoot();
        binding.setTenant(currentTenant);


        View editButton = view.findViewById(R.id.editTenantBtn);
        editButton.setOnClickListener(
                new View.OnClickListener(){
            final TenantModel editTenant = currentTenant;
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                EditTenantDetailsDialogFragment editTenantDetailsDialogFragment = new EditTenantDetailsDialogFragment();
                Bundle args = new Bundle();
                args.putSerializable(EditTenantDetailsDialogFragment.ARG_EDIT_TENANT, editTenant);
                editTenantDetailsDialogFragment.setArguments(args);
                editTenantDetailsDialogFragment.show(fm, "fragment_edit_name");
            }
        }
        );


        View deleteButton = view.findViewById(R.id.deleteTenantBtn);
        deleteButton.setOnClickListener(
                new View.OnClickListener(){
                    final TenantModel toDeleteTenant = currentTenant;
                    @Override
                    public void onClick(View v) {

                        ((OnDeleteTenantListener)getActivity()).onTenantDeleted(toDeleteTenant);

                    }
                }
        );



        View editImages = view.findViewById( R.id.edit_tenant_images_button );
        editImages.setOnClickListener(new View.OnClickListener(){
            final ImagesModel fragmentImagesModel = currentImagesModel;
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                ImageHandlingDialogFragment editImagesDialogFragment = new ImageHandlingDialogFragment();
                //editImagesDialogFragment.setDBHelper(dbHelper.getLocalDao());
                Bundle args = new Bundle();
                args.putSerializable(ImageHandlingDialogFragment.ARG_SELECTED_MODEL_IMAGE, fragmentImagesModel);
                args.putSerializable(ImageHandlingDialogFragment.ARG_DB_HELPER, dbHelper.getLocalDao());
                editImagesDialogFragment.setArguments(args);
                editImagesDialogFragment.show(fm, "fragment_edit_tenant_images");

            }
        });


        View editPayments = view.findViewById( R.id.edit_tenant_payments_button );
        editPayments.setOnClickListener(new View.OnClickListener(){
            final TenantModel editTenant = currentTenant;
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                PaymentHandlingDialogFragment editPaymentsDialogFragment = new PaymentHandlingDialogFragment();
                Bundle args = new Bundle();
                args.putSerializable(PaymentHandlingDialogFragment.ARG_PROCESS_TENANT_PAYMENT , editTenant);
                editPaymentsDialogFragment.setArguments(args);
                editPaymentsDialogFragment.show(fm, "fragment_edit_tenant_payments");
            }
        });



        return view;

    }

    @Override
    public void onResume() {
        super.onResume();

        PhotoProcUtil.showOrRefreshImageListFragment(currentImagesModel,getFragmentManager(),dbHelper,false);
        TextListProcUtils.showOrRefreshNotesListFragment(currentImagesModel.getModelId(),R.id.notes_list_framelayout,getFragmentManager(),dbHelper,false);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(dbHelper!=null){
            dbHelper.close();
        }
    }

    /*
    @Override
    public void onStart() {
        super.onStart();
        //TenantModel tenant = (TenantModel)getActivity().getIntent().getSerializableExtra("tenant");
        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
        //TODO move these to onCreateView
        Bundle args = getArguments();
        if (args != null) {
            // Set article based on argument passed in
            updateTenantView((TenantModel) args.getSerializable(ARG_SELECTED_TENANT));
            ImagesModel imagesModel = (ImagesModel) args.getSerializable(ARG_SELECTED_TENANT_IMAGES);
            PhotoProcUtil.showOrRefreshImageListFragment(imagesModel,getFragmentManager(),dbHelper,false);

        } else if (currentTenant != null) {
            // Set article based on saved builder state defined during onCreateView
            updateTenantView(currentTenant);
            PhotoProcUtil.showOrRefreshImageListFragment(currentImagesModel,getFragmentManager(),dbHelper,false);

        }


    }

    public void updateTenantView(TenantModel tenant ) {
        //TextView article = (TextView) getActivity().findViewById(R.id.tenant_fragment_position);
        currentTenant = tenant;

        LayoutInflater inflater = getActivity().getLayoutInflater();
        ViewGroup container = (ViewGroup) getView();//getView().getRootView();

        TenantDetailLayoutBinding  binding  = DataBindingUtil.inflate(
                inflater, R.layout.tenant_detail_layout, container, false);
        //View view = binding.getRoot();
        binding.setTenant(currentTenant);
        binding.notifyChange();
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //TODO this is not necessary because this page does not directly modify data; memory location is tracked by downstream processes, in terms of data changes
        // Save the current tenant selection in case we need to recreate the fragment
        outState.putSerializable(ARG_SELECTED_TENANT, currentTenant);

    }

    */


}
