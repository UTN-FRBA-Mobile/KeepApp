package com.utn.mobile.keepapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v7.app.NotificationCompat;

/**
 * Created by Fernando on 21/10/2017.
 */

public class GeofenceReceiver extends BroadcastReceiver {

    Intent broadcastIntent = new Intent();


    @Override
    public void onReceive(Context context, Intent intent) {
        notificar("Algo!!", context);
    }

    private void handleError(Intent intent){

    }


    private void handleEnterExit(Intent intent) {

    }

    public void notificar(String mensaje, Context context){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.keepapp_launcher)
                .setContentTitle("Saliste del gimnasio")
                .setContentText(mensaje);
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

        Notification notification = mBuilder.build();

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, notification);
    }

}