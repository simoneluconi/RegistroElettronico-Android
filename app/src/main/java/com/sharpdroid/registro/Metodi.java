package com.sharpdroid.registro;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

class Metodi {
    static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

}

