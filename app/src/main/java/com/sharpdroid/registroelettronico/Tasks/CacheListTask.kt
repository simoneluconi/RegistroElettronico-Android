package com.sharpdroid.registroelettronico.Tasks

import android.os.AsyncTask
import android.support.annotation.WorkerThread
import android.util.Log

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectOutputStream

class CacheListTask
/**
 * @param cacheDir    the directory in which the data will be stored.
 * *
 * @param cacheSubDir the subdirectory in which the data will be stored.
 */
(
        /**
         * File representing the directory in which the data will be stored.
         */
        private val cacheDir: File,
        /**
         * String representing the subdirectory in which the data will be stored.
         */
        private val cacheSubDir: String) : AsyncTask<List<*>, Void, Void>() {

    /**
     * Caches the specified List. Will execute in a separate Thread.

     * @param list to be cached.
     * *
     * @return null
     */
    @WorkerThread
    override fun doInBackground(vararg list: List<*>): Void? {
        try {
            val fileOutputStream = FileOutputStream(File(cacheDir, cacheSubDir))
            val objectOutputStream = ObjectOutputStream(fileOutputStream)
            for (obj in list[0]) {
                objectOutputStream.writeObject(obj)
            }
            objectOutputStream.writeObject(null)
            objectOutputStream.close()
            Log.d(TAG, "Successfully cached data")
        } catch (e: IOException) {
            Log.e(TAG, "Error while writing cache")
        }

        return null
    }

    companion object {

        /**
         * Logcat tag.
         */
        private val TAG = "CacheListTask"
    }
}
