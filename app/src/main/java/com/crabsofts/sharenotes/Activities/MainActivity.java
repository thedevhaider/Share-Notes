package com.crabsofts.sharenotes.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.crabsofts.sharenotes.Extras.NotificationRecievedHandler;
import com.crabsofts.sharenotes.R;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;
import com.thebrownarrow.customfont.CustomFontTextView;

public class MainActivity extends AppCompatActivity {
    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    CustomFontTextView logo;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = this.getSharedPreferences("com.crabsofts.sharenotes", Context.MODE_PRIVATE);
        OneSignal.startInit(getApplicationContext())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .setNotificationReceivedHandler(new NotificationRecievedHandler())
                .init();
        if(!sharedPreferences.getString("DOUBT_NOTIFICATION", "").equals("no")){
            OneSignal.sendTag("DOUBT_POST", "doubt");
        }
        if(!sharedPreferences.getString("addnotify", "").equals("no")){
            OneSignal.sendTag("GENERAL_NOTI", "yes");
        }
        setContentView(R.layout.activity_main);
        logo = (CustomFontTextView) findViewById(R.id.logoView);
        logo.setTranslationY(-200f);

        logo.animate().translationY(200).setDuration(2000).start();
        try {
            new CountDownTimer(3000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();

                }
            }.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
