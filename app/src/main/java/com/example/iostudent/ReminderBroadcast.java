package com.example.iostudent;

import static android.content.Context.ALARM_SERVICE;
import static android.media.AudioAttributes.USAGE_NOTIFICATION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import static androidx.core.content.ContextCompat.getSystemService;

import java.time.LocalDateTime;
import java.util.Calendar;

public class ReminderBroadcast extends BroadcastReceiver {

    public String CHANNEL_ID = "IOSTUDENT_CHANNEL_ID";
    public String CONTENT_TITLE = "IOStudent reminds you";
    public String CONTENT_TEXT = "It's time to review what you should do today!";
    public Class<?> RETURN_ACTIVITY = MainActivity.class;
    public String DATA_KEY_SEND = "DATA_KEY_SEND";
    public String DATA_VALUE_SEND = "Welcome back!";
    public int REQUEST_CODE = 132;

    private long TIME_APPEAR = System.currentTimeMillis() + 1000 * 10;

    public void SetReturnActivity(Class<?> returnActivity, String dataKey, String dataValue , int requestCode){
        RETURN_ACTIVITY = returnActivity;
        DATA_KEY_SEND = dataKey;
        DATA_VALUE_SEND = dataValue;
        REQUEST_CODE = requestCode;
    }
    public void Initialize(String CHANNEL_ID, String contentTitle, String contentText) {
        this.CHANNEL_ID = CHANNEL_ID;
        this.CONTENT_TITLE = contentTitle;
        this.CONTENT_TEXT = contentText;
    }

    public void SetTimeDisplay(@NonNull LocalDateTime dateTime){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, dateTime.getYear());
        calendar.set(Calendar.MONTH, dateTime.getMonthValue() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, dateTime.getDayOfMonth());
        calendar.set(Calendar.HOUR_OF_DAY, dateTime.getHour());
        calendar.set(Calendar.MINUTE, dateTime.getMinute());
        calendar.set(Calendar.SECOND, dateTime.getSecond());
        this.TIME_APPEAR = calendar.getTimeInMillis();
    }

    public void createReminderBroadcast(Context context){
        SharedPreferences preferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String ringtoneUriString = preferences.getString("ringtone_uri", null);
        Toast.makeText(context, ringtoneUriString, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(context, ReminderBroadcast.class);
        intent.putExtra("CHANNEL_ID", CHANNEL_ID);
        intent.putExtra("CONTENT_TITLE", CONTENT_TITLE);
        intent.putExtra("CONTENT_TEXT", CONTENT_TEXT);
        intent.putExtra("RETURN_ACTIVITY", RETURN_ACTIVITY);
        intent.putExtra("DATA_KEY_SEND", DATA_KEY_SEND);
        intent.putExtra("DATA_VALUE_SEND", DATA_VALUE_SEND);;
        intent.putExtra("REQUEST_CODE", REQUEST_CODE);
        intent.putExtra("RINGTONE_URI", ringtoneUriString);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP,
                TIME_APPEAR,
                pendingIntent);
    }



    @Override
    public void onReceive(Context context, @NonNull Intent intent) {
        String chanelId = intent.getStringExtra("CHANNEL_ID");
        Uri ringtoneUri = Uri.parse(intent.getStringExtra("RINGTONE_URI"));
        // Bắt đầu Service
        Intent serviceIntent = new Intent(context, MyNotificationListenerService.class);

        context.startService(serviceIntent);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, chanelId)
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setContentTitle(intent.getStringExtra("CONTENT_TITLE"))
                .setContentText(intent.getStringExtra("CONTENT_TEXT"))
                .setSound(ringtoneUri)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        //Intent when clicking to notification
        PendingIntent pendingIntent =
                setNotificationAction(
                        context,
                        (Class<?>)intent.getSerializableExtra("RETURN_ACTIVITY"),
                        intent.getStringExtra("DATA_KEY_SEND"),
                        intent.getStringExtra("DATA_VALUE_SEND"),
                        intent.getIntExtra("REQUEST_CODE",999));


        //Ringtone r = RingtoneManager.getRingtone(context, ringtoneUri);
        //r.play();

        builder.setContentIntent(pendingIntent);

        //Create channel
        createNotificationChannel(ringtoneUri,builder, context, chanelId);

    }

    private PendingIntent setNotificationAction(Context context, Class<?> returnActivity, String dataKey, String dataValue,int requestCode){
        Intent intent = new Intent(context, returnActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(dataKey, dataValue);

        return PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    private void createNotificationChannel(Uri ringtoneUri, NotificationCompat.Builder builder, @NonNull Context context, String chanelId){
        NotificationManager notificationManager =(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(chanelId);
            if(notificationChannel == null){
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(chanelId, "Some description here", importance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);

                notificationManager.createNotificationChannel(notificationChannel);
            }
//            Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ context.getPackageName() + "/" + R.raw.test1);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            notificationChannel.setSound(ringtoneUri,null);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(0, builder.build());
    }


}
