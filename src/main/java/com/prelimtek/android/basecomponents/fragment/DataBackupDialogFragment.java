package com.prelimtek.android.basecomponents.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prelimtek.android.basecomponents.dao.DataBackupDAOIterface;
import com.prelimtek.android.customcomponents.R;

public class DataBackupDialogFragment  extends DialogFragment {

    DataBackupDAOIterface dbHelper;
    public  void setDbHelper(DataBackupDAOIterface dbHelper) {
        this.dbHelper=dbHelper;
    }

    public interface DataBackupExportInterface {

        void onExportComplete();

        void onExportFailed();

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.backup_activity_layout, container);


        return view;
    }

}
