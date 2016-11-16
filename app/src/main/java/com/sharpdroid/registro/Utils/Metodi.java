package com.sharpdroid.registro.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sharpdroid.registro.API.RESTFulAPI;
import com.sharpdroid.registro.Interfaces.Absence;
import com.sharpdroid.registro.Interfaces.Media;
import com.sharpdroid.registro.R;

import java.util.List;

public class Metodi {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public static String MessaggioVoto(float Obb, float media, float somma, int voti) {
        // Calcolo
        if (Obb > 10 || media > 10)
            return "Errore";
        if (Obb >= 10 && media < Obb)
            return "Impossibile raggiungere la media del " + media;
        double[] array = {0.75, 0.5, 0.25, 0};
        int index = 0;
        float sommaVotiDaPrendere;
        double[] votiMinimi = new double[10];
        double diff;
        double diff2;
        double resto = 0;
        double parteIntera;
        double parteDecimale;
        do {
            index = index + 1;
            sommaVotiDaPrendere = (Obb * (voti + index)) - (media * voti);
        } while ((sommaVotiDaPrendere / index) > 10);
        for (int i = 0; i < index; i = i + 1) {
            votiMinimi[i] = (sommaVotiDaPrendere / index) + resto;
            resto = 0;
            parteIntera = Math.floor(votiMinimi[i]);
            parteDecimale = (votiMinimi[i] - parteIntera) * 100;
            if (parteDecimale != 25 && parteDecimale != 50 && parteDecimale != 75) {
                int k = 0;
                do {
                    diff = votiMinimi[i] - (parteIntera + array[k]);
                    k++;
                } while (diff < 0);
                votiMinimi[i] = votiMinimi[i] - diff;
                resto = diff;
            }
            if (votiMinimi[i] > 10) {
                diff2 = votiMinimi[i] - 10;
                votiMinimi[i] = 10;
                resto = resto + diff2;
            }
        }
        // Stampa
        String toReturn;
        if (votiMinimi[0] <= 0)
            return "Errore";
        if (votiMinimi[0] <= Obb)
            toReturn = "Non prendere meno di " + votiMinimi[0];
        else {
            toReturn = "Devi prendere almeno ";
            for (int a = 0; a < votiMinimi.length; a = a + 1) {
                if (votiMinimi[a] != 0) {
                    toReturn = toReturn + votiMinimi[a] + ", ";
                }
            }
            toReturn = toReturn.substring(0, toReturn.length() - 2);
        }
        return toReturn;
    }

    public static boolean isMediaSufficiente(Media media, String tipo) {
        switch (tipo) {
            case RESTFulAPI.ORALE:
                return media.getMediaOrale() > 6;
            case RESTFulAPI.PRATICO:
                return media.getMediaPratico() > 6;
            case RESTFulAPI.SCRITTO:
                return media.getMediaScritto() > 6;
            default:
                return media.getMediaGenerale() > 6;
        }
    }

    public static boolean isMediaSufficiente(Media media) {
        return isMediaSufficiente(media, "Generale");
    }

    public static int getMediaColor(Media media, String tipo, float voto_obiettivo) {
        switch (tipo) {
            case RESTFulAPI.ORALE:
                final float media_orale = media.getMediaOrale();
                if (media_orale > voto_obiettivo)
                    return R.color.greenmaterial;
                else if (media_orale < 5)
                    return R.color.redmaterial;
                else if (media_orale >= 5 && media_orale < 6)
                    return R.color.orangematerial;
                else
                    return R.color.lightgreenmaterial;
            case RESTFulAPI.PRATICO:
                final float media_pratico = media.getMediaPratico();
                if (media_pratico > voto_obiettivo)
                    return R.color.greenmaterial;
                else if (media_pratico < 5)
                    return R.color.redmaterial;
                else if (media_pratico >= 5 && media_pratico < 6)
                    return R.color.orangematerial;
                else
                    return R.color.lightgreenmaterial;
            case RESTFulAPI.SCRITTO:
                final float media_scritto = media.getMediaScritto();
                if (media_scritto > voto_obiettivo)
                    return R.color.greenmaterial;
                else if (media_scritto < 5)
                    return R.color.redmaterial;
                else if (media_scritto >= 5 && media_scritto < 6)
                    return R.color.orangematerial;
                else
                    return R.color.lightgreenmaterial;
            default:
                final float meadia_generale = media.getMediaGenerale();
                if (meadia_generale > voto_obiettivo)
                    return R.color.greenmaterial;
                else if (meadia_generale < 5)
                    return R.color.redmaterial;
                else if (meadia_generale >= 5 && meadia_generale < 6)
                    return R.color.orangematerial;
                else
                    return R.color.lightgreenmaterial;
        }
    }

    public static int getMediaColor(Media media, float voto_obiettivo) {
        return getMediaColor(media, "Generale", voto_obiettivo);
    }

    public static int getNumberDaysAbsences(List<Absence> absences) {
        int days = 0;
        for (Absence a :
                absences) {
            days += a.getDays();
        }
        return days;
    }
}
