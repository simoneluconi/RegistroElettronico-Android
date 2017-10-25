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
import com.sharpdroid.registroelettronico.Fragments.*
import com.sharpdroid.registroelettronico.Info
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.Utils.Account
import com.sharpdroid.registroelettronico.Utils.Metodi.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //  actionBar
        setSupportActionBar(toolbar)
        params = toolbar.layoutParams as AppBarLayout.LayoutParams?

        this.savedInstanceState = savedInstanceState
        init(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        val profile = Profile.getProfile(this)
        when {
            PreferenceManager.getDefaultSharedPreferences(this).getBoolean("first_run", true) -> // first time task
                startActivityForResult(Intent(this, Intro::class.java), 1)
            profile == null -> startActivity(Intent(this, LoginActivity::class.java))
            else -> headerResult?.setActiveProfile(profile.id, true)
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d("MAIN", "STOP")
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
                init(null)
            }
        }
    }

    private fun init(savedInstanceState: Bundle?) {
        initDrawer(savedInstanceState)
        Log.d("MAIN", "init drawer")
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

        // Programmatically start a fragment
        if (savedInstanceState == null) {
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

        toggle = ActionBarDrawerToggle(
                this, drawer!!.drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        toggle!!.isDrawerIndicatorEnabled = true
        drawer!!.actionBarDrawerToggle = toggle!!

        drawer!!.drawerLayout?.addDrawerListener(toggle!!)
        toggle!!.syncState()

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
        clearBackstack()
        val fragment: Fragment
        calendar.visibility = View.GONE
        tab_layout.visibility = View.GONE
        fab_big_add.visibility = View.GONE

        params?.scrollFlags = 0
        when (position) {
            1 -> {
                calendar.visibility = View.VISIBLE
                fab_big_add.visibility = View.VISIBLE

                fragment = FragmentAgenda()
                updateAgenda(this)
                updatePeriods(this)
            }
            2 -> {
                tab_layout?.visibility = View.VISIBLE
                params?.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS or AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP

                fragment = FragmentMediePager()
                //updateSubjects(this)
                updateMarks(this)
            }
            3 -> {
                fragment = FragmentSubjects()
                updateSubjects(this)
                updateLessons(this)
            }
            4 -> {
                fragment = FragmentFolders()
                updateFolders(this)
            }
            5 -> {
                fragment = FragmentAllAbsences()
                updateAbsence(this)
            }
            6 -> {
                fragment = FragmentNote()
                updateNote(this)
            }
            7 -> {
                fragment = FragmentCommunications()
                updateBacheca(this)
            }
            8 -> fragment = FragmentSettings()
            9 -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "Registro Elettronico")
                val url = "https://play.google.com/store/apps/details?id=com.sharpdroid.registroelettronico"
                intent.putExtra(Intent.EXTRA_TEXT, url)
                startActivity(Intent.createChooser(intent, getString(R.string.share_with)))
                return false
            }
            10 -> {
                val intentMail = Intent(Intent.ACTION_SENDTO)
                intentMail.data = Uri.parse("mailto:registroelettronico@simoneluconi.com")
                intentMail.putExtra(Intent.EXTRA_SUBJECT, "Registro Elettronico")
                intentMail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intentMail)
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

    override fun onProfileChanged(view: View?, profile: IProfile<*>, current: Boolean): Boolean {
        println("${profile.name} - ${profile.identifier} ")
        if (profile.identifier == 1234L) {
            startActivityForResult(Intent(this, LoginActivity::class.java), 2)
        } else if (!current) {

            Log.d(Info.ACCOUNT, profile.email.text)
            Account.with(this).user = profile.identifier

            fetchDataOfUser(this)

            //Update fragment
            drawer?.setSelectionAtPosition(drawer?.currentSelectedPosition!!, true)
        }
        return false
    }

    override fun onProfileLongClick(view: View?, profile: IProfile<*>, current: Boolean): Boolean {
        println("${profile.name} - ${profile.identifier} ")
        if (profile.identifier != 1234L) {
            MaterialDialog.Builder(this).title("Eliminare il profilo?").content("Continuare con l'eliminazione di " + profile.email.text + " ?").positiveText("SI").negativeText("NO").onPositive { _, _ ->
                SugarRecord.delete(SugarRecord.findById(Profile::class.java, profile.identifier))

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
