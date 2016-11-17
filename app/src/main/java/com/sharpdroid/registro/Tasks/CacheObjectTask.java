package com.sharpdroid.registro.Tasks;

import android.os.AsyncTask;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class CacheObjectTask extends AsyncTask<Object, Void, Void> {

    /**
     * Logcat tag.
     */
    private static final String TAG = "CacheObjectTask";

    /**
     * File representing the directory in which the data will be stored.
     */
    private final File cacheDir;

    /**
     * String representing the subdirectory in which the data will be stored.
     */
    private final String cacheSubDir;

    /**
     * @param cacheDir    the directory in which the data will be stored.
     * @param cacheSubDir the subdirectory in which the data will be stored.
     */
    public CacheObjectTask(File cacheDir, String cacheSubDir) {
        this.cacheDir = cacheDir;
        this.cacheSubDir = cacheSubDir;
    }

    /**
     * Caches the specified Object. Will execute in a separate Thread.
     *
     * @param object to be cached.
     * @return null
     */
    @WorkerThread
    @Override
    protected Void doInBackground(Object... object) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(cacheDir, cacheSubDir));
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(object[0]);
            objectOutputStream.close();
            Log.d(TAG, "Successfully cached data");
        } catch (IOException e) {
            Log.e(TAG, "Error while writing cache");
        }
        return null;
    }
}
