package com.example.exp.sleep.Fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.exp.sleep.Activity.AudioTestAcitivity;
import com.example.exp.sleep.R;
import com.example.exp.sleep.Service.RecordingService;
import com.example.exp.sleep.Tools.MyCalendar;
import com.example.exp.sleep.View.AudioView;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * The HomePageActivity shows the homepgae views and provides the entrance of setting time and monitoring.
 */

public class HomePageActivity extends Fragment implements View.OnClickListener {
    public static  final  String CLASS_NAME = "HomePageActivity";
    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;
    private View mView;
    private Calendar mCalendar;
    private TextView mTextTime;
    private boolean mHasNotChooseTime;
    @Nullable
    public  AudioView testView;
    public  MainActivity mainActivity;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_homepage,container,false);
        return  mView;
    }

    /**
     * Init The intents and check the permission
     */
    public  void  Init(){
//        Intent intent = new Intent(HomePageActivity.this,AlarmActivity.class);
//        mPendingIntent = PendingIntent.getActivity(HomePageActivity.this,0,intent,0);
        mHasNotChooseTime = true;
        mAlarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
        mCalendar = Calendar.getInstance();
        mainActivity = new MainActivity();
        mTextTime    = getView().findViewById(R.id.CurrentTime);

        getView().findViewById(R.id.btn_sleep).setOnClickListener(this);
        getView().findViewById(R.id.btn_choose_earlist_time2).setOnClickListener(this);
    }


    /**
     * implement the onCLick funtion to respond two types of click : choose time and begin to sleep
     * @param v
     */

    @Override
    public void onClick(View v) {
//        start to show the user's sleep data
//        startActivity(new Intent(this,MainActivity.class));

        switch (v.getId()){
            // begin to sleep
            case R.id.btn_sleep:
                if(mHasNotChooseTime){
                    Toast.makeText(getContext(),"Please choose the wake up time", Toast.LENGTH_SHORT).show();
                    break;
                }
                MyCalendar myCalendar = new MyCalendar(mCalendar);
                AudioTestAcitivity.actionStart(getContext(),myCalendar);

                try {
                    Thread.sleep(1400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Intent trackingIntent = new Intent(getContext(), RecordingService.class);
//                MyApplication.recorder.audioRecorder.
                getContext().startService(trackingIntent);
//                mAlarmManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), mPendingIntent);
                break;
            case R.id.btn_choose_earlist_time2:
                final Calendar currentTime = Calendar.getInstance();
                new TimePickerDialog(getContext(), 0,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view,
                                                  int hourOfDay, int minute) {
                                //set the current time
                                Calendar currentCalendar = Calendar.getInstance();
                                currentCalendar.setTimeInMillis(System.currentTimeMillis());

                                mCalendar = Calendar.getInstance();
                                mCalendar.setTimeInMillis(System.currentTimeMillis());
                                int Day =  mCalendar.get(Calendar.DAY_OF_MONTH);

                                mCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                mCalendar.set(Calendar.MINUTE,minute);

                                if(currentCalendar.getTimeInMillis() - mCalendar.getTimeInMillis() > 1000){
                                    Day = Day + 1;
                                    Log.d("SOMETHING","ADD");
//                                    Toast.makeText(HomePageActivity.this,"Tomorrow",Toast.LENGTH_SHORT).show();
                                }

                                // Set the Current time
                                mCalendar.set(Calendar.DAY_OF_MONTH,Day);

                                // update the text
                                mHasNotChooseTime = false;
                                mTextTime.setText("Wake Up Time :  " + hourOfDay + " : " + minute);

                                Log.d("sleepcondition",mCalendar.getTimeInMillis() + " ");
                                // make to toast to infrom user that alarm have been set.
//                                Toast.makeText(HomePageActivity.this, mCalendar.get(Calendar.HOUR_OF_DAY) + ":" +
//                                                mCalendar.get(Calendar.MINUTE),
//                                        Toast.LENGTH_SHORT).show();
                            }
                        }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime
                        .get(Calendar.MINUTE), true).show();

                break;
        }
    }
}
