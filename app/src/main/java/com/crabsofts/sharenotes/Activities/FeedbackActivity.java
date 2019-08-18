package com.crabsofts.sharenotes.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crabsofts.sharenotes.BuildConfig;
import com.crabsofts.sharenotes.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.thebrownarrow.customfont.CustomFontTextView;

import java.util.HashMap;
import java.util.Map;

public class FeedbackActivity extends AppCompatActivity {
    CustomFontTextView fontTextView;
    TextView fromuser;
    EditText feedbackEditText;
    FirebaseUser user;
    InterstitialAd interstitialAd;
    public void sendMail(View view){
        if(feedbackEditText.getText().toString().trim().replaceAll("\\s{2,}", " ").isEmpty()){
            new AlertDialog.Builder(this)
                    .setMessage("Dont leave the Field Empty please")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }else{
            if(isInternetAvailable()){
                RequestQueue requestQueue = Volley.newRequestQueue(FeedbackActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, BuildConfig.SEND_FEEDBACK_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        new AlertDialog.Builder(FeedbackActivity.this)
                                .setMessage(response)
                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        new AlertDialog.Builder(FeedbackActivity.this)
                                .setMessage("An Error Occured while sending your feedback.")
                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("from", user.getEmail().trim().replaceAll("\\s{2,}", " "));
                        params.put("message", feedbackEditText.getText().toString().trim().replaceAll("\\s{2,}", " "));
                        return params;
                    }
                };
                requestQueue.add(stringRequest);

            }else{
                Toast.makeText(this, "Network Problem", Toast.LENGTH_SHORT).show();
            }



        }
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        fontTextView = (CustomFontTextView) findViewById(R.id.logoView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        fontTextView.setText("Feedback");

        fromuser = (TextView) findViewById(R.id.fromuser);
        feedbackEditText = (EditText) findViewById(R.id.feedbackEditText);
        user = FirebaseAuth.getInstance().getCurrentUser();
        fromuser.setText("From: " + user.getEmail());

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(BuildConfig.FEEDBACK_ADD_UNIT);
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
                        .setMessage(R.string.feedback_info)
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
