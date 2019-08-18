package com.crabsofts.sharenotes.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crabsofts.sharenotes.Activities.NotesActivity;
import com.crabsofts.sharenotes.Activities.ViewNotesActivity;
import com.crabsofts.sharenotes.Models.SubjectDetailsModel;
import com.crabsofts.sharenotes.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by CodeGod on 5/13/2017.
 */

public class SubjectRecyclerAdapter extends RecyclerView.Adapter<SubjectRecyclerAdapter.RecyclerViewHolder>{
    ArrayList<SubjectDetailsModel> arrayList = new ArrayList<SubjectDetailsModel>();
    ArrayList<SubjectDetailsModel> newList = new ArrayList<SubjectDetailsModel>();
    Context context;
    String subject_text = null;
    public SubjectRecyclerAdapter(ArrayList<SubjectDetailsModel> arrayList, Context cntxt, String subject_text){

        this.arrayList = arrayList;
        this.context = cntxt;
        this.subject_text = subject_text;

    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_row_item, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view, context, arrayList, subject_text);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        SubjectDetailsModel subjectDetailsModel = arrayList.get(position);
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
        //holder.subjectCard.setCardBackgroundColor(ContextCompat.getColor(context, subjectDetailsModel.getCardBackground()));
        holder.subjectCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        holder.subjectName.setText(subjectDetailsModel.getSubjectText());
        //holder.papersBtn.setTextColor(ContextCompat.getColor(context, subjectDetailsModel.getCardBackground()));
        holder.papersBtn.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        //holder.notesBtn.setTextColor(ContextCompat.getColor(context, subjectDetailsModel.getCardBackground()));
        holder.notesBtn.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        holder.subject_owner.setText(subjectDetailsModel.getSubject_owner());
        //holder.subjectName.setTypeface(tf);
    }

    @Override
    public int getItemCount() {

        return arrayList.size();
    }
    public void filter(ArrayList<SubjectDetailsModel> newList){
        arrayList = new ArrayList<>();
        arrayList = newList;
        notifyDataSetChanged();
    }


    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView subjectName, subject_owner;
        Button papersBtn, notesBtn;
        CardView subjectCard;
        ArrayList<SubjectDetailsModel> arrayList = new ArrayList<>();
        Context context;
        String subject_text = null;
        ImageView delete_book;
        File myFile, directory;
        ProgressDialog prgressDialog;
        public RecyclerViewHolder(View itemView, Context ctx, ArrayList<SubjectDetailsModel> arrayList, String subject_text) {
            super(itemView);
            this.context = ctx;
            this.arrayList = arrayList;
            this.subject_text = subject_text;
            subjectName = (TextView) itemView.findViewById(R.id.subjectname);
            subjectCard = (CardView) itemView.findViewById(R.id.SubjectCard);
            papersBtn = (Button) itemView.findViewById(R.id.papersbtn);
            notesBtn = (Button) itemView.findViewById(R.id.notesbtn);
            subject_owner = (TextView) itemView.findViewById(R.id.subject_owner);
            delete_book = (ImageView) itemView.findViewById(R.id.delete_book);
            papersBtn.setOnClickListener(this);
            notesBtn.setOnClickListener(this);
            delete_book.setOnClickListener(this);
            ContextWrapper cr = new ContextWrapper(context);
            directory = cr.getDir("Share Notes", Context.MODE_PRIVATE);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.notesbtn:
                    SubjectDetailsModel subjectDetailsModel = this.arrayList.get(getAdapterPosition());
                    Intent intent = new Intent(this.context, NotesActivity.class);
                    intent.putExtra("titlename", subject_text);
                    intent.putExtra("subjectname", subjectDetailsModel.getSubjectText());
                    intent.putExtra("subjectid", subjectDetailsModel.getId());
                    this.context.startActivity(intent);
                    break;
                case R.id.papersbtn:
                    SubjectDetailsModel subjectsModel = this.arrayList.get(getAdapterPosition());
                    Intent i = new Intent(this.context, ViewNotesActivity.class);
                    i.putExtra("key", "ebook");
                    i.putExtra("subjectname", subjectsModel.getSubjectText());
                    i.putExtra("subjectid", subjectsModel.getId());
                    this.context.startActivity(i);
                    break;
                case R.id.delete_book:
                    new AlertDialog.Builder(context)
                            .setMessage("Do you really wanna delete this E-Book?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    prgressDialog = ProgressDialog.show(context, null, "Deleting...", true, true);
                                    myFile = new File(directory, arrayList.get(getAdapterPosition()).getSubjectText());
                                    if(myFile.exists()){
                                        myFile.delete();
                                        prgressDialog.dismiss();
                                        Toast.makeText(context, "E-Book deleted successfuly", Toast.LENGTH_SHORT).show();
                                    }else{
                                        prgressDialog.dismiss();
                                        Toast.makeText(context, "E-Book not exists", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                    break;
            }

        }
    }
}
