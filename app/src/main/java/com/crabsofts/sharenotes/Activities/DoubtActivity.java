package com.crabsofts.sharenotes.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crabsofts.sharenotes.Adapters.DoubtRecyclerAdapter;
import com.crabsofts.sharenotes.BuildConfig;
import com.crabsofts.sharenotes.Models.DoubtDetailsModel;
import com.crabsofts.sharenotes.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thebrownarrow.customfont.CustomFontTextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class DoubtActivity extends AppCompatActivity {
    CustomFontTextView fontTextView;
    SwipeRefreshLayout swipeRefreshLayout;
    EditText post_title, post_question;
    LinearLayout doubt_post_layout;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    DoubtRecyclerAdapter adapter;
    ArrayList<DoubtDetailsModel> arrayList = new ArrayList<>();
    FloatingActionButton post_doubt;
    FirebaseUser user;
    DatabaseReference databaseReference;
    DatabaseReference commentOneSignal;
    String OneSignalId = "";
    Random random;
    String user_display_name = "", user_profile_url = "", user_uid = "";
    ProgressDialog progressDialog;
    ProgressBar loadmore;
    InterstitialAd interstitialAd;
    int arraySize = 0;
    int colorStrip[] = {R.color.RedColorStrip, R.color.PinkColorStrip, R.color.PurpleColorStrip, R.color.DPurpleColorStrip, R.color.IndigoColorStrip
            , R.color.BlueColorStrip, R.color.CyanColorStrip, R.color.TealColorStrip, R.color.GreenColorStrip, R.color.GreenColorStrip, R.color.LimeColorStrip
            , R.color.YellowColorStrip, R.color.OrangeColorStrip, R.color.DOrangeColorStrip};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doubt);
        fontTextView = (CustomFontTextView) findViewById(R.id.logoView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        fontTextView.setText("Doubt Section");
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        post_doubt = (FloatingActionButton) findViewById(R.id.post_boubt);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                showDoubtDetails();
            }
        });
        user = FirebaseAuth.getInstance().getCurrentUser();
        user_display_name = user.getDisplayName();
        user_profile_url = user.getPhotoUrl().toString();
        user_uid = user.getUid();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        loadmore = (ProgressBar) findViewById(R.id.loadmore);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        random = new Random();
        Log.i("uid", user_uid);
        Log.i("url", user_profile_url);
        databaseReference = FirebaseDatabase.getInstance().getReference("Doubt");
        databaseReference.keepSynced(true);
        swipeRefreshLayout.setRefreshing(true);
        post_doubt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.doubt_post_layout, null);
                post_title = (EditText) view.findViewById(R.id.title);
                post_question = (EditText) view.findViewById(R.id.question);
                doubt_post_layout = (LinearLayout) view.findViewById(R.id.doubt_post_layout);
                showPostDialog();
            }
        });
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i("loc", "in child added");
                showDoubtDetails();
                //startService(new Intent(DoubtActivity.this, DoubtPostNotificationService.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                showDoubtDetails();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(BuildConfig.DOUBT_ADD_UNIT);
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                interstitialAd.show();
            }
        });
    }
    public void showDoubtDetails(){
        databaseReference.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    arrayList.clear();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Map<String, Object> singleUnit = (Map<String, Object>) child.getValue();
                        String user_image = singleUnit.get("user_image").toString();
                        String user_name = singleUnit.get("user_name").toString();
                        String user_uid = singleUnit.get("user_uid").toString();
                        String post_time = singleUnit.get("post_time").toString();
                        String post_title = singleUnit.get("post_title").toString();
                        String post_content = singleUnit.get("post_content").toString();
                        String post_key = singleUnit.get("post_key").toString();
                            /*Log.i("uid", user_uid);
                            Log.i("photo", user_image);
                            Log.i("time", post_time);
                            Log.i("name", user_name);
                            Log.i("title", post_title);
                            Log.i("content", post_content);
                            Log.i("key", post_key);
                            */
                        DoubtDetailsModel doubtDetailsModel = new DoubtDetailsModel(colorStrip[random.nextInt(colorStrip.length)], user_image, user_name, post_time, post_title, post_content, post_key, user_uid);
                        arrayList.add(doubtDetailsModel);
                    }
                    arraySize = arrayList.size();
                    Collections.reverse(arrayList);
                    swipeRefreshLayout.setRefreshing(false);
                    adapter = new DoubtRecyclerAdapter(arrayList, DoubtActivity.this, getSupportFragmentManager());
                    recyclerView.setAdapter(adapter);
                } catch (Exception e) {
                    swipeRefreshLayout.setRefreshing(false);
                    new AlertDialog.Builder(DoubtActivity.this)
                            .setTitle("Error")
                            .setMessage("Something went wrong. Please try again later.")
                            .setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.doubt_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        View view = getLayoutInflater().inflate(R.layout.doubt_post_layout, null);
        post_title = (EditText) view.findViewById(R.id.title);
        post_question = (EditText) view.findViewById(R.id.question);
        doubt_post_layout = (LinearLayout) view.findViewById(R.id.doubt_post_layout);
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
            case R.id.action_post:
                showPostDialog();
                break;
            case R.id.action_notification:
                new AlertDialog.Builder(this)
                        .setTitle("Information")
                        .setMessage(R.string.mute_notification)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                break;
            case R.id.action_info:
                new AlertDialog.Builder(this)
                        .setTitle("Information")
                        .setMessage(R.string.doubt_info)
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
    public void showPostDialog(){
        new AlertDialog.Builder(this)
                .setView(doubt_post_layout)
                .setPositiveButton("Post", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(post_title.getText().toString().trim().replaceAll("\\s{2,}", " ").isEmpty() || post_question.getText().toString().trim().replaceAll("\\s{2,}", " ").isEmpty()){
                            new AlertDialog.Builder(DoubtActivity.this)
                                    .setMessage("Fields should have a proper body.")
                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .show();
                        }else{
                            postQuestion();
                            //OneSignal.sendTag("key", "doubt");
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false)
                .show();
    }
    public void postQuestion(){
        if(isInternetAvailable()){
            progressDialog = ProgressDialog.show(this, null, "Posting...", false, false);


            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, hh:mm aaa", Locale.US);
            String date = df.format(Calendar.getInstance().getTime());
            final Map<String, String> postDetails = new HashMap<>();
            postDetails.put("user_image", user_profile_url);
            postDetails.put("user_name", user_display_name);
            postDetails.put("user_uid", user_uid);
            postDetails.put("post_time", date);
            postDetails.put("post_title", post_title.getText().toString());
            postDetails.put("post_content", post_question.getText().toString());
            postDetails.put("post_key", databaseReference.push().getKey());
            databaseReference.child(postDetails.get("post_key")).setValue(postDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        commentOneSignal = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                        commentOneSignal.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                OneSignalId = dataSnapshot.child("OneSignal Id").getValue().toString();
                                Log.i("ownone", OneSignalId);
                                commentOneSignal = FirebaseDatabase.getInstance().getReference("Mutual").child(postDetails.get("post_key"));
                                commentOneSignal.child(OneSignalId).child("OneSignal Id").setValue(OneSignalId);


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                progressDialog.dismiss();
                                Toast.makeText(DoubtActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        sendNotificationToAll();
                        new AlertDialog.Builder(DoubtActivity.this)
                                .setTitle("Confirmation")
                                .setMessage("Your Question is succesfully posted.")
                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();
                        showDoubtDetails();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(DoubtActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(DoubtActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            });


        }else{
            progressDialog.dismiss();
            new AlertDialog.Builder(DoubtActivity.this)
                    .setMessage("Network Problem. Try again later.")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }

    }
    public void sendNotificationToAll(){
        Log.i("imploc", "before response");

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BuildConfig.SEND_NOTIFICATION_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("res", response.toString());
                Log.i("imploc", "response");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("message", user.getDisplayName()+" has posted a question in the doubt section. Tap to check it out");
                params.put("appid", BuildConfig.ONE_SIGNAL_APP_ID);
                params.put("restkey", BuildConfig.REST_API_KEY);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}