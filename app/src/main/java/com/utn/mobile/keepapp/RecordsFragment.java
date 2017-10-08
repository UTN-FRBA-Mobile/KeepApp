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
import java.util.Collections;
import java.util.Iterator;
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

                RecordsFragment.filtrarRecords(lista_ejercicios);
                Collections.sort(lista_ejercicios); // Para ordenar (por ahora alfabeticamente)

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
                }
            }

            if(!es_record){
                it.remove();
            }
        }
    }
}
