package com.sharpdroid.registroelettronico;

import android.app.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.google.android.gms.security.ProviderInstaller;

public class App extends Application {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private final String TAG = App.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        upgradeSecurityProvider();
    }

    private void upgradeSecurityProvider() {
        ProviderInstaller.installIfNeededAsync(this, new ProviderInstaller.ProviderInstallListener() {
            @Override
            public void onProviderInstalled() {

            }

            @Override
            public void onProviderInstallFailed(int i, Intent intent) {
                Log.w(TAG, "Failed installing security provider, error code: " + i);
            }
        });
    }
}