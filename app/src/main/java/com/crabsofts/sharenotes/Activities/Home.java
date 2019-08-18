package com.crabsofts.sharenotes.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crabsofts.sharenotes.BuildConfig;
import com.crabsofts.sharenotes.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thebrownarrow.customfont.CustomFontTextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    CircleImageView profile_image;
    CustomFontTextView fontTextView;
    TextView userName, userEmail;
    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;
    InterstitialAd interstitialAd;
    public void selectDepartment(View view){

        CardView depCard = (CardView) view;
        Intent i = new Intent(Home.this, SubjectsActivity.class);
        switch (Integer.valueOf(depCard.getTag().toString())){
            case 1:
                i.putExtra("key", "it");
                startActivity(i);
                break;
            case 2:
                i.putExtra("key", "cse");
                startActivity(i);
                break;
            case 3:
                i.putExtra("key", "mec");
                startActivity(i);
                break;
            case 4:
                i.putExtra("key", "ece");
                startActivity(i);
                break;
            case 5:
                startActivity(new Intent(this, DoubtActivity.class));
                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent i = getIntent();
        View header=navigationView.getHeaderView(0);
        userName = (TextView)header.findViewById(R.id.userName);
        userEmail = (TextView)header.findViewById(R.id.userEmail);
        profile_image = (CircleImageView)header.findViewById(R.id.profile_image);
        fontTextView = (CustomFontTextView) findViewById(R.id.logoView);
        fontTextView.setText("Home");
        userEmail.setText(i.getStringExtra("email"));
        userName.setText(i.getStringExtra("name"));
        Glide.with(this).load(i.getStringExtra("photo")).into(profile_image);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("Block");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("block", dataSnapshot.getValue().toString());
                if(dataSnapshot.getValue().toString().equals("1")){
                    new AlertDialog.Builder(Home.this)
                            .setTitle("Blocked")
                            .setMessage("Your are temporarily blocked from using this app due to breaking the Rules.")
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(Home.this, LoginActivity.class));
                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        sharedPreferences = this.getSharedPreferences("com.crabsofts.sharenotes.Activities", Context.MODE_PRIVATE);
        if (!sharedPreferences.getString("start", "").equals("yes")) {
            new AlertDialog.Builder(this)
                    .setTitle("Welcome...")
                    .setMessage(R.string.welcome_message)
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setCancelable(true)
                    .show();
            sharedPreferences.edit().putString("start", "yes").apply();
        }
        MobileAds.initialize(this, BuildConfig.ADD_APP_ID);
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(BuildConfig.HOME_ADD_UNIT);
        interstitialAd.loadAd(new AdRequest.Builder().build());


    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            if (interstitialAd.isLoaded()) {
                interstitialAd.show();
            } else {
                Log.d("TAG", "The interstitial wasn't loaded yet.");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, R.string.share_info);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;
            case R.id.action_info:
                new AlertDialog.Builder(Home.this)
                        .setTitle("Information")
                        .setMessage(R.string.home_info)
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch(id){
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.action_contribute:
                startActivity(new Intent(this, ContributeActivity.class));
                break;
            case R.id.action_feedback:
                startActivity(new Intent(this, FeedbackActivity.class));
                break;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
