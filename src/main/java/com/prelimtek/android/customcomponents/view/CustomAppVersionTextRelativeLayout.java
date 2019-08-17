package com.prelimtek.android.customcomponents.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.prelimtek.android.customcomponents.R;

/**
 * This class is used to load App Version text contained in a panel as a header or footer.
 * This is in place of performing <include> which caused some Activity  invalidateAll() null pointer exceptions.
 * See the design xml : custom_versiontext_header_footer_layout.xml
 * </>*/
public class CustomAppVersionTextRelativeLayout extends RelativeLayout {

    protected RelativeLayout viewlayout;


    public CustomAppVersionTextRelativeLayout(Context context) {
        super(context);
        initControl(context);
    }

    public CustomAppVersionTextRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl(context);
    }

    public CustomAppVersionTextRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context);
    }

    public CustomAppVersionTextRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initControl(context);
    }

    private void initControl(Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        viewlayout = (RelativeLayout) inflater.inflate(R.layout.custom_versiontext_header_footer_layout, this, false);

        this.addView(viewlayout);
    }
}
