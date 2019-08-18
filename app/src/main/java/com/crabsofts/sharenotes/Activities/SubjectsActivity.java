package com.crabsofts.sharenotes.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.crabsofts.sharenotes.Adapters.SubjectRecyclerAdapter;
import com.crabsofts.sharenotes.BuildConfig;
import com.crabsofts.sharenotes.Models.SubjectDetailsModel;
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

public class SubjectsActivity extends AppCompatActivity {
    CustomFontTextView fontTextView;
    RecyclerView recyclerView;
    SubjectRecyclerAdapter recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<SubjectDetailsModel> arrayList = new ArrayList<>();
    Random random;
    DatabaseReference reference;
    ProgressDialog progressDialog;
    ProgressBar progressBar;
    InterstitialAd interstitialAd;
    int cardBackground[] = {R.color.RedColorStrip, R.color.PinkColorStrip, R.color.PurpleColorStrip, R.color.DPurpleColorStrip, R.color.IndigoColorStrip
            , R.color.BlueColorStrip, R.color.CyanColorStrip, R.color.TealColorStrip, R.color.GreenColorStrip, R.color.GreenColorStrip, R.color.LimeColorStrip
            , R.color.YellowColorStrip, R.color.OrangeColorStrip, R.color.DOrangeColorStrip};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);

        fontTextView = (CustomFontTextView) findViewById(R.id.logoView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(BuildConfig.SUBJECTS_ADD_UNIT);
        interstitialAd.loadAd(new AdRequest.Builder().build());

        random = new Random();
        Intent i = getIntent();
        switch (i.getStringExtra("key")){
            case "it":
                fontTextView.setText("IT Notes");
                fetchSubjectDetails("IT Notes");
                break;
            case "cse":
                fontTextView.setText("CSE Notes");
                fetchSubjectDetails("CSE Notes");
                break;
            case "mec":
                fontTextView.setText("MEC Notes");
                fetchSubjectDetails("MEC Notes");
                break;
            case "ece":
                fontTextView.setText("ECE Notes");
                fetchSubjectDetails("ECE Notes");
                break;
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

    public void fetchSubjectDetails(String trade){
        progressDialog = ProgressDialog.show(this, null, "Loading", false, true);
        progressBar.setVisibility(View.VISIBLE);
        reference = FirebaseDatabase.getInstance().getReference("Subjects").child(trade);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                progressBar.setVisibility(View.GONE);
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    Map<String, Object> subjectUnit = (Map<String, Object>) data.getValue();
                    String subject_name = subjectUnit.get("subject_name").toString();
                    String subject_id = subjectUnit.get("subject_id").toString();
                    String subject_owner = subjectUnit.get("subject_owner").toString();
                    Log.i("subject_name", subject_name);
                    Log.i("subject_id", subject_id);
                    Log.i("subject_owner", subject_owner);
                    SubjectDetailsModel subjectDetailsModel = new SubjectDetailsModel(cardBackground[random.nextInt(cardBackground.length)], subject_name, Integer.valueOf(subject_id), subject_owner);
                    arrayList.add(subjectDetailsModel);
                }

                recyclerAdapter = new SubjectRecyclerAdapter(arrayList, SubjectsActivity.this, fontTextView.getText().toString());
                recyclerView.setAdapter(recyclerAdapter);
                new AlertDialog.Builder(SubjectsActivity.this)
                        .setTitle("Contribute")
                        .setMessage(R.string.contribute_req)
                        .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setCancelable(true)
                        .show();
                if (arrayList.isEmpty()) {
                    new AlertDialog.Builder(SubjectsActivity.this)
                            .setMessage("Currently the notes are not Available. Please Contribute to this Application by Uploading your notes from the Contribute section of Share Notes.")
                            .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                progressDialog.dismiss();
                progressBar.setVisibility(View.GONE);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.subjects_menu, menu);
            MenuItem menuItem = menu.findItem(R.id.action_search);
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    newText = newText.toLowerCase();
                    ArrayList<SubjectDetailsModel> newList = new ArrayList<SubjectDetailsModel>();
                    for (SubjectDetailsModel model : arrayList) {
                        String name = model.getSubjectText().toLowerCase();
                        if (name.contains(newText)) {
                            newList.add(model);
                        }
                    }
                    recyclerAdapter.filter(newList);
                    return true;
                }
            });
        } catch (Exception e) {
            Log.i("searcherror", e.getStackTrace().toString());
        }

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
        switch (item_id){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_info:
                new AlertDialog.Builder(this)
                        .setTitle("Information")
                        .setMessage(R.string.subjects_info)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                break;
        }

        return true;
    }
}
