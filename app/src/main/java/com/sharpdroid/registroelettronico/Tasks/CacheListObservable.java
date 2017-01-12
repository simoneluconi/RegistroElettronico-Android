package com.sharpdroid.registroelettronico.Tasks;

import android.util.Log;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;

public class CacheListObservable {
    private final String TAG = CacheListObservable.class.getSimpleName();
    private final File file;

    public CacheListObservable(File file) {
        this.file = file;
    }

    public <T> Observable<List<T>> getCachedList(Class<T> klazz) {
        return Observable.fromCallable(() -> {
                    ObjectInputStream objectInputStream = null;
                    List<T> cachedData = new LinkedList<>();
                    try {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        objectInputStream = new ObjectInputStream(fileInputStream);
                        Object temp;
                        while ((temp = objectInputStream.readObject()) != null) {
                            cachedData.add(klazz.cast(temp));
                        }
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
                    return cachedData;
                }
        );
    }
}
