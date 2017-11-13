package com.utn.mobile.keepapp;

import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.utn.mobile.keepapp.domain.Ejercicio;
import com.utn.mobile.keepapp.domain.FriendsAdapter;
import com.utn.mobile.keepapp.domain.Notificacion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    AppCompatButton btnSave;

    String str_imagen = null;

    private final String NEW_NATACION = "NEW_EJERCICIO_NATACION";
    private final String NEW_5KM = "NEW_EJERCICIO_5KM";
    private final String NEW_BANCO_PLANO = "NEW_EJERCICIO_BANCO_PLANO";

    private String newEjercicioWidget = null;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(FirebaseAuth.getInstance().getCurrentUser() == null ) {
            Intent i_login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i_login);
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_ejercicio);
        setTitle("Nuevo ejercicio");

        this.spinner_ejercicios = (Spinner)findViewById(R.id.spn_ejercicios_agregar_ejercicio);
        this.unidades = (TextView)findViewById(R.id.unidades_agregar_ejercicio);
        this.textoNotas = (EditText)findViewById(R.id.txt_notas_agregar_ejercicio);
        this.textoResultado = (EditText)findViewById(R.id.txt_resultado_agregar_ejercicio);
        this.imagenEjercicio = (ImageView)findViewById(R.id.imagen_ejercicio);
        this.progressDialog = new ProgressDialog(this);
        this.btnSave = (AppCompatButton) findViewById(R.id.btn_guardar_agregar_ejercicio);
        this.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarEjercicio();
            }
        });

        String action = getIntent().getAction();
        if (action != null) {
            newEjercicioWidget = action;
        }

        this.cargarEjercicios();
        this.escucharOnItemSelected();
    }

    @Override
    public void onResume(){
        super.onResume();

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
                str_imagen = ejElegido.getImagen();
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

        String busqueda = "";
        if(newEjercicioWidget != null){
            switch (newEjercicioWidget) {
                case NEW_NATACION:
                    busqueda = "Natacion";
                    break;
                case NEW_5KM:
                    busqueda = "5 KM";
                    break;
                case NEW_BANCO_PLANO:
                    busqueda = "Banco Plano";
                    break;
            }
            for (int position = 0; position < listaEjercicios.size(); position++){
                if(listaEjercicios.get(position).getNombre().equalsIgnoreCase(busqueda)) {
                    spinner_ejercicios.setSelection(position);
                }
            }
        }
    }

    public void agregarEjercicio() {
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

        if(str_imagen != null)
            nuevoEjercicio.setImagen(str_imagen);

        mDatabase.push().setValue(nuevoEjercicio);

        onExerciseSaved();


    }

    private void onExerciseSaved(){
        final AccessToken fbToken = AccessToken.getCurrentAccessToken();
        if(fbToken == null){
            finish();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Sigue superándote día a día")
                .setTitle("Felicitaciones");
        builder.setPositiveButton("Compartir con mis amigos", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                progressDialog.show();
                //FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if(fbToken == null){
                    Toast.makeText(AgregarEjercicio.this, "No tiene vinculada una cuenta de Facebook", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                if(fbToken.isExpired()){
                    Toast.makeText(AgregarEjercicio.this, "Ocurrió un error, intente en unos instantes", Toast.LENGTH_LONG).show();
                    AccessToken.refreshCurrentAccessTokenAsync();
                    return;
                }
                GraphRequest request = GraphRequest.newGraphPathRequest(fbToken, "me/friends",
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            progressDialog.hide();
                            Log.d("RESPONSE", response.toString());
                            try {
                                JSONArray users = response.getJSONObject().getJSONArray("data");
                                Log.d("FACEBOOK_FRIENDS", users.toString());
                                showShareDialog(users);
                            }catch(JSONException e){
                                Toast.makeText(AgregarEjercicio.this, "Error en formato de JSON", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,installed");
                request.setParameters(parameters);
                request.executeAsync();
            }
        });
        builder.setNegativeButton("Solo guardar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            } });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showShareDialog(JSONArray users){
        AlertDialog.Builder builder = new AlertDialog.Builder(AgregarEjercicio.this, R.style.AppTheme_Dark_Dialog);
        LayoutInflater inflater = LayoutInflater.from(AgregarEjercicio.this);
        View view = inflater.inflate(R.layout.dialog_sharewithfriends, null);
        final RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.friendsRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        final FriendsAdapter mAdapter = new FriendsAdapter(users, AgregarEjercicio.this);
        mRecyclerView.setAdapter(mAdapter);
        builder.setView(view);
        builder.setTitle("Elige un amigo");
        builder.setPositiveButton("Compartir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //compartir a los amigos seleccionados
                List<String> selectedIds = mAdapter.getSelectedIds();
                for(int i = 0; i<selectedIds.size(); i++){
                    crearNotification(selectedIds.get(i));
                }
                Toast.makeText(getApplicationContext(), "Notificaciones enviadas a "+
                        selectedIds.size()+" amigo"+(selectedIds.size()>1?"s":""), Toast.LENGTH_LONG).show();
                finish();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create().show();
    }


    private void crearNotification(String facebookId){
        //obtengo el id de firebase del amigo de facebook
        FirebaseDatabase.getInstance().getReference("facebook_users").child(facebookId+"/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userToId = dataSnapshot.child("firebaseId").getValue().toString();
                    String userFrom = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    String message = "Cargué un nuevo ejercicio de " + spinner_ejercicios.getSelectedItem().toString()+
                            ". No te quedes atrás.";

                    //inserto la notification
                    Notificacion notification = new Notificacion(userToId, userFrom, message);
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Notifications");
                    mDatabase.push().setValue(notification);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void updateWidget(String tipoEjercicio){
        //int ej1 = R.id.newEjercicio1;
        //int ej2 = R.id.newEjercicio2;
        //int ej3 = R.id.newEjercicio3;

        Context context = this;

        /*SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("ej1", "lalala");
        editor.commit();*/

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        ComponentName thisWidget = new ComponentName(context, WidgetProvider.class);
        //remoteViews.setTextViewText(R.id.textView, tipoEjercicio);
        //remoteViews.setImageViewResource(ej1, R.drawable.ic_running);
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);

        //TextView tvw = (TextView)findViewById(R.id.textViewWidget);

        //Toast.makeText(this,tvw.getText().toString(),Toast.LENGTH_SHORT).show();
    }

}
