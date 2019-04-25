package com.example.micandmaster.audio;

import android.content.Context;
import android.media.AudioTrack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class WaveForm {
    private Audio mAudio;
    private FileInputStream in;
    private Thread mThread;
    private long size;
    private byte[] byteData;
    private int[] values;
    private int numSamples = 256;


    public WaveForm(Audio audio, Context context) {
        mAudio = audio;
        mAudio.generatePath(context);
        File audioFile = new File(mAudio.path);
        size = audioFile.length();
        byteData = new byte[(int) size/numSamples];
        values = new int[numSamples];
        try {
            in = new FileInputStream(audioFile);
            mThread = new Thread(new WaveformCalcProcess());
            mThread.start();
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }

    }

    private int getAverage(byte[] audioBytes){
        int total = 0;
        for(byte val: audioBytes){
            total += (int) val;
        }
        return total/numSamples;
    }

    private class WaveformCalcProcess implements Runnable {

        @Override
        public void run() {
            int ret = 0;
            for (int n = 0; n < numSamples; n++) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                try {
                    in.read(byteData, 0, (int) size/numSamples);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                values[n] = getAverage(byteData);
                System.out.println(values[n]);
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mThread = null;
        }
    }
}
