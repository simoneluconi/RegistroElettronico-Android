package com.sharpdroid.registroelettronico.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.content.Context
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IProfile
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.Metodi.capitalizeEach
import com.sharpdroid.registroelettronico.utils.Metodi.getAccountImage
import java.util.*

@Entity(tableName = "PROFILE")
class Profile(
        @ColumnInfo(name = "USERNAME") var username: String = "",
        @ColumnInfo(name = "NAME") var name: String = "",
        @ColumnInfo(name = "PASSWORD") var password: String = "",
        @ColumnInfo(name = "CLASSE") var classe: String = "",
        @ColumnInfo(name = "ID") @PrimaryKey var id: Long = 0L,
        @ColumnInfo(name = "TOKEN") var token: String = "",
        @ColumnInfo(name = "EXPIRE") var expire: Date = Date(0),
        @ColumnInfo(name = "IDENT") var ident: String = "",
        @ColumnInfo(name = "IS_MULTI") var isMulti: Boolean = false
) {
    fun asIProfile(): IProfile<ProfileDrawerItem> {
        return ProfileDrawerItem()
                .withName(name)
                .withEmail(ident)
                .withNameShown(true)
                .withIcon(getAccountImage(name))
                .withIdentifier(id)
    }

    // Need to override for spinner usage in Widget's Configuration activity
    override fun toString() = capitalizeEach(name, true)

    companion object {
        fun getIProfiles(): List<IProfile<ProfileDrawerItem>> {
            return DatabaseHelper.database.profilesDao().profilesSync.map {
                it.asIProfile()
            }
        }

        fun getProfile(context: Context): Profile? {
            return DatabaseHelper.database.profilesDao().getProfile(Account.with(context).user)
        }
    }
}