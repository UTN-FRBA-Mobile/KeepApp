package com.utn.mobile.keepapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class RecordsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.cargarListView();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.boton_records);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i_agregar = new Intent(getApplicationContext(), AgregarEjercicio.class);
                startActivity(i_agregar);
            }
        });
    }

    public void cargarListView(){
        List<Ejercicio> lista_ejercicios = new ArrayList<>();
        lista_ejercicios.add(new Ejercicio("5 KM"));
        lista_ejercicios.add(new Ejercicio("Clean & Jerk"));
        lista_ejercicios.add(new Ejercicio("Banco Plano"));

        EjerciciosAdapter adapter = new EjerciciosAdapter(lista_ejercicios, this);
        ListView mi_lista = (ListView)findViewById(R.id.list_records);
        mi_lista.setAdapter(adapter);
    }

}
