package com.sharpdroid.registroelettronico.database.entities

import android.content.Context
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IProfile
import com.orm.SugarRecord
import com.orm.dsl.Table
import com.orm.dsl.Unique
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.Metodi.AccountImage

@Table
data class Profile(
        var username: String,
        var name: String,
        var password: String,
        var classe: String,
        @Unique var id: Long,
        var token: String,
        var expire: Long,
        var ident: String,
        var isMulti: Boolean
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
            return SugarRecord.findWithQuery(Profile::class.java, "SELECT * FROM PROFILE")?.filter {
                it != null
            }?.map {
                it?.asIProfile() ?: throw IllegalStateException("Profile cannot be null")
            } ?: emptyList()
        }

        fun getProfile(context: Context): Profile? {
            return SugarRecord.findById(Profile::class.java, Account.with(context).user)
        }
    }
}