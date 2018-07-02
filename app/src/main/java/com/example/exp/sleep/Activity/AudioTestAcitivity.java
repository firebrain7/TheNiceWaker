package com.example.exp.sleep.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.exp.sleep.App.MyApplication;
import com.example.exp.sleep.R;
import com.example.exp.sleep.Service.RecordingService;
import com.example.exp.sleep.Tools.Hooks;
import com.example.exp.sleep.Tools.MyCalendar;
import com.example.exp.sleep.View.AudioView;
import com.example.exp.sleep.View.TestView;

import java.io.Serializable;
import java.util.Calendar;
import java.util.concurrent.Callable;

/**
 *  This class is aim to show the data of the user's sleep
 *  it contains a mTestView which show the graph of user' sleep dynamically.
 */

public class AudioTestAcitivity extends AppCompatActivity {
    private AudioView mTestView;
    public  Calendar myCalendar;
    public static String TAG = "???????";

    public  static void actionStart(Context context, MyCalendar calendar) {
        Intent intent = new Intent(context, AudioTestAcitivity.class);
//        Log.d("minute",calendar.get(Calendar.MINUTE) + " ");
        intent.putExtra("Ser",calendar);
        Log.d(TAG, "actionStart: ");

//        Log.i(TAG, "actionStart: ");
//        intent.putExtra("day",calendar.get(Calendar.DAY_OF_MONTH));
//        intent.putExtra("hour",calendar.get(Calendar.HOUR_OF_DAY));
//        intent.putExtra("minute",calendar.get(Calendar.MINUTE));

        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTestView = new AudioView(this,myCalendar,this);
        MyApplication.recorder.testView = mTestView;
        setContentView(mTestView);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        MyCalendar ser = (MyCalendar) intent.getExtras().getSerializable("Ser");
        myCalendar = ser.getCalendar();
        Log.d("sleepcondition",myCalendar.getTimeInMillis() + " ");
    }

    @Override
    public  void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        RecordingService.instance.stopSelf();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d("AudioTest","onPause");
        super.onPause();
//        mTestView.stop();
    }
}



