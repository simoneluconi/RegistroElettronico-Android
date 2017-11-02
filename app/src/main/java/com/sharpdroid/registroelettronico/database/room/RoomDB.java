package com.sharpdroid.registroelettronico.database.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.sharpdroid.registroelettronico.database.entities.Absence;
import com.sharpdroid.registroelettronico.database.entities.Communication;
import com.sharpdroid.registroelettronico.database.entities.File;
import com.sharpdroid.registroelettronico.database.entities.Folder;
import com.sharpdroid.registroelettronico.database.entities.Grade;
import com.sharpdroid.registroelettronico.database.entities.Lesson;
import com.sharpdroid.registroelettronico.database.entities.LocalAgenda;
import com.sharpdroid.registroelettronico.database.entities.LocalGrade;
import com.sharpdroid.registroelettronico.database.entities.Note;
import com.sharpdroid.registroelettronico.database.entities.Option;
import com.sharpdroid.registroelettronico.database.entities.Period;
import com.sharpdroid.registroelettronico.database.entities.Profile;
import com.sharpdroid.registroelettronico.database.entities.RemoteAgenda;
import com.sharpdroid.registroelettronico.database.entities.RemoteAgendaInfo;
import com.sharpdroid.registroelettronico.database.entities.Subject;
import com.sharpdroid.registroelettronico.database.entities.SubjectTeacher;
import com.sharpdroid.registroelettronico.database.entities.Teacher;
import com.sharpdroid.registroelettronico.database.room.dao.LessonDao;

@Database(entities = {
        Absence.class,

        RemoteAgenda.class,
        LocalAgenda.class,
        RemoteAgendaInfo.class,

        Communication.class,

        File.class,
        Folder.class,

        Grade.class,
        LocalGrade.class,

        Lesson.class,

        Note.class,
        Option.class,
        Period.class,
        Profile.class,

        Subject.class,
        SubjectTeacher.class,
        Teacher.class

}, version = 5)
@TypeConverters({Converters.class})
public abstract class RoomDB extends RoomDatabase {
    public abstract LessonDao lessonsDao();
}
