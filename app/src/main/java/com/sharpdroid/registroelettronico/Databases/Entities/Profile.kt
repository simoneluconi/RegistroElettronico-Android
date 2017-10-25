package com.sharpdroid.registroelettronico.Databases.Entities

import android.content.Context
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IProfile
import com.orm.SugarRecord
import com.orm.dsl.Table
import com.orm.dsl.Unique
import com.sharpdroid.registroelettronico.Utils.Account
import com.sharpdroid.registroelettronico.Utils.Metodi.AccountImage

@Table
data class Profile(
        var username: String,
        var name: String,
        var password: String,
        var classe: String,
        @Unique var id: Long,
        var token: String,
        var expire: Long

) {

    constructor() : this("", "", "", "", 0, "", 0)

    fun asIProfile(): IProfile<ProfileDrawerItem> {
        return ProfileDrawerItem()
                .withName(name)
                .withEmail(username)
                .withNameShown(true)
                .withIcon(AccountImage(name))
                .withIdentifier(id)
    }

    companion object {
        fun getIProfiles(): List<IProfile<ProfileDrawerItem>> {
            return SugarRecord.find(Profile::class.java, "").map {
                it.asIProfile()
            }
        }

        fun getProfile(context: Context): Profile? {
            return SugarRecord.findById(Profile::class.java, Account.with(context).user)
        }
    }


    fun getTeachers(): List<Teacher> {
        return SugarRecord.find(SubjectTeacher::class.java, "USERNAME = ?", username)?.map { it?.teacher!! } ?: emptyList()
    }

    fun getSubjects(): List<Subject> {
        return SugarRecord.find(SubjectTeacher::class.java, "USERNAME = ?", username)?.map { it?.subject!! } ?: emptyList()
    }
}