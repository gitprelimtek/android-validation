package io.mtini.model;


/*
public class AppCache {


    public void open(Context context){
        File cacheDir = getDiskCacheDir(context, Configuration.DISK_CACHE_SUBDIR);
        new InitDiskCacheTask().execute(cacheDir);
    }

    private File getDiskCacheDir(Context context, String uniqueName){
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !isExternalStorageRemovable() ? getExternalCacheDir(context).getPath() :
                        context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }


    private static String mDiskCacheLock = "myLock";

    public class InitDiskCacheTask extends AsyncTask<File, Void, Void> {

        @Override
        protected Void doInBackground(File... params) {
            synchronized (mDiskCacheLock) {
                File cacheDir = params[0];
                mDiskLruCache = DiskLruCache.open(cacheDir, Configuration.DISK_CACHE_SIZE);
                mDiskCacheStarting = false; // Finished initialization
                mDiskCacheLock.notifyAll(); // Wake any waiting threads
            }
            return null;
        }

    }

}
*/