package com.sharpdroid.registroelettronico.Databases.Entities

import com.orm.SugarRecord

data class SubjectTeacher(val subject: Subject,
                          val teacher: Teacher
) : SugarRecord()