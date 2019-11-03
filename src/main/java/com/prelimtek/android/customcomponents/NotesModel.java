package com.prelimtek.android.customcomponents;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.prelimtek.android.customcomponents.BR;

import java.io.Serializable;
import java.util.Objects;


public class NotesModel extends BaseObservable implements Serializable,Cloneable{

    String id;
    long date;
    String noteText;
    String modelId;

    public NotesModel(String id, long date,String modelId, String noteText){
        this.id = id;
        this.date = date;
        this.modelId=modelId;
        this.noteText=noteText;
    }

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

    @Override
    public int hashCode() {

        return Objects.hash(id, date, modelId, noteText);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotesModel)) return false;
        NotesModel that = (NotesModel) o;
        return
                Objects.equals(hashCode(), that.hashCode());
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder = builder.append("date").append(date).append("\n").
                append("modelId").append(modelId).append("\n").
                append("noteText").append(noteText).append("\n");

        return builder.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object createClone() {
        try
        {
            return this.clone();
        }catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return null;
    }

}
