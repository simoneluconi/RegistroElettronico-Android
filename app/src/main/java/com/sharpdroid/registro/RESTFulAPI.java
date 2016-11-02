package com.sharpdroid.registro;

class RESTFulAPI {
    private final String BASE_URL = "https://api.daniele.ml/";

    final String LOGIN_URL = BASE_URL + "login";

    final String MARKS_URL = BASE_URL + "marks";

    final String FILES_URL = BASE_URL + "file";

    final String ABSANCES_URL = BASE_URL + "absences";

    final String SUBJECTS_URL = BASE_URL + "subjects";

    final String NOTES_URL = BASE_URL + "notes";

    final String COMMUNICATIONS_URL = BASE_URL + "communications";

    final String SCRUTINIES_URL = BASE_URL + "scrutinies";

    RESTFulAPI() {

    }

    public String FILE_DOWNLOAD_URL(String id, String cksum) {
        return String.format("%s/%s/%s/download", FILES_URL, id, cksum);
    }

    public String SUBJECT_LESSONS_URL(String id) {
        return String.format("%s/%s/lessons", SUBJECTS_URL, id);
    }

    public String COMMUNICATION_URL(String id) {
        return String.format("%s/%s/desc", COMMUNICATIONS_URL, id);
    }
}
