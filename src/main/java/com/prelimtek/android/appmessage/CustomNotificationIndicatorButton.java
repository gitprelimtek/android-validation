package com.prelimtek.android.appmessage;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.common.base.Strings;
import com.prelimtek.android.customcomponents.R;

public class CustomNotificationIndicatorButton extends RelativeLayout {
    public CustomNotificationIndicatorButton(Context context) {
        super(context);
        initControl( context);
    }

    public CustomNotificationIndicatorButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.attrs = attrs;
        initControl( context);
    }

    public CustomNotificationIndicatorButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.attrs = attrs;
        initControl( context);
    }

    protected AttributeSet attrs = null;
    RelativeLayout layout;
    TextView textview;
    ImageView imageview ;

    private CharSequence noticeText ="";
    private boolean hideIfEmpty;
    private void initControl(Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        //Important! set attachRoot to false so that viewLayout layout orientation , width etc can take effect
        //remember to add viewLayout to component after making changes
        layout = (RelativeLayout) inflater.inflate(R.layout.custom_notification_indicator_layout, this);

        //Init components
        imageview = (ImageView) layout.findViewById(R.id.indicator_counterBackground);

        //Init components
        textview = (TextView) layout.findViewById(R.id.indicator_countText);


        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CustomNotificationIndicatorButton,0,0);

        try {
            setNoticeText(ta.getText(R.styleable.CustomNotificationIndicatorButton_noticeText ));
            setHideIfEmpty(ta.getBoolean(R.styleable.CustomNotificationIndicatorButton_hideIfEmpty,false));
        } finally {
            ta.recycle();
        }

        //Important! do this because inflated view was not attached to root.
        //this.addView(layout);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        //readNotifications();
        reDrawUI();

    }

    public void reDrawUI(){

        textview.setText(noticeText);
        if(noticeText==null || Strings.isNullOrEmpty(noticeText.toString().trim())) {

            if (hideIfEmpty) {
                this.layout.setVisibility(GONE);
            }

        }else{
            this.layout.setVisibility(VISIBLE);
        }


    }

   /* protected  void readNotifications(){
        //acquire count before adding clicklistener
        StatusBarNotification[] notifications = null;


        Context context = getContext();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notifications = notificationManager.getActiveNotifications();
            if(notifications!=null){
                final int noteCount = notifications.length;
                textview.setNoticeText(""+noteCount);
                layout.setClickable(true);
                layout.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        //got to a list
                        Toast.makeText(context, "You have "+noteCount+" notes ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
*/

    public CharSequence getNoticeText() {
        return noticeText;
    }

    public void setNoticeText(CharSequence noticeText) {
        this.noticeText = noticeText;
        reDrawUI();
    }

    public boolean isHideIfEmpty() {
        return hideIfEmpty;
    }

    public void setHideIfEmpty(boolean hideIfEmpty) {
        this.hideIfEmpty = hideIfEmpty;
        reDrawUI();
    }

}
