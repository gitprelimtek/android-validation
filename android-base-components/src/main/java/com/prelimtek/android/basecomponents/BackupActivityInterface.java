package com.prelimtek.android.basecomponents;

import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface BackupActivityInterface {

    public static final String FILE_PREFIX_ENCRYPTED = "EncryptedAppBackupTmpFile_";
    public static final String FILE_PREFIX_UNENCRYPTED = "UnencryptedAppBackupTmpFile_";
    public static final String FILE_PREFIX_NEW_UPLOADFILE = "newuploaddecrypteddatafile_";

    public static final String UPLOAD_FILE_PATH_KEY="uploadedbackupfilepath";
    public static final String FILE_PATH_KEY="backupfilepath";

    public Uri compressUnencryptedData() throws IOException ;

    public Uri compressAndEncrypteData(String password) throws IOException;

    public Uri decryptAndDecompressData(String password, Uri encryptedFile) throws IOException;

    public Uri decompressData(Uri dataFile) throws IOException;

    public void uploadDataFrom(Uri dataFile, boolean append) throws IOException;

    }
