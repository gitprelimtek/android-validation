package com.prelimtek.android.appmessage;

import java.util.List;

public interface AppMessageDAOInterface {

    List<AppMessageModel> retrieveAllAppMessages();

    List<AppMessageModel> retrieveAppMessages(AppMessageModel.MSG_TYPE type, AppMessageModel.MSG_STATUS[] status, int rowCount, int pageOffset);

    boolean changeMessageStatus(AppMessageModel.MSG_STATUS status, String... modelId);

    boolean deleteMessage(String... modelId);

    boolean saveMessage(AppMessageModel message);

    int getAppMessageCountByStatus(AppMessageModel.MSG_TYPE type,AppMessageModel.MSG_STATUS ... status);

}
