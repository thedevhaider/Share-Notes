package com.crabsofts.sharenotes.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.crabsofts.sharenotes.Adapters.NotesRecyclerAdapter;
import com.crabsofts.sharenotes.BuildConfig;
import com.crabsofts.sharenotes.Models.NotesDetailsModel;
import com.crabsofts.sharenotes.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thebrownarrow.customfont.CustomFontTextView;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class NotesActivity extends AppCompatActivity {
    CustomFontTextView fontTextView;
    FirebaseDatabase mDatabase;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    NotesRecyclerAdapter adapter;
    ArrayList<NotesDetailsModel> arrayList = new ArrayList<>();
    ProgressBar progressBar;
    Random random;
    String subjectname;
    InterstitialAd interstitialAd;
    int colorStrip[] = {R.color.RedColorStrip, R.color.PinkColorStrip, R.color.PurpleColorStrip, R.color.DPurpleColorStrip, R.color.IndigoColorStrip
    , R.color.BlueColorStrip, R.color.CyanColorStrip, R.color.TealColorStrip, R.color.GreenColorStrip, R.color.GreenColorStrip, R.color.LimeColorStrip
    , R.color.YellowColorStrip, R.color.OrangeColorStrip, R.color.DOrangeColorStrip};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        fontTextView = (CustomFontTextView) findViewById(R.id.logoView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        random = new Random();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        int subjectId;
        Intent intent = getIntent();
        String toolbarTitle = intent.getStringExtra("titlename");
        subjectId = intent.getIntExtra("subjectid", 0);
        subjectname = intent.getStringExtra("subjectname");
        fontTextView.setText(toolbarTitle);
        mDatabase = FirebaseDatabase.getInstance();
        databaseReference = mDatabase.getReference("Units").child(subjectname);
        databaseReference.keepSynced(true);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(BuildConfig.NOTES_ADD_UNIT);
        interstitialAd.loadAd(new AdRequest.Builder().build());
        showNotesDetails();


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
    public void showNotesDetails(){
            databaseReference.orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                            String pages = map.get("page").toString();
                            String subjectname = map.get("subjectname").toString();
                            String unitname = map.get("unitname").toString();
                            Log.i("pages", pages);
                            Log.i("subjectname", subjectname);
                            Log.i("unitname", unitname);
                            NotesDetailsModel notesDetailsModel = new NotesDetailsModel(unitname, subjectname, pages, colorStrip[random.nextInt(colorStrip.length)]);
                            arrayList.add(notesDetailsModel);

                        }
                        if(arrayList.isEmpty()){
                            progressBar.setVisibility(View.GONE);
                            new AlertDialog.Builder(NotesActivity.this)
                                    .setTitle("Information")
                                    .setMessage(R.string.notes_not_available)
                                    .setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                                    .setCancelable(false)
                                    .show();
                        }else{
                            progressBar.setVisibility(View.GONE);
                            adapter = new NotesRecyclerAdapter(arrayList, NotesActivity.this);
                            recyclerView.setAdapter(adapter);
                        }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

            });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.general_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();

        if(item_id == android.R.id.home){
            finish();
            if (interstitialAd.isLoaded()) {
                //interstitialAd.show();
            } else {
                Log.d("TAG", "The interstitial wasn't loaded yet.");
            }
        }

        return true;
    }
}
