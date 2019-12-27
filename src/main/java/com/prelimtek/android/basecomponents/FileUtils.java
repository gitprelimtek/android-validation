package com.prelimtek.android.basecomponents;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;

import com.prelimtek.android.basecomponents.dao.DataBackupDAOIterface;
import com.prelimtek.android.basecomponents.fragment.DataBackupDialogFragment;
import com.prelimtek.android.customcomponents.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class FileUtils {

    public static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 1101;
    public static final int REQUEST_READ_FILE_CODE = 1102;
    public static final String FILE_READ_CHOOSER = "read file from internal or external source";

    public static class EncryptionCodec{

        private static int BUFFER_SIZE = 1024;
        private static String CIPHER_TRANSFORMATION = "AES/ECB/PKCS5Padding";
        private static String CIPHER_TRANSFORMATION_256 = "AES/CBC/PKCS5Padding";
        private static String ALGORITHM = "AES";

        public static SecretKeySpec createAESKey(String myKey) throws NoSuchAlgorithmException,UnsupportedEncodingException
        {

            byte[] key = myKey.getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
            return secretKey;

        }

        public static SecretKeySpec createAES256Key(String myKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
            String salt = "blahblah";

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(myKey.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
            return secretKey;
        }

        public static void decryptAES256(InputStream input, OutputStream output,SecretKeySpec secretKey)throws IOException {
            Cipher cipher = null;
            try {
                byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
                IvParameterSpec ivspec = new IvParameterSpec(iv);

                cipher = Cipher.getInstance(CIPHER_TRANSFORMATION_256);
                cipher.init(Cipher.DECRYPT_MODE, secretKey,ivspec);
            } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException e) {
                throw new IOException(e);
            }

            decrypt(input,output,cipher);
        }

        public static void encryptAES256(InputStream input, OutputStream output, SecretKeySpec secretKey)throws IOException {

            Cipher cipher = null;
            try {
                byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
                IvParameterSpec ivspec = new IvParameterSpec(iv);

                cipher = Cipher.getInstance(CIPHER_TRANSFORMATION_256);
                cipher.init(Cipher.ENCRYPT_MODE, secretKey,ivspec);
            } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException e) {
                throw new IOException(e);
            }

            encrypt(input, output, cipher);
        }


        public static void decryptAES(InputStream input, OutputStream output,SecretKeySpec secretKey)throws IOException {
            Cipher cipher = null;
            try {
                cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
            } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
               throw new IOException(e);
            }

            decrypt(input,output,cipher);
        }

        public static void encryptAES(InputStream input, OutputStream output, SecretKeySpec secretKey)throws IOException {

            Cipher cipher = null;
            try {
                cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
                throw new IOException(e);
            }

            encrypt(input, output, cipher);
        }

        public static void encrypt(InputStream input, OutputStream output, Cipher cipher)throws IOException{

            try (CipherOutputStream out = new CipherOutputStream(new BufferedOutputStream(output),cipher)){
                try (BufferedInputStream in = new BufferedInputStream(input)){
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int len;
                    while((len=in.read(buffer)) != -1){
                        out.write(buffer, 0, len);
                    }
                }
            }

        }

        public static void decrypt(InputStream input, OutputStream output,Cipher cipher)throws IOException{

            try (CipherInputStream in = new CipherInputStream(new BufferedInputStream(input),cipher)){
                try (BufferedOutputStream out = new BufferedOutputStream(output)){
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int len;
                    while((len = in.read(buffer)) != -1){
                        out.write(buffer, 0, len);
                    }
                }
            }

        }

    }

    public static class CompressionCodec{

        private static int BUFFER_SIZE = 1024;

        public static void compressGZIP(InputStream input, OutputStream output) throws IOException {
            try (GZIPOutputStream out = new GZIPOutputStream(new BufferedOutputStream(output))){
                try (BufferedInputStream in = new BufferedInputStream(input)){
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int len;
                    while((len=in.read(buffer)) != -1){
                        out.write(buffer, 0, len);
                    }
                }
            }
        }

        public static void compressGZIP(InputStream input, File output) throws IOException {
            try (GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(output))){
                try (BufferedInputStream in = new BufferedInputStream(input)){
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int len;
                    while((len=in.read(buffer)) != -1){
                        out.write(buffer, 0, len);
                    }
                }
            }
        }

        public static void compressGZIP(File input, File output) throws IOException {
            try (GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(output))){
                try (FileInputStream in = new FileInputStream(input)){
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int len;
                    while((len=in.read(buffer)) != -1){
                        out.write(buffer, 0, len);
                    }
                }
            }
        }


        public static void decompressGzip(InputStream input, OutputStream output) throws IOException {
            try (GZIPInputStream in = new GZIPInputStream(new BufferedInputStream(input))){
                try (BufferedOutputStream out = new BufferedOutputStream(output)){
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int len;
                    while((len = in.read(buffer)) != -1){
                        out.write(buffer, 0, len);
                    }
                }
            }
        }

        public static void decompressGzip(File input, File output) throws IOException {
            try (GZIPInputStream in = new GZIPInputStream(new FileInputStream(input))){
                try (FileOutputStream out = new FileOutputStream(output)){
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int len;
                    while((len = in.read(buffer)) != -1){
                        out.write(buffer, 0, len);
                    }
                }
            }
        }

        public static byte[] compress(byte[] input){

            ByteArrayOutputStream o = new ByteArrayOutputStream();
            byte[] output = new byte[BUFFER_SIZE];

            Deflater compresser = new Deflater(Deflater.BEST_SPEED);
            compresser.setInput(input,0,input.length);
            compresser.finish();
            while(!compresser.finished()){
                int compressedDataLength = compresser.deflate(output);
                if (compressedDataLength == 0) {
                    break;
                }
                o.write(output, 0, compressedDataLength);
            }

            compresser.end();

            return o.toByteArray();
        }

        public static byte[] decompress(byte[] compressed) throws DataFormatException {

            ByteArrayOutputStream o = new ByteArrayOutputStream();
            byte[] output = new byte[BUFFER_SIZE];

            Inflater decompresser = new Inflater();
            decompresser.setInput(compressed,0,compressed.length);
            while(!decompresser.finished()) {
                int decompressedDataLength = decompresser.inflate(output);
                if (decompressedDataLength == 0) {
                    break;
                }
                o.write(output, 0, decompressedDataLength);
            }
            return o.toByteArray();
        }

    }

    public static File getTempFile(Context context, String url) throws IOException {

        File directory = context.getExternalCacheDir();//context.getCacheDir();
        String fileName = Uri.parse(url).getLastPathSegment();
        File file = File.createTempFile(fileName, null, directory);

        return file;
    }

    public static Uri toUri(Activity activity, File file){
        Uri uri = FileProvider.getUriForFile(activity,
                getAuthorityProvider(activity),
                file);
        //Uri.parse(file.getAbsolutePath());
        return uri;
    }

    public static Uri toUri(Service service, File file){
        Uri uri = FileProvider.getUriForFile(service,
                getAuthorityProvider(service),
                file);
        //Uri.parse(file.getAbsolutePath());
        return uri;
    }

    public static String getAuthorityProvider(Context context) {

        String res =  context.getResources().getString(R.string.authority_file_provider);
        if(res ==null) return "io.mtini.android.fileprovider";

        return res;
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /** Checks if external storage is available to at least read
     * */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static void requestFileRead(Fragment fragment){
        if (ActivityCompat.checkSelfPermission(
                fragment.getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(
                    Intent.ACTION_OPEN_DOCUMENT
            );
            //intent.setType( "image/*");
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            //intent.putExtra("return-data", true);

            fragment.startActivityForResult(Intent.createChooser(intent,FILE_READ_CHOOSER) , REQUEST_READ_FILE_CODE);
        } else {
            ActivityCompat.requestPermissions(
                    fragment.getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_REQUEST_CODE
            );
        }
    }

    public static void requestFileRead(Activity activity){
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(
                    Intent.ACTION_OPEN_DOCUMENT
            );
            //intent.setType( "image/*");
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            //intent.putExtra("return-data", true);

            activity.startActivityForResult(Intent.createChooser(intent,FILE_READ_CHOOSER) , REQUEST_READ_FILE_CODE);
        } else {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_REQUEST_CODE
            );
        }
    }

    /** Get the directory for the app's private pictures directory.
     * */
    public static File getPrivateAlbumStorageDir(Context context, String albumName) throws IOException {

        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            throw new IOException("Directory not created");
        }
        return file;
    }


    /**
     * Display UI with password edit text. Submit triggers encryption and compression.
     * The parent activity should implement a listener with onCompressed() where a whatsapp, sms, email
     * UI is displayed. Ex. if whatsapp icon is selected then TelephonyUtils is called from activity or fragment..
     * */
    public static void startFileEncryptionAndExportFragmentIntent(FragmentManager fm, DataBackupDAOIterface dbHelper){


        DataBackupDialogFragment dataBckDialogFragment = new DataBackupDialogFragment();
        Bundle args = new Bundle();
        dataBckDialogFragment.setArguments(args);
        dataBckDialogFragment.setCancelable(true);
        dataBckDialogFragment.setDbHelper(dbHelper);

        FragmentTransaction transaction =  fm.beginTransaction();
        transaction.add(dataBckDialogFragment,"fragment_data_backup");
        transaction.commit();

    }

    /*
    public static String getAuthorityProvider(Context context) {

        String res =  context.getResources().getString(R.string.authority_file_provider);
        if(res ==null) return "io.mtini.android.fileprovider";

        return res;
    }
     */

}
