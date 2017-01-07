package com.sharpdroid.registro.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.sharpdroid.registro.Fragments.FragmentAgenda;
import com.sharpdroid.registro.Fragments.FragmentAllAbsences;
import com.sharpdroid.registro.Fragments.FragmentCommunications;
import com.sharpdroid.registro.Fragments.FragmentFolders;
import com.sharpdroid.registro.Fragments.FragmentMedie;
import com.sharpdroid.registro.Fragments.FragmentNote;
import com.sharpdroid.registro.Fragments.FragmentSettings;
import com.sharpdroid.registro.Fragments.FragmentTimetable;
import com.sharpdroid.registro.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.calendar)
    CompactCalendarView calendarView;
    @BindView(R.id.month)
    TextView monthView;

    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

            if (isAgendaSelected()) {
                calendarView.setVisibility(View.VISIBLE);
                monthView.setVisibility(View.VISIBLE);
            } else {
                calendarView.setVisibility(View.GONE);
                monthView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment;
        calendarView.setVisibility(View.GONE);
        monthView.setVisibility(View.GONE);
        switch (item.getItemId()) {
            case R.id.agenda:
                fragment = FragmentAgenda.getInstance(calendarView, monthView);
                calendarView.setVisibility(View.VISIBLE);
                monthView.setVisibility(View.VISIBLE);
                break;
            case R.id.medie:
                fragment = new FragmentMedie();
                break;
            case R.id.timetable:
                fragment = new FragmentTimetable();
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
                fragment = new FragmentFolders();
                break;
            case R.id.nav_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Registro Elettronico");
                String url = "https://play.google.com/store/apps/details?id=com.sharpdroid.registroelettronico";
                intent.putExtra(Intent.EXTRA_TEXT, url);
                startActivity(Intent.createChooser(intent, getString(R.string.select_app)));
                return false;

            case R.id.nav_send:
                Intent intent_mail = new Intent(Intent.ACTION_SEND);
                intent_mail.putExtra(Intent.EXTRA_EMAIL, new String[]{"bugreport@registroelettronico.ml"});
                intent_mail.putExtra(Intent.EXTRA_SUBJECT, "Registro Elettronico");
                intent_mail.setType("text/plain");
                startActivity(Intent.createChooser(intent_mail, getString(R.string.select_email_client)));
                return false;

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
        setTitle(item.getTitle());

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

    boolean isAgendaSelected() {
        return mNavigationView.getMenu().findItem(R.id.agenda).isChecked();
    }
}
