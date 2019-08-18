package com.crabsofts.sharenotes.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.crabsofts.sharenotes.Extras.BottomSheetDialog;
import com.crabsofts.sharenotes.Models.DoubtDetailsModel;
import com.crabsofts.sharenotes.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by CodeGod on 6/17/2017.
 */

public class DoubtRecyclerAdapter extends RecyclerView.Adapter<DoubtRecyclerAdapter.RecyclerViewHolder> {
    ArrayList<DoubtDetailsModel> arrayList = new ArrayList<>();
    Context context;
    FragmentManager fragmentManager;
    public DoubtRecyclerAdapter(ArrayList<DoubtDetailsModel> arrayList, Context context, FragmentManager fragmentManager){
        this.arrayList = arrayList;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doubt_row_item, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view, arrayList, context, fragmentManager);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        DoubtDetailsModel doubtDetailsModel = arrayList.get(position);
        //Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
        //holder.colorStrip.setBackgroundResource(R.color.DoubtcolorPrimary);
        //holder.post_title.setTextColor(ContextCompat.getColor(context, R.color.DoubtcolorPrimary));
        holder.colorStrip.setBackgroundResource(doubtDetailsModel.getColorStrip());
        holder.post_title.setTextColor(ContextCompat.getColor(context, doubtDetailsModel.getColorStrip()));
        holder.user_name.setText(doubtDetailsModel.getUser_name());
        holder.post_time.setText(doubtDetailsModel.getPost_time());
        holder.post_title.setText(doubtDetailsModel.getPost_title());
        holder.post_content.setText(doubtDetailsModel.getPost_content());
        Glide.with(context).load(doubtDetailsModel.getProfile_image()).into(holder.profile_image);
        /*holder.user_name.setTypeface(tf);
        holder.post_time.setTypeface(tf);
        holder.post_title.setTypeface(tf);
        holder.post_content.setTypeface(tf);
        holder.commentBtn.setTypeface(tf);
        */
    }
    @Override
    public int getItemCount() {

        return arrayList.size();
    }
    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ArrayList<DoubtDetailsModel> arrayList = new ArrayList<>();
        Context context;
        ImageView colorStrip, admin_power, expand_card;
        CardView doubtCard;
        CircleImageView profile_image;
        TextView user_name, post_time, post_title, post_content;
        LinearLayout commentBtn;
        DatabaseReference reference, commentRef, mutualRef;
        FirebaseUser user;
        int expand_tracker = 0;
        FragmentManager fragmentManager;
        BottomSheetBehavior bottomSheetBehavior;
        View itemView;
        ProgressDialog progressDialog;
        public RecyclerViewHolder(View itemView, ArrayList<DoubtDetailsModel> arrayList, Context context, FragmentManager fragmentManager) {
            super(itemView);
            this.arrayList = arrayList;
            this.context = context;
            this.itemView = itemView;
            this.fragmentManager = fragmentManager;
            colorStrip = (ImageView) itemView.findViewById(R.id.colorstrip);
            profile_image = (CircleImageView) itemView.findViewById(R.id.profile_image);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            post_time = (TextView) itemView.findViewById(R.id.post_time);
            post_title = (TextView) itemView.findViewById(R.id.post_title);
            post_content = (TextView) itemView.findViewById(R.id.post_content);
            admin_power = (ImageView) itemView.findViewById(R.id.admin_power);
            doubtCard = (CardView) itemView.findViewById(R.id.DoubtCard);
            commentBtn = (LinearLayout) itemView.findViewById(R.id.commentbtn);
            expand_card = (ImageView) itemView.findViewById(R.id.expand_card);
            user = FirebaseAuth.getInstance().getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference("Admins");
            commentRef = FirebaseDatabase.getInstance().getReference("Comments");
            mutualRef = FirebaseDatabase.getInstance().getReference("Mutual");
            admin_power.setOnClickListener(this);
            doubtCard.setOnClickListener(this);
            commentBtn.setOnClickListener(this);
            expand_card.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.DoubtCard:
                    break;
                case R.id.admin_power:
                    deletePost();
                    break;
                case R.id.commentbtn:
                    Bundle args = new Bundle();
                    args.putString("post_key", arrayList.get(getAdapterPosition()).getPost_key());
                    BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetDialog();
                    bottomSheetDialogFragment.setArguments(args);
                    bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                    break;
                case R.id.expand_card:
                    if(expand_tracker == 0){
                        expand_card.setImageResource(R.drawable.ic_action_keyboard_arrow_up);
                        post_content.setEllipsize(null);
                        post_content.setMaxLines(post_content.length());
                        expand_tracker = 1;
                    }else if(expand_tracker == 1){
                        expand_card.setImageResource(R.drawable.ic_action_keyboard_arrow_down);
                        post_content.setEllipsize(TextUtils.TruncateAt.END);
                        post_content.setMaxLines(2);
                        expand_tracker = 0;
                    }
                    break;
            }
        }
        public boolean isInternetAvailable(){
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
        public void deletePost(){
            if(isInternetAvailable()){
                progressDialog = ProgressDialog.show(context, null, "Loading...", true, true);
                reference = FirebaseDatabase.getInstance().getReference("Admins");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i("admin", dataSnapshot.getValue().toString());
                        if(dataSnapshot.hasChild(user.getUid()) || arrayList.get(getAdapterPosition()).getUser_uid().equals(user.getUid())){
                            progressDialog.dismiss();
                            new AlertDialog.Builder(context)
                                    .setTitle("Confirm")
                                    .setMessage("Do you really wanna remove this post?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            progressDialog = ProgressDialog.show(context, null, "Removing...", true, true);
                                            final String post_key = arrayList.get(getAdapterPosition()).getPost_key();
                                            final String post_time = arrayList.get(getAdapterPosition()).getPost_time();
                                            final String post_title = arrayList.get(getAdapterPosition()).getPost_title();
                                            final String post_content = arrayList.get(getAdapterPosition()).getPost_content();
                                            final String post_uid = arrayList.get(getAdapterPosition()).getUser_uid();
                                            final String post_user = arrayList.get(getAdapterPosition()).getUser_name();
                                            reference = FirebaseDatabase.getInstance().getReference("Doubt");
                                            reference.child(arrayList.get(getAdapterPosition()).getPost_key()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(context, "Successfully deleted this post.", Toast.LENGTH_SHORT).show();
                                                    reference = FirebaseDatabase.getInstance().getReference("Doubt Delete Record").child(user.getUid());
                                                    Map<String, String> deleteDetails = new HashMap<String, String>();
                                                    deleteDetails.put("post_key", post_key);
                                                    deleteDetails.put("post_time", post_time);
                                                    deleteDetails.put("post_title", post_title);
                                                    deleteDetails.put("post_content", post_content);
                                                    deleteDetails.put("post_uid", post_uid);
                                                    deleteDetails.put("post_user", post_user);
                                                    reference.child(post_key).setValue(deleteDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            reference = FirebaseDatabase.getInstance().getReference("Comments");
                                                            reference.child(post_key).removeValue();
                                                            mutualRef = FirebaseDatabase.getInstance().getReference("Mutual");
                                                            mutualRef.child(post_key).removeValue();
                                                        }
                                                    });

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(context, "Something went wrong.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .show();

                        }else {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(context)
                                    .setTitle("Confirmation")
                                    .setMessage("Do you really wanna report this user? Reporting the user without any mistake may put you in Trouble.")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            progressDialog = ProgressDialog.show(context, null, "Reporting...", true, true);
                                            reference = FirebaseDatabase.getInstance().getReference("User Reports").child("Doubt Reports");
                                            Map<String, String> report_details = new HashMap<String, String>();
                                            report_details.put("post_key", arrayList.get(getAdapterPosition()).getPost_key());
                                            report_details.put("reported_by", user.getUid());
                                            report_details.put("reported_user", arrayList.get(getAdapterPosition()).getUser_uid());
                                            report_details.put("reported_user_name", arrayList.get(getAdapterPosition()).getUser_name());
                                            report_details.put("post_title", arrayList.get(getAdapterPosition()).getPost_title());
                                            report_details.put("post_content", arrayList.get(getAdapterPosition()).getPost_content());
                                            report_details.put("post_time", arrayList.get(getAdapterPosition()).getPost_time());
                                            reference.child(report_details.get("reported_by")).child(report_details.get("reported_user")).child(report_details.get("post_key")).setValue(report_details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(context, "Post Successfully reported to Admin.", Toast.LENGTH_LONG).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(context, "Something went wrong.", Toast.LENGTH_LONG).show();
                                                }
                                            });


                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }else{
                Toast.makeText(context, "Network Problem", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
