package com.prelimtek.android.appmessage;

import java.util.List;

public interface AppMessageDAOInterface {

    List<AppMessageModel> retrieveAllAppMessages();

    void dismissMessage(Integer id);

    void deleteMessage(Integer id);


}
