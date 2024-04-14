package com.example.iostudent;

import static android.content.Context.ALARM_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TaskFragment extends Fragment {
    private static final int PICK_RINGTONE_REQUEST = 142;
    private List<AppInfo> selectedAppByUser= new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        //SELECT RINGTON
        Button btnSelectRingtone = view.findViewById(R.id.btnSelectRingtone);
        btnSelectRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo Intent để mở màn hình chọn ringtone //ACTION_NOTIFICATION_LISTENER_SETTINGS
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);

                // Thiết lập các thuộc tính cho Intent
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Chọn ringtone");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);

                // Gọi startActivityForResult để mở màn hình chọn ringtone và chờ kết quả trả về
                startActivityForResult(intent, PICK_RINGTONE_REQUEST);

            }
        });

        //SELECT MUSIC
        Button btnSelectMusic = view.findViewById(R.id.btnSelectMusic);
        btnSelectMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo Intent để mở màn hình chọn ringtone //ACTION_NOTIFICATION_LISTENER_SETTINGS
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");

                // Gọi startActivityForResult để mở màn hình chọn ringtone và chờ kết quả trả về
                startActivityForResult(intent, 123);

            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
        Button btnRemind = view.findViewById(R.id.btnRemind);
        btnRemind.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Reminder Set!", Toast.LENGTH_SHORT).show();
            ReminderBroadcast reminderBroadcast = new ReminderBroadcast();
            reminderBroadcast.Initialize("ST1", "Reminder by IOStudent", "You have a task at ..");
            reminderBroadcast.SetReturnActivity(MainActivity.class, "data", "Clicked on Noti", 192);
            reminderBroadcast.SetTimeDisplay(LocalDateTime.now().plusSeconds(5));
            reminderBroadcast.createReminderBroadcast(requireContext());
        });

        //LOAD SPIN
        PackageManager packageManager = getContext().getPackageManager();
        List<ApplicationInfo> appList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        List<AppInfo> appInfoList = new ArrayList<>();
        for (ApplicationInfo appInfo : appList) {
            String appName = appInfo.loadLabel(packageManager).toString();
            String packageName = appInfo.packageName;
            AppInfo appInfoItem = new AppInfo(appName, packageName);
            appInfoList.add(appInfoItem);
        }

        AppAdapter adapter = new AppAdapter(getContext(), appInfoList);
        Spinner spinner = view.findViewById(R.id.spinner);
        spinner.setAdapter(adapter);

        SelectedAppAdapter selectedAppAdapter= new SelectedAppAdapter(getContext(), selectedAppByUser);
        ListView listView = view.findViewById(R.id.lstViewSelected);

        listView.setAdapter(selectedAppAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AppInfo selectedAppInfo = (AppInfo) parent.getItemAtPosition(position);
                String selectedPackageName = selectedAppInfo.getPackageName();
                // Cập nhật danh sách các package đã chọn
                if (!selectedAppByUser.stream().anyMatch(appInfo -> appInfo.getPackageName().equals(selectedPackageName))) {
                    selectedAppByUser.add(selectedAppInfo);
                    selectedAppAdapter.notifyDataSetChanged();
                }
                Toast.makeText(requireContext(), selectedPackageName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Xử lý khi không có ứng dụng nào được chọn
            }
        });

        //FOCUS MODE
        Button btnFocus = view.findViewById(R.id.btnFocus);
        btnFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));

                List<String> selectedPackages = new ArrayList<>();
                for (AppInfo appInfo: selectedAppByUser) {
                    selectedPackages.add(appInfo.getPackageName());
                }

                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefsPackage", Context.MODE_PRIVATE);
                Set<String> selectedPackageSet = new HashSet<>(selectedPackages);
                sharedPreferences.edit().putStringSet("SelectedPackages", selectedPackageSet).apply();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_RINGTONE_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri ringtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            Log.e("Ringtone selected:", ringtoneUri.toString());
            Toast.makeText(requireContext(), "Ringtone selected!", Toast.LENGTH_SHORT).show();
            saveRingtoneUri(ringtoneUri);

        }

        if (requestCode == 123) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedAudioUri = data.getData();
                saveRingtoneUri(selectedAudioUri);
            }
        }
    }

    private void saveRingtoneUri(Uri ringtoneUri) {
        // Lưu URI của ringtone đã chọn vào SharedPreferences
        SharedPreferences preferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ringtone_uri", ringtoneUri.toString());
        editor.apply();
    }



}