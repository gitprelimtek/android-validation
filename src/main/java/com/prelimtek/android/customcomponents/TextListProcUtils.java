package com.prelimtek.android.customcomponents;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;


public class TextListProcUtils {

    public static void showOrRefreshNotesListFragment(@NonNull String modelId ,@NonNull int notes_list_framelayout, @NonNull FragmentManager childTransactionManager , @NonNull TextDAOInterface dbHelper, boolean editable){

        //System.out.println("!!!!!!!!  showOrRefreshImageListFragment called -> currentImagesModel size ="+currentImagesModel.getImageNames().size());

        //FragmentManager childTransactionManager = getChildFragmentManager();

        FragmentTransaction transaction = childTransactionManager.beginTransaction();

        Fragment oldFragment = childTransactionManager.findFragmentById(notes_list_framelayout);


        NotesListDisplayFragment notesListFragment = new NotesListDisplayFragment();
        notesListFragment.setDBHelper(dbHelper);
        Bundle imgBundle = new Bundle();
        imgBundle.putSerializable(NotesListDisplayFragment.MODEL_ID_KEY, modelId);
        notesListFragment.setArguments(imgBundle);


        if(oldFragment!=null){
            //transaction.detach(oldFragment).attach(newImgsListFragment).commit();
            transaction
                    .replace(notes_list_framelayout, notesListFragment).commit();
        }else {
            transaction
                    .add(notes_list_framelayout, notesListFragment).commit();
        }


    }
}
