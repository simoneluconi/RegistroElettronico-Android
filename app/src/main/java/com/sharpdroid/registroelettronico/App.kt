package com.sharpdroid.registroelettronico

import android.app.Application
import android.content.Intent
import android.support.v7.app.AppCompatDelegate
import android.util.Log
import com.google.android.gms.security.ProviderInstaller
import com.google.firebase.FirebaseApp
import com.orm.SugarContext

class App : Application() {

    private val TAG = "App"

    override fun onCreate() {
        super.onCreate()
        upgradeSecurityProvider()
        SugarContext.init(this)
        FirebaseApp.initializeApp(this)
    }

    private fun upgradeSecurityProvider() {
        ProviderInstaller.installIfNeededAsync(this, object : ProviderInstaller.ProviderInstallListener {
            override fun onProviderInstalled() {

            }

            override fun onProviderInstallFailed(i: Int, intent: Intent) {
                Log.w(TAG, "Failed installing security provider, error code: " + i)
            }
        })
    }

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }
}