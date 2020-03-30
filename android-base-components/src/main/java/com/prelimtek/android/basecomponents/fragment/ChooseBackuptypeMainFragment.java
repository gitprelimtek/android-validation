package com.prelimtek.android.basecomponents.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.prelimtek.android.basecomponents.BackupActivityInterface;
import com.prelimtek.android.basecomponents.FileUtils;
import com.prelimtek.android.customcomponents.R;

import java.io.IOException;
import java.util.zip.ZipException;

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

    private ProgressBar progress;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        progress = view.findViewById(R.id.progress_bar);
        progress.setVisibility(ProgressBar.GONE);

        Button encryptTypebutton = (Button) view.findViewById(R.id.typeEncryptBtn);
        encryptTypebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NavController navController = getNavigationController();
                navController.navigate(R.id.action_bcmTypeFragment_to_encryptFragment );

            }
        });

        //initiate backup to tempfile; upon complete got to send data page
        Button skipEncryptTypebutton = (Button) view.findViewById(R.id.typeSkipEncryptBtn);
        skipEncryptTypebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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


        Button uploadDataButton = (Button) view.findViewById(R.id.requestUploadBtn);
        uploadDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{

                    uploadDataButton.setError(null);

                    requestUploadData(view);

                }catch (IOException e){
                    Log.e(TAG,e.getMessage(),e);
                    uploadDataButton.setError("Upload failed due to "+e.getLocalizedMessage());
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
        NavController navController = getNavigationController();
        Bundle bundle = new Bundle();
        bundle.putString(BackupActivityInterface.FILE_PATH_KEY,dataFile.toString());
        navController.navigate(R.id.action_bcmTypeFragment_to_sendDataFragment,bundle);

    }

    /**
     * This request triggers an external app intent whose results should be handled onActivityResult
     * */
    private void requestUploadData(View view) throws IOException{

        FileUtils.requestFileRead(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case FileUtils.REQUEST_READ_FILE_CODE :
                if(resultCode== Activity.RESULT_OK){
                    Uri dataUri = data.getData();
                    View view = getView();
                    Snackbar.make(view,"Backup file found",Snackbar.LENGTH_LONG);

                    progress.setVisibility(ProgressBar.VISIBLE);
                    //finish
                    try {

                        gotToLoadDecryptedFile(dataUri);

                    }catch (ZipException e){

                        Log.e(TAG,e.getMessage());

                        gotToLoadEncryptedFile(dataUri);

                    }catch(Throwable e1){
                        //navigate to somewhere else or report error
                        Snackbar.make(view,"Error trying to load file due to "+e1.getMessage(),Snackbar.LENGTH_LONG);
                    }finally {
                        progress.setVisibility(ProgressBar.GONE);
                    }
                }
                break;
        }

    }

    private void gotToLoadEncryptedFile(Uri path){
        NavController navController = getNavigationController();
        Bundle bundle = new Bundle();
        bundle.putParcelable(BackupActivityInterface.UPLOAD_FILE_PATH_KEY,path);
        navController.navigate(R.id.action_choose_backup_type_to_decrypt_data_fragment,bundle);
    }

    private void gotToLoadDecryptedFile(Uri path)throws IOException{

        Uri dataFile = ((BackupActivityInterface)getActivity()).decompressData(path);
        //Snackbar.make(view,"Backup file created successfully",Snackbar.LENGTH_LONG);
        NavController navController = getNavigationController();
        Bundle bundle = new Bundle();
        bundle.putParcelable(BackupActivityInterface.UPLOAD_FILE_PATH_KEY,dataFile);
        navController.navigate(R.id.action_choose_backup_type_to_upload_data_fragment,bundle);

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
