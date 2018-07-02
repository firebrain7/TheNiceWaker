package com.example.exp.sleep.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.exp.sleep.Activity.AudioTestAcitivity;
import com.example.exp.sleep.Parameter.Parameter;
import com.example.exp.sleep.R;
import com.example.exp.sleep.Tools.AudioRecorder;
import com.example.exp.sleep.Tools.NoiseModel;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * This class  is a view to show the graph of user's sleep
 * it contains several ArrayList to save the data and three features.
 */


public class AudioView extends View implements TestView {
    Paint mPaint;
    AudioTestAcitivity mParentActivity;
    ArrayList<Double> points = null;
    ArrayList<Double[]> points2 = null;
    public static AudioView instance = null;
    public Calendar mCalendar;
    public static float lux = 0;
    private int snore = 0;
    private int move = 0;
    private int i = 0;
    private boolean NotAlarmed = true;
    ArrayList<Double> rlh = null;
    ArrayList<Double> var = null;
    ArrayList<Double> rms = null;

    private AudioRecorder recorder;
    private NoiseModel noiseModel;

    public AudioView(Context context,Calendar calendar,AudioTestAcitivity parentActivity) {
        super(context);
        mCalendar = calendar;
        Log.d("sleepcondition",mCalendar.getTimeInMillis() + " ");
        mParentActivity = mParentActivity;
        init();
    }

    public AudioView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AudioView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setTextSize(80);

        points = new ArrayList<>();
        points2 = new ArrayList<>();
        rlh = new ArrayList<>();
        var = new ArrayList<>();
        rms = new ArrayList<>();
        instance = this;

        noiseModel = new NoiseModel();

//        recorder = new AudioRecorder(noiseModel,this);
//        recorder.start();
    }

    @Override
    public void invalidate(int mode) {
        super.invalidate();
        if(mode == Parameter.MODE_ALARM && NotAlarmed){
            Alarm();
            NotAlarmed = false;
        }
    }


    private void Alarm(){
        final MediaPlayer mMediaPlayer = MediaPlayer.create(this.getContext(),R.raw.dream);
        mMediaPlayer.start();

        //创建一个闹钟提醒的对话框,点击确定关闭铃声与页面
        new AlertDialog.Builder(this.getContext()).setTitle("Nice Wake").setMessage("It is time to change the world~")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMediaPlayer.stop();
//                        mParentActivity.onStop();
                    }
                }).show();
    }

    public void addPoint2(Double x, Double y) {
        if(points2.size() > 10) {
            points2.remove(0);
        }
        Double[] p = new Double[2];
        p[0] = x;
        p[1] = y;
        Log.d("AudioView\\addPoint",x + ": " + y);
        points2.add(p);
    }

    public void setLux(Float lux) {
        AudioView.lux = lux;
    }

    public void addRMS(Double p) {
        if(rms.size() > 300) {
            rms.remove(0);
        }
        rms.add(p);
    }
    public void addRLH(Double p) {
        if(rlh.size() > 300) {
            rlh.remove(0);
        }
        rlh.add(p);
    }
    public void addVAR(Double p) {
        if(var.size() > 300) {
            var.remove(0);
        }
        var.add(p);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for(int i = 0;i<points2.size();i++) {
            Double[] p = points2.get(i);
            canvas.drawCircle((float)(500 + p[0]),(float)(500+p[1]), 2, mPaint);
        }
        if(points2.size() > 0) {
            Double[] curr = points2.get(points2.size() - 1);
            mPaint.setColor(Color.RED);
            canvas.drawText("RLH: " + curr[0], 100f, 200f, mPaint);
            mPaint.setColor(Color.YELLOW);
            canvas.drawText("VAR: " + curr[1], 100f, 300f, mPaint);
            mPaint.setColor(Color.BLUE);
            canvas.drawText("RMS: " + lux, 100f, 400f, mPaint);
            Log.d("lux",lux + " ");

            if(curr[1] > 1) { // Filter noise
                if(curr[0] > 2) {
                    snore++;
                } else {
                    if(lux > 0.5) {
                        move++;
                    }
                }
            }

            canvas.drawText("Snore: " + snore, 100f, 500f, mPaint);
            canvas.drawText("Move: " + move, 100f, 600f, mPaint);
            addRLH((curr[0]*20 + 900));
            addVAR((curr[1]*20  + 900));
            addRMS((double) (lux * 20 + 900));
            drawPoints(canvas);
        }
        this.i++;

    }

    protected void drawPoints(Canvas canvas) {

        for(int i = 0;i<rms.size();i++) {

            Paint paint = new Paint();
            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(80);
            Double p = rms.get(i);
            canvas.drawCircle(i*4,p.floatValue(), 8, paint);
        }
        for(int i = 0;i<var.size();i++) {
            Paint paint = new Paint();
            paint.setColor(Color.YELLOW);
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(80);
            Double p = var.get(i);
            canvas.drawCircle(i*4,p.floatValue(), 8, paint);
        }
        for(int i = 0;i<rlh.size();i++) {
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(80);
            Double p = rlh.get(i);
            canvas.drawCircle(i*4,p.floatValue(), 8, paint);
        }
    }

    public void stop() {
        recorder.close();
    }
}
