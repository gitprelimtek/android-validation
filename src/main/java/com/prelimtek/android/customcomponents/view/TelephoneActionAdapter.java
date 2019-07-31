package com.prelimtek.android.customcomponents.view;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;

import com.prelimtek.android.customcomponents.view.TelephoneActionDialogFragment;

public class TelephoneActionAdapter {

    /**
     * 1. extract numbr from view
     * 2. Initiate text or call dialog; pass phone numbr
     *      2.a. make intent call*/
    public static void onClickTelephoneAction(View view, final String phoneNumber) {
        FragmentManager fm = ((Activity)view.getContext()).getFragmentManager();
        TelephoneActionDialogFragment dialog = new TelephoneActionDialogFragment();
        Bundle args = new Bundle();
        args.putString(TelephoneActionDialogFragment.ARG_TELEPHONE_NUMBER, phoneNumber);
        dialog.setArguments(args);
        dialog.setCancelable(true);
        dialog.show(fm, TelephoneActionDialogFragment.TAG);

    }
}
