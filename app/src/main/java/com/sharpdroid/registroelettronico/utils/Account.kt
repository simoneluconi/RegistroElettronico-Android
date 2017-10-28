package com.sharpdroid.registroelettronico.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.sharpdroid.registroelettronico.Info

class Account(context: Context) {
    var preference: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var user: Long
        get() = preference.getLong(Info.ACCOUNT, -1)
        set(value) = preference.edit().putLong(Info.ACCOUNT, value).apply()


    companion object {
        private var instance: Account? = null

        fun with(context: Context): Account {
            if (instance == null) instance = Account(context)
            return instance as Account
        }
    }
}