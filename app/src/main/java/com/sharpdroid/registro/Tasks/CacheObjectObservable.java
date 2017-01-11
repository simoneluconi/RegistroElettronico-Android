package com.sharpdroid.registro.Tasks;

import android.util.Log;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;

import io.reactivex.Observable;

public class CacheObjectObservable {
    private final String TAG = CacheObjectObservable.class.getSimpleName();
    private final File file;

    public CacheObjectObservable(File file) {
        this.file = file;
    }

    public <T> Observable<T> getCachedList(Class<T> klazz) {
        return Observable.fromCallable(() -> {
                    ObjectInputStream objectInputStream = null;
                    Object obj = null;
                    try {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        objectInputStream = new ObjectInputStream(fileInputStream);
                        obj = objectInputStream.readObject();
                    } catch (FileNotFoundException e) {
                        Log.w(TAG, "Cache not found.");
                    } catch (EOFException e) {
                        Log.e(TAG, "Error while reading cache! (EOF) ");
                    } catch (StreamCorruptedException e) {
                        Log.e(TAG, "Corrupted cache!");
                    } catch (IOException e) {
                        Log.e(TAG, "Error while reading cache!");
                    } catch (ClassCastException | ClassNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        if (objectInputStream != null) {
                            try {
                                objectInputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return klazz.cast(obj);
                }
        );
    }
}
