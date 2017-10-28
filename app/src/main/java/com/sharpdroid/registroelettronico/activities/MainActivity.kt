package com.sharpdroid.registroelettronico.activities

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
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
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
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
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IProfile
import com.orm.SugarRecord
import com.sharpdroid.registroelettronico.BuildConfig
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.database.entities.Profile
import com.sharpdroid.registroelettronico.fragments.*
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.Metodi.*
import com.transitionseverywhere.ChangeText
import com.transitionseverywhere.TransitionManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), Drawer.OnDrawerItemClickListener, AccountHeader.OnAccountHeaderListener, AccountHeader.OnAccountHeaderItemLongClickListener {
    private var drawer: Drawer? = null
    private var params: AppBarLayout.LayoutParams? = null
    private var fragmentManager: FragmentManager? = null
    private var toggle: ActionBarDrawerToggle? = null
    private var canOpenDrawer = true
    private var anim: ObjectAnimator? = null
    private var headerResult: AccountHeader? = null

    private var savedInstanceState: Bundle? = null

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

        FirebaseMessaging.getInstance().subscribeToTopic("v479")
        if (BuildConfig.DEBUG)
            FirebaseMessaging.getInstance().subscribeToTopic("dev")

        this.savedInstanceState = savedInstanceState
        init(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        var profile = Profile.getProfile(this)

        if (profile == null) {
            profile = SugarRecord.first(Profile::class.java)
            onProfileChanged(null, profile?.asIProfile(), false)
        }

        when {
            PreferenceManager.getDefaultSharedPreferences(this).getBoolean("first_run", true) -> // first time task
                startActivityForResult(Intent(this, Intro::class.java), 1)
            profile == null -> startActivityForResult(Intent(this, LoginActivity::class.java), 2)
            else -> {
                headerResult?.setActiveProfile(profile.id, true)
                ifHuaweiAlert()
            }
        }
    }


    override fun onStop() {
        super.onStop()
        savedInstanceState = null
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
        } else if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("MAIN", "LOGIN OK")
                init(null)
            } else if (Profile.getProfile(this) == null) {
                finish()
            }
        }
    }

    private fun init(savedInstanceState: Bundle?) {
        initDrawer(savedInstanceState)
        println("INIT")
        fragmentManager = supportFragmentManager
        fragmentManager?.addOnBackStackChangedListener {
            initBackButton()
        }
        if (savedInstanceState != null)
            initBackButton()

        toolbar.setNavigationOnClickListener { _ ->
            if (canOpenDrawer) {
                drawer?.drawerLayout?.openDrawer(GravityCompat.START)
            } else {
                fragmentManager?.popBackStack()
            }
        }

        // Aperto da notifica
        println(intent?.extras?.toString())
        if (intent?.extras?.containsKey("drawer_open_id") == true) {
            println("INIT BY NOTIFICATION")
            drawer?.setSelection(intent?.extras?.getLong("drawer_open_id") ?: -1L, true)
            intent?.extras?.clear()

            //Primo avvio
        } else if (savedInstanceState == null) {
            val default = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString("drawer_to_open", "0")) ?: 0
            val drawerToOpen = intent.extras?.getInt("drawer_to_open", default) ?: default
            drawer?.setSelectionAtPosition(drawerToOpen + 1, true)
        }
    }

    private fun initBackButton() {
        canOpenDrawer = fragmentManager?.backStackEntryCount == 0
        if (toggle != null) {
            if (!canOpenDrawer) {
                anim = ObjectAnimator.ofFloat(toggle?.drawerArrowDrawable, "progress", 1f)
                with(anim!!) {
                    interpolator = DecelerateInterpolator(1f)
                    duration = 250
                    start()
                }
                drawer?.drawerLayout?.removeDrawerListener(toggle!!)
            } else {
                anim = ObjectAnimator.ofFloat(toggle?.drawerArrowDrawable, "progress", 0f)
                with(anim!!) {
                    interpolator = DecelerateInterpolator(1f)
                    duration = 250
                    start()
                }
                drawer?.drawerLayout?.addDrawerListener(toggle!!)
            }
        }
    }

    override fun setTitle(title: CharSequence) {
        TransitionManager.beginDelayedTransition(toolbar, ChangeText().setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_IN).setDuration(250))
        super.setTitle(title)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        if (drawer != null) {
            drawer?.saveInstanceState(outState)
            headerResult?.saveInstanceState(outState)
        }

        super.onSaveInstanceState(outState)
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

    private fun initDrawer(savedInstanceState: Bundle?) {
        if (headerResult == null || savedInstanceState == null)
            headerResult = AccountHeaderBuilder()
                    .withActivity(this)
                    .withSavedInstance(savedInstanceState)
                    .withHeaderBackground(R.drawable.side_nav_bar)
                    .withProfiles(Profile.getIProfiles())
                    .addProfiles(ProfileSettingDrawerItem().withName("Aggiungi account").withIcon(R.drawable.fab_add).withIconTinted(true).withIdentifier(1234L))
                    .withOnAccountHeaderItemLongClickListener(this)
                    .withOnAccountHeaderListener(this)
                    .build()

        if (drawer == null || savedInstanceState == null)
            drawer = DrawerBuilder()
                    .withActivity(this)
                    .withToolbar(toolbar)
                    .withSavedInstance(savedInstanceState)
                    .withAccountHeader(headerResult!!)
                    .withActionBarDrawerToggleAnimated(true)
                    .withOnDrawerItemClickListener(this)
                    .addDrawerItems(
                            PrimaryDrawerItem().withIdentifier(R.id.today.toLong()).withName(R.string.today_at_school).withIcon(R.drawable.ic_home_black_24dp).withIconTintingEnabled(true),
                            PrimaryDrawerItem().withIdentifier(R.id.agenda.toLong()).withName(R.string.agenda).withIcon(R.drawable.ic_event).withIconTintingEnabled(true),
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

        toggle = ActionBarDrawerToggle(
                this, drawer!!.drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        with(toggle!!) {
            isDrawerIndicatorEnabled = true
            drawer!!.actionBarDrawerToggle = this
            drawer!!.drawerLayout?.addDrawerListener(this)
            syncState()
        }

    }

    override fun onBackPressed() {
        if (drawer?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
            drawer?.drawerLayout?.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun clearBackstack() {
        fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    /**
     * listener for volume down
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            event.startTracking()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * listener for volume down
     */
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
        val fragment: Fragment
        val p = Profile.getProfile(this)

        params?.scrollFlags = 0
        when (drawerItem.identifier.toInt()) {
            R.id.today -> {
                fragment = FragmentToday()
                updateAbsence(this, p)
                updateLessons(this, p)
                updateAgenda(this, p)
            }
            R.id.agenda -> {
                //calendar.visibility = View.VISIBLE
                //fab_big_add.visibility = View.VISIBLE

                fragment = FragmentAgenda()
                updateAgenda(this, p)
                updatePeriods(this, p)
            }
            R.id.medie -> {
                tab_layout?.visibility = View.VISIBLE
                params?.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS or AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP

                fragment = FragmentMediePager()
                //updateSubjects(this, p)
                updateMarks(this, p)
            }
            R.id.lessons -> {
                fragment = FragmentSubjects()
                updateSubjects(this, p)
                updateLessons(this, p)
            }
            R.id.files -> {
                fragment = FragmentFolders()
                updateFolders(this, p)
            }
            R.id.absences -> {
                fragment = FragmentAllAbsences()
                updateAbsence(this, p)
            }
            R.id.notes -> {
                fragment = FragmentNote()
                updateNote(this, p)
            }
            R.id.communications -> {
                fragment = FragmentCommunications()
                updateBacheca(this, p)
            }
            R.id.settings -> fragment = FragmentSettings()
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

        clearBackstack()
        calendar.visibility = View.GONE
        tab_layout.visibility = View.GONE
        fab_big_add.visibility = View.GONE

        val transaction = supportFragmentManager.beginTransaction()

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, fragment).commit()

        drawer?.closeDrawer()
        return false
    }

    override fun onProfileChanged(view: View?, profile: IProfile<*>?, current: Boolean): Boolean {
        if (profile == null) return false
        if (profile.identifier == 1234L) {
            startActivityForResult(Intent(this, LoginActivity::class.java), 2)
        } else if (!current) {

            Account.with(this).user = profile.identifier

            fetchDataOfUser(this)

            //Update fragment
            drawer?.setSelectionAtPosition(drawer?.currentSelectedPosition!!, true)
        }
        return false
    }

    override fun onProfileLongClick(view: View?, profile: IProfile<*>, current: Boolean): Boolean {
        if (profile.identifier != 1234L) {
            MaterialDialog.Builder(this).title("Eliminare il profilo?").content("Continuare con l'eliminazione di " + profile.email.text + " ?").positiveText("SI").negativeText("NO").onPositive { _, _ ->
                deleteUser(profile.identifier.toString())

                drawer?.closeDrawer()
                initDrawer(savedInstanceState)

                headerResult?.removeProfileByIdentifier(profile.identifier)
                drawer?.headerAdapter?.notifyDataSetChanged()

                val p = SugarRecord.first(Profile::class.java)
                if (p != null) {
                    Account.with(this).user = p.id
                    headerResult?.setActiveProfile(p.id, true)
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }.show()
        }
        return false
    }

    companion object {
        const val REQUEST_CODE = 1905
    }
}