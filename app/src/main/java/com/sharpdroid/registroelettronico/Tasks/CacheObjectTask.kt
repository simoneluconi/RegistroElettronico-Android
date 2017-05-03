package com.sharpdroid.registroelettronico.Tasks

import android.os.AsyncTask
import android.support.annotation.WorkerThread
import android.util.Log

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectOutputStream

class CacheObjectTask
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
        private val cacheSubDir: String) : AsyncTask<Any, Void, Void>() {

    /**
     * Caches the specified Object. Will execute in a separate Thread.

     * @param object to be cached.
     * *
     * @return null
     */
    @WorkerThread
    override fun doInBackground(vararg `object`: Any): Void? {
        try {
            val fileOutputStream = FileOutputStream(File(cacheDir, cacheSubDir))
            val objectOutputStream = ObjectOutputStream(fileOutputStream)
            objectOutputStream.writeObject(`object`[0])
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
        private val TAG = "CacheObjectTask"
    }
}
