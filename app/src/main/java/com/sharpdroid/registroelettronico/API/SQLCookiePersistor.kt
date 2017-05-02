package com.sharpdroid.registroelettronico.API

import android.content.Context
import android.preference.PreferenceManager

import com.franmontiel.persistentcookiejar.persistence.CookiePersistor
import com.sharpdroid.registroelettronico.Databases.RegistroDB

import java.util.ArrayList

import okhttp3.Cookie

class SQLCookiePersistor(private val mContext: Context) : CookiePersistor {

    override fun loadAll(): List<Cookie> {
        val username = PreferenceManager.getDefaultSharedPreferences(mContext).getString("currentProfile", null)
        val db = RegistroDB.getInstance(mContext)
        val cookies = ArrayList<Cookie>()
        if (username != null)
            cookies.addAll(db!!.getCookies(username))

        return cookies
    }

    override fun saveAll(cookies: Collection<Cookie>) {
        val username = PreferenceManager.getDefaultSharedPreferences(mContext).getString("currentProfile", null)
        val db = RegistroDB.getInstance(mContext)
        if (username != null) {
            db!!.addCookies(username, cookies)
        }

    }

    override fun removeAll(cookies: Collection<Cookie>) {
        val db = RegistroDB.getInstance(mContext)
        db!!.removeCookies(cookies)

    }

    override fun clear() {
        val db = RegistroDB.getInstance(mContext)
        db!!.removeCookies()

    }
}
