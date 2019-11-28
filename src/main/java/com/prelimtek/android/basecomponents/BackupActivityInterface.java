package com.prelimtek.android.basecomponents;

import android.net.Uri;

import java.io.IOException;

public interface BackupActivityInterface {

    public static final String FILE_PATH_KEY="backupfilepath";

    public Uri compressUnencryptedData() throws IOException ;

    public Uri compressAndEncrypteData(String password) throws IOException;

    }
