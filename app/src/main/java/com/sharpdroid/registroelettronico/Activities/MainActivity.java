package com.sharpdroid.registroelettronico.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.sharpdroid.registroelettronico.Fragments.FragmentAgenda;
import com.sharpdroid.registroelettronico.Fragments.FragmentAllAbsences;
import com.sharpdroid.registroelettronico.Fragments.FragmentCommunications;
import com.sharpdroid.registroelettronico.Fragments.FragmentFolders;
import com.sharpdroid.registroelettronico.Fragments.FragmentMediePager;
import com.sharpdroid.registroelettronico.Fragments.FragmentNote;
import com.sharpdroid.registroelettronico.Fragments.FragmentSettings;
import com.sharpdroid.registroelettronico.Fragments.FragmentSubjects;
import com.sharpdroid.registroelettronico.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.calendar)
    CompactCalendarView calendarView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    AppBarLayout.LayoutParams params;

    SharedPreferences settings;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //  actionBar
        setSupportActionBar(toolbar);
        params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();

        //  Back/Menu Icon
        bindDrawerToggle();

        mNavigationView.setNavigationItemSelectedListener(this);

        //  first run
        settings = getSharedPreferences("REGISTRO", MODE_PRIVATE);
        if (settings.getBoolean("primo_avvio", true)) {
            // first time task
            startActivityForResult(new Intent(this, Intro.class), 1);
        } else {
            init(savedInstanceState);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                settings.edit().putBoolean("primo_avvio", false).apply();
                init(null);
            } else {
                settings.edit().putBoolean("primo_avvio", true).apply();
                //User cancelled the intro so we'll finish this activity too.
                finish();
            }
        }
    }

    private void bindDrawerToggle() {
        if (drawer != null && toolbar != null) {
            toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment;
        int id = item.getItemId();
        calendarView.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        params.setScrollFlags(0);
        switch (id) {
            case R.id.agenda:
                fragment = new FragmentAgenda();
                break;
            case R.id.medie:
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
        transaction.replace(R.id.fragment_container, fragment);

        // Commit the transaction
        transaction.commit();

        // Set action bar title
        toolbar.setTitle(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void init(Bundle savedInstanceState) {
        View header = mNavigationView.getHeaderView(0);
        TextView text = (TextView) header.findViewById(R.id.name);

        SharedPreferences settings = getSharedPreferences("REGISTRO", MODE_PRIVATE);
        String value = settings.getString("name", getString(R.string.app_name));
        text.setText(value);

        // Programmatically start a fragment
        if (savedInstanceState == null) {
            int drawer_to_open = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this)
                    .getString("drawer_to_open", "0"));

            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                drawer_to_open = extras.getInt("drawer_to_open", drawer_to_open);
            }

            mNavigationView.getMenu().getItem(drawer_to_open).setChecked(true);
            onNavigationItemSelected(mNavigationView.getMenu().getItem(drawer_to_open));
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
}
