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


public class EncrypDataMainFragment extends Fragment {

    private static final String TAG = EncrypDataMainFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.encryption_password_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        EditText pwdTextField = (EditText)view.findViewById(R.id.passphraseText);
        CharSequence pass = pwdTextField.getText();

        Button button = (Button) view.findViewById(R.id.encryptDataBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                button.setError(null);
                try {
                    encryptCompressedTempFile(view,pass,getContext());
                } catch (IOException e) {
                    Log.e(TAG,e.getLocalizedMessage(),e);
                    button.setError("Backup failed due to "+e.getLocalizedMessage());
                }


            }
        });
    }

    private void encryptCompressedTempFile(View view,CharSequence pass, Context context) throws IOException {

        Uri dataFile = ((BackupActivityInterface)getActivity()).compressAndEncrypteData(pass==null?"":pass.toString());

        Snackbar.make(view,"Backup file created successfully",Snackbar.LENGTH_LONG);
        Bundle bundle = new Bundle();
        bundle.putString(BackupActivityInterface.FILE_PATH_KEY,dataFile.toString());

        NavController navController = Navigation.findNavController(getActivity(), R.id.data_backup_nav_host_fragment);
        navController.navigate(R.id.action_encryptFragment_to_sendDataFragment,bundle);

    }


}
