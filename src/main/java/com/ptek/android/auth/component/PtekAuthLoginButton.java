package com.ptek.android.auth.component;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.prelimtek.android.basecomponents.ResourcesUtils;
import com.prelimtek.android.customcomponents.view.CustomImageButton;
import com.ptek.android.auth.R;


public class PtekAuthLoginButton extends CustomImageButton {

    public PtekAuthLoginButton(Context context) {
        super(context);
        initControl(context);
    }

    public PtekAuthLoginButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initControl(context);
    }

    public PtekAuthLoginButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context);
    }

    private void initControl(Context context)
    {
        //LayoutInflater inflater = (LayoutInflater)
        //        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //viewlayout = (LinearLayout)inflater.inflate(R.layout.custom_imagebutton_layout, this);

        //Init components
        imageview = (ImageView)viewlayout.findViewById(R.id.custom_button_image);

        textview = (TextView)viewlayout.findViewById(R.id.custom_button_textView);

        TypedArray ta = getContext().obtainStyledAttributes(attrs,R.styleable.CustomImageButton,0,0);

        try {
            viewlayout.setBackgroundColor(ResourcesUtils.getColor(this,R.color.io_mtini_login_button_background_color));

            imageview.setPadding(7,7,5,7);
            textview.setPadding(5,7,7,7);

            //viewparams.
            textview.setTextColor(ResourcesUtils.getColor(this,R.color.io_mtini_login_button_text_color ));
            setShowText( true );
            setText(getResources().getString(R.string.io_mtini_login_button_text_log_in));
            setImageSrc(getResources().getResourceName(R.drawable.add_person_group_40));
            setImageLength(ta.getString(R.styleable.CustomImageButton_imageLength ));
            setImageWidth(ta.getString(R.styleable.CustomImageButton_imageWidth ));
            //setTextPosition(R.attr.layout_constraintHorizontal_bias);//right
            setTextPosition(ta.getInt(R.styleable.CustomImageButton_textPosition,0));
        } finally {
            ta.recycle();
        }

        //viewlayout.setOrientation(LinearLayout.VERTICAL);
        //viewlayout.setGravity(Layout.CEN);
        viewlayout.setOnClickListener(loginClick);
        //invalidate();
        //requestLayout();

    }

    @Override
    public void reDrawUI(boolean activated){
        super.setClickable(activated);

        if(activated)
            viewlayout.setBackgroundColor(ResourcesUtils.getColor(this,R.color.io_mtini_login_button_background_color));
            //viewlayout.setForegroundTintMode(PorterDuff.Mode.DARKEN);
        else
            viewlayout.setBackgroundColor(ResourcesUtils.getColor(this,R.color.io_mtini_deactivate_login_button_background_color));
            //viewlayout.setForegroundTintMode(PorterDuff.Mode.CLEAR);
    }

    public Fragment getParentFragment() {
        return parentFragment;
    }

    public void setParentFragment(Fragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    Fragment parentFragment;

    OnClickListener loginClick = new OnClickListener() {
        @Override
        public void onClick(View v) {


            FragmentManager fgmtMgr =
                            getParentFragment()==null
                            ? ((Activity)getContext()).getFragmentManager()
                            : getParentFragment().getFragmentManager();

            FragmentTransaction transaction = fgmtMgr.beginTransaction();
            PtekAuthLoginFragment eatLogin = new PtekAuthLoginFragment();
            eatLogin.setCancelable(false);
            eatLogin.setTargetFragment(parentFragment,0);

            Intent intent = new Intent();
            Bundle args = intent.getExtras();
            eatLogin.setArguments(args);

            transaction.add(eatLogin,"eat_login_fragment");
            transaction.commit();
        }
    };

    OnClickListener logoutClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //TODO call EATTokenManager.logout()
        }
    };


}
