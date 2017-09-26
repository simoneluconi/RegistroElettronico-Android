package com.sharpdroid.registroelettronico.Databases.Entities

import com.orm.SugarRecord

data class SubjectTeacher(val username: String,
                          val subject: Subject,
                          val teacher: Teacher
) : SugarRecord()