package com.utn.mobile.keepapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AgregarEjercicio extends AppCompatActivity {

    Spinner spinner_ejercicios;
    Spinner spinner_unidades;
    EditText textoNotas;
    EditText textoResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_ejercicio);
        setTitle("Nuevo ejercicio");

        this.spinner_ejercicios = (Spinner)findViewById(R.id.spn_ejercicios_agregar_ejercicio);
        this.spinner_unidades = (Spinner)findViewById(R.id.spn_unidades_agregar_ejercicio);
        this.textoNotas = (EditText)findViewById(R.id.txt_notas_agregar_ejercicio);
        this.textoResultado = (EditText)findViewById(R.id.txt_resultado_agregar_ejercicio);

        this.cargarSpinnerEjercicios();
        this.cargarSpinnerUnidades();
    }



    private void cargarSpinnerEjercicios()
    {
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("5K");
        spinnerArray.add("Clean & Jerk");
        spinnerArray.add("Banco Plano");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner_ejercicios.setAdapter(adapter);
    }

    private void cargarSpinnerUnidades()
    {
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("min");
        spinnerArray.add("kg");
        spinnerArray.add("mts");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner_unidades.setAdapter(adapter);
    }

    public void agregarEjercicio(View view)
    {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("ejercicios");

        //String userId = mDatabase.push().getKey();
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentFirebaseUser.getUid();
        String nombreEjercicio = spinner_ejercicios.getSelectedItem().toString();
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String fecha = df.format(new Date());
        Double resultado = Double.parseDouble(textoResultado.getText().toString());
        String unidad = spinner_unidades.getSelectedItem().toString();

        Ejercicio nuevoEjercicio = new Ejercicio(nombreEjercicio, fecha, resultado, unidad);

        mDatabase.child(userId).child(nombreEjercicio).setValue(nuevoEjercicio);

        this.finish();

    }

}
