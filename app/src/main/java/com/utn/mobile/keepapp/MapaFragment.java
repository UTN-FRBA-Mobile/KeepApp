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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;


public class MapaFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mapa;
    private Activity activity;
    private Location myLocation;

    public MapaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mapa, container,
                false);
        activity = getActivity();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return v;

    }

    @Override
    public void onMapReady(GoogleMap map) {
        mapa = map;
        getMyLocation();
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
}
