package com.prelimtek.android.basecomponents.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.prelimtek.android.basecomponents.BackupActivityInterface;
import com.prelimtek.android.customcomponents.R;


import java.io.IOException;


/**
 *
 */
public class UploadDataFragment extends Fragment {


    private BackupActivityInterface mListener;

    public UploadDataFragment() {
        // Required empty public constructor
    }


    private Uri dataFile;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
             dataFile = getArguments().getParcelable(BackupActivityInterface.UPLOAD_FILE_PATH_KEY);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.upload_data_fragment, container, false);



        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //begin
        //show process which will be stopped on success
        //hide success
        //hide error
        //
        Button overwriteBtn = view.findViewById(R.id.overwriteDataBtn);
        overwriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mListener.uploadDataFrom(dataFile,false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Button appendBtn = view.findViewById(R.id.appendDataBtn);
        appendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mListener.uploadDataFrom(dataFile,true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BackupActivityInterface) {
            mListener = (BackupActivityInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
