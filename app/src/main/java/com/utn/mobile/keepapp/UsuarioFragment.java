package com.utn.mobile.keepapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.squareup.picasso.Picasso;

import java.util.concurrent.Executor;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.facebook.FacebookSdk.getApplicationContext;

public class UsuarioFragment extends Fragment {

    private View thisView;

    @BindView(R.id.profile_profile_pic)
    public ImageView profilePic;
    @BindView(R.id.profile_username)
    public TextView username;
    @BindView(R.id.profile_email)
    public TextView email;

    @BindView(R.id.fb_link_account)
    LoginButton fbLinkButton;

    private CallbackManager callbackManager;

    private ProgressDialog progressDialog;

    public FirebaseAuth mAuth;

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

        UserInfo user = mAuth.getCurrentUser();
        if(user != null){
            username.setText(user.getDisplayName()==null?user.getEmail():user.getDisplayName());
            email.setText(user.getEmail());
            Uri url = user.getPhotoUrl();
            if(url != null) {
                Picasso.with(getActivity())
                        .load(url)
                        .noFade()
                        .into(profilePic);
            }
        }

        /*if(mAuth.getCurrentUser().getProviders().contains("facebook.com")) {
            fbLinkButton.setClickable(false);
            fbLinkButton.setBackgroundColor(Color.GRAY);
            fbLinkButton.setText("Ya linkeado");
        }*/

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

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("Profile", "handleFacebookAccessToken:" + token);

        //showProgressDialog();

        Toast.makeText(getActivity(), "aadasdffgfdgbcde",
                Toast.LENGTH_SHORT).show();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Profile", "linkWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(getActivity(), "Linkeado!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w("Profile", "linkWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        //hideProgressDialog();
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
