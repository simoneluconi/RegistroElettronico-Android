package com.sharpdroid.registroelettronico.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.content.Context
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IProfile
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.Metodi.AccountImage

@Table
@Entity(tableName = "PROFILE")
data class Profile(
        @ColumnInfo(name = "USERNAME") var username: String = "",
        @ColumnInfo(name = "NAME") var name: String = "",
        @ColumnInfo(name = "PASSWORD") var password: String = "",
        @ColumnInfo(name = "CLASSE") var classe: String = "",
        @ColumnInfo(name = "ID") @PrimaryKey @Unique var id: Long = -1L,
        @ColumnInfo(name = "TOKEN") var token: String = "",
        @ColumnInfo(name = "EXPIRE") var expire: Long = -1L,
        @ColumnInfo(name = "IDENT") var ident: String = "",
        @ColumnInfo(name = "IS_MULTI") var isMulti: Boolean
) {

    constructor() : this("", "", "", "", 0, "", 0, "", false)

    fun asIProfile(): IProfile<ProfileDrawerItem> {
        return ProfileDrawerItem()
                .withName(name)
                .withEmail(ident)
                .withNameShown(true)
                .withIcon(AccountImage(name))
                .withIdentifier(id)
    }

    companion object {
        fun getIProfiles(): List<IProfile<ProfileDrawerItem>> {
            return SugarRecord.findWithQuery(Profile::class.java, "SELECT * FROM PROFILE").filter {
                it != null
            }.map {
                it.asIProfile() ?: throw IllegalStateException("Profile cannot be null")
            } ?: emptyList()
        }

        fun getProfile(context: Context): Profile? {
            return SugarRecord.findById(Profile::class.java, Account.with(context).user)
        }
    }
}