package com.crabsofts.sharenotes.Extras;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.crabsofts.sharenotes.Adapters.CommentAdapter;
import com.crabsofts.sharenotes.Models.CommentModel;
import com.crabsofts.sharenotes.R;
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
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by CodeGod on 04-07-2017.
 */

public class BottomSheetDialog extends BottomSheetDialogFragment {
    float slide;
    FloatingActionButton fb;
    ImageButton backBtn;
    EditText comment_box;
    ProgressBar comment_progress, comment_loadmore;
    FirebaseUser user;
    DatabaseReference reference, userReference, downloadUsersRef;
    ArrayList<CommentModel> arrayList = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    CommentAdapter adapter;
    View root;
    LinearLayout no_comments;
    String user_display_name = "", user_profile_url = "", user_uid = "";
    DatabaseReference commentListRef;
    ChildEventListener childlistener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Log.i("loc", "in child added");
            showCommentDetails();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

            showCommentDetails();
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            Log.i("offset", String.format("%f", slide));
            if(slide <= -0.801075){
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            slide = slideOffset;
        }
    };
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.back_button:
                    dismiss();
                    break;
                case R.id.sendBtn:
                    if(comment_box.getText().toString().trim().replaceAll("\\s{2,}", " ").isEmpty()){
                        new AlertDialog.Builder(getContext())
                                .setMessage("A comment should have a proper body.")
                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();
                    }else{
                        if(isInternetAvailable()){
                            writeComment();
                        }else{
                            Toast.makeText(getContext(), "Connection Problem", Toast.LENGTH_SHORT).show();
                        }

                    }
                    break;
            }
        }
    };
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (v.getId() ==R.id.comment) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction()&MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
            }
            return false;
        }
    };

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        root = View.inflate(getContext(), R.layout.comment_sheet_layout, null);
        dialog.setContentView(root);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) root.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
        View parent = (View) root.getParent();
        parent.setFitsSystemWindows(true);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(parent);
        root.measure(0, 0);
        //Total Window height
        Rect rectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        //Window height except title bar
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels - rectangle.top;
        bottomSheetBehavior.setPeekHeight(screenHeight);
        if (params.getBehavior() instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) params.getBehavior()).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
        params.height = screenHeight;
        parent.setLayoutParams(params);
        fb = (FloatingActionButton) root.findViewById(R.id.sendBtn);
        backBtn = (ImageButton) root.findViewById(R.id.back_button);
        comment_box = (EditText) root.findViewById(R.id.comment);
        comment_progress = (ProgressBar) root.findViewById(R.id.progressloadcommemts);
        comment_loadmore = (ProgressBar) root.findViewById(R.id.progressloadmore);
        no_comments = (LinearLayout) root.findViewById(R.id.no_comments);
        comment_progress.bringToFront();
        comment_loadmore.bringToFront();
        comment_box.setOnTouchListener(touchListener);
        backBtn.setOnClickListener(clickListener);
        fb.setOnClickListener(clickListener);
        user = FirebaseAuth.getInstance().getCurrentUser();
        user_display_name = user.getDisplayName();
        user_profile_url = user.getPhotoUrl().toString();
        user_uid = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Comments").child(getArguments().getString("post_key"));
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        reference.addChildEventListener(childlistener);
        if (arrayList.isEmpty()) {
            no_comments.setVisibility(View.VISIBLE);
        }
    }

    public void writeComment(){
        comment_progress.setVisibility(View.VISIBLE);
        InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = root.findFocus();
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, hh:mm aaa", Locale.US);
        String date = df.format(Calendar.getInstance().getTime());
        final Map<String, String> postDetails = new HashMap<>();
        postDetails.put("user_image", user_profile_url);
        postDetails.put("user_name", user_display_name);
        postDetails.put("user_uid", user_uid);
        postDetails.put("comment_time", date);
        postDetails.put("comment_message", comment_box.getText().toString());
        postDetails.put("comment_key", reference.push().getKey());
        postDetails.put("post_key", getArguments().getString("post_key"));
        reference.child(postDetails.get("comment_key")).setValue(postDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                comment_progress.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    comment_box.setText("");
                    Toast.makeText(getContext(), "Comment Posted.", Toast.LENGTH_SHORT).show();
                    showCommentDetails();
                    userReference = FirebaseDatabase.getInstance().getReference("Doubt");
                    userReference.child(postDetails.get("post_key")).child("user_uid").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            userReference = FirebaseDatabase.getInstance().getReference("Users");
                            Log.i("commentownuid", dataSnapshot.getValue().toString());
                            userReference.child(dataSnapshot.getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String onesignalId = dataSnapshot.child("OneSignal Id").getValue().toString();
                                    final String comment_noti = dataSnapshot.child("comment_noti").getValue().toString();
                                    Log.i("comownpla", onesignalId);
                                    Log.i("noti", comment_noti);
                                    final StringBuilder playerList = new StringBuilder();
                                    downloadUsersRef = FirebaseDatabase.getInstance().getReference("Mutual").child(getArguments().getString("post_key"));
                                    downloadUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Log.i("snapshot", dataSnapshot.toString());
                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                Map<String, Object> single = (Map<String, Object>) child.getValue();
                                                Log.i("single", single.get("OneSignal Id").toString());
                                                playerList.append(single.get("OneSignal Id").toString() + ",");
                                            }
                                            playerList.deleteCharAt(playerList.length() - 1);
                                            Log.i("list", playerList.toString());
                                            if (comment_noti.equals("yes")) {
                                                String strJsonBody = "{"
                                                        + "\"app_id\": \"8d0e9144-92a7-4431-9893-dfbef41f063a\","
                                                        + "\"include_player_ids\": [" + playerList.toString() + "],"
                                                        + "\"data\": {\"foo\": \"bar\"},"
                                                        + "\"contents\": {\"en\": \"" + user_display_name + " commented on the post you are linked with.\"}"
                                                        + "}";
                                                Log.i("jsonbosy", strJsonBody);
                                                OneSignal.postNotification(strJsonBody, new OneSignal.PostNotificationResponseHandler() {
                                                    @Override
                                                    public void onSuccess(JSONObject response) {

                                                    }

                                                    @Override
                                                    public void onFailure(JSONObject response) {

                                                    }
                                                });
                                            }
                                            userReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    String curOneSignalid = dataSnapshot.child("OneSignal Id").getValue().toString();
                                                    Log.i("curuserone", curOneSignalid);
                                                    commentListRef = FirebaseDatabase.getInstance().getReference("Mutual").child(postDetails.get("post_key"));
                                                    commentListRef.child(curOneSignalid).child("OneSignal Id").setValue(curOneSignalid);
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }else{
                    Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                comment_progress.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public boolean isInternetAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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
    public void showCommentDetails(){
        reference.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    if(dataSnapshot!=null){
                        arrayList.clear();
                        no_comments.setVisibility(View.GONE);
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Map<String, Object> singleUnit = (Map<String, Object>) child.getValue();
                            String user_image = singleUnit.get("user_image").toString();
                            String user_name = singleUnit.get("user_name").toString();
                            String user_uid = singleUnit.get("user_uid").toString();
                            String comment_time = singleUnit.get("comment_time").toString();
                            String comment_message = singleUnit.get("comment_message").toString();
                            String comment_key = singleUnit.get("comment_key").toString();
                            String post_key = singleUnit.get("post_key").toString();
                            Log.i("uid", user_uid);
                            Log.i("photo", user_image);
                            Log.i("time", comment_time);
                            Log.i("name", user_name);
                            Log.i("message", comment_message);
                            Log.i("commentkey", comment_key);
                            Log.i("post_key", post_key);
                            CommentModel commentModel = new CommentModel(user_name, user_image, comment_message, comment_time, comment_key, post_key, user_uid);
                            arrayList.add(commentModel);
                        }
                        comment_progress.setVisibility(View.GONE);
                        if(arrayList.isEmpty()){
                            no_comments.setVisibility(View.VISIBLE);
                        }
                        adapter = new CommentAdapter(arrayList, getContext());
                        recyclerView.setAdapter(adapter);
                        recyclerView.scrollToPosition(arrayList.size()-1);
                    }


                }catch(Exception e){
                    comment_progress.setVisibility(View.GONE);
                    new AlertDialog.Builder(getContext())
                            .setMessage("Something went wrong. Please try again later.")
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
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
}