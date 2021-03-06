package com.utn.mobile.keepapp;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utn.mobile.keepapp.domain.Ejercicio;
import com.utn.mobile.keepapp.domain.Gimnasio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by garci on 20/10/2017.
 */

public class EjerciciosFragment extends Fragment  implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status>{

    private View thisView;

    private static ArrayList<Geofence> mGeofenceList;
    private GoogleApiClient mGoogleApiClient;

    private PendingIntent mGeofencePendingIntent;

    private DatabaseReference mDatabase;

    Semaphore sem_geofencing;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        thisView = inflater.inflate(R.layout.fragment_ejercicios, container, false);

        this.cargarListView();

        FloatingActionButton fab = (FloatingActionButton) thisView.findViewById(R.id.boton_records);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i_agregar = new Intent(getContext(), AgregarEjercicio.class);
                startActivity(i_agregar);
            }
        });


        sem_geofencing = new Semaphore(0);

        //Lista vacia de geofences
        mGeofenceList = new ArrayList<>();
        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();


        return thisView;
    }


    public void cargarListView(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuarios/".concat(currentUser.getUid()).concat("/ejercicios"));

        final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();

        final ListView mi_lista = (ListView) thisView.findViewById(R.id.list_ejercicios);
        final Context context = getContext();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Ejercicio> lista_ejercicios = new ArrayList<>();

                int i = 1;

                for(DataSnapshot dspEjercicio : dataSnapshot.getChildren()){
                    Ejercicio ejercicio = dspEjercicio.getValue(Ejercicio.class);
                    //ejercicio.setNombre(dspEjercicio.getKey());
                    if(i < 4){
                        editor.putString("ej"+i+"Name",ejercicio.getNombre());
                        editor.putString("ej"+i+"Image",ejercicio.getImagen());
                        i++;
                    }

                    lista_ejercicios.add(ejercicio);
                }

                editor.commit();

                // Para ordenar, por ahora alfabeticamente, pero se puede configurar cualquier criterio
                Collections.sort(lista_ejercicios);

                EjerciciosAdapter adapter = new EjerciciosAdapter(lista_ejercicios, context);
                mi_lista.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }


    public void probarGeo(View view){
        if (!mGoogleApiClient.isConnected()) {
            try {
                sem_geofencing.acquire();
                Thread.sleep(3000);
            }catch (Exception e){
                createToast(EjerciciosFragment.this.getActivity(), "Error de semaforo");
                return;
            }
        }

        if(mGeofenceList.size() == 0){
            return;
        }

        if (!mGoogleApiClient.isConnected()) {
            createToast(EjerciciosFragment.this.getActivity(), "Google API Client not connected");
        }else {
            try {
                mGeofencePendingIntent = getGeofencePendingIntent();
                LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, mGeofencePendingIntent);
                LocationServices.GeofencingApi.addGeofences(
                        mGoogleApiClient,
                        getGeofencingRequest(),
                        mGeofencePendingIntent
                ).setResultCallback(this); // Result processed in onResult().
            } catch (SecurityException securityException) {
                createToast(EjerciciosFragment.this.getActivity(), "Error en permisos de ubicación");
            }
        }
    }

    public void createToast(final Activity activity, final String texto){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, texto, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void populateGeofenceList() {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("usuarios/".concat(currentFirebaseUser.getUid()).concat("/gimnasios"));
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean hasElements = false;
                for(DataSnapshot dspGimnasio : dataSnapshot.getChildren()){
                    hasElements = true;
                    Gimnasio gimnasio = dspGimnasio.getValue(Gimnasio.class);
                    //gimnasio.setFirebaseId(dspGimnasio.getKey());
                    mGeofenceList.add(new Geofence.Builder()
                            .setRequestId(gimnasio.getNombre())
                            .setCircularRegion(gimnasio.getLatitud(), gimnasio.getLongitud(), 100)
                            .setExpirationDuration(9999999)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                    Geofence.GEOFENCE_TRANSITION_EXIT)
                            .build());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
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
        //builder.setInitialTrigger(0);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (null != mGeofencePendingIntent) {
            return mGeofencePendingIntent;
        } else {
            Intent intent = new Intent(getContext(), GeofenceTransitionsIntentService.class);
            return PendingIntent.getService(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            //Intent intent = new Intent(getContext(), GeofenceReceiver.class);
            //return PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
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
    public void onResume(){
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                populateGeofenceList();
                probarGeo(null);
            }
        }).start();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        this.sem_geofencing.release();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        this.sem_geofencing.release();
    }

    @Override
    public void onResult(@NonNull Status status) {
        if (!status.isSuccess()) {
            createToast(EjerciciosFragment.this.getActivity(), "Error al agregar Geofences");
        }
    }
}
