package com.crabsofts.sharenotes.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.crabsofts.sharenotes.BuildConfig;
import com.crabsofts.sharenotes.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;
import com.thebrownarrow.customfont.CustomFontTextView;

public class SettingsActivity extends AppCompatActivity {
    CustomFontTextView fontTextView;
    TextView doubtposttext, doubtcommenttext, notesadditiontext;
    Switch doubtpostswitch, doubtcommentswitch, notesadditionswitch;
    int postTracker = 0, commentTracker = 0, notesTracker = 0;
    SharedPreferences sharedPreferences;
    DatabaseReference reference;
    FirebaseUser user;
    InterstitialAd interstitialAd;
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.doubtpostswitch:
                    if (!sharedPreferences.getString("DOUBT_NOTIFICATION", "").equals("no")) {
                        doubtposttext.setText(R.string.doubt_post_off);
                        OneSignal.deleteTag("DOUBT_POST");
                        sharedPreferences.edit().putString("DOUBT_NOTIFICATION", "no").apply();
                    } else {
                        doubtposttext.setText(R.string.doubt_post_on);
                        OneSignal.sendTag("DOUBT_POST", "doubt");
                        sharedPreferences.edit().putString("DOUBT_NOTIFICATION", "yes").apply();
                    }
                    break;
                case R.id.doubtcommentswitch:
                    if (!sharedPreferences.getString("COMMENT_NOTIFICATION", "").equals("no")) {
                        doubtcommenttext.setText(R.string.doubt_comment_off);
                        reference.child("comment_noti").setValue("no");
                        sharedPreferences.edit().putString("COMMENT_NOTIFICATION", "no").apply();
                    } else {
                        doubtcommenttext.setText(R.string.doubt_comment_on);
                        reference.child("comment_noti").setValue("yes");
                        sharedPreferences.edit().putString("COMMENT_NOTIFICATION", "yes").apply();
                    }
                    break;
                case R.id.notesadditionswitch:
                    if (!sharedPreferences.getString("addnotify", "").equals("no")) {
                        notesadditiontext.setText(R.string.notes_addition_off);
                        OneSignal.deleteTag("GENERAL_NOTI");
                        sharedPreferences.edit().putString("addnotify", "no").apply();
                    } else {
                        notesadditiontext.setText(R.string.notes_addition_on);
                        OneSignal.sendTag("GENERAL_NOTI", "yes");
                        sharedPreferences.edit().putString("addnotify", "yes").apply();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        fontTextView = (CustomFontTextView) findViewById(R.id.logoView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        fontTextView.setText("Settings");
        sharedPreferences = this.getSharedPreferences("com.crabsofts.sharenotes", Context.MODE_PRIVATE);
        doubtposttext = (TextView) findViewById(R.id.doubtposttext);
        doubtcommenttext = (TextView) findViewById(R.id.doubtcommenttext);
        notesadditiontext = (TextView) findViewById(R.id.notesadditiontext);
        doubtpostswitch = (Switch) findViewById(R.id.doubtpostswitch);
        doubtcommentswitch = (Switch) findViewById(R.id.doubtcommentswitch);
        notesadditionswitch = (Switch) findViewById(R.id.notesadditionswitch);
        doubtpostswitch.setOnClickListener(listener);
        doubtcommentswitch.setOnClickListener(listener);
        notesadditionswitch.setOnClickListener(listener);
        doubtpostswitch.setChecked(true);
        doubtcommentswitch.setChecked(true);
        switchSet();
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(BuildConfig.SETTINGS_ADD_UNIT);
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

    public void switchSet(){
        if(!sharedPreferences.getString("addnotify", "").equals("no")){
            notesadditiontext.setText(R.string.notes_addition_on);
            notesadditionswitch.setChecked(true);
        }else{
            notesadditiontext.setText(R.string.notes_addition_off);
            notesadditionswitch.setChecked(false);
        }

        if(!sharedPreferences.getString("DOUBT_NOTIFICATION", "").equals("no")){
            doubtposttext.setText(R.string.doubt_comment_on);
            doubtpostswitch.setChecked(true);
        }else{
            doubtposttext.setText(R.string.doubt_comment_off);
            doubtpostswitch.setChecked(false);
        }

        if(!sharedPreferences.getString("COMMENT_NOTIFICATION", "").equals("no")){
            doubtcommenttext.setText(R.string.doubt_comment_on);
            doubtcommentswitch.setChecked(true);
        }else{
            doubtcommenttext.setText(R.string.notes_addition_on);
            doubtcommentswitch.setChecked(false);
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
                        .setMessage(R.string.settings_info)
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
