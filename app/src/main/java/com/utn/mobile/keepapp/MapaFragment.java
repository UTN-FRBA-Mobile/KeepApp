package com.utn.mobile.keepapp;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utn.mobile.keepapp.domain.Gimnasio;

public class MapaFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {
    private GoogleMap mapa;
    private Marker currentMarker;
    private Activity activity;
    private Location myLocation;
    private LinearLayout form;
    private LinearLayout help;
    private DatabaseReference mDatabase;
    private EditText inputName;

    public MapaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mapa, container,
                false);
        activity = getActivity();

        getActivity().setTitle("Mi Gimnasio");

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("usuarios/".concat(currentFirebaseUser.getUid()).concat("/gimnasios"));

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        form = (LinearLayout) getView().findViewById(R.id.markerForm);
        help = (LinearLayout) getView().findViewById(R.id.helpLayout);
        inputName = (EditText) getView().findViewById(R.id.inputName);

        final ImageButton saveButton = (ImageButton) getView().findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String nombre = inputName.getText().toString();
                Gimnasio nuevoGimnasio = new Gimnasio(nombre, currentMarker.getPosition().latitude, currentMarker.getPosition().longitude);
                currentMarker.setTag(nuevoGimnasio);

                mDatabase.push().setValue(nuevoGimnasio, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        hideForm();
                        Toast.makeText(getContext(), "El gimnasio ha sido guardado satisfactoriamente", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        final ImageButton deleteButton = (ImageButton) getView().findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Gimnasio currentGym = (Gimnasio) currentMarker.getTag();
                mDatabase.child(currentGym.getFirebaseId()).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        hideForm();
                        Toast.makeText(getContext(), "El gimnasio ha sido eliminado satisfactoriamente", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        final ImageButton closeButton = (ImageButton) getView().findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideForm();
            }
        });
    }

    private void hideForm() {
        form.setVisibility(View.GONE);
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showForm() {
        help.setVisibility(View.INVISIBLE);
        form.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mapa = map;
        getMyLocation();
        mapa.setOnMapLongClickListener(this);
        mapa.setOnMarkerClickListener(this);
        loadSavedGyms();
    }

    private void loadSavedGyms() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mapa.clear();
                drawSavedGyms(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        mDatabase.removeEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mapa.clear();
                drawSavedGyms(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void drawSavedGyms(DataSnapshot dataSnapshot) {
        boolean hasElements = false;
        for(DataSnapshot dspGimnasio : dataSnapshot.getChildren()){
            hasElements = true;
            Gimnasio gimnasio = dspGimnasio.getValue(Gimnasio.class);
            gimnasio.setFirebaseId(dspGimnasio.getKey());

            Marker marker = mapa.addMarker(new MarkerOptions()
                    .position(new LatLng(gimnasio.getLatitud(), gimnasio.getLongitud()))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            );
            marker.setTag(gimnasio);
        }
        if(hasElements) help.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onMapLongClick(LatLng point) {
        inputName.setText("");
        currentMarker = mapa.addMarker(new MarkerOptions()
            .position(point)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        showForm();
    }

    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            String provider = lm.getBestProvider(criteria, true);

            lm.requestSingleUpdate(provider, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    myLocation = location;
                    drawLocation();
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {}

                @Override
                public void onProviderEnabled(String s) {}

                @Override
                public void onProviderDisabled(String s) {}
            }, null);
        }
    }

    private void drawLocation() {
        if (myLocation != null) {
            mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 17));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getMyLocation();
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        currentMarker = marker;

        Gimnasio currentGym = (Gimnasio) marker.getTag();
        inputName.setText(currentGym.getNombre());
        showForm();

        return true;
    }

}
