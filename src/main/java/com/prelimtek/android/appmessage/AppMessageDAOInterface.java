package com.prelimtek.android.appmessage;

import java.util.List;

import javax.annotation.Nullable;

public interface AppMessageDAOInterface {

    List<AppMessageModel> retrieveAllAppMessages();

    List<AppMessageModel> retrieveAppMessages(AppMessageModel.MSG_STATUS status,int rowCount, int pageOffset);

    boolean dismissMessage(String... modelId);

    boolean deleteMessage(String... modelId);

    boolean saveMessage(AppMessageModel message);

    int getAppMessageCountByStatus(AppMessageModel.MSG_STATUS ... status);

}
