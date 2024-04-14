package com.example.iostudent;

public class AppInfo {
    private String appName;
    private String packageName;

    public AppInfo(String appName, String packageName) {
        this.appName = appName;
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    @Override
    public String toString() {
        return appName;
    }
}