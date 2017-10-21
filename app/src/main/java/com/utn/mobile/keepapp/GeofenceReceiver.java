package com.utn.mobile.keepapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Fernando on 21/10/2017.
 */

public class GeofenceReceiver extends BroadcastReceiver {
    Context context;

    Intent broadcastIntent = new Intent();

    @Override
    public void onReceive(Context context, Intent intent) {
        int a = 0;
    }

    private void handleError(Intent intent){

    }


    private void handleEnterExit(Intent intent) {

    }

    /**
     * Posts a notification in the notification bar when a transition is
     * detected. If the user clicks the notification, control goes to the main
     * Activity.
     *
     * @param transitionType
     *            The type of transition that occurred.
     *
     */
    private void sendNotification(String transitionType, String locationName) {

    }
}
