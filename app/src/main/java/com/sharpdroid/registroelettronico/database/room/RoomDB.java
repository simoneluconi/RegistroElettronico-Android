package com.sharpdroid.registroelettronico.database.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.sharpdroid.registroelettronico.database.dao.AbsenceDao;
import com.sharpdroid.registroelettronico.database.dao.AgendaDao;
import com.sharpdroid.registroelettronico.database.dao.CommunicationDao;
import com.sharpdroid.registroelettronico.database.dao.FolderDao;
import com.sharpdroid.registroelettronico.database.dao.GradeDao;
import com.sharpdroid.registroelettronico.database.dao.LessonDao;
import com.sharpdroid.registroelettronico.database.dao.NoteDao;
import com.sharpdroid.registroelettronico.database.dao.ProfileDao;
import com.sharpdroid.registroelettronico.database.dao.SubjectDao;
import com.sharpdroid.registroelettronico.database.entities.Absence;
import com.sharpdroid.registroelettronico.database.entities.Communication;
import com.sharpdroid.registroelettronico.database.entities.CommunicationInfo;
import com.sharpdroid.registroelettronico.database.entities.File;
import com.sharpdroid.registroelettronico.database.entities.FileInfo;
import com.sharpdroid.registroelettronico.database.entities.Folder;
import com.sharpdroid.registroelettronico.database.entities.Grade;
import com.sharpdroid.registroelettronico.database.entities.Lesson;
import com.sharpdroid.registroelettronico.database.entities.LocalAgenda;
import com.sharpdroid.registroelettronico.database.entities.LocalGrade;
import com.sharpdroid.registroelettronico.database.entities.Note;
import com.sharpdroid.registroelettronico.database.entities.Period;
import com.sharpdroid.registroelettronico.database.entities.Profile;
import com.sharpdroid.registroelettronico.database.entities.RemoteAgenda;
import com.sharpdroid.registroelettronico.database.entities.RemoteAgendaInfo;
import com.sharpdroid.registroelettronico.database.entities.Subject;
import com.sharpdroid.registroelettronico.database.entities.SubjectInfo;
import com.sharpdroid.registroelettronico.database.entities.SubjectTeacher;
import com.sharpdroid.registroelettronico.database.entities.Teacher;

@Database(entities = {
        Absence.class,
        RemoteAgenda.class,
        RemoteAgendaInfo.class,
        LocalAgenda.class,
        Communication.class,
        CommunicationInfo.class,
        File.class,
        FileInfo.class,
        Folder.class,
        Grade.class,
        LocalGrade.class,
        Lesson.class,
        Note.class,
        Period.class,
        Profile.class,
        Subject.class,
        SubjectInfo.class,
        SubjectTeacher.class,
        Teacher.class
}, version = 7)
@TypeConverters({Converters.class})
public abstract class RoomDB extends RoomDatabase {
    public abstract AbsenceDao absencesDao();

    public abstract AgendaDao eventsDao();

    public abstract CommunicationDao communicationsDao();

    public abstract FolderDao foldersDao();

    public abstract GradeDao gradesDao();

    public abstract LessonDao lessonsDao();

    public abstract NoteDao notesDao();

    public abstract ProfileDao profilesDao();

    public abstract SubjectDao subjectsDao();
}
