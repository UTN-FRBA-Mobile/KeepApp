package com.utn.mobile.keepapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.utn.mobile.keepapp.domain.Usuario;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainMenuActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    TextView menuUsername;
    TextView menuEmail;
    ImageView menuProfilePic;

    FirebaseUser user;
    FirebaseDatabase firebaseDb;


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
        menuEmail = (TextView) header.findViewById(R.id.menu_email);
        menuProfilePic = (ImageView) header.findViewById(R.id.menu_profile_pic);

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment userFragment = new UsuarioFragment();
                MainMenuActivity.this.getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.pop_enter,R.anim.pop_exit)
                        .replace(R.id.contentFragment, userFragment)
                        .addToBackStack(String.valueOf(userFragment.getId()))
                        .commit();

                drawer.closeDrawer(GravityCompat.START);
            }
        });

        //selecciono la pantalla default
        if(savedInstanceState == null){
            Fragment recordsFragment = new RecordsAndExercisesFragment();
            MainMenuActivity.this.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.pop_enter,R.anim.pop_exit)
                    .replace(R.id.contentFragment, recordsFragment)
                    //.addToBackStack(String.valueOf(recordsFragment.getId()))
                    .commit();
            navigationView.setCheckedItem(R.id.nav_records);
        }

        firebaseDb = FirebaseDatabase.getInstance();

        updateUserUIData();


// ...


        /*user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            menuUsername.setText(user.getDisplayName()==null?user.getEmail():user.getDisplayName());
            menuEmail.setText(user.getEmail());
            Uri url = user.getPhotoUrl();
            if(url != null) {
                Picasso.with(this)
                        .load(url)
                        .noFade()
                        .into(menuProfilePic);
            }
        }*/


    }

    private void updateUserUIData() {
        firebaseDb
                .getReference("usuarios")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            Usuario usuario = dataSnapshot.getValue(Usuario.class);
                            menuUsername.setText(usuario.getUsername());
                            menuEmail.setText(usuario.getEmail());
                            if(usuario.getProfilePic() != null) {
                                Uri uri = Uri.parse(usuario.getProfilePic());
                                Picasso.with(getApplicationContext())
                                        .load(uri)
                                        .noFade()
                                        .into(menuProfilePic);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
            nextFragment = new RecordsAndExercisesFragment();
        }else if(id == R.id.nav_mapa){
            nextFragment = new MapaFragment();
        }else if(id == R.id.nav_sign_out) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Cerrar Sesión")
                    .setMessage("¿Estás seguro?")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseAuth.getInstance().signOut();
                            LoginManager.getInstance().logOut();
                            Intent logInIntent = new Intent(getApplicationContext(), LoginActivity.class);
                            logInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(logInIntent);
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }

        if (nextFragment != null) {
            MainMenuActivity.this.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.pop_enter,R.anim.pop_exit)
                    .replace(R.id.contentFragment, nextFragment)
                    .addToBackStack(String.valueOf(nextFragment.getId()))
                    .commit();
        }
        getSupportActionBar().setTitle(item.getTitle());
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            //System.out.println("@#@");
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }


}
