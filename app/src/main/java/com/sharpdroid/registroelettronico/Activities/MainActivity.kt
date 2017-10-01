package com.sharpdroid.registroelettronico.Activities

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.afollestad.materialdialogs.MaterialDialog
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IProfile
import com.orm.SugarRecord
import com.sharpdroid.registroelettronico.Databases.Entities.Profile
import com.sharpdroid.registroelettronico.Databases.RegistroDB
import com.sharpdroid.registroelettronico.Fragments.*
import com.sharpdroid.registroelettronico.Info
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.Utils.Metodi.updateSubjects
import com.transitionseverywhere.ChangeText
import com.transitionseverywhere.TransitionManager
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), Drawer.OnDrawerItemClickListener, AccountHeader.OnAccountHeaderListener, AccountHeader.OnAccountHeaderItemLongClickListener {
    internal var drawer: Drawer? = null
    internal var params: AppBarLayout.LayoutParams? = null
    internal var fragmentManager: FragmentManager? = null
    internal var toggle: ActionBarDrawerToggle? = null
    internal var needUpdate = true
    internal var canOpenDrawer = true
    internal var anim: ObjectAnimator? = null
    internal var headerResult: AccountHeader? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //  actionBar
        setSupportActionBar(toolbar)
        params = toolbar.layoutParams as AppBarLayout.LayoutParams?

        //  first run
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("first_run", true)) {
            // first time task
            startActivityForResult(Intent(this, Intro::class.java), 1)
        } else {
            init(savedInstanceState)
        }
    }

    override fun onResume() {
        super.onResume()

        val profile = Profile.getProfile(this)
        if (profile == null && !PreferenceManager.getDefaultSharedPreferences(this).getBoolean("first_run", true))
            startActivity(Intent(this, LoginActivity::class.java))

        headerResult?.profiles = Profile.getIProfiles()
        headerResult?.addProfiles(ProfileSettingDrawerItem().withName("Aggiungi account").withIcon(R.drawable.fab_add).withIconTinted(true))
        headerResult?.setActiveProfile(profile?.id!!, false)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("first_run", false).apply()
                init(null)
            } else {
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("first_run", true).apply()
                //User cancelled the intro so we'll finish this activity too.
                finish()
            }
        }
    }

    override fun setTitle(title: CharSequence) {
        TransitionManager.beginDelayedTransition(toolbar, ChangeText().setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_IN).setDuration(250))
        super.setTitle(title)
    }

    private fun initDrawer() {
        headerResult = AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.side_nav_bar)
                .withProfiles(Profile.getIProfiles())
                .addProfiles(ProfileSettingDrawerItem().withName("Aggiungi account").withIcon(R.drawable.fab_add).withIconTinted(true))
                .withOnAccountHeaderItemLongClickListener(this)
                .withOnAccountHeaderListener(this)
                .build()

        drawer = DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult!!)
                .withActionBarDrawerToggleAnimated(true)
                .withOnDrawerItemClickListener(this)
                .addDrawerItems(PrimaryDrawerItem().withIdentifier(R.id.agenda.toLong()).withName(R.string.agenda).withIcon(R.drawable.ic_event).withIconTintingEnabled(true),
                        PrimaryDrawerItem().withIdentifier(R.id.medie.toLong()).withName(R.string.medie).withIcon(R.drawable.ic_timeline).withIconTintingEnabled(true),
                        PrimaryDrawerItem().withIdentifier(R.id.lessons.toLong()).withName(R.string.lessons).withIcon(R.drawable.ic_view_agenda).withIconTintingEnabled(true),
                        PrimaryDrawerItem().withIdentifier(R.id.files.toLong()).withName(R.string.files).withIcon(R.drawable.ic_folder).withIconTintingEnabled(true),
                        PrimaryDrawerItem().withIdentifier(R.id.absences.toLong()).withName(R.string.absences).withIcon(R.drawable.ic_supervisor).withIconTintingEnabled(true),
                        PrimaryDrawerItem().withIdentifier(R.id.notes.toLong()).withName(R.string.note).withIcon(R.drawable.ic_error).withIconTintingEnabled(true),
                        PrimaryDrawerItem().withIdentifier(R.id.communications.toLong()).withName(R.string.communications).withIcon(R.drawable.ic_assignment).withIconTintingEnabled(true),
                        PrimaryDrawerItem().withIdentifier(R.id.settings.toLong()).withName(R.string.settings).withIcon(R.drawable.ic_settings).withIconTintingEnabled(true))
                .addDrawerItems(DividerDrawerItem(),
                        PrimaryDrawerItem().withIdentifier(R.id.nav_share.toLong()).withName(R.string.share).withIcon(R.drawable.ic_menu_share).withIconTintingEnabled(true).withSelectable(false),
                        PrimaryDrawerItem().withIdentifier(R.id.nav_send.toLong()).withName(R.string.send).withIcon(R.drawable.ic_menu_send).withIconTintingEnabled(true).withSelectable(false))
                .build()

        if (toolbar != null) {
            toggle = ActionBarDrawerToggle(
                    this, drawer?.drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
            toggle?.isDrawerIndicatorEnabled = true
            drawer?.actionBarDrawerToggle = toggle!!

            drawer?.drawerLayout?.addDrawerListener(toggle!!)
            toggle?.syncState()
        }
    }

    override fun onBackPressed() {
        if (drawer?.drawerLayout?.isDrawerOpen(GravityCompat.START)!!) {
            drawer?.drawerLayout?.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun clearBackstack() {
        fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    private fun init(savedInstanceState: Bundle?) {
        initDrawer()

        fragmentManager = supportFragmentManager
        fragmentManager?.addOnBackStackChangedListener {
            canOpenDrawer = fragmentManager?.backStackEntryCount == 0
            if (toggle != null) {
                if (!canOpenDrawer) {
                    anim = ObjectAnimator.ofFloat(toggle?.drawerArrowDrawable, "progress", 1f)
                    anim?.interpolator = DecelerateInterpolator(1f)
                    anim?.duration = 250
                    anim?.start()
                    drawer?.drawerLayout?.removeDrawerListener(toggle!!)
                } else {
                    anim = ObjectAnimator.ofFloat(toggle?.drawerArrowDrawable, "progress", 0f)
                    anim?.interpolator = DecelerateInterpolator(1f)
                    anim?.duration = 250
                    anim?.start()
                    drawer?.drawerLayout?.addDrawerListener(toggle!!)
                }
            }
        }
        toolbar?.setNavigationOnClickListener { v ->
            if (canOpenDrawer) {
                drawer?.drawerLayout?.openDrawer(GravityCompat.START)
            } else {
                fragmentManager?.popBackStack()
            }
        }

        if (needUpdate) {
            updateSubjects(this)
            needUpdate = false
        }

        // Programmatically start a fragment
        if (savedInstanceState == null) {
            var drawer_to_open = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString("drawer_to_open", "0"))!!

            val extras = intent.extras
            drawer_to_open = extras?.getInt("drawer_to_open", drawer_to_open) ?: 0


            drawer?.setSelectionAtPosition(drawer_to_open + 1, true)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            event.startTracking()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            //Cancella Dati Friendly
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
            return true
        }
        return super.onKeyLongPress(keyCode, event)
    }

    /**
     * Click listener for drawer's items
     */
    override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*, *>): Boolean {
        clearBackstack()
        val fragment: Fragment
        val id = drawerItem.identifier.toInt()
        calendar?.visibility = View.GONE
        tab_layout?.visibility = View.GONE
        params?.scrollFlags = 0
        when (id) {
            R.id.agenda -> {
                calendar?.visibility = View.VISIBLE
                fragment = FragmentAgenda()
            }
            R.id.medie -> {
                tab_layout?.visibility = View.VISIBLE
                params?.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS or AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
                fragment = FragmentMediePager()
            }
            R.id.communications -> fragment = FragmentCommunications()
            R.id.notes -> fragment = FragmentNote()
            R.id.absences -> fragment = FragmentAllAbsences()
            R.id.settings -> fragment = FragmentSettings()
            R.id.files -> {
                val fragmentFolders = FragmentFolders()
                fragmentFolders.getInstance(supportActionBar)
                fragment = fragmentFolders
            }
            R.id.lessons -> fragment = FragmentSubjects()
            R.id.nav_share -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "Registro Elettronico")
                val url = "https://play.google.com/store/apps/details?id=com.sharpdroid.registroelettronico"
                intent.putExtra(Intent.EXTRA_TEXT, url)
                startActivity(Intent.createChooser(intent, getString(R.string.share_with)))
                return false
            }
            R.id.nav_send -> {
                val intent_mail = Intent(Intent.ACTION_SENDTO)
                intent_mail.data = Uri.parse("mailto:registroelettronico@simoneluconi.com")
                intent_mail.putExtra(Intent.EXTRA_SUBJECT, "Registro Elettronico")
                intent_mail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent_mail)
                return false
            }
            else -> return false
        }

        val transaction = supportFragmentManager.beginTransaction()

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, fragment).commit()

        drawer?.closeDrawer()
        return false
    }

    override fun onStop() {
        super.onStop()
        RegistroDB.getInstance(this).close()
    }

    override fun onProfileChanged(view: View?, profile: IProfile<*>, current: Boolean): Boolean {
        if (profile is ProfileSettingDrawerItem) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {

            Log.d(Info.ACCOUNT, profile.email.text)
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString(Info.ACCOUNT, profile.email.text).apply()

            updateSubjects(this)
            //Update fragment
            drawer?.setSelectionAtPosition(1, true)
        }
        return false
    }

    override fun onProfileLongClick(view: View?, profile: IProfile<*>, current: Boolean): Boolean {
        if (profile !is ProfileSettingDrawerItem) {
            MaterialDialog.Builder(this).title("Eliminare il profilo?").content("Continuare con l'eliminazione di " + profile.email.text + " ?").positiveText("SI").negativeText("NO").onPositive { _, _ ->

                SugarRecord.deleteAll(Profile::class.java, "username = ?", profile.email.text)
                //db.removeProfile(profile.getEmail().getText());
                headerResult?.clear()
                //headerResult.removeProfile(profile);

                headerResult?.profiles = Profile.getIProfiles()
                headerResult?.addProfiles(ProfileSettingDrawerItem().withName("Aggiungi account").withIcon(R.drawable.fab_add).withIconTinted(true))

                if (SugarRecord.count<Profile>(Profile::class.java) > 0) {
                    PreferenceManager.getDefaultSharedPreferences(this).edit().putString(Info.ACCOUNT, Profile.getProfile(this)?.username).apply()
                    headerResult?.setActiveProfile(Profile.getProfile(this)?.asIProfile(), true)
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }.show()
        }
        return false
    }
}
