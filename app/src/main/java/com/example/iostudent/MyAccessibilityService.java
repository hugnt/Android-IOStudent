package com.example.iostudent;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MyAccessibilityService extends AccessibilityService {
    private static final String TAG = "MyAccessibilityService";
    @Override
    public void onAccessibilityEvent(@NonNull AccessibilityEvent event) {
        Log.e(TAG, "onAccessibilityEvent: ");
        String packageName = event.getPackageName().toString();
        PackageManager packageManager = this.getPackageManager();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            CharSequence applicationLabel = packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
            Log.e(TAG, "app name is: "+ applicationLabel);
            ReminderBroadcast reminderBroadcast = new ReminderBroadcast();
            reminderBroadcast.Initialize("ST1", "Reminder by IOStudent", "You have a task at ..");
            reminderBroadcast.SetReturnActivity(NotificationActivity.class, "data", "Clicked on Noti", 192);
            reminderBroadcast.SetTimeDisplay(LocalDateTime.now().plusSeconds(5));
            reminderBroadcast.createReminderBroadcast(getApplicationContext());
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onInterrupt() {
        Log.e(TAG, "Interrupt: something went wrong");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        //Get app
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefsPackage", Context.MODE_PRIVATE);
        Set<String> selectedPackageSet = sharedPreferences.getStringSet("SelectedPackages", null);
        List<String> selectedPackages = new ArrayList<>(selectedPackageSet);

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();

        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED |
                AccessibilityEvent.TYPE_VIEW_FOCUSED;
        info.packageNames = selectedPackages.toArray(new String[0]);
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;

        info.notificationTimeout = 300;

        this.setServiceInfo(info);
        Log.e(TAG, "onServiceConnected: ");
    }


}
