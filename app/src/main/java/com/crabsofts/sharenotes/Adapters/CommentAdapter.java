package com.crabsofts.sharenotes.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crabsofts.sharenotes.Models.CommentModel;
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

/**
 * Created by CodeGod on 07-07-2017.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.RecyclerViewHolder> {
    ArrayList<CommentModel> arrayList = new ArrayList<>();
    Context context;
    public CommentAdapter(ArrayList<CommentModel> arrayList, Context context){
        this.arrayList = arrayList;
        this.context = context;
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_row_item, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view, arrayList, context);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        CommentModel commentModel = arrayList.get(position);
        holder.comment_user_name.setText(commentModel.getComment_user_name());
        holder.comment_user_message.setText(commentModel.getComment_user_message());
        holder.comment_time.setText(commentModel.getComment_time());
        Glide.with(context).load(commentModel.getComment_profile_image()).into(holder.comment_profile_image);
    }

    @Override
    public int getItemCount() {

        return arrayList.size();
    }
    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView comment_profile_image;
        TextView comment_user_name, comment_user_message, comment_time;
        ArrayList<CommentModel> arrayList = new ArrayList<>();
        Context context;
        ImageView admin_power;
        DatabaseReference reference;
        FirebaseUser user;
        ProgressDialog progressDialog;
        public RecyclerViewHolder(View itemView, ArrayList<CommentModel> arrayList, Context context) {
            super(itemView);
            this.arrayList = arrayList;
            this.context = context;
            comment_profile_image = (ImageView) itemView.findViewById(R.id.comment_profile_image);
            comment_user_name = (TextView) itemView.findViewById(R.id.comment_user_name);
            comment_user_message = (TextView) itemView.findViewById(R.id.comment_user_message);
            comment_time = (TextView) itemView.findViewById(R.id.comment_time);
            admin_power = (ImageView) itemView.findViewById(R.id.admin_power);
            admin_power.setOnClickListener(this);
            user = FirebaseAuth.getInstance().getCurrentUser();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.admin_power:
                    deleteComment();
                    break;
            }
        }
        public void deleteComment(){
            if(isInternetAvailable()){
                progressDialog = ProgressDialog.show(context, null, "Loading...", true, true);
                reference = FirebaseDatabase.getInstance().getReference("Admins");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i("admin", dataSnapshot.getValue().toString());
                        progressDialog.dismiss();
                        if(dataSnapshot.hasChild(user.getUid()) || arrayList.get(getAdapterPosition()).getComment_owner().equals(user.getUid())){
                            new AlertDialog.Builder(context)
                                    .setTitle("Confirm")
                                    .setMessage("Do you really wanna remove this comment?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            progressDialog = ProgressDialog.show(context, null, "Removing...", true, true);
                                            reference = FirebaseDatabase.getInstance().getReference("Comments");
                                            final String comment_owner = arrayList.get(getAdapterPosition()).getComment_owner();
                                            final String comment_time = arrayList.get(getAdapterPosition()).getComment_time();
                                            final String comment_key = arrayList.get(getAdapterPosition()).getComment_key();
                                            final String user_message = arrayList.get(getAdapterPosition()).getComment_user_message();
                                            final String user_name = arrayList.get(getAdapterPosition()).getComment_user_name();
                                            final String post_key = arrayList.get(getAdapterPosition()).getPost_key();
                                            reference.child(arrayList.get(getAdapterPosition()).getPost_key()).child(arrayList.get(getAdapterPosition()).getComment_key()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(context, "Successfully deleted this comment.", Toast.LENGTH_SHORT).show();
                                                    reference = FirebaseDatabase.getInstance().getReference("Comment Delete Record").child(user.getUid()).child(post_key).child(comment_key);
                                                    Map<String, String> commentDetails = new HashMap<String, String>();
                                                    commentDetails.put("comment_owner", comment_owner);
                                                    commentDetails.put("comment_time", comment_time);
                                                    commentDetails.put("comment_key", comment_key);
                                                    commentDetails.put("user_message", user_message);
                                                    commentDetails.put("user_name", user_name);
                                                    commentDetails.put("post_key", post_key);
                                                    reference.setValue(commentDetails);
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
                                    .setCancelable(false)
                                    .show();

                        }else {
                            new AlertDialog.Builder(context)
                                    .setTitle("Confirmation")
                                    .setMessage("Do you really wanna report this user? Reporting the user without any mistake may put you in Trouble.")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            progressDialog = ProgressDialog.show(context, null, "Reporting...", true, true);
                                            reference = FirebaseDatabase.getInstance().getReference("User Reports").child("Comment Reports");
                                            Map<String, String> report_details = new HashMap<String, String>();
                                            report_details.put("post_key", arrayList.get(getAdapterPosition()).getPost_key());
                                            report_details.put("reported_by", user.getUid());
                                            report_details.put("comment", arrayList.get(getAdapterPosition()).getComment_user_message());
                                            report_details.put("comment_key", arrayList.get(getAdapterPosition()).getComment_key());
                                            report_details.put("reported_user", arrayList.get(getAdapterPosition()).getComment_owner());
                                            reference.child(report_details.get("reported_by")).child(report_details.get("reported_user")).child(report_details.get("comment_key")).setValue(report_details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(context, "Comment Successfully reported to Admin.", Toast.LENGTH_LONG).show();
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
    }
}
