package com.sharpdroid.registroelettronico.activities

import android.animation.ObjectAnimator
import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ShareEvent
import com.google.firebase.messaging.FirebaseMessaging
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IProfile
import com.sharpdroid.registroelettronico.BuildConfig
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.database.entities.*
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.fragments.*
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.Metodi.deleteUser
import com.sharpdroid.registroelettronico.utils.Metodi.dp
import com.sharpdroid.registroelettronico.utils.Metodi.fetchDataOfUser
import com.sharpdroid.registroelettronico.utils.Metodi.updateSubjects
import com.sharpdroid.registroelettronico.widget.agenda.WidgetAgenda
import com.sharpdroid.registroelettronico.widget.orario.WidgetOrario
import com.transitionseverywhere.ChangeText
import com.transitionseverywhere.TransitionManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.io.Serializable

class MainActivity : AppCompatActivity(), Drawer.OnDrawerItemClickListener, AccountHeader.OnAccountHeaderListener, AccountHeader.OnAccountHeaderItemLongClickListener {
    private var params: AppBarLayout.LayoutParams? = null
    private lateinit var drawerView: Drawer
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var headerResult: AccountHeader

    private var huaweiAlert: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab_mini_verifica.setImageResource(R.drawable.agenda_fab_verifiche)
        fab_mini_esercizi.setImageResource(R.drawable.agenda_fab_compiti)
        fab_mini_altro.setImageResource(R.drawable.agenda_fab_altro)

        //  actionBar
        setSupportActionBar(toolbar)
        params = toolbar.layoutParams as AppBarLayout.LayoutParams?

        FirebaseMessaging.getInstance().subscribeToTopic("v491")
        if (BuildConfig.DEBUG)
            FirebaseMessaging.getInstance().subscribeToTopic("dev")

