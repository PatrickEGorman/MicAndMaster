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
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        values = getWaveFormData();
    }

    private int getAverage(byte[] audioBytes){
        int total = 0;
        for(byte val: audioBytes){
            total += (int) val;
        }
        return total/numSamples;
    }

    public int[] getWaveFormData() {
        int ret = 0;
        int[] sampleAverage = new int[numSamples];
        for (int n = 0; n < numSamples; n++) {
            try {
                in.read(byteData, 0, (int) size/numSamples);
            } catch (IOException e) {
                e.printStackTrace();
            }
            sampleAverage[n] = getAverage(byteData);
            System.out.println(sampleAverage[n]);
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sampleAverage;
    }
}
