package com.crabsofts.sharenotes.Services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.crabsofts.sharenotes.Activities.DoubtActivity;
import com.crabsofts.sharenotes.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by CodeGod on 15-07-2017.
 */

public class DoubtPostNotificationService extends Service {
    DatabaseReference databaseReference;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("location", "onbind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("loclocation", "oncreate");
        databaseReference = FirebaseDatabase.getInstance().getReference("Doubt");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("location", "onstart");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long[] vibrate = {0, 400, 0};
                Intent i = new Intent(DoubtPostNotificationService.this, DoubtActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(DoubtPostNotificationService.this, 0, i, PendingIntent.FLAG_ONE_SHOT);
                Notification notification = new NotificationCompat.Builder(DoubtPostNotificationService.this)
                        .setContentTitle("Doubt post")
                        .setContentText("Someone posted a question. Tap to check it out.")
                        .setSmallIcon(R.drawable.ic_action_phone_android)
                        .setContentIntent(pendingIntent)
                        .setVibrate(vibrate)
                        .setOngoing(false)
                        .build();
                //NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                //notificationManager.notify(0, notification);
                startForeground(1337, notification);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return super.onStartCommand(intent, flags, startId);
    }
}
