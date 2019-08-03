package io.mtini.android.tenantmanager.dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prelimtek.android.basecomponents.dialog.DialogUtils;

import io.mtini.android.tenantmanager.R;
import io.mtini.android.tenantmanager.databinding.EditTenantDetailLayoutBinding;
import io.mtini.android.tenantmanager.databinding.TenantDetailLayoutBinding;
import io.mtini.model.EstateModel;
import io.mtini.model.TenantModel;

public class EditTenantDetailsDialogFragment extends DialogFragment {

    public static String TAG = Class.class.getSimpleName();
    public interface OnEditTenantListener {
        public void onTenantEdited(TenantModel tenant, TenantModel oldTenant);
    }

    //public final static String ARG_POSITION = "position";
    public final static String ARG_EDIT_TENANT = "editSelectedTenant";
    //int mCurrentPosition = -1;
    TenantModel currentTenant = null;
    TenantModel oldTenant = null;
    EditTenantDetailLayoutBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {

            currentTenant = (TenantModel)savedInstanceState.getSerializable(ARG_EDIT_TENANT);
            oldTenant = (TenantModel)getArguments().getSerializable(ARG_EDIT_TENANT);

        }else if(currentTenant==null && getArguments() != null) {

            oldTenant = (TenantModel)getArguments().getSerializable(ARG_EDIT_TENANT);
            currentTenant =  oldTenant.createClone();

        }

    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null) {
            // Set article based on argument passed in
            updateTenantView((TenantModel) args.getSerializable(ARG_EDIT_TENANT));
        } else if (currentTenant != null) {
            // Set article based on saved builder state defined during onCreateView
            updateTenantView(currentTenant);
        }
    }

    public void updateTenantView(TenantModel tenant ) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        ViewGroup container = (ViewGroup) getView();//getView().getRootView();

        TenantDetailLayoutBinding  td_binding  = DataBindingUtil.inflate(
                inflater, R.layout.tenant_detail_layout, container, false);

        td_binding.setTenant(tenant);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {

         binding  = DataBindingUtil.inflate(
                inflater, R.layout.edit_tenant_detail_layout, container, false);
        View view = binding.getRoot();
        binding.setTenant(currentTenant);

        //Setup update button to finally persist changes
        View updateBtn = view.findViewById(R.id.updateTenantBtn);
        updateBtn.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(final View v) {
                        //final TenantModel oldTenant = currentTenant;
                        EditTenantDetailLayoutBinding binding  = DataBindingUtil.findBinding(v);
                        final TenantModel newTenant = binding.getTenant();
                        //TODO verify before calling this updateComplete?
                        if(!validate(binding)){

                            return;
                        }
                        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                        dialogBuilder.setMessage(R.string.dialog_changes_message)
                                .setTitle(R.string.dialog_changes_title);

                        dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
                                //TODO figure out why the initial cancel does not hide alertdialog
                                try{

                                    Dialog progress = DialogUtils.startProgressDialog(getActivity());

                                    try {

                                        updateComplete(newTenant, oldTenant);
                                    }catch(Throwable e){
                                        Log.e(TAG,e.getMessage(),e);
                                        progress.dismiss();
                                        DialogUtils.startErrorDialog(getActivity(),"An error occurred. '"+e.getLocalizedMessage()+"'" );
                                        return;
                                    }

                                    progress.dismiss();
                                    dismiss();
                                    dialog.cancel();
                                } catch (Throwable e) {
                                    Log.e(TAG, e.getMessage(), e);
                                }

                            }
                        });

                        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });

                        AlertDialog dialog = dialogBuilder.create();
                        dialog.show();

                    }
                }
        );

        //Setup cancel button to ignore changes
        View cancelBtn = view.findViewById(R.id.cancelTenantEditBtn );
        cancelBtn.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                }
        );

        return view;
    }



    ////Handle orientation changes etc
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(outState!=null) {
            TenantModel newTenant = binding.getTenant();
            outState.putSerializable(ARG_EDIT_TENANT, newTenant);
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState!=null) {
            this.currentTenant = (TenantModel) savedInstanceState.getSerializable(ARG_EDIT_TENANT);
        }
    }


    private boolean validate(EditTenantDetailLayoutBinding binding) {
        if( isEmpty(binding.name.getError())
                && isEmpty( binding.contacts.getError())
                && isEmpty(binding.payScheduleLabel.getError())
                && isEmpty(binding.rentEditTxt.getError())){
            return true;
        }
        System.out.println("name: "+binding.name.getError());
        System.out.println("contacts: "+binding.contacts.getError());
        System.out.println("payScheduleLabel: "+binding.payScheduleLabel.getError());
        System.out.println("rentEditTxt: "+binding.rentEditTxt.getError());
        return false;
    }

    private boolean isEmpty(CharSequence seq){
        return seq==null || seq.length() == 0;
    }


    public void updateComplete(TenantModel newTenant, TenantModel oldTenant){
        OnEditTenantListener listener = (OnEditTenantListener) getActivity();
        listener.onTenantEdited(newTenant,oldTenant);
    }


}
