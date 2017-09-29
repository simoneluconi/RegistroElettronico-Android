package com.sharpdroid.registroelettronico.Databases.Entities

import android.content.Context
import android.preference.PreferenceManager
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IProfile
import com.orm.SugarRecord
import com.orm.dsl.Unique
import com.sharpdroid.registroelettronico.Info
import com.sharpdroid.registroelettronico.Utils.Metodi.AccountImage

data class Profile(
        val username: String,
        var name: String,
        var password: String,
        var classe: String?,
        @Unique val ident: String

) : SugarRecord() {

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
            return find(Profile::class.java, "username = ?", PreferenceManager.getDefaultSharedPreferences(context).getString(Info.ACCOUNT, ""))?.get(0)!!
        }
    }


    fun getTeachers(): List<Teacher> {
        return SugarRecord.find(SubjectTeacher::class.java, "username = ?", username)?.map { it?.teacher!! } ?: emptyList()
    }

    fun getSubjects(): List<Subject> {
        return SugarRecord.find(SubjectTeacher::class.java, "username = ?", username)?.map { it?.subject!! } ?: emptyList()
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