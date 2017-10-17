package com.utn.mobile.keepapp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.utn.mobile.keepapp.domain.Ejercicio;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AgregarEjercicio extends AppCompatActivity {

    Spinner spinner_ejercicios;
    TextView unidades;
    EditText textoNotas;
    EditText textoResultado;
    ImageView imagenEjercicio;
    List<Ejercicio> listaEjercicios;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_ejercicio);
        setTitle("Nuevo ejercicio");

        this.spinner_ejercicios = (Spinner)findViewById(R.id.spn_ejercicios_agregar_ejercicio);
        this.unidades = (TextView)findViewById(R.id.unidades_agregar_ejercicio);
        this.textoNotas = (EditText)findViewById(R.id.txt_notas_agregar_ejercicio);
        this.textoResultado = (EditText)findViewById(R.id.txt_resultado_agregar_ejercicio);
        this.imagenEjercicio = (ImageView)findViewById(R.id.imagen_ejercicio);

        this.cargarEjercicios();
        this.escucharOnItemSelected();
    }

    private void escucharOnItemSelected() {
        final Context context = this;
        final ImageView imageView = this.imagenEjercicio;
        spinner_ejercicios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Ejercicio ejElegido = listaEjercicios.get(position);
                unidades.setText(ejElegido.getUnidad());
                Picasso.with(context).load(ejElegido.getImagen()).into(imageView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}

        });
    }

    private void cargarEjercicios() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ejercicios/");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaEjercicios = new ArrayList<>();
                for(DataSnapshot dspEjercicio : dataSnapshot.getChildren()){
                    Ejercicio ejercicio = dspEjercicio.getValue(Ejercicio.class);
                    listaEjercicios.add(ejercicio);
                }
                cargarSpinnerEjercicios();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void cargarSpinnerEjercicios()
    {
        List<String> spinnerArray =  new ArrayList<String>();
        for (Ejercicio ejercicio : listaEjercicios) {
            spinnerArray.add(ejercicio.getNombre());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner_ejercicios.setAdapter(adapter);
    }

    public void agregarEjercicio(View view) {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("usuarios/".concat(currentFirebaseUser.getUid()).concat("/ejercicios"));

        //String userId = mDatabase.push().getKey();
        String userId = currentFirebaseUser.getUid();
        String nombreEjercicio = spinner_ejercicios.getSelectedItem().toString();
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String fecha = df.format(new Date());
        Double resultado = Double.parseDouble(textoResultado.getText().toString());
        String unidad = unidades.getText().toString();

        Ejercicio nuevoEjercicio = new Ejercicio(nombreEjercicio, fecha, resultado, unidad);

        mDatabase.push().setValue(nuevoEjercicio);

        updateWidget(nombreEjercicio);

        this.finish();

    }

    public void updateWidget(String tipoEjercicio){
        int ej1 = R.id.newEjercicio1;
        int ej2 = R.id.newEjercicio2;
        int ej3 = R.id.newEjercicio3;

        Context context = this;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        ComponentName thisWidget = new ComponentName(context, WidgetProvider.class);
        //remoteViews.setTextViewText(R.id.textView, tipoEjercicio);
        remoteViews.setImageViewResource(ej1, R.drawable.ic_running);
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }

}
