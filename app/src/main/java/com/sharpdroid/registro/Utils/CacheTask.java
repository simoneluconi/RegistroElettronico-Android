package com.sharpdroid.registro.Utils;

import android.os.AsyncTask;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class CacheTask extends AsyncTask<List, Void, Void> {

    /**
     * Logcat tag.
     */
    private static final String TAG = "CacheTask";

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
    public CacheTask(File cacheDir, String cacheSubDir) {
        this.cacheDir = cacheDir;
        this.cacheSubDir = cacheSubDir;
    }

    /**
     * Caches the specified set of Changes. Will execute in a separate Thread.
     *
     * @param list the list of Changes to be cached.
     * @return null
     */
    @WorkerThread
    @Override
    protected Void doInBackground(List... list) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(cacheDir, cacheSubDir));
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            for (Object obj : list[0]) {
                objectOutputStream.writeObject(obj);
            }
            objectOutputStream.writeObject(null);
            objectOutputStream.close();
            Log.d(TAG, "Successfully cached data");
        } catch (IOException e) {
            Log.e(TAG, "Error while writing cache");
        }
        return null;
    }
}
