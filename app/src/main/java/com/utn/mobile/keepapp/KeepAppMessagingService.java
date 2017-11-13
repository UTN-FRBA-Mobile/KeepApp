package com.utn.mobile.keepapp;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;


public class KeepAppMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(remoteMessage.getData().size() > 0){
            //String body = remoteMessage.getNotification().getBody();
            Log.d("KeepAppMessagingService", "Notificación recibida");
            Map<String, String> payload = remoteMessage.getData();

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.mipmap.keepapp_launcher_round);
            builder.setContentTitle("Mensaje de "+payload.get("userFrom"));
            builder.setContentText(payload.get("message"));

            Intent intent = new Intent(this, MainMenuActivity.class);
            intent.putExtra("notification_userfrom", payload.get("userFrom"));
            intent.putExtra("notification_message", payload.get("message"));
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
            taskStackBuilder.addNextIntent(intent);
            PendingIntent resultPendingIntent = taskStackBuilder.getPendingIntent(0,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0,builder.build());
        }
    }
}