        // Build Drawer View
        drawerView = DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withSavedInstance(savedInstanceState)
                .withActionBarDrawerToggleAnimated(true)
                .withOnDrawerItemClickListener(this)
                .addDrawerItems(
                        PrimaryDrawerItem().withIdentifier(R.id.today.toLong()).withName(R.string.today_at_school).withIcon(R.drawable.ic_home_black_24dp).withIconTintingEnabled(true),
                        PrimaryDrawerItem().withIdentifier(R.id.agenda.toLong()).withName(R.string.agenda).withIcon(R.drawable.ic_event).withIconTintingEnabled(true),
                        PrimaryDrawerItem().withIdentifier(R.id.medie.toLong()).withName(R.string.medie).withIcon(R.drawable.ic_timeline).withIconTintingEnabled(true),
                        PrimaryDrawerItem().withIdentifier(R.id.schedule.toLong()).withName(R.string.schedule).withIcon(R.drawable.ic_schedule_black_24dp).withIconTintingEnabled(true),
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

        // Build Drawer's Header view
        headerResult = AccountHeaderBuilder()
                .withActivity(this)
                .withSavedInstance(savedInstanceState)
                .withHeaderBackground(R.drawable.side_nav_bar)
                .withOnAccountHeaderItemLongClickListener(this)
                .withOnAccountHeaderListener(this)
                .withDrawer(drawerView)
                .build()

        // Open specified fragment if any
        if (intent.extras != null && intent.extras.containsKey("drawer_open_id")) {
            drawerView.setSelection(intent.extras.getLong("drawer_open_id"), false)
            openFragment(intent.extras.getLong("drawer_open_id"), intent.extras)
            saveContentFromNotification(intent.getSerializableExtra("list") as? List<Serializable>)
        } else if (savedInstanceState == null) {
            val default = PreferenceManager.getDefaultSharedPreferences(this).getString("drawer_to_open", "0").toInt()
            drawerView.setSelectionAtPosition((intent.extras?.getInt("drawer_to_open", default)
                    ?: default) + 1, true)
        }

        // Animate Hamburger->Arrow on Drawer swipe
        // Keep this code after creating drawer
        toggle = ActionBarDrawerToggle(
                this, drawerView.drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        with(toggle) {
            isDrawerIndicatorEnabled = true
            drawerView.actionBarDrawerToggle = this
            drawerView.drawerLayout?.addDrawerListener(this)
            syncState()
        }

        // Support greater BackStacks (do not fuck up the home button)
        toolbar.setNavigationOnClickListener { _ ->
            Log.d("MainActivity", "NavigationClick")
            if (canOpenDrawer()) {
                drawerView.drawerLayout?.openDrawer(GravityCompat.START)
            } else {
                supportFragmentManager?.popBackStack()
            }
        }


        // Animate Hamburger on BackStack changes
        supportFragmentManager.addOnBackStackChangedListener {
            Log.d("MainActivity", "BackStack count" + supportFragmentManager.backStackEntryCount.toString())
            updateDrawerBehaviour()
        }
        if (savedInstanceState != null)
            updateDrawerBehaviour()
    }

    private fun updateDrawerBehaviour() {
        val openDrawer = canOpenDrawer()
        with(ObjectAnimator.ofFloat(toggle.drawerArrowDrawable, "progress", if (!openDrawer) 1f else 0f)) {
            interpolator = DecelerateInterpolator(1f)
            duration = 250
            start()
        }

        drawerView.drawerLayout?.setDrawerLockMode(if (!openDrawer) DrawerLayout.LOCK_MODE_LOCKED_CLOSED else DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    private fun canOpenDrawer() = supportFragmentManager?.backStackEntryCount == 0

    /**
     * Watch for profile changes. If needed, show login activity
     */
    override fun onResume() {
        super.onResume()

        Log.d("MainActivity", "Selected position: " + drawerView.currentSelectedPosition)

        val profile = Profile.getProfile(this)

        when {
        // First time opening the app
            PreferenceManager.getDefaultSharedPreferences(this).getBoolean("first_run", true) ->
                startActivityForResult(Intent(this, Intro::class.java), 1)

        // Last user has logged out
            profile == null -> {
                val otherProfile = DatabaseHelper.database.profilesDao().randomProfile

                //Found another user logged in
                if (otherProfile != null) {
                    onProfileChanged(null, otherProfile.asIProfile(), false)
                } else {
                    startActivityForResult(Intent(this, LoginActivity::class.java), LOGIN_REQUEST_CODE)
                }
            }
        // Everything's OK
            else -> {
                updateDrawerHeader()
                headerResult.setActiveProfile(profile.id, true)
                ifHuaweiAlert()
                updateSubjects(profile)
            }
        }
    }

    override fun onStop() {
        super.onStop()

        // Update widgets
        sendBroadcast(Intent(this, WidgetAgenda::class.java).setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE).putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                AppWidgetManager.getInstance(this).getAppWidgetIds(ComponentName(this, WidgetAgenda::class.java))))
        sendBroadcast(Intent(this, WidgetOrario::class.java).setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE).putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                AppWidgetManager.getInstance(this).getAppWidgetIds(ComponentName(this, WidgetOrario::class.java))))
    }

    override fun onBackPressed() {
        if (drawerView.drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
            drawerView.drawerLayout?.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun updateDrawerHeader() {
        headerResult.clear()
        headerResult.profiles = Profile.getIProfiles() + Profile.NEW_ACCOUNT_ROW
        drawerView.headerAdapter.notifyDataSetChanged()
    }

    /**
     * Listener for login activities
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //User is coming from Intro
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("first_run", false).apply()
                updateDrawerHeader()
                Log.i("MainActivity", "Logged in from Intro")
            } else {
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("first_run", true).apply()
                //User cancelled the intro so we'll finish this activity too.
                Log.i("MainActivity", "Could not log in from Intro")
                finish()
            }
        }

        //User is coming from LoginActivity or FragmentLogin
        else if (requestCode == LOGIN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                updateDrawerHeader()
                Log.i("MainActivity", "Logged in from LoginActivity or FragmentLogin")
            } else if (Profile.getProfile(this) == null) {
                Log.i("MainActivity", "Could not log in from LoginActivity or FragmentLogin")
                finish()
            }
        }
    }

    /**
     * Animate title change
     */
    override fun setTitle(title: CharSequence) {
        TransitionManager.beginDelayedTransition(toolbar, ChangeText().setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_IN).setDuration(250))
        super.setTitle(title)
    }

    /**
     * Save drawer's state
     */
    override fun onSaveInstanceState(outState: Bundle?) {
        //drawerView.saveInstanceState(outState)
        headerResult.saveInstanceState(outState)
        outState?.putInt("drawerView.currentSelectedPosition", drawerView.currentSelectedPosition)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        drawerView.setSelectionAtPosition(savedInstanceState?.getInt("drawerView.currentSelectedPosition")
                ?: 0, false)
    }

    private fun clearBackStack() {
        supportFragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    /**
     * Click listener for drawer's items
     */
    override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*, *>): Boolean {
        return openFragment(drawerItem.identifier, null)
    }

    private fun openFragment(tabId: Number, extras: Bundle?): Boolean {
        val fragment: Fragment = when (tabId.toInt()) {
            R.id.today -> {
                FragmentToday()
            }
            R.id.agenda -> {
                FragmentAgenda()
            }
            R.id.medie -> {
                FragmentMediePager()
            }
            R.id.lessons -> {
                FragmentSubjects()
            }
            R.id.files -> {
                FragmentFolders()
            }
            R.id.absences -> {
                FragmentAllAbsences()
            }
            R.id.notes -> {
                FragmentNote()
            }
            R.id.communications -> {
                FragmentCommunications()
            }
            R.id.schedule -> {
                FragmentTimetable()
            }
            R.id.settings -> FragmentSettings()
            R.id.nav_share -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "Registro Elettronico")
                val url = "https://play.google.com/store/apps/details?id=com.sharpdroid.registroelettronico"
                intent.putExtra(Intent.EXTRA_TEXT, url)
                startActivity(Intent.createChooser(intent, getString(R.string.share_with)))

                if (!BuildConfig.DEBUG)
                    Answers.getInstance().logShare(ShareEvent().putMethod("ACTION_SEND"))
                return true
            }
            R.id.nav_send -> {
                val intentMail = Intent(Intent.ACTION_SENDTO)
                intentMail.data = Uri.parse("mailto:registroelettronico@simoneluconi.com")
                intentMail.putExtra(Intent.EXTRA_SUBJECT, "Registro Elettronico")
                intentMail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intentMail)

                if (!BuildConfig.DEBUG)
                    Answers.getInstance().logShare(ShareEvent().putMethod("ACTION_SEND"))
                return false
            }
            else -> return false
        }
        extras?.apply {
            fragment.arguments = this
        }
        clearBackStack()

        params?.scrollFlags = 0
        calendar.visibility = View.GONE
        tab_layout.visibility = View.GONE
        fab_big_add.visibility = View.GONE

        val transaction = supportFragmentManager.beginTransaction()

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, fragment).commit()

        drawerView.closeDrawer()
        return true
    }

    override fun onProfileChanged(view: View?, profile: IProfile<*>?, current: Boolean): Boolean {
        if (profile == null || profile.identifier == Profile.NEW_ACCOUNT) {
            startActivityForResult(Intent(this, LoginActivity::class.java), LOGIN_REQUEST_CODE)
        } else {

            Account.with(this).user = profile.identifier

            //Update fragment
            //if (!current) {
            drawerView.setSelectionAtPosition(drawerView.currentSelectedPosition, false)
            openFragment(drawerView.currentSelection, intent.extras)
            fetchDataOfUser(this)
            //}
        }
        return false
    }

    override fun onProfileLongClick(view: View?, profile: IProfile<*>, current: Boolean): Boolean {
        println(profile.identifier.toString() + " " + Profile.NEW_ACCOUNT.toString())
        if (profile.identifier != Profile.NEW_ACCOUNT) {
            MaterialDialog.Builder(this).title("Eliminare il profilo?").content("Continuare con l'eliminazione di " + profile.email.text + " ?").positiveText("SI").negativeText("NO").onPositive { _, _ ->
                deleteUser(profile.identifier)

                drawerView.closeDrawer()
                updateDrawerHeader()

                //headerResult.removeProfileByIdentifier(profile.identifier)
                //drawerView.headerAdapter.notifyDataSetChanged()


                // Find any other profile
                val p = DatabaseHelper.database.profilesDao().randomProfile
                if (p != null) {
                    Account.with(this).user = p.id
                    headerResult.setActiveProfile(p.id, true)
                } else {
                    startActivityForResult(Intent(this, LoginActivity::class.java), LOGIN_REQUEST_CODE)
                }
            }.show()
        }
        return false
    }


    private fun ifHuaweiAlert() {
        val intent = Intent()
        intent.component = ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")

        if (intent.resolveActivityInfo(packageManager, 0) != null &&
                !PreferenceManager.getDefaultSharedPreferences(this).getBoolean("huawei_do_not_ask_again", false) &&
                "huawei".equals(android.os.Build.MANUFACTURER, true) &&
                !PreferenceManager.getDefaultSharedPreferences(this).getBoolean("huawei_protected", false)) {

            val doNotAskAgain = CheckBox(this)
            val l = LinearLayout(this)
            with(doNotAskAgain) {
                text = context.getString(R.string.do_not_ask_again)
                isChecked = false
                gravity = Gravity.CENTER_VERTICAL
            }
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(dp(16), dp(16), dp(16), dp(0))
            l.addView(doNotAskAgain, params)

            if (huaweiAlert == null)
                huaweiAlert = AlertDialog.Builder(this)
                        .setTitle(R.string.huawei_headline)
                        .setMessage(R.string.huawei_text)
                        .setView(l)
                        .setPositiveButton("OK") { _, _ ->
                            try {
                                startActivity(intent)
                                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("huawei_protected", true).apply()
                            } catch (e: ActivityNotFoundException) {
                                Toast.makeText(applicationContext, "Non è possibile aggiungere l'app fra le app protette.", Toast.LENGTH_SHORT).show()
                                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("huawei_do_not_ask_again", true).apply()
                            }
                        }.setNegativeButton(android.R.string.cancel) { _, _ ->
                            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("huawei_do_not_ask_again", doNotAskAgain.isChecked).apply()

                        }.show()

        }
    }

    private fun saveContentFromNotification(content: List<Serializable>?) {
        if (content == null) return
        val firstElement = content.first()
        when (firstElement) {
            is RemoteAgenda ->
                DatabaseHelper.database.eventsDao().insertRemote(content as List<RemoteAgenda>)
            is Grade ->
                DatabaseHelper.database.gradesDao().insert(content as List<Grade>)
            is Communication ->
                DatabaseHelper.database.communicationsDao().insert(content as List<Communication>)
            is Note ->
                DatabaseHelper.database.notesDao().insert(content as List<Note>)
        }
    }

    companion object {
        const val REQUEST_CODE = 1905
        const val LOGIN_REQUEST_CODE = 2
    }
}