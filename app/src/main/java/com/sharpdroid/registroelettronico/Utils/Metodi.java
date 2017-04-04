package com.sharpdroid.registroelettronico.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.CalendarContract;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.sharpdroid.registroelettronico.API.SpiaggiariAPI;
import com.sharpdroid.registroelettronico.API.SpiaggiariApiClient;
import com.sharpdroid.registroelettronico.Databases.RegistroDB;
import com.sharpdroid.registroelettronico.Interfaces.API.Absence;
import com.sharpdroid.registroelettronico.Interfaces.API.Absences;
import com.sharpdroid.registroelettronico.Interfaces.API.Delay;
import com.sharpdroid.registroelettronico.Interfaces.API.Event;
import com.sharpdroid.registroelettronico.Interfaces.API.Exit;
import com.sharpdroid.registroelettronico.Interfaces.API.Lesson;
import com.sharpdroid.registroelettronico.Interfaces.API.LessonSubject;
import com.sharpdroid.registroelettronico.Interfaces.API.Mark;
import com.sharpdroid.registroelettronico.Interfaces.API.MarkSubject;
import com.sharpdroid.registroelettronico.Interfaces.Client.AbsenceEntry;
import com.sharpdroid.registroelettronico.Interfaces.Client.AbsencesEntry;
import com.sharpdroid.registroelettronico.Interfaces.Client.AdvancedEvent;
import com.sharpdroid.registroelettronico.Interfaces.Client.DelayEntry;
import com.sharpdroid.registroelettronico.Interfaces.Client.Entry;
import com.sharpdroid.registroelettronico.Interfaces.Client.ExitEntry;
import com.sharpdroid.registroelettronico.Interfaces.Client.Media;
import com.sharpdroid.registroelettronico.Interfaces.Client.Subject;
import com.sharpdroid.registroelettronico.R;

import org.apache.commons.lang3.text.WordUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import okhttp3.Headers;
import okhttp3.ResponseBody;

