package com.prelimtek.android.picha;

import java.io.Serializable;
import java.util.Objects;

public class EncodedImageModel implements Serializable, Cloneable{

    private String encodedBitmap;
    private String modelId;
    private String id;


    public EncodedImageModel(String id, String modelId, String encodedImage){
        this.id = id;this.modelId=modelId;this.encodedBitmap=encodedImage;
    }
    public String getEncodedBitmap() {
        return encodedBitmap;
    }

    public void setEncodedBitmap(String encodedImage) {
        this.encodedBitmap = encodedImage;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public int hashCode() {

        return Objects.hash( id,modelId, encodedBitmap );
    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof EncodedImageModel)) return false;
        return ((EncodedImageModel)o).hashCode() == hashCode();

    }
}
