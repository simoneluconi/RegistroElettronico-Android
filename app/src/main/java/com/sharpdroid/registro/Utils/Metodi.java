package com.sharpdroid.registro.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sharpdroid.registro.API.SpiaggiariAPI;
import com.sharpdroid.registro.Interfaces.Absence;
import com.sharpdroid.registro.Interfaces.Delay;
import com.sharpdroid.registro.Interfaces.Exit;
import com.sharpdroid.registro.Interfaces.FileTeacher;
import com.sharpdroid.registro.Interfaces.Folder;
import com.sharpdroid.registro.Interfaces.Mark;
import com.sharpdroid.registro.Interfaces.MarkSubject;
import com.sharpdroid.registro.Interfaces.Media;
import com.sharpdroid.registro.Interfaces.Subject;
import com.sharpdroid.registro.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;

public class Metodi {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public static String MessaggioVoto(float Obb, float media, int voti) {
        // Calcolo
        if (Obb > 10 || media > 10)
            return "Errore"; // Quando l'obiettivo o la media sono > 10
        if (Obb >= 10 && media < Obb)
            return "Impossibile raggiungere la media del " + media; // Quando l'obiettivo è 10 (o più) e la media è < 10 (non si potrà mai raggiungere)
        double[] array = {0.75, 0.5, 0.25, 0};
        int index = 0;
        float sommaVotiDaPrendere;
        double[] votiMinimi = new double[20];
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
            return "Puoi stare tranquillo"; // Quando i voti da prendere sono negativi
        if (votiMinimi[0] <= Obb)
            toReturn = "Non prendere meno di " + votiMinimi[0];
        else {
            toReturn = "Devi prendere almeno ";
            for (double aVotiMinimi : votiMinimi) {
                if (aVotiMinimi != 0) {
                    toReturn = toReturn + aVotiMinimi + ", ";
                }
            }
            toReturn = toReturn.substring(0, toReturn.length() - 2);
        }
        return toReturn;
    }

    public static boolean isMediaSufficiente(Media media, String tipo) {
        switch (tipo) {
            case SpiaggiariAPI.ORALE:
                return media.getMediaOrale() > 6;
            case SpiaggiariAPI.PRATICO:
                return media.getMediaPratico() > 6;
            case SpiaggiariAPI.SCRITTO:
                return media.getMediaScritto() > 6;
            default:
                return media.getMediaGenerale() > 6;
        }
    }

    public static boolean isMediaSufficiente(Media media) {
        return isMediaSufficiente(media, "Generale");
    }

    public static int getMarkColor(float voto, float voto_obiettivo) {
        if (voto >= voto_obiettivo)
            return R.color.greenmaterial;
        else if (voto < 5)
            return R.color.redmaterial;
        else if (voto >= 5 && voto < 6)
            return R.color.orangematerial;
        else
            return R.color.lightgreenmaterial;
    }

    public static int getMarkColor(Mark mark, float voto_obiettivo) {
        if (!mark.isNs()) {
            float voto = Float.parseFloat(mark.getMark());
            if (voto >= voto_obiettivo)
                return R.color.greenmaterial;
            else if (voto < 5)
                return R.color.redmaterial;
            else if (voto >= 5 && voto < 6)
                return R.color.orangematerial;
            else
                return R.color.lightgreenmaterial;
        } else {
            return R.color.intro_blue;
        }
    }

    public static int getMediaColor(Media media, String tipo, float voto_obiettivo) {
        switch (tipo) {
            case SpiaggiariAPI.ORALE:
                return getMarkColor(media.getMediaOrale(), voto_obiettivo);
            case SpiaggiariAPI.PRATICO:
                return getMarkColor(media.getMediaPratico(), voto_obiettivo);
            case SpiaggiariAPI.SCRITTO:
                return getMarkColor(media.getMediaScritto(), voto_obiettivo);
            default:
                return getMarkColor(media.getMediaGenerale(), voto_obiettivo);
        }
    }

    public static int getMediaColor(Media media, float voto_obiettivo) {
        return getMediaColor(media, "Generale", voto_obiettivo);
    }

    public static int getNumberDaysAbsences(List<Absence> absences) {
        int days = 0;
        for (Absence a : absences) {
            days += a.getDays();
        }
        return days;
    }

    public static FileTeacher getFileTeacherFromPositionInList(List<FileTeacher> list, int p) {
        int acc = 0;
        for (FileTeacher fileTeacher : list) {
            acc += 1 + fileTeacher.getFolders().size();
            if (p < acc) return fileTeacher;
        }
        return null;
    }

    public static List<Integer> getListLayouts(List<FileTeacher> data) {
        List<Integer> list = new ArrayList<>();

        for (FileTeacher fileTeacher : data) {
            list.add(R.layout.adapter_file_teacher);
            for (Folder ignored : fileTeacher.getFolders()) {
                list.add(R.layout.adapter_folder);
            }
        }
        return list;
    }

    public static int getUndoneCountAbsences(List<Absence> absences) {
        int c = 0;
        if (absences == null) return c;
        for (Absence a : absences) {
            if (!a.isDone()) c++;
        }
        return c;
    }

    public static int getUndoneCountDelays(List<Delay> delays) {
        int c = 0;
        if (delays == null) return c;
        for (Delay d : delays) {
            if (!d.isDone()) c++;
        }
        return c;
    }

    public static int getUndoneCountExits(List<Exit> exits) {
        int c = 0;
        if (exits == null) return c;
        for (Exit e : exits) {
            if (!e.isDone()) c++;
        }
        return c;
    }

    public static String NomeDecente(String name) {
        if (!isEmptyOrNull(name)) {
            String new_name = "";
            String[] insV = name.trim().split("\\s+");
            for (String ins : insV) {
                new_name += ins.substring(0, 1).toUpperCase() + ins.substring(1).toLowerCase() + " ";
            }
            return new_name;
        } else {
            return name;
        }
    }

    public static String beautifyName(String name) {
        if (!isEmptyOrNull(name))
            return name.substring(0, 1).toUpperCase(Locale.getDefault()) + name.substring(1).toLowerCase();
        else return name;
    }

    public static String getSubjectName(Subject subject) {
        return (!isEmptyOrNull(subject.getName())) ? subject.getName() : subject.getOriginalName();
    }

    public static float getOverallAverage(List<MarkSubject> subjects) {
        float media = 0f;
        int n = subjects.size();

        for (MarkSubject subject : subjects) {
            Media _media = new Media();
            _media.addMarks(subject.getMarks());

            media += _media.getMediaGenerale();
        }
        return media / n;
    }


    public static boolean writeResponseBodyToDisk(ResponseBody body, File file) {
        try {
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isEmptyOrNull(String string) {
        return string == null || string.isEmpty();
    }

    public static List<Mark> sortMarksByDate(List<Mark> marks) {
        Collections.sort(marks, (o1, o2) -> o1.getDate().compareTo(o2.getDate()));
        return marks;
    }
}
