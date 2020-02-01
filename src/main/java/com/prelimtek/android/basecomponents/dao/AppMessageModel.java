package com.prelimtek.android.basecomponents.dao;

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

    private String messageId;
    private String title;
    private byte[] data;
    private long receiptDate;
    private long actionDate;
    private MSG_STATUS status;

    @Bindable
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
        notifyPropertyChanged(BR.messageId);
    }
    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
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


}
