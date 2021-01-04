package com.example.chatapp.Notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

public class OreoNotification extends ContextWrapper {
    private static final String ChannelId="chatapp";
    private static final String ChannelName="ChatApp";
    private NotificationManager manager;

    public OreoNotification(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createChannel();
        }
    }
    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel(){
        NotificationChannel channel=new NotificationChannel(ChannelId,ChannelName, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(false);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel);
    }
    public NotificationManager getManager(){
        if(manager==null){
            manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }
    @TargetApi(Build.VERSION_CODES.O)
    public NotificationCompat.Builder getOreoNotification(String title, String body, PendingIntent pendingIntent, Uri uri
    ,String icon){
        NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext(),ChannelId);
           builder.setContentIntent(pendingIntent)
                   .setContentText(body)
                   .setContentTitle(title)
                   .setSmallIcon(Integer.parseInt(icon))
                   .setSound(uri)
                   .setAutoCancel(true);
           return builder;
    }
}
