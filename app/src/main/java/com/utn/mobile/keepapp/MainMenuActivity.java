package com.utn.mobile.keepapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    TextView menuUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        menuUsername = (TextView) header.findViewById(R.id.menu_username);

        //selecciono la pantalla default
        if(savedInstanceState == null){
            Fragment recordsFragment = new RecordsFragment();
            MainMenuActivity.this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contentFragment, recordsFragment)
                    //.addToBackStack(String.valueOf(recordsFragment.getId()))
                    .commit();
            navigationView.setCheckedItem(R.id.nav_records);
        }
        if(FirebaseAuth.getInstance().getCurrentUser().getDisplayName() != null){
            menuUsername.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int count = getFragmentManager().getBackStackEntryCount();

            if (count == 0) {
                super.onBackPressed();
                //additional code
            } else {
                getFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment nextFragment = null;

        if (id == R.id.nav_records) {
            nextFragment = new RecordsFragment();
        }else if(id == R.id.nav_records_viejo){
            //por el momento, dejo un acceso a la pantalla vieja
            Intent recordsIntent = new Intent(getApplicationContext(), RecordsActivity.class);
            startActivity(recordsIntent);
            return true;
        }else if(id == R.id.nav_mapa){
            nextFragment = new MapaFragment();
        }else if(id == R.id.nav_sign_out) {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            Intent logInIntent = new Intent(getApplicationContext(), LoginActivity.class);
            logInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(logInIntent);
            finish();
        }

        if (nextFragment != null) {
            MainMenuActivity.this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contentFragment, nextFragment)
                    .addToBackStack(String.valueOf(nextFragment.getId()))
                    .commit();
        }
        getSupportActionBar().setTitle(item.getTitle());
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
