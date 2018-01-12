package com.sharpdroid.registroelettronico.database.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.sharpdroid.registroelettronico.database.dao.*
import com.sharpdroid.registroelettronico.database.entities.*

@Database(
        entities = [
            (Absence::class),
            (RemoteAgenda::class),
            (RemoteAgendaInfo::class),
            (LocalAgenda::class),
            (Communication::class),
            (CommunicationInfo::class),
            (File::class),
            (FileInfo::class),
            (Folder::class),
            (Grade::class),
            (LocalGrade::class),
            (Lesson::class),
            (Note::class),
            (Period::class),
            (Profile::class),
            (Subject::class),
            (SubjectInfo::class),
            (SubjectTeacher::class),
            (TimetableItem::class),
            (Teacher::class),
            (ExcludedMark::class)
        ],
        version = 13)
@TypeConverters(Converters::class)
abstract class RoomDB : RoomDatabase() {
    abstract fun absencesDao(): AbsenceDao

    abstract fun eventsDao(): AgendaDao

    abstract fun communicationsDao(): CommunicationDao

    abstract fun foldersDao(): FolderDao

    abstract fun gradesDao(): GradeDao

    abstract fun lessonsDao(): LessonDao

    abstract fun notesDao(): NoteDao

    abstract fun profilesDao(): ProfileDao

    abstract fun subjectsDao(): SubjectDao

    abstract fun timetableDao(): TimetableDao
}
