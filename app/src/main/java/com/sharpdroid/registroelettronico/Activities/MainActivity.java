package com.sharpdroid.registroelettronico.Activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.sharpdroid.registroelettronico.Databases.RegistroDB;
import com.sharpdroid.registroelettronico.Fragments.FragmentAgenda;
import com.sharpdroid.registroelettronico.Fragments.FragmentAllAbsences;
import com.sharpdroid.registroelettronico.Fragments.FragmentCommunications;
import com.sharpdroid.registroelettronico.Fragments.FragmentFolders;
import com.sharpdroid.registroelettronico.Fragments.FragmentMediePager;
import com.sharpdroid.registroelettronico.Fragments.FragmentNote;
import com.sharpdroid.registroelettronico.Fragments.FragmentSettings;
import com.sharpdroid.registroelettronico.Fragments.FragmentSubjects;
import com.sharpdroid.registroelettronico.R;
import com.transitionseverywhere.ChangeText;
import com.transitionseverywhere.TransitionManager;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sharpdroid.registroelettronico.Utils.Metodi.updateSubjects;

public class MainActivity extends AppCompatActivity
        implements Drawer.OnDrawerItemClickListener, AccountHeader.OnAccountHeaderListener, AccountHeader.OnAccountHeaderItemLongClickListener {
    @BindView(R.id.calendar)
    CompactCalendarView calendarView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    Drawer drawer;
    AppBarLayout.LayoutParams params;

    FragmentManager fragmentManager;
    ActionBarDrawerToggle toggle;
    boolean needUpdate = true;
    boolean canOpenDrawer = true;
    ObjectAnimator anim;
    AccountHeader headerResult;
    RegistroDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //  actionBar
        setSupportActionBar(toolbar);
        params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();

        //  first run
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("first_run", true)) {
            // first time task
            startActivityForResult(new Intent(this, Intro.class), 1);
        } else {
            init(savedInstanceState);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (db == null) db = RegistroDB.getInstance(this);
        if (headerResult != null) {
            headerResult.setProfiles(db.getProfiles());
            headerResult.addProfiles(new ProfileSettingDrawerItem().withName("Aggiungi account").withIcon(R.drawable.fab_add).withIconTinted(true));
            headerResult.setActiveProfile(db.getProfile().getIdentifier(), false);
            Log.d("MAIN", "RESUME - " + db.getProfile().getEmail() + " - " + headerResult.getActiveProfile().getEmail());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("first_run", false).apply();
                init(null);
            } else {
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("first_run", true).apply();
                //User cancelled the intro so we'll finish this activity too.
                finish();
            }
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        TransitionManager.beginDelayedTransition(toolbar, new ChangeText().setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_IN).setDuration(250));
        super.setTitle(title);
    }

    private void initDrawer() {
        db = RegistroDB.getInstance(this);
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.side_nav_bar)
                .withProfiles(db.getProfiles())
                .addProfiles(new ProfileSettingDrawerItem().withName("Aggiungi account").withIcon(R.drawable.fab_add).withIconTinted(true))
                .withOnAccountHeaderItemLongClickListener(this)
                .withOnAccountHeaderListener(this)
                .build();

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withActionBarDrawerToggleAnimated(true)
                .withOnDrawerItemClickListener(this)
                .addDrawerItems(new PrimaryDrawerItem().withIdentifier(R.id.agenda).withName(R.string.agenda).withIcon(R.drawable.ic_event).withIconTintingEnabled(true),
                        new PrimaryDrawerItem().withIdentifier(R.id.medie).withName(R.string.medie).withIcon(R.drawable.ic_timeline).withIconTintingEnabled(true),
                        new PrimaryDrawerItem().withIdentifier(R.id.lessons).withName(R.string.lessons).withIcon(R.drawable.ic_view_agenda).withIconTintingEnabled(true),
                        new PrimaryDrawerItem().withIdentifier(R.id.files).withName(R.string.files).withIcon(R.drawable.ic_folder).withIconTintingEnabled(true),
                        new PrimaryDrawerItem().withIdentifier(R.id.absences).withName(R.string.absences).withIcon(R.drawable.ic_supervisor).withIconTintingEnabled(true),
                        new PrimaryDrawerItem().withIdentifier(R.id.notes).withName(R.string.note).withIcon(R.drawable.ic_error).withIconTintingEnabled(true),
                        new PrimaryDrawerItem().withIdentifier(R.id.communications).withName(R.string.communications).withIcon(R.drawable.ic_assignment).withIconTintingEnabled(true),
                        new PrimaryDrawerItem().withIdentifier(R.id.settings).withName(R.string.settings).withIcon(R.drawable.ic_settings).withIconTintingEnabled(true))
                .addDrawerItems(new DividerDrawerItem(),
                        new PrimaryDrawerItem().withIdentifier(R.id.nav_share).withName(R.string.share).withIcon(R.drawable.ic_menu_share).withIconTintingEnabled(true).withSelectable(false),
                        new PrimaryDrawerItem().withIdentifier(R.id.nav_send).withName(R.string.send).withIcon(R.drawable.ic_menu_send).withIconTintingEnabled(true).withSelectable(false))
                .build();

        if (toolbar != null) {
            toggle = new ActionBarDrawerToggle(
                    this, drawer.getDrawerLayout(), toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            toggle.setDrawerIndicatorEnabled(true);
            drawer.setActionBarDrawerToggle(toggle);

            drawer.getDrawerLayout().addDrawerListener(toggle);
            toggle.syncState();
        }
        Log.d("MAIN", "INIT");
    }

    @Override
    public void onBackPressed() {
        if (drawer.getDrawerLayout().isDrawerOpen(GravityCompat.START)) {
            drawer.getDrawerLayout().closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void clearBackstack() {
        if (fragmentManager != null)
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void init(Bundle savedInstanceState) {
        initDrawer();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(() -> {
            canOpenDrawer = fragmentManager.getBackStackEntryCount() == 0;
            if (toggle != null) {
                if (!canOpenDrawer) {
                    anim = ObjectAnimator.ofFloat(toggle.getDrawerArrowDrawable(), "progress", 1f);
                    anim.setInterpolator(new DecelerateInterpolator(1f));
                    anim.setDuration(250);
                    anim.start();
                    drawer.getDrawerLayout().removeDrawerListener(toggle);
                } else {
                    anim = ObjectAnimator.ofFloat(toggle.getDrawerArrowDrawable(), "progress", 0f);
                    anim.setInterpolator(new DecelerateInterpolator(1f));
                    anim.setDuration(250);
                    anim.start();
                    drawer.getDrawerLayout().addDrawerListener(toggle);
                }
            }
        });
        toolbar.setNavigationOnClickListener(v -> {
            if (canOpenDrawer) {
                drawer.getDrawerLayout().openDrawer(GravityCompat.START);
            } else {
                fragmentManager.popBackStack();
            }
        });

        if (needUpdate) {
            updateSubjects(this);
            needUpdate = false;
        }

        // Programmatically start a fragment
        if (savedInstanceState == null) {
            int drawer_to_open = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString("drawer_to_open", "0"));

            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                drawer_to_open = extras.getInt("drawer_to_open", drawer_to_open);
            }

            drawer.setSelectionAtPosition(drawer_to_open + 1, true);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            event.startTracking();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            //Cancella Dati Friendly
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    /**
     * Click listener for drawer's items
     */
    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        clearBackstack();
        Fragment fragment;
        int id = (int) drawerItem.getIdentifier();
        calendarView.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        params.setScrollFlags(0);
        switch (id) {
            case R.id.agenda:
                calendarView.setVisibility(View.VISIBLE);
                fragment = new FragmentAgenda();
                break;
            case R.id.medie:
                tabLayout.setVisibility(View.VISIBLE);
                params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
                fragment = new FragmentMediePager();
                break;
            case R.id.communications:
                fragment = new FragmentCommunications();
                break;
            case R.id.notes:
                fragment = new FragmentNote();
                break;
            case R.id.absences:
                fragment = new FragmentAllAbsences();
                break;
            case R.id.settings:
                fragment = new FragmentSettings();
                break;
            case R.id.files:
                FragmentFolders fragmentFolders = new FragmentFolders();
                fragmentFolders.getInstance(getSupportActionBar());
                fragment = fragmentFolders;
                break;
            case R.id.lessons:
                fragment = new FragmentSubjects();
                break;
            case R.id.nav_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Registro Elettronico");
                String url = "https://play.google.com/store/apps/details?id=com.sharpdroid.registroelettronico";
                intent.putExtra(Intent.EXTRA_TEXT, url);
                startActivity(Intent.createChooser(intent, getString(R.string.share_with)));
                return false;
            case R.id.nav_send:
                Intent intent_mail = new Intent(Intent.ACTION_SENDTO);
                intent_mail.setData(Uri.parse("mailto:bugreport@registroelettronico.ml"));
                intent_mail.putExtra(Intent.EXTRA_SUBJECT, "Registro Elettronico");
                intent_mail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_mail);
            default:
                return false;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, fragment).commit();

        drawer.closeDrawer();
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        RegistroDB.getInstance(this).close();
    }

    @Override
    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
        if (profile instanceof ProfileSettingDrawerItem) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            Log.d("currentProfile", profile.getEmail().getText());

            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("currentProfile", profile.getEmail().getText()).apply();
            db.updateProfile();

            updateSubjects(this);
            //Update fragment
            drawer.setSelection(drawer.getDrawerItem(drawer.getCurrentSelection()), true);
        }
        return false;
    }

    @Override
    public boolean onProfileLongClick(View view, IProfile profile, boolean current) {
        if (!(profile instanceof ProfileSettingDrawerItem)) {
            new MaterialDialog.Builder(this).title("Eliminare il profilo?").content("Continuare con l'eliminazione di " + profile.getEmail().getText() + " ?").positiveText("SI").negativeText("NO").onPositive((dialog, which) -> {
                db.removeProfile(profile.getEmail().getText());
                //headerResult.clear();
                headerResult.removeProfile(profile);
                //headerResult.setProfiles(db.getProfiles());
                //headerResult.addProfiles(new ProfileSettingDrawerItem().withName("Aggiungi account").withIcon(R.drawable.fab_add).withIconTinted(true));

                if (db.getProfiles().size() > 0) {
                    PreferenceManager.getDefaultSharedPreferences(this).edit().putString("currentProfile", db.getProfiles().get(0).getEmail().getText()).apply();
                    headerResult.setActiveProfile(db.getProfile(), true);
                } else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
            }).show();
        }
        return false;
    }
}
