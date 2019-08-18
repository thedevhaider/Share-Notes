package com.crabsofts.sharenotes.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
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
import android.widget.RelativeLayout;
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

public class AboutActivity extends AppCompatActivity {
    CustomFontTextView fontTextView;
    TextView haideremail, mustaqemail, lpemail, elemail, privacytext, contacttext;
    TextView fromUser, ToUser;
    EditText subject, message;
    FirebaseUser user;
    RelativeLayout relativeLayout;
    InterstitialAd interstitialAd;
    String reciever = "", subjectHistory = "", messageHistory = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        fontTextView = (CustomFontTextView) findViewById(R.id.logoView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        fontTextView.setText("About Us");
        haideremail = (TextView) findViewById(R.id.haideremail);
        mustaqemail = (TextView) findViewById(R.id.mustaqemail);
        contacttext = (TextView) findViewById(R.id.contacttext);
        privacytext = (TextView) findViewById(R.id.privacytext);
        lpemail = (TextView) findViewById(R.id.lpemail);
        elemail = (TextView) findViewById(R.id.elemail);
        user = FirebaseAuth.getInstance().getCurrentUser();
        haideremail.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        mustaqemail.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        lpemail.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        elemail.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(BuildConfig.ABOUT_ADD_UNIT);
        interstitialAd.loadAd(new AdRequest.Builder().build());


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
                        .setMessage(R.string.about_info)
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
    public void composeMail(View view){
        switch (view.getTag().toString()){
            case "1":
                reciever = haideremail.getText().toString();
                showComposeAlert();
                break;
            case "2":
                reciever = mustaqemail.getText().toString();
                showComposeAlert();
                break;
            case "3":
                reciever = lpemail.getText().toString();
                showComposeAlert();
                break;
            case "5":
                reciever = elemail.getText().toString();
                showComposeAlert();
                break;
            case "4":
                reciever = "contact.crabsofts@gmail.com";
                showComposeAlert();
                break;
        }
    }
    public void showComposeAlert(){
        View view = getLayoutInflater().inflate(R.layout.compose_email_alert, null);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.composemail);
        fromUser = (TextView) view.findViewById(R.id.fromuser);
        ToUser = (TextView) view.findViewById(R.id.touser);
        subject = (EditText) view.findViewById(R.id.subjectEditText);
        message = (EditText) view.findViewById(R.id.messageEditText);
        subject.setText(subjectHistory);
        message.setText(messageHistory);
        fromUser.setText("From : "+user.getEmail());
        ToUser.setText("To : "+reciever);
        showAlert();
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
    public void showAlert(){
        new AlertDialog.Builder(this)
                .setView(relativeLayout)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(subject.getText().toString().trim().replaceAll("\\s{2,}", " ").isEmpty() || message.getText().toString().trim().replaceAll("\\s{2,}", " ").isEmpty()){
                            new AlertDialog.Builder(AboutActivity.this)
                                    .setMessage("Fill the fields properly.")
                                    .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            subjectHistory  = subject.getText().toString();
                                            messageHistory = message.getText().toString();
                                            showComposeAlert();
                                        }
                                    })
                                    .setCancelable(false)
                                    .show();

                        }else{
                            subjectHistory = "";
                            messageHistory = "";
                            sendMail();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        subjectHistory = "";
                        messageHistory = "";
                    }
                })
                .setCancelable(false)
                .show();
    }
    public void sendMail(){
        if(isInternetAvailable()){
            final RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, BuildConfig.SEND_MAIL_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    new AlertDialog.Builder(AboutActivity.this)
                            .setTitle("Confirmation")
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
                    new AlertDialog.Builder(AboutActivity.this)
                            .setTitle("Error")
                            .setMessage("An Error Occured while sending your mail. Try Again in few minutes.")
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
                    params.put("from", user.getEmail());
                    params.put("to", reciever);
                    params.put("subject", subject.getText().toString().trim().replaceAll("\\s{2,}", " "));
                    params.put("message", message.getText().toString().trim().replaceAll("\\s{2,}", " "));
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }else{
            Toast.makeText(this, "Network Problem", Toast.LENGTH_SHORT).show();
        }

    }
    public void openInWebView(View view){
        Intent intent = new Intent(AboutActivity.this, PrivacyPolicyActivity.class);
        startActivity(intent);

    }
}
