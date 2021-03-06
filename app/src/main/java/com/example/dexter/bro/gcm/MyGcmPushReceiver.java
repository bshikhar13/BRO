package com.example.dexter.bro.gcm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.dexter.bro.app.Config;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by Dexter on 5/28/2016.
 */
public class MyGcmPushReceiver extends GcmListenerService {
    private static final String TAG = MyGcmPushReceiver.class.getSimpleName();

    @Override
    public void onMessageReceived(String from, Bundle bundle) {
        String title = bundle.getString("title");
        String message = bundle.getString("message");
        String image = bundle.getString("image");
        String timestamp = bundle.getString("created_at");
        Log.e(TAG, "From: " + from);
        Log.e(TAG, "Title: " + title);
        Log.e(TAG, "message: " + message);
        Log.e(TAG, "image: " + image);
        Log.e(TAG, "timestamp: " + timestamp);

        processUserMessage(message);
    }

    private void processUserMessage(String message){
        Intent pushNotification = new Intent(Config.MESSAGE_RECEIVED);
        pushNotification.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

    }
}
