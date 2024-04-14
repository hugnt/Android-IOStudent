package com.example.iostudent;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class MyNotificationListenerService extends NotificationListenerService {
    Context context;
    Ringtone r;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String myPackageName = context.getPackageName();
        String notificationPackageName = sbn.getPackageName();

        if (myPackageName.equals(notificationPackageName)) {
            // Đúng app của mình phát ra thông báo, thực hiện hành động
            SharedPreferences preferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
            String ringtoneUriString = preferences.getString("ringtone_uri", null);
            Uri ringtoneUri = Uri.parse(ringtoneUriString);
            r = RingtoneManager.getRingtone(context, ringtoneUri);
            r.play();
        }

    }
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
       r.stop();
    }

}