package com.prelimtek.android.customcomponents.view;

import android.content.Context;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.prelimtek.android.basecomponents.Configuration;


/**
 * This class helps with binding a textview with configuration method call to get version text.
 * **/
public class VersionTextView extends AppCompatTextView {


    public VersionTextView(Context context) {
        super(context);
        initControl(context);
    }

    public VersionTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl(context);
    }

    public VersionTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context);
    }

    private void initControl(Context context) {

        CharSequence s = this.getText();
        String ret =  Configuration.getVersionText(getContext());
        this.setText(s+ret);

    }


}
