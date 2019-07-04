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
import io.mtini.android.tenantmanager.databinding.EditEstateDetailsLayoutBinding;
import io.mtini.model.EstateModel;

public class EditEstateDetailsDialogFragment extends DialogFragment {

    public static String TAG = Class.class.getSimpleName();

    public interface OnEditEstateListener {
        public void onEstateEdited(EstateModel estate, EstateModel oldEstate);
    }

    public final static String ARG_EDIT_ESTATE = "editSelectedEstate";

    EstateModel currentEstate = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            currentEstate = (EstateModel)savedInstanceState.getSerializable(ARG_EDIT_ESTATE);
        }else if(getArguments() != null) {
            currentEstate = (EstateModel)getArguments().getSerializable(ARG_EDIT_ESTATE);
        }

        final EditEstateDetailsLayoutBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.edit_estate_details_layout , container, false);
        View view = binding.getRoot();

        binding.setEstate((EstateModel)currentEstate.createClone());

        //Setup update button to finally persist changes
        final View updateBtn = view.findViewById(R.id.update_estate_button );

        updateBtn.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(final View v) {

                        final EstateModel oldEstate = currentEstate;
                        //EditEstateDetailsLayoutBinding binding  = DataBindingUtil.findBinding(v);
                        final EstateModel newEstate = binding.getEstate();
                        //TODO verify before calling this updateComplete?
                        if(validate(binding)) {

                            //v.setClickable(false);

                            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                            dialogBuilder.setMessage(R.string.dialog_changes_message)
                                    .setTitle(R.string.dialog_changes_title);

                            dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User clicked OK button
                                    //TODO figure out why the initial cancel does not hide alertdialog
                                    try {
                                            Dialog progress = DialogUtils.startProgressDialog(getActivity());

                                            try {
                                                updateComplete(newEstate, oldEstate);
                                            } catch (Throwable e) {
                                                Log.e(TAG, e.getMessage(), e);
                                                progress.dismiss();
                                                DialogUtils.startErrorDialog(getActivity(), "An error occurred. '" + e.getLocalizedMessage() + "'");
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
                        }//validate

                        //v.setClickable(true);

                    }
                }
        );

        //Setup cancel button to ignore changes
        View cancelBtn = view.findViewById(R.id.cancel_edit_estate_button  );
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

    private boolean validate(EditEstateDetailsLayoutBinding binding) {
        if( isEmpty(binding.name.getError())
                && isEmpty( binding.contacts.getError())
                && isEmpty( binding.address.getError())
                && isEmpty(binding.estateTypeLabel.getError())){
            System.out.println("Validation true");
            return true;
        }
        System.out.println("Validation false");
        System.out.println("name: "+binding.name.getError());
        System.out.println("contacts: "+binding.contacts.getError());
        System.out.println("address: "+binding.address.getError());
        System.out.println("estateTypeLabel: "+binding.estateTypeLabel.getError());
        return false;
    }

    private boolean isEmpty(CharSequence seq){
        return seq==null || seq.length() == 0;
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
            updateEstateView((EstateModel) args.getSerializable(ARG_EDIT_ESTATE));
        } else if (currentEstate != null) {
            // Set article based on saved builder state defined during onCreateView
            updateEstateView(currentEstate);
        }
    }

    public void updateEstateView(EstateModel estate ) {
        currentEstate = estate;

        LayoutInflater inflater = getActivity().getLayoutInflater();
        ViewGroup container = (ViewGroup) getView();//getView().getRootView();

        EditEstateDetailsLayoutBinding  binding  = DataBindingUtil.inflate(
                inflater, R.layout.edit_estate_details_layout , container, false);

        binding.setEstate(currentEstate);

    }

    public void updateComplete(EstateModel newEstate, EstateModel oldEstate){
        OnEditEstateListener listener = (OnEditEstateListener) getActivity();
        listener.onEstateEdited(newEstate,oldEstate);
    }


}
