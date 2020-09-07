package com.example.ultramarket.firebase;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.ultramarket.R;
import com.example.ultramarket.ui.userUi.Activities.ProductActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    /**
     * holds receiving notification message
     * @param remoteMessage holds the message data
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //sends data to be retrieved
        Map<String, String> map = remoteMessage.getData();

        String product_id = map.get("product_id");
        String product_Image = map.get("imageUrl");

        loadImageUrlThenShowNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody()
                , product_id, product_Image);

    }

    /**
     * holds register token of device to be sent to
     * @param s holds the token of device
     */
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("newToken",s);
    }

    private void showNotification(String title, String body, String prodId, Bitmap imageProd){

        //create new notification manager to handle the channel
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String notifyChannelId = getString(R.string.notifiy_id);

        //checks android device SDK to see if suitable or not
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel notificationChannel = new NotificationChannel(notifyChannelId,"Notification"
                    ,NotificationManager.IMPORTANCE_HIGH);

            //sets channel name
            notificationChannel.setDescription("UltraMarket channel");
            notificationManager.createNotificationChannel(notificationChannel);

            //holds the page to go to on pressed (Receive page)
            Intent notificationIntent = new Intent(getApplicationContext(), ProductActivity.class);

            //store required data (type and id) and send them to receiver page
            Bundle bundle =new Bundle();
            bundle.putString("prod_id",prodId);
            notificationIntent.putExtras(bundle);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(notificationIntent);
            PendingIntent contentIntent = stackBuilder.getPendingIntent(0
                    , PendingIntent.FLAG_UPDATE_CURRENT);



            //sets notification properties to be shown
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,notifyChannelId);
            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_notification_small)
                    .setLargeIcon(imageProd)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentIntent(contentIntent)
                    .setContentInfo("Info");

            notificationManager.notify(new Random().nextInt(),notificationBuilder.build());

        }
    }
    // Load bitmap from image url on background thread and display image notification
    private void loadImageUrlThenShowNotification(String title, String body, String prodId, String imageUrl) {

        Glide.with(getApplicationContext())
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        showNotification(title, body, prodId, resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

}
