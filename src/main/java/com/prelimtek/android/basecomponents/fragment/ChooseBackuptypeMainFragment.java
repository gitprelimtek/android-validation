package com.prelimtek.android.basecomponents.fragment;

import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import com.prelimtek.android.basecomponents.BackupActivityInterface;
import com.prelimtek.android.customcomponents.R;

import java.io.IOException;

public class ChooseBackuptypeMainFragment extends Fragment {

    private static final String TAG = ChooseBackuptypeMainFragment.class.getSimpleName();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.choose_backup_type_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Button encryptTypebutton = (Button) view.findViewById(R.id.typeEncryptBtn);
        encryptTypebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //View frag = getParentFragment().getView().findViewById(R.id.data_backup_nav_host_fragment);
                //NavController navController = Navigation.findNavController(frag);
                NavController navController = Navigation.findNavController(getActivity(), R.id.data_backup_nav_host_fragment);
                navController.navigate(R.id.action_bcmTypeFragment_to_encryptFragment );

            }
        });

        //initiate backup to tempfile; upon complete got to send data page
        Button skipEncryptTypebutton = (Button) view.findViewById(R.id.typeSkipEncryptBtn);
        skipEncryptTypebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //View frag = getParentFragment().getView().findViewById(R.id.data_backup_nav_host_fragment);
                //NavController navController = Navigation.findNavController(frag);
                try{
                    skipEncryptTypebutton.setError(null);

                    initiateUnencryptedBackup(view);

                }catch (IOException e){
                    Log.e(TAG,e.getMessage(),e);
                    skipEncryptTypebutton.setError("Backup failed due to "+e.getLocalizedMessage());
                    return;
                }

            }
        });
    }


    /**
     * initiate backup to tempfile; upon complete got to send data page
     *
     */
    private void initiateUnencryptedBackup(View view) throws IOException {

        Uri dataFile = ((BackupActivityInterface)getActivity()).compressUnencryptedData();


        Snackbar.make(view,"Backup file created successfully",Snackbar.LENGTH_LONG);
        NavController navController = Navigation.findNavController(getActivity(), R.id.data_backup_nav_host_fragment);
        Bundle bundle = new Bundle();
        bundle.putString(BackupActivityInterface.FILE_PATH_KEY,dataFile.toString());
        navController.navigate(R.id.action_bcmTypeFragment_to_sendDataFragment,bundle);

    }

}
