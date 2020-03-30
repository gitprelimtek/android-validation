package com.prelimtek.android.customcomponents.view;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;

import com.prelimtek.android.basecomponents.dialog.DialogUtils;

public class TelephoneActionAdapter {

    /**
     * 1. extract numbr from view
     * 2. Initiate text or call dialog; pass phone numbr using intents
     * */
    public static void onClickTelephoneAction(View view, final String phoneNumber) {
        FragmentManager fm = DialogUtils.getActivity(view).getFragmentManager();
        TelephoneActionDialogFragment dialog = new TelephoneActionDialogFragment();
        Bundle args = new Bundle();
        args.putString(TelephoneActionDialogFragment.ARG_TELEPHONE_NUMBER, phoneNumber);
        dialog.setArguments(args);
        dialog.setCancelable(true);
        dialog.setMenuVisibility(false);
        dialog.show(fm, TelephoneActionDialogFragment.TAG);

    }

}
