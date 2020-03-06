package com.prelimtek.android.appmessage;

import android.graphics.drawable.Icon;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.prelimtek.android.customcomponents.BR;

import java.util.Map;
import java.util.UUID;

/**
 * This is model class for messages or notifications persisted in this app.
 * */
public class AppMessageModel extends BaseObservable {

    public enum MSG_STATUS {
        not_set,
        new_message,
        read_message,
        archived
    }


    private String modelId;
    private Integer messageId;
    private CharSequence title;
    private CharSequence sender;
    private CharSequence body;
    private Map<String,String> data;
    private Long receiptDate;
    private Long actionDate;
    private MSG_STATUS status;
    private Icon icon;

    @Bindable
    public Integer getMessageId() {
        return messageId;
    }

    private void setMessageId(int messageId) {
        this.messageId = messageId;
        notifyPropertyChanged(BR.messageId);
    }

    @Bindable
    public String getModelId() {
        return modelId;
    }

    private void setModelId(String modelId) {
        this.modelId = modelId;
        notifyPropertyChanged(BR.modelId);
    }

    @Bindable
    public CharSequence getTitle() {
        return title;
    }

    private void setTitle(CharSequence title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    @Bindable
    public CharSequence getSender() {
        return sender;
    }

    private void setSender(CharSequence sender) {
        this.sender = sender;
        notifyPropertyChanged(BR.sender);
    }

    @Bindable
    public Map<String,String> getData() {
        return data;
    }

    private void setData(Map<String,String> data) {
        this.data = data;
        notifyPropertyChanged(BR.data);
    }

    @Bindable
    public Long getReceiptDate() {
        return receiptDate;
    }

    private void setReceiptDate(Long receiptDate) {
        this.receiptDate = receiptDate;
        notifyPropertyChanged(BR.receiptDate);
    }

    @Bindable
    public Long getActionDate() {
        return actionDate;
    }

    private void setActionDate(Long actionDate) {
        this.actionDate = actionDate;
        notifyPropertyChanged(BR.actionDate);
    }

    @Bindable
    public MSG_STATUS getStatus() {
        return status;
    }

    private void setStatus(MSG_STATUS status) {
        this.status = status;
        notifyPropertyChanged(BR.status);
    }

    @Bindable
    public CharSequence getBody() {
        return body;
    }

    private void setBody(CharSequence body) {
        this.body = body;
        notifyPropertyChanged(BR.body);
    }

    @Bindable
    public Icon getIcon() {
        return icon;
    }

    private void setIcon(Icon icon) {
        this.icon = icon;
        notifyPropertyChanged(BR.icon);
    }

    public static class Builder{

        private Integer _messageId;
        private CharSequence _title;
        private CharSequence _body;
        private CharSequence _sender;
        private Map<String,String> _data;
        private Long _receiptDate;
        private Long _actionDate;
        private Icon _icon;
        private String _status;

        public Builder set_sender(CharSequence _sender) {
            this._sender = _sender;return this;
        }

        public Builder set_messageId(Integer _messageId) {
            this._messageId = _messageId;return this;
        }

        public Builder set_title(CharSequence _title) {
            this._title = _title;
            return this;
        }

        public Builder set_body(CharSequence _body) {
            this._body = _body;return this;
        }

        public Builder set_data(Map<String,String> _data) {
            this._data = _data;return this;
        }

        public Builder set_receiptDate(Long _receiptDate) {
            this._receiptDate = _receiptDate;return this;
        }

        public Builder set_actionDate(Long _actionDate) {
            this._actionDate = _actionDate;return this;
        }

        public Builder set_icon(Icon _icon) {
            this._icon = _icon;return this;
        }

        public Builder set_status(String _status) {
            this._status = _status;return this;
        }

        public AppMessageModel build(){

            String modelId = UUID.randomUUID().toString();
            AppMessageModel model = new AppMessageModel();
            model.setModelId(modelId);
            model.setBody(_body);
            model.setSender(_sender);
            model.setReceiptDate(_receiptDate);
            model.setActionDate(_actionDate);
            model.setData(_data);
            model.setIcon(_icon);
            model.setTitle(_title);
            //model.setStatus(AppMessageModel.MSG_STATUS.new_message);
            model.setStatus(MSG_STATUS.valueOf(_status!=null?_status.toLowerCase():"not_set"));

            return model;
        }
    }



}