package com.walkandrock.touristguiderapp;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.facebook.AccessToken;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;


public class NotificationService extends IntentService {
    public NotificationService() {
        super("NotificationService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("New Event in " + "Jaupur");
        mBuilder.setContentText("Mujra");
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
        while (true) {
            try {
                URL obj = new URL(Constant.SERVER_URL + "/user/" + AccessToken.getCurrentAccessToken().getUserId() + "/notifications");
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                int responseCode = con.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                JSONObject notifyData = new JSONObject(readAll(in));
                if(!notifyData.getString("city_name").equals("")) {
                    mBuilder.setContentTitle("New Event in " + notifyData.getString("city_name"));
                    mBuilder.setContentText(notifyData.getString("title"));
                    mNotificationManager.notify(1, mBuilder.build());

                }
                Log.d("Login", responseCode + " is response code");
                Thread.sleep(10);
            } catch (Exception e) {
                Log.d("Login", e.toString() + " in City List");
            }
        }
    }

    private static String readAll(Reader rd) {
        StringBuilder sb = null;
        try {
            sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
        } catch (Exception e) {
        }
        return sb.toString();
    }
}

