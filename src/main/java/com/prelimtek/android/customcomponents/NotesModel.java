package com.prelimtek.android.customcomponents;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.prelimtek.android.customcomponents.BR;

import java.io.Serializable;

public class NotesModel extends BaseObservable implements Serializable{

    long date;
    String noteText;
    String modelId;

    public NotesModel(long date,String modelId, String noteText){
        this.date = date;
        this.modelId=modelId;
        this.noteText=noteText;
    }

    public void setDate(long date){
        this.date = date;
        notifyPropertyChanged(BR.date);
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
        notifyPropertyChanged(BR.modelId);
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
        notifyPropertyChanged(BR.noteText);
    }

    @Bindable
    public long getDate() {
        return date;
    }

    @Bindable
    public String getModelId() {
        return modelId;
    }

    @Bindable
    public String getNoteText() {
        return noteText;
    }
}
