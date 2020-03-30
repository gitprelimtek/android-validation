package com.prelimtek.android.basecomponents.dao;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public interface DataBackupDAOIterface {

    ByteArrayInputStream getAllLocalDataToStream();

    void uploadDataFromStream(InputStream stream, boolean append) throws IOException;

}


