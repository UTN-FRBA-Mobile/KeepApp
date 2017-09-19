package com.utn.mobile.keepapp;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import java.util.List;


public class RecordsFragment extends Fragment {


    public RecordsFragment() {}

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
        return thisView;
    }

    public void cargarListView(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuarios/".concat(currentUser.getUid()).concat("/ejercicios"));

        final ListView mi_lista = (ListView) thisView.findViewById(R.id.list_records);
        final Context context = getContext();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Ejercicio> lista_ejercicios = new ArrayList<>();

                for(DataSnapshot dspEjercicio : dataSnapshot.getChildren()){
                    Ejercicio ejercicio = dspEjercicio.getValue(Ejercicio.class);
                    //ejercicio.setNombre(dspEjercicio.getKey());

                    lista_ejercicios.add(ejercicio);
                }

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
