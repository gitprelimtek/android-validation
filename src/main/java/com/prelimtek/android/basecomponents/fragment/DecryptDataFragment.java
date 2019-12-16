package com.prelimtek.android.basecomponents.fragment;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.prelimtek.android.basecomponents.BackupActivityInterface;
import com.prelimtek.android.customcomponents.R;

import java.io.IOException;

import static com.prelimtek.android.basecomponents.BackupActivityInterface.UPLOAD_FILE_PATH_KEY;

public class DecryptDataFragment extends Fragment {

    private static final String TAG = DecryptDataFragment.class.getSimpleName();

    private Uri uploadFile;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uploadFile = getArguments().getParcelable(UPLOAD_FILE_PATH_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.decrypt_data_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        EditText pwdTextField = (EditText)view.findViewById(R.id.passphraseText);
        CharSequence pass = pwdTextField.getText();

        Button button = (Button) view.findViewById(R.id.decryptDataBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                button.setError(null);
                try {
                    decryptDecompressToTempFile(view,pass,getContext());
                } catch (IOException e) {
                    Log.e(TAG,e.getLocalizedMessage(),e);
                    pwdTextField.setError("Backup failed due to "+e.getLocalizedMessage());
                    Snackbar.make(view,"Error trying to load decrypt file due to "+e.getMessage(),Snackbar.LENGTH_LONG);

                }


            }
        });

        if(uploadFile==null){
            //report error
            Log.e(TAG,"Upload file was not created something went wrong, try again");
            pwdTextField.setError("Upload file was not created something went wrong, try again");
            button.setError("Upload file was not created something went wrong, try again");
            button.setEnabled(false);
            Snackbar.make(view,"Upload file was not created something went wrong, try again",Snackbar.LENGTH_LONG);

        }
    }

    private void decryptDecompressToTempFile(View view,CharSequence pass, Context context) throws IOException {

        Uri dataFile = ((BackupActivityInterface)getActivity()).decryptAndDecompressData(pass==null?"":pass.toString(),uploadFile);

        Snackbar.make(view,"Backup file decrypted and decompressed successfully",Snackbar.LENGTH_SHORT);
        Bundle bundle = new Bundle();
        bundle.putParcelable(UPLOAD_FILE_PATH_KEY,dataFile);

        NavController navController = getNavigationController();
        navController.navigate(R.id.action_decryptDataFragment_to_uploadDataFragment,bundle);

    }

    private NavController getNavigationController(){

        NavController navController =null;

        View frag = getParentFragment()!=null?(getParentFragment().getView()!=null?getParentFragment().getView().findViewById(R.id.data_backup_nav_host_fragment):null):null;
        if(frag!=null)
            navController = Navigation.findNavController(frag);
        else
            navController = Navigation.findNavController(getActivity(), R.id.data_backup_nav_host_fragment);

        return navController;
    }


}
