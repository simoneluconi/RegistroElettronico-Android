package com.sharpdroid.registroelettronico.API;

import android.content.Context;
import android.preference.PreferenceManager;

import com.franmontiel.persistentcookiejar.persistence.CookiePersistor;
import com.sharpdroid.registroelettronico.Databases.RegistroDB;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import okhttp3.Cookie;

public class SQLCookiePersistor implements CookiePersistor {
    private final Context mContext;

    public SQLCookiePersistor(Context context) {
        mContext = context;
    }

    @Override
    public List<Cookie> loadAll() {
        String username = PreferenceManager.getDefaultSharedPreferences(mContext).getString("currentProfile", null);
        RegistroDB db = RegistroDB.getInstance(mContext);
        List<Cookie> cookies = new ArrayList<>();
        if (username != null)
            cookies.addAll(db.getCookies(username));

        return cookies;
    }

    @Override
    public void saveAll(Collection<Cookie> cookies) {
        String username = PreferenceManager.getDefaultSharedPreferences(mContext).getString("currentProfile", null);
        RegistroDB db = RegistroDB.getInstance(mContext);
        if (username != null) {
            db.addCookies(username, cookies);
        }

    }

    @Override
    public void removeAll(Collection<Cookie> cookies) {
        RegistroDB db = RegistroDB.getInstance(mContext);
        db.removeCookies(cookies);

    }

    @Override
    public void clear() {
        RegistroDB db = RegistroDB.getInstance(mContext);
        db.removeCookies();

    }
}
