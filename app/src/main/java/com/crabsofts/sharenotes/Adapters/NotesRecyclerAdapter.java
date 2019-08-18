package com.crabsofts.sharenotes.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crabsofts.sharenotes.Activities.ViewNotesActivity;
import com.crabsofts.sharenotes.Models.NotesDetailsModel;
import com.crabsofts.sharenotes.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by CodeGod on 5/28/2017.
 */

public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.RecyclerViewHolder> {
    ArrayList<NotesDetailsModel> arrayList = new ArrayList<NotesDetailsModel>();
    Context context;
    public NotesRecyclerAdapter(ArrayList<NotesDetailsModel> arrayList, Context cntxt){

        this.arrayList = arrayList;
        this.context = cntxt;

    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_row_item, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view, arrayList, context);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        NotesDetailsModel notesDetailsModel = arrayList.get(position);
        holder.unitname.setText(notesDetailsModel.getUnitname());
        holder.subjectname.setText(notesDetailsModel.getSubjectname());
        holder.pages.setText("Page "+notesDetailsModel.getPages());
        //holder.colorstrip.setBackgroundResource(notesDetailsModel.getColorStrip());
        holder.colorstrip.setBackgroundResource(R.color.colorPrimary);
        //holder.unitname.setTextColor(ContextCompat.getColor(context, notesDetailsModel.getColorStrip()));
        holder.unitname.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        //holder.colorstrip.setBackgroundResource(R.color.DoubtcolorAccent);
        //holder.unitname.setTextColor(ContextCompat.getColor(context, R.color.DoubtcolorAccent));
    }

    @Override
    public int getItemCount() {

        return arrayList.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ArrayList<NotesDetailsModel> arrayList = new ArrayList<>();
        Context context;
        ImageView colorstrip;
        TextView unitname, subjectname, pages;
        Button viewNotesBtn;
        ImageView delete_unit;
        File myFile, directory;
        ProgressDialog progressDialog;
        public RecyclerViewHolder(View itemView, ArrayList<NotesDetailsModel> arrayList, Context context) {
            super(itemView);
            this.arrayList = arrayList;
            this.context = context;
            colorstrip = (ImageView) itemView.findViewById(R.id.colorstrip);
            unitname = (TextView) itemView.findViewById(R.id.unitname);
            subjectname = (TextView) itemView.findViewById(R.id.subjectname);
            pages = (TextView) itemView.findViewById(R.id.pages);
            colorstrip = (ImageView) itemView.findViewById(R.id.colorstrip);
            viewNotesBtn = (Button) itemView.findViewById(R.id.viewNotesButton);
            delete_unit = (ImageView) itemView.findViewById(R.id.delete_unit);
            delete_unit.setOnClickListener(this);
            viewNotesBtn.setOnClickListener(this);
            ContextWrapper cr = new ContextWrapper(context);
            directory = cr.getDir("Share Notes", Context.MODE_PRIVATE);
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, ViewNotesActivity.class);
            switch (v.getId()){
                case R.id.viewNotesButton:
                    i.putExtra("key", "notes");
                    i.putExtra("subjectname", arrayList.get(getAdapterPosition()).getSubjectname());
                    i.putExtra("unitname", arrayList.get(getAdapterPosition()).getUnitname());
                    context.startActivity(i);
                    break;
                case R.id.delete_unit:
                    new AlertDialog.Builder(context)
                            .setMessage("Do you really wanna delete this Unit?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressDialog = ProgressDialog.show(context, null, "Deleting...", true, true);
                                    myFile = new File(directory, arrayList.get(getAdapterPosition()).getUnitname());
                                    if(myFile.exists()){
                                        myFile.delete();
                                        progressDialog.dismiss();
                                        Toast.makeText(context, "Subject Unit deleted successfuly", Toast.LENGTH_SHORT).show();
                                    }else{
                                        progressDialog.dismiss();
                                        Toast.makeText(context, "Unit not exists", Toast.LENGTH_SHORT).show();
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
