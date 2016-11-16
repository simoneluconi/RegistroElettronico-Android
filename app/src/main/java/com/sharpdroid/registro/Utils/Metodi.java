package com.sharpdroid.registro.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sharpdroid.registro.API.RESTFulAPI;
import com.sharpdroid.registro.Interfaces.Media;
import com.sharpdroid.registro.R;

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
                    return "Devi prendere almeno " + newvoto;
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

    public static int getMediaColor(Media media, String tipo, float obbiettivo_voto) {
        switch (tipo) {
            case RESTFulAPI.ORALE:
                final float media_orale = media.getMediaOrale();
                if (media_orale < obbiettivo_voto)
                    return R.color.lightgreenmaterial;
                else if (media_orale < 5)
                    return R.color.redmaterial;
                else if (media_orale >= 5 && media_orale < 6)
                    return R.color.orangematerial;
                else
                    return R.color.greenmaterial;
            case RESTFulAPI.PRATICO:
                final float media_pratico = media.getMediaPratico();
                if (media_pratico < obbiettivo_voto)
                    return R.color.lightgreenmaterial;
                else if (media_pratico < 5)
                    return R.color.redmaterial;
                else if (media_pratico >= 5 && media_pratico < 6)
                    return R.color.orangematerial;
                else
                    return R.color.greenmaterial;
            case RESTFulAPI.SCRITTO:
                final float media_scritto = media.getMediaScritto();
                if (media_scritto < obbiettivo_voto)
                    return R.color.lightgreenmaterial;
                else if (media_scritto < 5)
                    return R.color.redmaterial;
                else if (media_scritto >= 5 && media_scritto < 6)
                    return R.color.orangematerial;
                else
                    return R.color.greenmaterial;
            default:
                final float meadia_generale = media.getMediaGenerale();
                if (meadia_generale < obbiettivo_voto)
                    return R.color.lightgreenmaterial;
                else if (meadia_generale < 5)
                    return R.color.redmaterial;
                else if (meadia_generale >= 5 && meadia_generale < 6)
                    return R.color.orangematerial;
                else
                    return R.color.greenmaterial;
        }
    }

    public static int getMediaColor(Media media, float obbiettivo_voto) {
        return getMediaColor(media, "Generale", obbiettivo_voto);
    }
}

