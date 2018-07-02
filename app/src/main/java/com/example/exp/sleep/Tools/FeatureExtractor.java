package com.example.exp.sleep.Tools;

import android.util.Log;

import com.example.exp.sleep.Parameter.Parameter;

/**
 * This class Extract the noiseModel three features : RMS,RLH,VAR
 * RLH means ratio of low frequency to high frequency
 * RMS means the root mean square
 * VAR means the volume variance of the frame
 */

public class FeatureExtractor {

    private NoiseModel noiseModel;
    private float[] lowFreq;
    private float[] highFreq;
    public  int PreCondtion = 0;
    public  int mCnt = 0;

    public FeatureExtractor(NoiseModel noiseModel) {
        this.noiseModel = noiseModel;
    }

    public boolean update(short[] buffer,int mode) {
        lowFreq = new float[buffer.length];
        highFreq = new float[buffer.length];
        noiseModel.addRLH(calculateRLH(buffer));
        noiseModel.addRMS(calculateRMS(buffer));
        noiseModel.addVAR(calculateVar(buffer));

        if(mode == Parameter.SLEEP_TIME ) mCnt = 0;
        else{
            PreCondtion = noiseModel.calculateFrame();
            mCnt += PreCondtion;
            if(PreCondtion == 0) mCnt = 0;
        }

        return  mCnt > Parameter.WAKE_UP_ACCURACY;
    }


    private double calculateRMS(short[] buffer) {
        long sum = 0;
        for(int i=0;i<buffer.length;i++) {
            sum += Math.pow(buffer[i],2);
        }
        return Math.sqrt(sum / buffer.length);
    }

    private double calculateRMS(float[] buffer) {
        long sum = 0;
        for(int i=0;i<buffer.length;i++) {
            sum += Math.pow(buffer[i],2);
        }
        return Math.sqrt(sum / buffer.length);
    }

    private double calculateLowFreqRMS(short[] buffer) {
        lowFreq[0] = 0;

        float a = 0.25f;

        for(int i=1;i<buffer.length;i++) {
            lowFreq[i] = lowFreq[i-1] + a * (buffer[i] - lowFreq[i-1]);
        }

        return calculateRMS(lowFreq);
    }

    private double calculateHighFreqRMS(short[] buffer) {
        highFreq[0] = 0;

        float a = 0.25f;

        for(int i=1;i<buffer.length;i++) {
            highFreq[i] = a * (highFreq[i-1] + buffer[i] - buffer[i-1]);
        }

        return calculateRMS(highFreq);
    }

    private double calculateRLH(short[] buffer) {
        double rmsh = calculateHighFreqRMS(buffer);
        double rmsl = calculateLowFreqRMS(buffer);
        if(rmsh == 0) return 0;
        if(rmsl == 0) return 0;
        return  rmsl / rmsh;
    }

    /**
     * Calculates the var of one frame
     *
     * @param buffer
     * @return
     */
    private double calculateVar(short[] buffer) {

        double mean = calculateMean(buffer);
        double var = 0;
        for(short s: buffer) {
            var += Math.pow(s - mean,2);
        }
        return var / buffer.length;
    }

    /**
     * Calculate the mean of one fram
     *
     * @param buffer
     * @return
     */
    private double calculateMean(short[] buffer) {
        double mean = 0;
        for(short s: buffer) {
            mean += s;
        }
        return mean / buffer.length;
    }
}
