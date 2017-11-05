package com.sharpdroid.registroelettronico.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.domain.Event;
import com.sharpdroid.registroelettronico.NotificationManager;
import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.api.v2.APIClient;
import com.sharpdroid.registroelettronico.database.entities.Communication;
import com.sharpdroid.registroelettronico.database.entities.CommunicationInfo;
import com.sharpdroid.registroelettronico.database.entities.FileInfo;
import com.sharpdroid.registroelettronico.database.entities.Folder;
import com.sharpdroid.registroelettronico.database.entities.Grade;
import com.sharpdroid.registroelettronico.database.entities.LocalAgenda;
import com.sharpdroid.registroelettronico.database.entities.Profile;
import com.sharpdroid.registroelettronico.database.entities.RemoteAgenda;
import com.sharpdroid.registroelettronico.database.entities.Subject;
import com.sharpdroid.registroelettronico.database.entities.SubjectTeacher;
import com.sharpdroid.registroelettronico.database.entities.SuperAgenda;
import com.sharpdroid.registroelettronico.database.entities.Teacher;
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kotlin.text.Regex;
import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Metodi {
    public static SimpleDateFormat month_year = new SimpleDateFormat("MMMM yyyy", Locale.ITALIAN);
    public static SimpleDateFormat complex = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.ITALIAN);
    private static Looper mainLooper = Looper.getMainLooper();
    private static Handler handler = new Handler(mainLooper);

    public static String MessaggioVoto(float Obb, float media, int nVoti) {
        // Calcolo
        if (Obb > 10 || media > 10)
            return "Errore"; // Quando l'obiettivo o la media sono > 10
        if (Obb >= 10 && media < Obb)
            return "Obiettivo irraggiungibile"; // Quando l'obiettivo è 10 (o più) e la media è < 10 (non si potrà mai raggiungere)
        double[] array = {0.75, 0.5, 0.25, 0};
        int index = 0;
        float sommaVotiDaPrendere;
        double[] votiMinimi = new double[5];
        double diff;
        double diff2;
        double resto = 0;
        double parteIntera;
        double parteDecimale;
        try {
            do {
                index = index + 1;
                sommaVotiDaPrendere = (Obb * (nVoti + index)) - (media * nVoti);
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
            StringBuilder toReturn;
            if (votiMinimi[0] <= 0)
                return "Puoi stare tranquillo"; // Quando i voti da prendere sono negativi
            if (votiMinimi[0] <= Obb)
                toReturn = new StringBuilder("Non prendere meno di " + votiMinimi[0]);
            else {
                toReturn = new StringBuilder("Devi prendere almeno ");
                for (double aVotiMinimi : votiMinimi) {
                    if (aVotiMinimi != 0) {
                        toReturn.append(aVotiMinimi).append(", ");
                    }
                }
                toReturn = new StringBuilder(toReturn.substring(0, toReturn.length() - 2));
            }
            return toReturn.toString();
        } catch (Exception e) {
            return "Obiettivo irraggiungibile";
        }
    }

    public static @NotNull
    String capitalizeEach(String input, boolean doSingleLetters) {
        Regex reg1 = new Regex(Pattern.compile(doSingleLetters ? "([a-z])\\w*" : "([a-z])\\w+"));
        return reg1.replace(input.toLowerCase(), matchResult -> matchResult.getValue().substring(0, 1).toUpperCase() + matchResult.getValue().substring(1).toLowerCase());
    }

    public static @NotNull
    String capitalizeEach(String input) {
        return capitalizeEach(input, false);
    }

    public static int dp(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dp(float dp) {
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

    public static int getMediaColor(Float media, float voto_obiettivo) {
        return getMarkColor(media, voto_obiettivo);
    }

    public static int getMarkColor(float voto, float voto_obiettivo) {
        if (voto == 0) return R.color.intro_blue;
        if (voto >= voto_obiettivo)
            return R.color.greenmaterial;
        else if (voto < 5)
            return R.color.redmaterial;
        else if (voto >= 5 && voto < 6)
            return R.color.orangematerial;
        else
            return R.color.lightgreenmaterial;
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
                e.printStackTrace();
                return false;
            } finally {
                if (inputStream != null)
                    inputStream.close();
                if (outputStream != null)
                    outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    public static List<Grade> sortMarksByDate(List<Grade> marks) {
        Collections.sort(marks, (o1, o2) -> o1.getMDate().compareTo(o2.getMDate()));
        return marks;
    }

    public static boolean isEventTest(SuperAgenda event) {
        return isEventTest(event.getAgenda());
    }

    public static boolean isEventTest(RemoteAgenda event) {
        String title = event.getNotes().toLowerCase();
        return title.contains("compito") || title.endsWith("compito") || title.endsWith("verifica") || title.contains("verifica ")
                || title.contains("interrogazione scritta") || title.contains("prova ") || title.contains("test ") || title.endsWith("test") || title.contains("verifiche orali");
    }

    public static List<com.github.sundeepk.compactcalendarview.domain.Event> convertEvents(List<Object> events) {
        List<com.github.sundeepk.compactcalendarview.domain.Event> list = new ArrayList<>();
        for (Object event : events) {
            if (event instanceof SuperAgenda)
                list.add(new Event(((SuperAgenda) event).getTest() ? Color.parseColor("#FF9800") : Color.WHITE, ((SuperAgenda) event).getAgenda().getStart().getTime()));
            else if (event instanceof LocalAgenda) {
                list.add(new Event(((LocalAgenda) event).getType().equalsIgnoreCase("verifica") ? Color.parseColor("#FF9800") : Color.WHITE, ((LocalAgenda) event).getDay()));
            }
        }
        return list;
    }

    public static void deleteUser(long account) {
        DatabaseHelper.database.profilesDao().delete(account);
        DatabaseHelper.database.absencesDao().delete(account);
        DatabaseHelper.database.communicationsDao().delete(account);
        DatabaseHelper.database.foldersDao().deleteFiles(account);
        DatabaseHelper.database.foldersDao().deleteFolders(account);
        DatabaseHelper.database.gradesDao().delete(account);
        DatabaseHelper.database.lessonsDao().delete(account);
        DatabaseHelper.database.eventsDao().deleteRemote(account);
        DatabaseHelper.database.subjectsDao().delete(account);
    }

    public static void fetchDataOfUser(@NotNull Context c) {
        Profile p = Profile.Companion.getProfile(c);
        updateSubjects(p);
        updateLessons(p);
        updateFolders(p);
        updateAgenda(p);
        updateAbsence(p);
        updateBacheca(p);
        updateNote(p);
        updatePeriods(p);
        updateMarks(p);
    }

    public static void updateMarks(@NotNull Context c) {
        updateMarks(Profile.Companion.getProfile(c));
    }

    public static void updateMarks(Profile p) {
        if (p == null) return;

        handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_MARKS_START, null));
        APIClient.Companion.with(p).getGrades().subscribe(gradeAPI -> {
            DatabaseHelper.database.gradesDao().delete(p.getId());
            DatabaseHelper.database.gradesDao().insert(gradeAPI.getGrades(p));
            handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_MARKS_OK, null));
        }, throwable -> {
            handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_MARKS_KO, null));
            throwable.printStackTrace();
        });
    }

    public static void updateSubjects(@NotNull Context c) {
        updateSubjects(Profile.Companion.getProfile(c));
    }

    public static void updateSubjects(Profile p) {
        if (p == null) return;
        handler.post(() -> {
            NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_SUBJECTS_START, null);
            NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_TEACHERS_START, null);
        });
        APIClient.Companion.with(p).getSubjects().subscribeOn(AndroidSchedulers.mainThread()).subscribe(subjectAPI -> {
            List<Teacher> allTeachers = new ArrayList<>();
            DatabaseHelper.database.subjectsDao().deleteSubjects(p.getId());

            for (Subject subject : subjectAPI.getSubjects()) {
                allTeachers.addAll(subject.getTeachers());
                for (Teacher t : subject.getTeachers()) {
                    SubjectTeacher obj = new SubjectTeacher(subject.getId(), t.getId(), p.getId());
                    DatabaseHelper.database.subjectsDao().deleteSingle(p.getId(), subject.getId(), t.getId());
                    DatabaseHelper.database.subjectsDao().insert(obj);
                }
            }

            DatabaseHelper.database.subjectsDao().insert(allTeachers);
            DatabaseHelper.database.subjectsDao().insert(subjectAPI.getSubjects());

            handler.post(() -> {
                NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_SUBJECTS_OK, null);
                NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_TEACHERS_OK, null);
            });
        }, throwable -> {
            handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_TEACHERS_KO, null));
            handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_SUBJECTS_KO, null));
            throwable.printStackTrace();
        });
    }

    public static void updateLessons(@NotNull Context c) {
        updateLessons(Profile.Companion.getProfile(c));
    }

    public static void updateLessons(Profile p) {
        String[] dates = getStartEnd("yyyyMMdd");
        if (p == null) return;
        handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_LESSONS_START, null));
        APIClient.Companion.with(p).getLessons(dates[0], dates[1])
                .subscribe(l -> {
                    DatabaseHelper.database.lessonsDao().delete(p.getId());
                    DatabaseHelper.database.lessonsDao().insert(l.getLessons(p));
                    handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_LESSONS_OK, null));
                }, throwable -> {
                    handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_LESSONS_KO, null));
                    throwable.printStackTrace();
                });
    }

    public static void updateFolders(@NotNull Context c) {
        updateFolders(Profile.Companion.getProfile(c));
    }

    public static void updateFolders(Profile p) {
        if (p == null) return;
        handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_FOLDERS_START, null));
        APIClient.Companion.with(p).getDidactics()
                .observeOn(Schedulers.computation())
                .subscribe(didacticAPI -> {
                    List<com.sharpdroid.registroelettronico.database.entities.File> files = new LinkedList<>();

                    DatabaseHelper.database.foldersDao().deleteFiles(p.getId());
                    DatabaseHelper.database.foldersDao().deleteFolders(p.getId());
                    DatabaseHelper.database.subjectsDao().insert(didacticAPI.getDidactics());

                    //collect folders and files
                    for (Teacher teacher : didacticAPI.getDidactics()) {
                        if (teacher != null) {
                            for (Folder folder : teacher.getFolders()) {
                                folder.setTeacher(teacher.getId());
                                folder.setProfile(p.getId());
                                if (folder.getName().equals("Uncategorized"))
                                    folder.setName("Altri materiali");
                                folder.setId(DatabaseHelper.database.foldersDao().insert(folder));

                                for (com.sharpdroid.registroelettronico.database.entities.File file : folder.getFiles()) {
                                    file.setFolder(folder.getId());
                                    file.setTeacher(teacher.getId());
                                    file.setProfile(p.getId());
                                    files.add(file);
                                }
                            }
                            teacher.setFolders(Collections.emptyList());
                        }
                    }

                    DatabaseHelper.database.foldersDao().insertFiles(files); //update otherwise will clean any additional info (path...)


                    //Download informations if not file
                    for (com.sharpdroid.registroelettronico.database.entities.File f : DatabaseHelper.database.foldersDao().getNoFiles(p.getId())) {
                        if (f.getType().equals("link"))
                            APIClient.Companion.with(p).getAttachmentUrl(f.getId())
                                    .observeOn(Schedulers.computation())
                                    .subscribe(downloadURL -> DatabaseHelper.database.foldersDao().insert(new FileInfo(f.getObjectId(), downloadURL.getItem().getLink())), Throwable::printStackTrace);
                        else
                            APIClient.Companion.with(p).getAttachmentTxt(f.getId())
                                    .observeOn(Schedulers.computation())
                                    .subscribe(downloadTXT -> DatabaseHelper.database.foldersDao().insert(new FileInfo(f.getObjectId(), downloadTXT.getItem().getText())));
                    }
                    handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_FOLDERS_OK, null));
                }, throwable -> {
                    handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_FOLDERS_KO, null));
                    throwable.printStackTrace();
                });

    }

    public static void updateAgenda(@NotNull Context c) {
        updateAgenda(Profile.Companion.getProfile(c));
    }

    public static void updateAgenda(Profile p) {
        String[] dates = getStartEnd("yyyyMMdd");
        if (p == null) return;
        handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_AGENDA_START, null));
        APIClient.Companion.with(p).getAgenda(dates[0], dates[1])
                .subscribe(agendaAPI -> {
                    List<RemoteAgenda> apiAgenda = agendaAPI.getAgenda(p);
                    DatabaseHelper.database.eventsDao().deleteRemote(p.getId());
                    DatabaseHelper.database.eventsDao().insert(apiAgenda);
                    handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_AGENDA_OK, null));
                }, throwable -> {
                    handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_AGENDA_KO, null));
                    throwable.printStackTrace();
                });
    }

    public static void updateAbsence(@NotNull Context c) {
        updateAbsence(Profile.Companion.getProfile(c));
    }

    public static void updateAbsence(Profile p) {
        if (p == null) return;
        handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_ABSENCES_START, null));
        APIClient.Companion.with(p).getAbsences()
                .subscribe(absenceAPI -> {
                    DatabaseHelper.database.absencesDao().delete(p.getId());
                    DatabaseHelper.database.absencesDao().insert(absenceAPI.getEvents(p));
                    handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_ABSENCES_OK, null));
                }, throwable -> {
                    handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_ABSENCES_KO, null));
                    throwable.printStackTrace();
                });
    }

    public static void updateBacheca(@NotNull Context c) {
        updateBacheca(Profile.Companion.getProfile(c));
    }

    public static void updateBacheca(Profile p) {
        if (p == null) return;
        handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_BACHECA_START, null));
        APIClient.Companion.with(p).getBacheca()
                .observeOn(Schedulers.computation())
                .subscribe(communicationAPI -> {
                    List<Communication> list = communicationAPI.getCommunications(p);
                    List<Communication> toRemove = new ArrayList<>();

                    for (Communication communication : list) {
                        if (communication.getCntStatus().equals("deleted")) {
                            toRemove.add(communication);
                            continue;
                        }

                        @Nullable CommunicationInfo info = DatabaseHelper.database.communicationsDao().getInfo(communication.getMyId());
                        if (info == null || !communication.isRead() || info.getContent().isEmpty()) {
                            System.out.println("REQUEST - " + communication.getTitle());
                            APIClient.Companion.with(p)
                                    .readBacheca(communication.getEvtCode(), communication.getId())
                                    .observeOn(Schedulers.computation())
                                    .subscribe(readResponse -> {
                                communication.setRead(true);
                                DatabaseHelper.database.communicationsDao().insert(communication);

                                CommunicationInfo downloadedInfo = readResponse.getItem();
                                downloadedInfo.setId(communication.getMyId());
                                downloadedInfo.setContent(
                                        downloadedInfo.getContent().isEmpty() ?
                                                ((info != null) ?
                                                        info.getContent() : "") :
                                                downloadedInfo.getContent());
                                DatabaseHelper.database.communicationsDao().insert(downloadedInfo);
                            });
                        }
                    }
                    list.removeAll(toRemove);

                    DatabaseHelper.database.communicationsDao().delete(p.getId());
                    DatabaseHelper.database.communicationsDao().insert(list);
                    handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_BACHECA_OK, null));
                }, throwable -> {
                    handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_BACHECA_KO, null));
                    throwable.printStackTrace();
                });
    }

    public static void updateNote(@NotNull Context c) {
        updateNote(Profile.Companion.getProfile(c));
    }

    public static void updateNote(Profile p) {
        if (p == null) return;
        handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_NOTES_START, null));
        APIClient.Companion.with(p).getNotes()
                .subscribe(notes -> {
                    DatabaseHelper.database.notesDao().delete(p.getId());
                    DatabaseHelper.database.notesDao().insert(notes.getNotes(p));
                    handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_NOTES_OK, null));
                }, throwable -> {
                    handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_NOTES_KO, null));
                    throwable.printStackTrace();
                });
    }

    public static void updatePeriods(@NotNull Context c) {
        updatePeriods(Profile.Companion.getProfile(c));
    }


    public static void updatePeriods(Profile p) {/*
        if (p == null) return;
        handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_PERIODS_START, null));
        APIClient.Companion.with(p).getPeriods()
                .subscribe(notes -> {
                    SugarRecord.deleteAll(Period.class, "PROFILE=?", String.valueOf(p.getId()));
                    SugarRecord.saveInTx(notes.getPeriods(p));
                    handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_PERIODS_OK, null));
                }, throwable -> {
                    handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.UPDATE_PERIODS_KO, null));
                    throwable.printStackTrace();
                });*/
    }

    public static void downloadAttachment(@NotNull Context c, Communication communication) {
        downloadAttachment(c, communication, Profile.Companion.getProfile(c));
    }

    public static void downloadAttachment(@NotNull Context c, Communication communication, Profile p) {
        if (p == null) return;
        File dir = new File(
                Environment.getExternalStorageDirectory() +
                        File.separator +
                        "Registro Elettronico" + File.separator + "Circolari");

        handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.DOWNLOAD_FILE_START, new Long[]{communication.getMyId()}));
        APIClient.Companion.with(p).getBachecaAttachment(communication.getEvtCode(), communication.getId())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String filename = getFileNamefromHeaders(response.headers());
                        if (!dir.exists()) dir.mkdirs();
                        File fileDir = new File(dir, filename);

                        CommunicationInfo communicationInfo = DatabaseHelper.database.communicationsDao().getInfo(communication.getMyId());
                        if (communicationInfo == null) communicationInfo = new CommunicationInfo();
                        communicationInfo.setId(communication.getMyId());

                        if (fileDir.exists()) {      //File esistente ma non salvato nel db
                            communicationInfo.setPath(fileDir.getAbsolutePath());
                            DatabaseHelper.database.communicationsDao().update(communicationInfo);
                            handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.DOWNLOAD_FILE_OK, new Long[]{communication.getMyId()}));
                        } else if (writeResponseBodyToDisk(response.body(), fileDir)) {
                            communicationInfo.setPath(fileDir.getAbsolutePath());
                            DatabaseHelper.database.communicationsDao().update(communicationInfo);
                            handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.DOWNLOAD_FILE_OK, new Long[]{communication.getMyId()}));
                        } else {
                            handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.DOWNLOAD_FILE_KO, new Long[]{communication.getMyId()}));
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                        handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.DOWNLOAD_FILE_KO, new Long[]{communication.getMyId()}));
                    }
                });
    }

    public static void downloadFile(@NotNull Context c, com.sharpdroid.registroelettronico.database.entities.File f) {
        downloadFile(c, f, Profile.Companion.getProfile(c));
    }

    public static void downloadFile(@NotNull Context c, com.sharpdroid.registroelettronico.database.entities.File f, Profile p) {
        if (p == null) return;

        File dir = new File(
                Environment.getExternalStorageDirectory() +
                        File.separator +
                        "Registro Elettronico" + File.separator + "Didattica");

        handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.DOWNLOAD_FILE_START, new Long[]{f.getObjectId()}));
        APIClient.Companion.with(p).getAttachmentFile((int) f.getId())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String filename = getFileNamefromHeaders(response.headers());
                        if (!dir.exists()) dir.mkdirs();
                        File fileDir = new File(dir, filename);

                        FileInfo info = new FileInfo(f.getObjectId(), fileDir.getAbsolutePath());
                        if (DatabaseHelper.database.foldersDao().insert(info) > 0 && writeResponseBodyToDisk(response.body(), fileDir))
                            handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.DOWNLOAD_FILE_OK, new Long[]{f.getObjectId()}));
                        else
                            handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.DOWNLOAD_FILE_KO, new Long[]{f.getObjectId()}));

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                        handler.post(() -> NotificationManager.Companion.getInstance().postNotificationName(EventType.DOWNLOAD_FILE_KO, new Long[]{f.getObjectId()}));
                    }
                });
    }

    public static String[] getStartEnd(String format) {
        Calendar from, to;
        from = Calendar.getInstance();
        to = Calendar.getInstance();

        if (from.get(Calendar.MONTH) >= Calendar.SEPTEMBER) { // Prima di gennaio
            to.add(Calendar.YEAR, 1);
        } else {
            from.add(Calendar.YEAR, -1);
        }
        from.set(Calendar.DAY_OF_MONTH, 1);
        from.set(Calendar.MONTH, Calendar.SEPTEMBER);

        to.set(Calendar.DAY_OF_MONTH, 31);
        to.set(Calendar.MONTH, Calendar.AUGUST);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return new String[]{simpleDateFormat.format(from.getTime()), simpleDateFormat.format(to.getTime())};
    }

    public static String capitalizeFirst(String a) {
        return a.substring(0, 1).toUpperCase() + a.substring(1);
    }

    public static void loginFeedback(Throwable error, Context c) {
        error.printStackTrace();
        if (error instanceof HttpException) {
            if (((HttpException) error).code() == 422)
                Toast.makeText(c, R.string.credenziali, Toast.LENGTH_LONG).show();
            else if (((HttpException) error).code() == 400) {
                Toast.makeText(c, "Bad request", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(c, c.getString(R.string.login_msg_failer, error.getLocalizedMessage()), Toast.LENGTH_LONG).show();
        }
    }

    public static void openFile(Context context, File file, Snackbar bar) {
        String mime = URLConnection.guessContentTypeFromName(file.toString());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file), mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            bar.show();
        }
    }

    public static void openLink(Context context, String url, Snackbar snackbar) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) url = "http://" + url;

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            context.startActivity(browserIntent);
        } catch (ActivityNotFoundException e) {
            snackbar.show();
        }
    }

    public static String eventToString(SuperAgenda e, String head) {
        return capitalizeFirst(complex.format(e.getAgenda().getStart())) + "\n" + capitalizeFirst(e.getAgenda().getNotes());
    }

    public static Bitmap AccountImage(String nome) {
        Bitmap src = Bitmap.createBitmap(255, 255, Bitmap.Config.ARGB_8888);
        src.eraseColor(Color.parseColor("#03A9F4"));
        StringBuilder nomef = new StringBuilder();
        String[] lett = nome.split("\\s+");
        for (String s :
                lett) {
            nomef.append(s.substring(0, 1).toUpperCase());
        }
        Canvas cs = new Canvas(src);
        Paint tPaint = new Paint();
        float reduce = tPaint.measureText(nomef.toString());
        tPaint.setTextSize(100 - reduce);
        tPaint.setColor(Color.WHITE);
        tPaint.setStyle(Paint.Style.FILL);
        tPaint.setTextAlign(Paint.Align.CENTER);
        float x_coord = src.getWidth() / 2;
        float height = (src.getHeight() / 2) + 33;
        cs.drawText(nomef.toString(), x_coord, height, tPaint);
        return src;
    }

}

