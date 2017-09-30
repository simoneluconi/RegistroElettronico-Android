package com.sharpdroid.registroelettronico.Databases.Entities

import android.content.Context
import android.util.Log
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IProfile
import com.orm.SugarRecord
import com.orm.dsl.Unique
import com.sharpdroid.registroelettronico.Utils.Account
import com.sharpdroid.registroelettronico.Utils.Metodi.AccountImage

data class Profile(
        var username: String,
        var name: String,
        var password: String,
        var classe: String,
        @Unique var ident: String

) : SugarRecord() {

    constructor() : this("", "", "", "", "")

    fun asIProfile(): IProfile<ProfileDrawerItem> {
        return ProfileDrawerItem()
                .withName(name)
                .withEmail(username)
                .withNameShown(true)
                .withIcon(AccountImage(name))
                .withIdentifier(id)
    }

    companion object {
        fun getIProfiles(): List<IProfile<*>> {
            return find(Profile::class.java, "").map {
                it.asIProfile()
            }
        }

        fun getProfile(context: Context): Profile {
            val list = SugarRecord.find(Profile::class.java, "USERNAME = '" + Account.with(context).user + "'")
            Log.d("Profile", list.size.toString())
            return list[0]!!
        }
    }


    fun getTeachers(): List<Teacher> {
        return SugarRecord.find(SubjectTeacher::class.java, "USERNAME = ?", username)?.map { it?.teacher!! } ?: emptyList()
    }

    fun getSubjects(): List<Subject> {
        return SugarRecord.find(SubjectTeacher::class.java, "USERNAME = ?", username)?.map { it?.subject!! } ?: emptyList()
    }

    fun getEvents() {

    }

    fun getAbsences() {

    }

    fun getNotes() {

    }

    fun getFolders() {

    }
}