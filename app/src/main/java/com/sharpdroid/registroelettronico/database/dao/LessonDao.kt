package com.sharpdroid.registroelettronico.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.sharpdroid.registroelettronico.database.entities.Lesson
import com.sharpdroid.registroelettronico.database.pojos.GeniusTimetable
import com.sharpdroid.registroelettronico.database.pojos.LessonMini
import io.reactivex.Flowable

@Dao
interface LessonDao {

    @Query("SELECT ID, M_ARGUMENT, M_AUTHOR_NAME, M_DATE, M_HOUR_POSITION, M_SUBJECT_DESCRIPTION, COUNT(ID) as `M_DURATION`, M_CLASS_DESCRIPTION, M_CODE, M_SUBJECT_CODE, M_SUBJECT_ID, M_TYPE, PROFILE FROM LESSON WHERE M_DATE = :date AND PROFILE=:profile GROUP BY M_ARGUMENT, M_AUTHOR_NAME ORDER BY M_HOUR_POSITION ASC")
    fun loadLessonsLiveData(profile: Long, date: Long): LiveData<List<Lesson>>

    @Query("SELECT * FROM LESSON WHERE M_SUBJECT_ID=:code AND PROFILE=:profile GROUP BY M_ARGUMENT, M_AUTHOR_NAME, M_DATE ORDER BY M_DATE DESC LIMIT 5")
    fun loadLastLessons(code: Long, profile: Long): LiveData<List<Lesson>>

    @Query("SELECT * FROM LESSON WHERE M_SUBJECT_ID=:code AND PROFILE=:profile GROUP BY M_ARGUMENT, M_AUTHOR_NAME, M_DATE ORDER BY M_DATE DESC")
    fun loadLessons(code: Long, profile: Long): List<LessonMini>

    @Query("SELECT * FROM LESSON WHERE PROFILE=:profile GROUP BY M_ARGUMENT, M_AUTHOR_NAME, M_DATE ORDER BY M_DATE DESC")
    fun flowableLessons(profile: Long): Flowable<List<Lesson>>

    @Transaction
    @Query("SELECT * FROM LESSON WHERE PROFILE=:profile AND (M_ARGUMENT LIKE :query OR M_SUBJECT_DESCRIPTION LIKE :query OR M_AUTHOR_NAME LIKE :query)")
    fun query(query: String, profile: Long): Flowable<List<Lesson>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(lessons: List<Lesson>)

    @Query("DELETE FROM LESSON WHERE PROFILE = :profile")
    fun delete(profile: Long)

    @Query("SELECT M_CLASS_DESCRIPTION FROM LESSON WHERE PROFILE = :profile LIMIT 1")
    fun getClassDescription(profile: Long): String

    @Transaction
    @Query("select `dayOfWeek`,`teacher`,`subject`,`start`,`end` from ( select " +
            "strftime('%w', M_DATE/1000, 'unixepoch') as `dayOfWeek`, " +
            "`TEACHER`.`ID` as `teacher`, " +
            "`M_SUBJECT_ID` as `subject`, " +
            "M_HOUR_POSITION+7 as `start`, " +
            "M_HOUR_POSITION+8 as `end`, " +
            "COUNT(M_DURATION) as `totalHours` " +
            "from `LESSON` LEFT JOIN `TEACHER` ON `M_AUTHOR_NAME`=`TEACHER_NAME` " +
            "where PROFILE = :profile AND M_SUBJECT_CODE!='SUPZ' AND M_DATE>(strftime('%s', 'now', 'localtime') - 45*24*3600)*1000 " +
            "group by `dayOfWeek`,M_HOUR_POSITION, M_SUBJECT_CODE " +
            "order by `dayOfWeek`, M_HOUR_POSITION  asc) where `totalHours`>1 " +
            "group by `dayOfWeek`, `start`")
    fun geniusTimetable(profile: Long): List<GeniusTimetable>

}
