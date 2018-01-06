package com.sharpdroid.registroelettronico.database.pojos

import android.arch.persistence.room.Embedded
import com.sharpdroid.registroelettronico.database.entities.Grade

data class GradeWithSubjectName(@Embedded var grade: Grade = Grade(),
                                var subject: String? = null
)