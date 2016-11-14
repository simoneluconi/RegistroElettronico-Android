package com.sharpdroid.registro.Interfaces;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Metodi {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public static String MessaggioVoto(float Obb, float media, float somma, int voti) {
        float backups = somma, newvoto = 6;
        int contavoti = voti;
        if (media < Obb) {
            while (true) {
                somma = somma + newvoto;
                contavoti = contavoti + 1;
                media = somma / contavoti;
                if (media >= Obb) {
                    return "Devi prendere " + newvoto + " per avere " + Obb;
                } else if (newvoto >= 10) {
                    // TODO: 14/11/2016 Invece di "devi prendere più di 10" dire un paio di voti-> es. devi prendere un 9 e un 7 per la sufficienza
                    return "Devi prendere più di 10";
                }
                newvoto = newvoto + 0.25f;
                contavoti = contavoti - 1;
                somma = backups;
            }
        } else {
            newvoto = 10;
            while (true) {
                somma = somma + newvoto;
                contavoti = contavoti + 1;
                media = somma / contavoti;
                if (media < Obb) {
                    newvoto = newvoto + 0.25f;
                    return "Non prendere meno di " + newvoto;
                } else if (newvoto == 1) {
                    return "Puoi stare tranquillo!";
                }
                newvoto = newvoto - 0.25f;
                contavoti = contavoti - 1;
                somma = backups;
            }
        }
    }

}

