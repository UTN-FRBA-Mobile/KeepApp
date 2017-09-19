package com.utn.mobile.keepapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.utn.mobile.keepapp.domain.Usuario;

import java.util.concurrent.Executor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.facebook.FacebookSdk.getApplicationContext;

public class UsuarioFragment extends Fragment {

    private View thisView;

    @BindView(R.id.profile_profile_pic)
    public ImageView profilePic;
    @BindView(R.id.profile_username)
    public TextView username;
    @BindView(R.id.profile_fullname)
    public TextView fullname;
    @BindView(R.id.profile_email)
    public TextView email;

    @BindView(R.id.fb_link_account)
    LoginButton fbLinkButton;

    private CallbackManager callbackManager;

    private ProgressDialog progressDialog;

    public FirebaseAuth mAuth;
    FirebaseDatabase firebaseDb;
    DatabaseReference dbUserLogged;

    public UsuarioFragment() {
        // Required empty public constructor
    }

    public void showProgressDialog() {
        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Autenticando...");
        progressDialog.show();
    }

    public void hideProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        thisView = inflater.inflate(R.layout.fragment_usuario, container, false);
        ButterKnife.bind(this,thisView);

        callbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();
        firebaseDb = FirebaseDatabase.getInstance();
        dbUserLogged = firebaseDb.getReference("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        showProgressDialog();
        updateUserUIData();
        hideProgressDialog();

        email.setFocusable(false);
        email.setEnabled(false);
        email.setCursorVisible(false);
        email.setKeyListener(null);
        email.setBackgroundColor(Color.TRANSPARENT);

        fbLinkButton.setReadPermissions("email", "public_profile");
        // Register your callback//
        fbLinkButton.registerCallback(callbackManager,

                // If the login attempt is successful, then call onSuccess and pass the LoginResult//
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Profile", "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    // If the user cancels the login, then call onCancel//
                    @Override
                    public void onCancel() {
                        Log.d("Profile", "facebook:onCancel");
                    }

                    // If an error occurs, then call onError//
                    @Override
                    public void onError(FacebookException exception) {
                        Log.d("Profile", "facebook:onError", exception);
                    }
                });

        return thisView;
    }

    private void updateUserUIData() {
        dbUserLogged
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            Usuario usuario = dataSnapshot.getValue(Usuario.class);
                            username.setText(usuario.getUsername());
                            email.setText(usuario.getEmail());
                            fullname.setText(usuario.getFullName());
                            if(usuario.getProfilePic() != null) {
                                Uri uri = Uri.parse(usuario.getProfilePic());
                                Picasso.with(getApplicationContext())
                                        .load(uri)
                                        .noFade()
                                        .into(profilePic);
                            }
                            if(usuario.getProvider().equals("facebook")) {
                                fbLinkButton.setClickable(false);
                                fbLinkButton.setBackgroundColor(Color.GRAY);
                                fbLinkButton.setText("Ya inició");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @OnClick(R.id.profile_save_changes)
    public void saveChanges(View view) {
        if(!validateForm()) {
            return;
        }

        dbUserLogged
                .child("username")
                .setValue(username.getText().toString());

        dbUserLogged
                .child("fullName")
                .setValue(fullname.getText().toString());

        Snackbar.make(view, "Cambios guardados", Snackbar.LENGTH_LONG).show();

    }

    private boolean validateForm() {
        boolean valid = true;

        String un = username.getText().toString();
        if (TextUtils.isEmpty(un)) {
            username.setError("Obligatorio");
            valid = false;
        } else {
            username.setError(null);
        }

        /*String fn = fullname.getText().toString();
        if (TextUtils.isEmpty(fn)) {
            fullname.setError("Obligatorio");
            valid = false;
        } else {
            fullname.setError(null);
        }*/

        return valid;
    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("Profile", "handleFacebookAccessToken:" + token);

        showProgressDialog();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Profile", "linkWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            Snackbar.make(thisView, "Cuentas asociadas", Snackbar.LENGTH_LONG).show();
                        } else {
                            LoginManager.getInstance().logOut();
                            Log.w("Profile", "linkWithCredential:failure", task.getException());
                            if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Snackbar.make(thisView, "La cuenta de FB ya esta en uso.", Snackbar.LENGTH_LONG).show();
                            }
                        }
                        hideProgressDialog();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
