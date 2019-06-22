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
import io.mtini.android.tenantmanager.databinding.ProcessRentPaymentLayoutBinding;
import io.mtini.android.tenantmanager.databinding.TenantDetailLayoutBinding;
import io.mtini.model.TenantModel;

public class PaymentHandlingDialogFragment extends DialogFragment {

    public static String TAG = Class.class.getSimpleName();
    public interface OnProcessPaymentListener {
        public void onPaymentSubmitted(TenantModel tenant, TenantModel oldTenant);
    }

    //public final static String ARG_POSITION = "position";
    public final static String ARG_PROCESS_TENANT_PAYMENT = "processTenantPayment";
    //int mCurrentPosition = -1;
    TenantModel currentTenant = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            currentTenant = (TenantModel)savedInstanceState.getSerializable(ARG_PROCESS_TENANT_PAYMENT);
        }else if(getArguments()!=null) {
            currentTenant = (TenantModel)getArguments().getSerializable(ARG_PROCESS_TENANT_PAYMENT);
        }

        ProcessRentPaymentLayoutBinding binding  = DataBindingUtil.inflate(
                inflater, R.layout.process_rent_payment_layout, container, false);
        View view = binding.getRoot();
        binding.setTenant((TenantModel)currentTenant.createClone());

        //Setup update button to finally persist changes
        View updateBtn = view.findViewById(R.id.updateTenantBtn);
        updateBtn.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(final View v) {
                        final TenantModel oldTenant = currentTenant;
                        ProcessRentPaymentLayoutBinding binding  = DataBindingUtil.findBinding(v);
                        final TenantModel newTenant = binding.getTenant();
                        //TODO verify before calling this updateComplete?
                        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                        dialogBuilder.setMessage(R.string.dialog_changes_message)
                                .setTitle(R.string.dialog_changes_title);

                        dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
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


    @Override
    public void onStart() {
        super.onStart();
        //TenantModel tenant = (TenantModel)getActivity().getIntent().getSerializableExtra("tenant");
        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
        Bundle args = getArguments();
        if (args != null) {
            // Set article based on argument passed in
            updateTenantView((TenantModel) args.getSerializable(ARG_PROCESS_TENANT_PAYMENT));
        } else if (currentTenant != null) {
            // Set article based on saved builder state defined during onCreateView
            updateTenantView(currentTenant);
        }
    }

    public void updateTenantView(TenantModel tenant ) {
        currentTenant = tenant;

        LayoutInflater inflater = getActivity().getLayoutInflater();
        ViewGroup container = (ViewGroup) getView();//getView().getRootView();

        TenantDetailLayoutBinding  binding  = DataBindingUtil.inflate(
                inflater, R.layout.tenant_detail_layout, container, false);

        binding.setTenant(currentTenant);

    }

    public void updateComplete(TenantModel newTenant, TenantModel oldTenant){
        OnProcessPaymentListener listener = (OnProcessPaymentListener) getActivity();
        listener.onPaymentSubmitted(newTenant,oldTenant);
    }


}
