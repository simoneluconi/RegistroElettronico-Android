package com.sharpdroid.registro.Utils;

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
        // Calcolo
        int numeroVoti = voti.length();
        if (Obb > 10 || media > 10)
            return "Errore";
        if (Obb >= 10 && media < Obb)
            return "Impossibile raggiungere la media del " + media;
        double [] array = {0.75, 0.5, 0.25, 0};
        int index = 0;
        float sommaVotiDaPrendere;
        float [] votiMinimi = {};
        float diff;
        float resto;
        String [] split;
        do {
            index++;
            sommaVotiDaPrendere = (Obb * (numeroVoti + index)) - (media * numeroVoti);
        } while ((sommaVotiDaPrendere/index) > 10);
        for (int i = 0; i < index; i++) {
            votiMinimi[i] = (sommaVotiDaPrendere / index) + resto;
            resto = 0;
            split = votiMinimi[i].split(".");
            if (split.length()>=2 && (split[1] != "25" && split[1] != "5" && split[1] != "75")) {
                int k = 0;
                do {
                    diff = votiMinimi[i] - (split[0] + array[k]);
                    k++;
                } while (diff < 0);
                votiMinimi [i] = votiMinimi[i] - diff;
                resto = diff;
            }
            if (votiMinimi[i] > 10) {
                float diff2 = votiMinimi[i] - 10;
                votiMinimi[i] = 10;
                resto += diff2;
            }
        }
        // Stampa
        String toReturn = "";
        if (votiMinimi.length() == 1) {
            if (votiMinimi[0] <= Obb)
                toReturn = "Non prendere meno di " + votiMinimi[0] + ".";
            else
                toReturn = "Devi prendere almeno " + votiMinimi[0] + " per avere " + Obb + ".";
        }
        else {
            toReturn = "Devi prendere: ";
            for (int a = 0; a < votiMinimi.length(); a++) {
                if (a < votiMinimi.length()-2)
                    toReturn = toReturn + votiMinimi[a] + ", ";
                else if (a == votiMinimi.length()-2)
                    toReturn = toReturn + votiMinimi[a] + " e ";
                else
                    toReturn = toReturn + votiMinimi[a] + ".";
            }
        }
        return toReturn;
    }

}

