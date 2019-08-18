package com.crabsofts.sharenotes.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crabsofts.sharenotes.BuildConfig;
import com.crabsofts.sharenotes.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.thebrownarrow.customfont.CustomFontTextView;

import java.util.HashMap;
import java.util.Map;

public class ContributeActivity extends AppCompatActivity {
    public final static int FILE_REQUEST_CODE = 1;
    public StorageReference storagereference;
    CustomFontTextView fontTextView;
    TextView fromuser;
    FirebaseUser user;
    UploadTask uploadTask;
    DatabaseReference mDatabase;
    ProgressDialog progressDialog;
    InterstitialAd interstitialAd;
    EditText subjectEditText, messageEditText, phoneEditText;

    public void showchooser(View view) {
        if(messageEditText.getText().toString().trim().replaceAll("\\s{2,}", " ").isEmpty() || subjectEditText.getText().toString().trim().replaceAll("\\s{2,}", " ").isEmpty() || phoneEditText.getText().toString().trim().replaceAll("\\s{2,}", " ").length()!=12){
            new AlertDialog.Builder(this)
                    .setMessage("Please Fill the Fields Properly. Make sure you have included country code along with your phone number.")
                    .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }else{
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/x-zip-compressed");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try{
                startActivityForResult(Intent.createChooser(intent, "Select a Zip File to upload."), FILE_REQUEST_CODE);
            }catch (Exception e){
                Toast.makeText(this, "File Manager Not found", Toast.LENGTH_SHORT).show();
            }
        }


    }
    @SuppressWarnings("VisibleForTests")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == FILE_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                if(isInternetAvailable()){
                    storagereference = FirebaseStorage.getInstance().getReference("User Uploads/"+user.getDisplayName()+"/"+subjectEditText.getText().toString());
                    Uri uri = data.getData();
                    Log.i("uri", uri.toString());
                    progressDialog.show();
                    uploadTask = storagereference.putFile(uri);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Map<String, String> details = new HashMap<String, String>();
                            details.put("Subject", subjectEditText.getText().toString().trim().replaceAll("\\s{2,}", " "));
                            details.put("Message", messageEditText.getText().toString().trim().replaceAll("\\s{2,}", " "));
                            details.put("Phone", phoneEditText.getText().toString().trim().replaceAll("\\s{2,}", " "));
                            mDatabase = FirebaseDatabase.getInstance().getReference("User Uploads");
                            mDatabase.child(user.getUid()).push().setValue(details);
                            new AlertDialog.Builder(ContributeActivity.this)
                                    .setTitle("Thanks")
                                    .setMessage("File Uploaded Succesfully. Thanks for the Contribution :)")
                                    .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .show();

                            Toast.makeText(ContributeActivity.this, "File Uploaded Succesfully", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded: "+(int) progress+"%");
                        }
                    });
                }else{
                    Toast.makeText(this, "Network Problem", Toast.LENGTH_SHORT).show();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
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
    public void onBackPressed() {
        super.onBackPressed();
        if (interstitialAd.isLoaded()) {
            //interstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribute);
        fontTextView = (CustomFontTextView) findViewById(R.id.logoView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        fontTextView.setText("Contribute");
        fromuser = (TextView) findViewById(R.id.fromuser);
        subjectEditText = (EditText) findViewById(R.id.subjectEditText);
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        phoneEditText = (EditText) findViewById(R.id.phoneEditText);
        user = FirebaseAuth.getInstance().getCurrentUser();
        fromuser.setText("From: " + user.getEmail());
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("File Uploading");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(uploadTask != null){
                    uploadTask.cancel();
                }

            }
        });
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(BuildConfig.CONTRIBUTE_ADD_UNIT);
        interstitialAd.loadAd(new AdRequest.Builder().build());

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.general_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                if (interstitialAd.isLoaded()) {
                    //interstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
                break;
            case R.id.action_info:
                new AlertDialog.Builder(this)
                        .setTitle("Information")
                        .setMessage(R.string.contribute_info)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
