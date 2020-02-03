package com.prelimtek.android.appmessage;

import android.graphics.Bitmap;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.prelimtek.android.customcomponents.BR;

/**
 * This is model class for messages or notifications persisted in this app.
 * */
public class AppMessageModel extends BaseObservable {

    enum MSG_STATUS{
        new_message,
        read_message,
        dismissed,
        deleted
    }

    private Integer messageId;
    private CharSequence title;
    private CharSequence body;
    private byte[] data;
    private long receiptDate;
    private long actionDate;
    private MSG_STATUS status;



    private Bitmap icon;

    @Bindable
    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
        notifyPropertyChanged(BR.messageId);
    }
    @Bindable
    public CharSequence getTitle() {
        return title;
    }

    public void setTitle(CharSequence title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }
    @Bindable
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
        notifyPropertyChanged(BR.data);
    }
    @Bindable
    public long getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(long receiptDate) {
        this.receiptDate = receiptDate;
        notifyPropertyChanged(BR.receiptDate);
    }
    @Bindable
    public long getActionDate() {
        return actionDate;
    }

    public void setActionDate(long actionDate) {
        this.actionDate = actionDate;
        notifyPropertyChanged(BR.actionDate);
    }
    @Bindable
    public MSG_STATUS getStatus() {
        return status;
    }

    public void setStatus(MSG_STATUS status) {
        this.status = status;
        notifyPropertyChanged(BR.status);
    }
    @Bindable
    public CharSequence getBody() {
        return body;
    }

    public void setBody(CharSequence body) {
        this.body = body;
        notifyPropertyChanged(BR.body);
    }

    @Bindable
    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
        notifyPropertyChanged(BR.icon);
    }


}
