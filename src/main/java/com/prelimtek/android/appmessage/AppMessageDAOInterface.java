package com.prelimtek.android.appmessage;

import java.util.List;

import javax.annotation.Nullable;

public interface AppMessageDAOInterface {

    List<AppMessageModel> retrieveAllAppMessages();

    List<AppMessageModel> retrieveAppMessages(AppMessageModel.MSG_STATUS status);

    boolean dismissMessage(Integer[] id);

    boolean deleteMessage(Integer[] id);

    boolean saveMessage(AppMessageModel message);

}
