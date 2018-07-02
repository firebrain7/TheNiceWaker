package com.example.exp.sleep.App;

import android.content.Context;

import com.example.exp.sleep.View.Recorder;

public class MyApplication extends android.app.Application {

    public static Context context;
    public static Recorder recorder;

    @Override
    public void onCreate()
    {
        super.onCreate();

        context = this;

        recorder = new Recorder();
    }
}