public class Metodi {
    public static SimpleDateFormat month_year = new SimpleDateFormat("MMMM yyyy", Locale.ITALIAN);
    public static SimpleDateFormat complex = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.ITALIAN);

    public static char[] Delimeters = {'.', ' ', '\'', '/', '\\'};

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
            return "Impossibile raggiungere la media del " + Obb; // Quando l'obiettivo è 10 (o più) e la media è < 10 (non si potrà mai raggiungere)
        double[] array = {0.75, 0.5, 0.25, 0};
        int index = 0;
        float sommaVotiDaPrendere;
        double[] votiMinimi = new double[20];
        double diff;
        double diff2;
        double resto = 0;
        double parteIntera;
        double parteDecimale;
        try {
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
        } catch (Exception e)
        {
            e.printStackTrace();
            return "Impossibile calcolare i voti per arrivare all'obiettivo";
        }
    }

    public static MyLinkedMap<String, Integer> sortByComparator(MyLinkedMap<String, Integer> unsortMap, final boolean order) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortMap.entrySet());

        // Sorting the list based on values

        Collections.sort(list, (Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) -> {
            if (order) {
                return o1.getValue().compareTo(o2.getValue());
            } else {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        // Maintaining insertion order with the help of LinkedList
        MyLinkedMap<String, Integer> sortedMap = new MyLinkedMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
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

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int CalculateScholasticCredits(int year, double average) {
        switch (year) {
            case 3:
            case 4:
                if (average == 6) return 3;
                else if (average > 6 && average <= 7) return 4;
                else if (average > 7 && average <= 8) return 5;
                else if (average > 8 && average <= 9) return 6;
                else if (average > 9 && average <= 10) return 7;
                break;

            case 5:
                if (average == 6) return 4;
                else if (average > 6 && average <= 7) return 5;
                else if (average > 7 && average <= 8) return 6;
                else if (average > 8 && average <= 9) return 7;
                else if (average > 9 && average <= 10) return 8;
                break;

            default:
                return 0;
        }

        return 0;
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

    public static String toLowerCase(String s) {
        return (s != null) ? s.toLowerCase() : null;
    }

    public static String getSubjectName(Subject subject) {
        try {
            return (!TextUtils.isEmpty(subject.getName())) ? subject.getName() : WordUtils.capitalize(subject.getOriginalName(), Delimeters);
        } catch (NullPointerException e) {
            return subject.getOriginalName();
        }
    }

    public static float getOverallAverage(List<MarkSubject> subjects) {
        float media = 0f;
        int n = subjects.size();

        for (MarkSubject subject : subjects) {
            Media _media = new Media();
            _media.addMarks(subject.getMarks());

            if (_media.containsValidMarks())
                media += _media.getMediaGenerale();
            else n--;
        }
        return media / n;
    }

    public static Media getHypotheticalAverage(MarkSubject markSubject, Mark mark) {
        Media m = new Media();
        m.addMarks(markSubject.getMarks());
        m.addMark(mark);
        return m;
    }

    public static int getPossibileSubjectTarget(double media) {
        if (media < 6)
            return 6;
        else {
            Double m = Math.ceil(media);
            return m.intValue();
        }
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
                    if (read == -1)
                        break;
                    outputStream.write(fileReader, 0, read);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null)
                    inputStream.close();
                if (outputStream != null)
                    outputStream.close();
            }
        } catch (IOException e) {
            return false;
        }
    }

    public static String getFileNamefromHeaders(Headers headers) {
        String contentd = headers.get("Content-Disposition");
        contentd = contentd.replace("attachment; filename=", "");
        contentd = contentd.replaceAll("\"", "");
        contentd = contentd.trim();
        return contentd;
    }

    public static List<Mark> sortMarksByDate(List<Mark> marks) {
        Collections.sort(marks, (o1, o2) -> o1.getDate().compareTo(o2.getDate()));
        return marks;
    }

    public static Map<String, List<Entry>> convertAbsencesToHashmap(Absences absences) {
        Map<String, List<Entry>> hashMap = new HashMap<>();

        //assenze
        for (Absence absence : absences.getAbsences()) {
            String month = month_year.format(absence.getFrom());
            if (hashMap.containsKey(month)) {
                List<Entry> entries = new ArrayList<>(hashMap.get(month));
                entries.add(new AbsenceEntry(absence));
                hashMap.put(month, entries);
            } else {
                hashMap.put(month, Collections.singletonList(new AbsenceEntry(absence)));
            }
        }
        //uscite
        for (Exit exit : absences.getExits()) {
            String month = month_year.format(exit.getDay());
            if (hashMap.containsKey(month)) {
                List<Entry> entries = new ArrayList<>(hashMap.get(month));
                entries.add(new ExitEntry(exit));
                hashMap.put(month, entries);
            } else {
                hashMap.put(month, Collections.singletonList(new ExitEntry(exit)));
            }
        }
        //ritardi
        for (Delay delay : absences.getDelays()) {
            String month = month_year.format(delay.getDay());
            if (hashMap.containsKey(month)) {
                List<Entry> entries = new ArrayList<>(hashMap.get(month));
                entries.add(new DelayEntry(delay));
                hashMap.put(month, entries);
            } else {
                hashMap.put(month, Collections.singletonList(new DelayEntry(delay)));
            }
        }
        return hashMap;
    }

    public static LinkedHashMap<String, List<Entry>> sortByDate(Map<String, List<Entry>> unsort) {
        LinkedHashMap<String, List<Entry>> sort = new LinkedHashMap<>();
        List<String> keys = new ArrayList<>(unsort.keySet());

        //ordina i mesi
        Collections.sort(keys, (s, t1) -> {
            try {
                return month_year.parse(t1).compareTo(month_year.parse(s));
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        });

        List<Entry> entries;

        for (String key : keys) {
            entries = new ArrayList<>(unsort.get(key));

            //ordina valori di ogni mese
            Collections.sort(entries, (entry, t1) -> {
                        if (entry instanceof AbsencesEntry && t1 instanceof AbsencesEntry) {
                            return ((AbsencesEntry) t1).getTime().compareTo(((AbsencesEntry) entry).getTime());
                        } else
                            return 0;
                    }
            );

            sort.put(key, entries);
        }
        return sort;
    }

    public static List<MarkSubject> getMarksOfThisPeriod(List<MarkSubject> markssubject, String p) {
        if (p == null) return markssubject;
        List<MarkSubject> marksSub = new ArrayList<>();
        for (MarkSubject s : markssubject) {
            List<Mark> marks = new ArrayList<>();
            for (Mark m : s.getMarks())
                if (m.getQ().equals(p))
                    marks.add(m);

            if (!marks.isEmpty())
                marksSub.add(new MarkSubject(s.getName(), marks));
        }

        return marksSub;
    }

    public static int[] splitTeachersCodes(String teacherCodes) {
        String[] s = teacherCodes.split(",");
        int[] c = new int[s.length];

        for (int i = 0; i < c.length; i++)
            c[i] = Integer.parseInt(s[i]);

        return c;
    }

    public static String getProfessorOfThisSubject(List<Lesson> lessons) {
        if (!lessons.isEmpty()) {
            return lessons.get(0).getTeacher();
        }
        return "";
    }

    public static boolean isEventTest(com.sharpdroid.registroelettronico.Interfaces.API.Event event) {
        String title = event.getTitle().toLowerCase();
        return title.contains("compito") || title.endsWith("compito") || title.endsWith("verifica") || title.contains("verifica ")
                || title.contains("interrogazione scritta") || title.contains("prova ") || title.contains("test ") || title.endsWith("test");
    }

    public static List<com.github.sundeepk.compactcalendarview.domain.Event> convertEvents(List<AdvancedEvent> events) {
        List<com.github.sundeepk.compactcalendarview.domain.Event> list = new ArrayList<>();
        for (com.sharpdroid.registroelettronico.Interfaces.API.Event event : events) {
            list.add(new com.github.sundeepk.compactcalendarview.domain.Event(isEventTest(event) ? Color.parseColor("#FF9800") : Color.WHITE, event.getStart().getTime(), null));
        }
        return list;
    }

    public static void updateSubjects(Context c) {
        //scarica le materie (nome, id, prof) per poter in seguito modificare a piacere tutte le caratteristiche nel db
        new SpiaggiariApiClient(c)
                .getSubjects()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subjects -> {
                    RegistroDB db = new RegistroDB(c);

                    //Per ogni materia aggiungo il suo professore cercandolo dalle lezioni
                    for (LessonSubject subject : subjects) {
                        for (Integer teacher_code : subject.getTeacherCodes())
                            new SpiaggiariApiClient(c)
                                    .getLessons(subject.getCode(), String.valueOf(teacher_code))
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(lessons -> {
                                        String profName = getProfessorOfThisSubject(lessons);

                                        db.addSubject(subject);
                                        db.removeLessons(teacher_code);
                                        db.addLessons(subject.getCode(), teacher_code, lessons);
                                        db.addProfessor(subject.getCode(), teacher_code, profName);

                                        //Log.d("Trova professore", String.format(Locale.getDefault(), "Professore di %1$s è %2$s", subject.getName(), profName));
                                    }, Throwable::printStackTrace);
                    }

                    db.close();
                }, Throwable::printStackTrace);
    }

    public static void addEventToCalendar(Context c, Event event) {
        Intent calIntent = new Intent(Intent.ACTION_INSERT);
        calIntent.setType("vnd.android.cursor.item/event");
        calIntent.putExtra(CalendarContract.Events.DESCRIPTION, event.getTitle());
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, event.isAllDay());
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.getStart().getTime());
        if (!event.isAllDay())
            calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.getEnd().getTime());
        c.startActivity(calIntent);
    }

    public static String capitalizeFirst(String a) {
        return a.substring(0, 1).toUpperCase() + a.substring(1);
    }

    public static String getSubjectNameOrProfessorName(Event event, RegistroDB db) {
        String subjectOrProf = TextUtils.isEmpty(event.getMateria_desc()) ? db.getSubjectOrProfessorName(event.getAutore_id()) : event.getMateria_desc();
        return WordUtils.capitalizeFully(TextUtils.isEmpty(subjectOrProf) ? event.getAutore_desc() : subjectOrProf, Delimeters);
    }

    public static String eventToString(Event e, String head) {
        return capitalizeFirst(complex.format(e.getStart())) + "\n---" + head + "---\n" + capitalizeFirst(e.getTitle()) + (e.getNota_2().trim().equalsIgnoreCase(e.getTitle().trim()) ? "" : "\n" + e.getNota_2());
    }

    public static List<String> getNamesFromSubjects(List<Subject> subjects) {
        List<String> names = new ArrayList<>();
        for (Subject s : subjects) {
            names.add(getSubjectName(s));
        }
        return names;
    }

    public static int[] getCodesFromSubjects(List<Subject> subjects) {
        int[] codes = new int[subjects.size()];
        for (int i = 0; i < subjects.size(); i++) {
            codes[i] = subjects.get(i).getCode();
        }
        return codes;
    }

    public static <T, K> List<T> pairToFirst(List<Pair<T, K>> pairs) {
        List<T> seconds = new ArrayList<>();
        for (Pair<T, K> pair : pairs) {
            seconds.add(pair.first);
        }
        return seconds;
    }

    public static <T, K> List<K> pairToSecond(List<Pair<T, K>> pairs) {
        List<K> seconds = new ArrayList<>();
        for (Pair<T, K> pair : pairs) {
            seconds.add(pair.second);
        }
        return seconds;
    }

    public static int[] toIntArray(List<Integer> list) {
        int[] array = new int[list.size()];

        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }

        return array;
    }

    public static <S extends CharSequence> List<String> capitalizeList(List<S> list) {
        List<String> capitalized = new ArrayList<>();
        for (S s : list) {
            capitalized.add(WordUtils.capitalizeFully(s.toString(), Delimeters));
        }
        return capitalized;
    }
}

