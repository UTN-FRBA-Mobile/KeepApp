package com.utn.mobile.keepapp;


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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;



public class RecordsFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status>{


    public RecordsFragment() {}

    private static ArrayList<Geofence> mGeofenceList;
    private GoogleApiClient mGoogleApiClient;

    private View thisView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        thisView = inflater.inflate(R.layout.fragment_records, container, false);

        this.cargarListView();

        FloatingActionButton fab = (FloatingActionButton) thisView.findViewById(R.id.boton_records);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i_agregar = new Intent(getContext(), AgregarEjercicio.class);
                startActivity(i_agregar);
            }
        });

        FloatingActionButton fabGeoF = (FloatingActionButton) thisView.findViewById(R.id.boton_geo);
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

        return thisView;
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
                .setRequestId("Mi geofence de prueba")
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

    public void cargarListView(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuarios/".concat(currentUser.getUid()).concat("/ejercicios"));

        final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();

        final ListView mi_lista = (ListView) thisView.findViewById(R.id.list_records);
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

                RecordsFragment.filtrarRecords(lista_ejercicios);
                //Toast.makeText(getContext(),"Filtrado por records",Toast.LENGTH_SHORT).show();

                // Para ordenar, por ahora alfabeticamente, pero se puede configurar cualquier criterio
                Collections.sort(lista_ejercicios);
                //Toast.makeText(getContext(),"Ordenado alfabeticamente",Toast.LENGTH_SHORT).show();

                EjerciciosAdapter adapter = new EjerciciosAdapter(lista_ejercicios, context);
                mi_lista.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public static void filtrarRecords(List<Ejercicio> lista_ejercicios){
        Iterator<Ejercicio> it = lista_ejercicios.iterator();
        while(it.hasNext()){
            Ejercicio ej_checkeo = it.next();

            boolean es_record = true;
            for(int j = 0; j < lista_ejercicios.size(); j++){

                if(lista_ejercicios.get(j).esMismoEjercicio(ej_checkeo)
                        && lista_ejercicios.get(j).esMejor(ej_checkeo)){
                    es_record = false;
                    break;
                }
            }

            if(!es_record){
                it.remove();
            }
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

    @Override
    public void onResume() {
        super.onResume();
        this.cargarListView();
    }
}
