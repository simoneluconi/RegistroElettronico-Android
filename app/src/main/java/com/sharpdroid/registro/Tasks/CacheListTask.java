package com.sharpdroid.registro.Tasks;

import android.os.AsyncTask;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class CacheListTask extends AsyncTask<List, Void, Void> {

    /**
     * Logcat tag.
     */
    private static final String TAG = "CacheListTask";

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
    public CacheListTask(File cacheDir, String cacheSubDir) {
        this.cacheDir = cacheDir;
        this.cacheSubDir = cacheSubDir;
    }

    /**
     * Caches the specified List. Will execute in a separate Thread.
     *
     * @param list to be cached.
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
