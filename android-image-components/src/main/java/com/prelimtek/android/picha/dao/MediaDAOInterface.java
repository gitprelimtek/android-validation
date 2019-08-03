package com.prelimtek.android.picha.dao;

import java.io.Serializable;

public interface MediaDAOInterface  extends Serializable {

    public String getImageById(String id);

    public String[] getImageIdList(String modelId);

    public boolean deleteImage(String id)throws Exception;

    public boolean addImage(String imageId, String modelId, String encodedImage) throws Exception;

}
