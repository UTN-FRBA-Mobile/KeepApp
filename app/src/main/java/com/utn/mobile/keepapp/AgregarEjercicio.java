package com.utn.mobile.keepapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class AgregarEjercicio extends AppCompatActivity {

    Spinner spinner_ejercicios;
    EditText textoNotas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_ejercicio);
        setTitle("Nuevo ejercicio");

        this.spinner_ejercicios = (Spinner)findViewById(R.id.spn_ejercicios_agregar_ejercicio);
        this.textoNotas = (EditText)findViewById(R.id.txt_notas_agregar_ejercicio);

        this.cargarSpinnerEjercicios();
    }



    private void cargarSpinnerEjercicios()
    {
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("5 KM");
        spinnerArray.add("Clean & Jerk");
        spinnerArray.add("Banco Plano");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner_ejercicios.setAdapter(adapter);
    }

    public void agregarEjercicio(View view)
    {


    }

}
