package com.prelimtek.android.picha.dao;

import com.prelimtek.android.basecomponents.dao.BaseDAOInterface;

public interface MediaDAOInterface  extends BaseDAOInterface {

    public String getImageById(String id);

    public String[] getImageIdList(String modelId);

    public boolean deleteImage(String id)throws Exception;

    public boolean addImage(String imageId, String modelId, String encodedImage) throws Exception;

}
