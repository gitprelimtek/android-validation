package com.prelimtek.android.appmessage;


import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.prelimtek.android.customcomponents.R;

public class AppMessageUtil {


    public static void showOrRefreshAppMessageListFragment(
            @NonNull Context context,
            @NonNull FragmentManager childTransactionManager,
            @NonNull AppMessageDAOInterface dao){


        FragmentTransaction transaction = childTransactionManager.beginTransaction();

        AppMessageListDialogFragment listFragment = new AppMessageListDialogFragment(context,dao);
        listFragment.setCancelable(true);
        listFragment.setMenuVisibility(true);

        //listFragment.setTargetFragment();
        transaction.add(listFragment,null);
        //transaction.add(R.id.messages_list_fragment ,listFragment,null);
        transaction.commit();

        //Fragment oldFragment = childTransactionManager.findFragmentById(R.id.);


    }
}
