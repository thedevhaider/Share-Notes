package com.crabsofts.sharenotes.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.crabsofts.sharenotes.BuildConfig;
import com.crabsofts.sharenotes.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class ViewNotesActivity extends AppCompatActivity {
    PDFView pdfView;
    File path, myFile;
    ProgressDialog progressDialog;
    StorageReference reference;
    String unitName = "", subjectname = "";
    SharedPreferences sharedPreference;
    FileDownloadTask downloadTask;
    boolean isCanceled = false;
    InterstitialAd interstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view_notes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.bringToFront();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(BuildConfig.VIEW_ADD_UNIT);
        interstitialAd.loadAd(new AdRequest.Builder().build());
        sharedPreference = this.getSharedPreferences("com.crabsofts.sharenotes", Context.MODE_PRIVATE);
        Intent i = getIntent();
        subjectname = i.getStringExtra("subjectname");
        if(i.getStringExtra("key").equals("notes")){
            unitName = i.getStringExtra("unitname");
        }else{
            unitName = subjectname;
            Log.i("unitname", unitName);
        }

        pdfView = (PDFView) findViewById(R.id.pdfView);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(downloadTask !=null){
                    downloadTask.cancel();
                    isCanceled = true;
                    Toast.makeText(ViewNotesActivity.this, "Download Failed", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    finish();
                }

            }
        });
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                interstitialAd.show();
            }
        });
        ContextWrapper cr = new ContextWrapper(this);
        File directory = cr.getDir("Share Notes", Context.MODE_PRIVATE);
        myFile = new File(directory, unitName);
        if(myFile.exists()){
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            showNotes(myFile);
        }else{
            progressDialog.setMessage("Fetching Notes...");
            progressDialog.show();
            downloadAndShow();
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

    @SuppressWarnings("VisibleForTests")
    public void downloadAndShow(){
        try {

            if(isInternetAvailable()){
                Log.i("url1", "Pdf Notes/"+subjectname+"/"+unitName+".pdf");
                reference = FirebaseStorage.getInstance().getReference("Pdf Notes/"+subjectname+"/"+unitName+".pdf");
                downloadTask = reference.getFile(myFile);
                downloadTask.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        showNotes(myFile);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("faltuerror", "in failure");
                        progressDialog.dismiss();
                        if (!isCanceled) {
                            Toast.makeText(ViewNotesActivity.this, "Notes of this unit are not available at this moment. Please try again later.", Toast.LENGTH_LONG).show();
                            finish();
                        }


                    }
                }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage("Downloaded: "+(int) progress+"%");
                    }
                });
            }else {
                progressDialog.dismiss();
                new AlertDialog.Builder(ViewNotesActivity.this)
                        .setMessage("Internet not available.")
                        .setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog = ProgressDialog.show(ViewNotesActivity.this, "Processing", "Fetching Notes...", false, false);
                                downloadAndShow();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void showNotes(File notesFile){
        Log.i("loc", "in shownotes");
        pdfView.fromFile(notesFile)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .enableAnnotationRendering(true)
                .enableAntialiasing(true)
                .spacing(0)
                .scrollHandle(new DefaultScrollHandle(this))
                .onRender(new OnRenderListener() {
                    @Override
                    public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
                        pdfView.fitToWidth();
                        //pdfView.jumpTo(Integer.parseInt(sharedPreference.getString(unitName, "0")));
                    }
                })
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        progressDialog.dismiss();;
                    }
                })
                .onError(new OnErrorListener() {
                    @Override
                    public void onError(Throwable t) {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(ViewNotesActivity.this)
                                .setTitle("Error")
                                .setMessage(t.getMessage() + ". Try deleting the old curupted notes.")
                                .setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    }
                })
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        //sharedPreference.edit().putString(unitName, String.valueOf(page)).apply();
                        //Toast.makeText(ViewNotesActivity.this, "Page "+page+"/"+pageCount, Toast.LENGTH_SHORT).show();
                    }
                })
                .load();
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
