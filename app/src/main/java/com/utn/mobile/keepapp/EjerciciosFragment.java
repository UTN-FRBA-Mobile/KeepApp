package com.utn.mobile.keepapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
import java.util.List;

/**
 * Created by garci on 20/10/2017.
 */

public class EjerciciosFragment extends Fragment {

    private View thisView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        thisView = inflater.inflate(R.layout.fragment_records, container, false);

        this.cargarListView();

        return thisView;
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
}
