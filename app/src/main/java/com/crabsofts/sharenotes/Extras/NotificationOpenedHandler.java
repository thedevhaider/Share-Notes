package com.crabsofts.sharenotes.Extras;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.crabsofts.sharenotes.Activities.DoubtActivity;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

/**
 * Created by CodeGod on 17-07-2017.
 */

public class NotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
    Context context;
    public NotificationOpenedHandler(Context context){
        this.context = context;
    }
    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;
        String customKey;

        if (data != null) {
            customKey = data.optString("customkey", null);
            if (customKey != null)
                Log.i("OneSignalExample", "customkey set with value: " + customKey);
        }

        if (actionType == OSNotificationAction.ActionType.ActionTaken)
            Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

        Intent intent = new Intent(context, DoubtActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
