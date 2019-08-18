package com.crabsofts.sharenotes.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.crabsofts.sharenotes.Activities.MainActivity;
import com.crabsofts.sharenotes.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by CodeGod on 6/9/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService{
    SharedPreferences sharedPreferences;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sharedPreferences = this.getSharedPreferences("com.crabsofts.sharenotes", Context.MODE_PRIVATE);
        if(!sharedPreferences.getString("addnotify", "").equals("no")){
            long[] vibrate = {100, 400, 100};
            Intent intent = new Intent(MyFirebaseMessagingService.this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(MyFirebaseMessagingService.this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
            Notification notification = new Notification.Builder(MyFirebaseMessagingService.this)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setSmallIcon(R.drawable.ic_action_phone_android)
                    .setVibrate(vibrate)
                    .setContentIntent(pendingIntent)
                    .build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, notification);
        }

        super.onMessageReceived(remoteMessage);
    }
}
