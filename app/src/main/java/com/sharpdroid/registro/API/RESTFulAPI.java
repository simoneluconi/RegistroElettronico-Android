package com.sharpdroid.registro.API;

public class RESTFulAPI {
    public static final String ORALE = "Orale";
    public static final String SCRITTO = "Scritto/Grafico";
    public static final String PRATICO = "Pratico";

    static private final String BASE_URL = "https://api.daniele.ml/";
    public static final String LOGIN_URL = BASE_URL + "login";
    public static final String FILES_URL = BASE_URL + "files";
    public static final String ABSENCES_URL = BASE_URL + "absences";
    public static final String NOTES_URL = BASE_URL + "notes";
    public static final String SCRUTINIES_URL = BASE_URL + "scrutinies";
    public static final String MARKS_URL = BASE_URL + "marks";
    public static final String SUBJECTS_URL = BASE_URL + "subjects";
    public static final String COMMUNICATIONS_URL = BASE_URL + "communications";

    public RESTFulAPI() {

    }

    public String FILE_DOWNLOAD_URL(String id, String cksum) {
        return String.format("%s/%s/%s/%s/download", BASE_URL, "file", id, cksum);
    }

    public String SUBJECT_LESSONS_URL(String id) {
        return String.format("%s/%s/lessons", SUBJECTS_URL, id);
    }

    public String COMMUNICATION_DESC_URL(String id) {
        return String.format("%s/%s/desc", COMMUNICATIONS_URL, id);
    }

    public String COMMUNICATION_DOWNLOAD_URL(String id) {
        return String.format("%s/%s/download", BASE_URL, id);
    }
}
