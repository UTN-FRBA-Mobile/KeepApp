package com.utn.mobile.keepapp;


import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;


public class RecordsAndExercisesFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {

    private AppBarLayout appBar;
    private TabLayout tabs;
    private ViewPager viewPager;

    private static ArrayList<Geofence> mGeofenceList;
    private GoogleApiClient mGoogleApiClient;

    public RecordsAndExercisesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_records_and_exercises, container, false);
        insertTabs(container);

        viewPager = (ViewPager) rootView.findViewById(R.id.pagerRecords);
        setupViewPager(viewPager);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.boton_records);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i_agregar = new Intent(getContext(), AgregarEjercicio.class);
                startActivity(i_agregar);
            }
        });

        FloatingActionButton fabGeoF = (FloatingActionButton) rootView.findViewById(R.id.boton_geo);
        fabGeoF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                probarGeo(view);
            }
        });

        //Lista vacia de geofences
        mGeofenceList = new ArrayList<>();
        populateGeofenceList();
        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();

        return rootView;
    }

    private void insertTabs(ViewGroup container) {
        View parent = (View) container.getParent();
        appBar = (AppBarLayout) parent.findViewById(R.id.appbar);
        tabs = new TabLayout(getContext());
        //tabs.setTabTextColors(R.color.colorNonSelected, R.color.colorSelected);
        appBar.addView(tabs);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsAdapter adapter = new SectionsAdapter(getFragmentManager());
        adapter.addFragment(new RecordsFragment(), "Records");
        adapter.addFragment(new EjerciciosFragment(), "Ejercicios"); //TODO crear fragmento
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        appBar.removeView(tabs);
    }

    public class SectionsAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentTitles = new ArrayList<>();

        public SectionsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(android.support.v4.app.Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitles.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }
    }



    public void probarGeo(View view){
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(getContext(), "Google API Client not connected!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
            Toast.makeText(getContext(),"Geofencing started", Toast.LENGTH_LONG).show();
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
        }
    }

    public void populateGeofenceList() {
        mGeofenceList.add(new Geofence.Builder()
                .setRequestId("Prueba2")
                .setCircularRegion(-34.606579, -58.435360, 100)
                .setExpirationDuration(0)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(getContext(), GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling addgeoFences()
        return PendingIntent.getService(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnecting() || !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            Toast.makeText(getContext(), "Geofences Added", Toast.LENGTH_SHORT).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = "Error función onResult"; //CAMBIAR?
            Log.e("RecordsFragment", errorMessage);
        }
    }

}
