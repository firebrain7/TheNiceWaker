package com.example.exp.sleep.Tools;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.example.exp.sleep.Parameter.Parameter;
import com.example.exp.sleep.View.AudioView;
import com.example.exp.sleep.View.TestView;

import java.util.Calendar;


public class AudioRecorder extends Thread {
    private boolean stopped = false;
    private static AudioRecord recorder = null;
    private static int N = 0;
    private NoiseModel noiseModel;
    private TestView debugView;
    private short[] buffer;
    private FeatureExtractor featureExtractor;

    private int mCondition;

    public AudioRecorder(NoiseModel noiseModel, TestView debugView) {
        this.noiseModel = noiseModel;
        this.debugView = debugView;
        this.featureExtractor = new FeatureExtractor(noiseModel);
    }

    @Override
    public void run() {
        capture();
    }

    private void capture() {
        int i = 0;
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        if(buffer == null) {
            buffer  = new short[1600];
        }

        if(N == 0 || (recorder == null || recorder.getState() != AudioRecord.STATE_INITIALIZED)) {
            N = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            if(N < 1600) {
                N = 1600;
            }
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    16000,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    N);
        }
        recorder.startRecording();

        while(!this.stopped) {
            N = recorder.read(buffer, 0, buffer.length);
            process(buffer);
        }
        recorder.stop();
        recorder.release();
    }

    private void process(short[] buffer) {

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(System.currentTimeMillis());
        long currentTime = currentCalendar.getTimeInMillis();

        final AudioView parentView = (AudioView) (debugView);
        Calendar ScheduledCalendar = parentView.mCalendar;
//        Log.d("sleepcondition1",ScheduledCalendar.getTimeInMillis() + " ");

        long EarlyTime = ScheduledCalendar.getTimeInMillis();
        ScheduledCalendar.add(Calendar.MINUTE,Parameter.SLEEP_PERIOD);
        long LateTime = ScheduledCalendar.getTimeInMillis();
        ScheduledCalendar.add(Calendar.MINUTE,-Parameter.SLEEP_PERIOD);

        if(currentTime < EarlyTime)
            mCondition = Parameter.SLEEP_TIME;
        else if(currentTime >= LateTime)
            mCondition = Parameter.LATE_WAKE_TIME;
        else
            mCondition = Parameter.EARLY_WAKE_TIME;

//        Log.d("timeCompare: \n", "Cur : " + currentTime + " \nEly :" + EarlyTime);

        final boolean shllowSleep = featureExtractor.update(buffer,mCondition);
        Log.d("sleep","sleep mode:" + mCondition);

        Log.d("AppState:","Running");
        if(debugView != null) {
            /*debugView.addPoint2(noiseModel.getNormalizedRLH(), noiseModel.getNormalizedVAR());
            debugView.setLux((float) (noiseModel.getNormalizedRMS()));*/
            debugView.addPoint2(noiseModel.getLastRLH(), noiseModel.getNormalizedVAR());
            debugView.setLux((float) (noiseModel.getLastRMS()));
            debugView.post(new Runnable() {
                @Override
                public void run() {
                    if(mCondition == Parameter.LATE_WAKE_TIME || ((mCondition == Parameter.EARLY_WAKE_TIME ) && shllowSleep)) {
                        debugView.invalidate(Parameter.MODE_ALARM);
                        stopped = true;
                    }
                    else {
                        debugView.invalidate(Parameter.MODE_SLEEP);
                    }
                }
            });
        }

    }

    public void close() {
        stopped = true;
    }

}