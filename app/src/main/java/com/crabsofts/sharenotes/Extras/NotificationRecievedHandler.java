package com.crabsofts.sharenotes.Extras;

import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

/**
 * Created by CodeGod on 21-07-2017.
 */

public class NotificationRecievedHandler implements OneSignal.NotificationReceivedHandler {
    @Override
    public void notificationReceived(OSNotification notification) {
        Log.i("imploc", "recieved");
    }
}
