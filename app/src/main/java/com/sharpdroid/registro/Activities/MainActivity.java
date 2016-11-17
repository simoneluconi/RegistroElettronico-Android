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

import com.sharpdroid.registro.Fragments.FragmentAllAbsences;
import com.sharpdroid.registro.Fragments.FragmentCommunications;
import com.sharpdroid.registro.Fragments.FragmentMedie;
import com.sharpdroid.registro.Fragments.FragmentNote;
import com.sharpdroid.registro.Fragments.FragmentSettings;
import com.sharpdroid.registro.R;

import static com.sharpdroid.registro.Utils.Metodi.NomeDecente;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        boolean firstStart = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("primo_avvio", true);

        if (firstStart) {
            Intent intent = new Intent(this, Intro.class);
            startActivityForResult(intent, 1);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences settings = getSharedPreferences("REGISTRO", MODE_PRIVATE);

        View header = navigationView.getHeaderView(0);
        TextView text = (TextView) header.findViewById(R.id.textview_name);

        String value = settings.getString("name", getString(R.string.app_name));
        text.setText(NomeDecente(value));

        // Programmatically start a fragment
        if (savedInstanceState == null) {
            int drawer_to_open = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this)
                    .getString("drawer_to_open", "0"));

            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                drawer_to_open = extras.getInt("drawer_to_open", drawer_to_open);
            }

            navigationView.getMenu().getItem(drawer_to_open).setChecked(true);
            onNavigationItemSelected(navigationView.getMenu().getItem(drawer_to_open));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putBoolean("primo_avvio", false)
                        .apply();
            } else {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putBoolean("primo_avvio", true)
                        .apply();
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
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.medie:
                fragment = new FragmentMedie();
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
            case R.id.nav_share:
            case R.id.nav_send:
            default:
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
}
