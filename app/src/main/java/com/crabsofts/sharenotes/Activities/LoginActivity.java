package com.crabsofts.sharenotes.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.crabsofts.sharenotes.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    final private int RC_SIGN_IN = 100;
    private Button googleLogin;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog dialog;
    private DatabaseReference databaseReference;
    private String player_id = "";
    @Override
    public void onClick(View v) {

        signInGoogle();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        googleLogin = (Button) findViewById(R.id.gmailloginButton);

        googleLogin.setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.v("UID", "onAuthStateChanged:signed_in:" + user.getUid());
                    if(user.getDisplayName() != null) {
                        SharedPreferences preferences = getApplicationContext().getSharedPreferences("com.crabsofts.sharenotes", Context.MODE_PRIVATE);
                        if(!preferences.getString("isBegin", "").equals("No")){
                            OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                                @Override
                                public void idsAvailable(String userId, String registrationId) {
                                    player_id = userId;
                                }
                            });
                            Map<String, String> userDetails = new HashMap<String, String>();
                            userDetails.put("Email", user.getEmail());
                            userDetails.put("Name", user.getDisplayName());
                            userDetails.put("Block", "0");
                            userDetails.put("OneSignal Id", player_id);
                            userDetails.put("comment_noti", "yes");
                            databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                            databaseReference.child(user.getUid()).setValue(userDetails);

                            Intent i = new Intent(LoginActivity.this, WelcomeActivity.class);
                            startActivity(i);
                            finish();
                            preferences.edit().putString("isBegin", "No").apply();
                        }else{
                            Intent i = new Intent(LoginActivity.this, Home.class);
                            i.putExtra("name", user.getDisplayName().toString());
                            i.putExtra("email", user.getEmail().toString());
                            i.putExtra("photo", user.getPhotoUrl().toString());
                            startActivity(i);
                            finish();
                        }
                    }
                } else {
                    // User is signed out
                    Log.v("SIGNed Out", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


    }
    private void signInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                dialog = ProgressDialog.show(this,"","Signing in...",false,false);
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);

            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                Toast.makeText(this, "Sign in Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.v("ID", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        if(isInternetAvailable()){
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("TASk", "signInWithCredential:onComplete:" + task.isSuccessful());

                            dialog.dismiss();
                            if (!task.isSuccessful()) {
                                if(isInternetAvailable()){
                                    Log.w("Error", "signInWithCredential", task.getException());
                                    new AlertDialog.Builder(LoginActivity.this)
                                            .setTitle("Error")
                                            .setMessage("You are Blocked from using this Application")
                                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                }
                                            })
                                            .show();
                                }else{
                                    Toast.makeText(LoginActivity.this, "Network Problem", Toast.LENGTH_SHORT).show();
                                }

                            }else{

                                Toast.makeText(LoginActivity.this, "Logged In.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else{
            Toast.makeText(LoginActivity.this, "Network Problem", Toast.LENGTH_SHORT).show();
        }

    }
    public boolean isInternetAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager != null){
            NetworkInfo networkInfos = connectivityManager.getActiveNetworkInfo();
            if(networkInfos != null){
                if(networkInfos.isConnectedOrConnecting()){
                    return true;
                }
            }

        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
