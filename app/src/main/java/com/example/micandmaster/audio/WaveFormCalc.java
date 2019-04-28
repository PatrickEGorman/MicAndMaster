package com.example.micandmaster.audio;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class WaveFormCalc {
    private String mPath;
    private FileInputStream in;
    private long size;
    private byte[] byteData;
    private int[] values;
    private int numSamples = 256;


    public WaveFormCalc(File audioFile) {
        size = audioFile.length();
        byteData = new byte[(int) size / numSamples];
        values = new int[numSamples];
        try {
            in = new FileInputStream(audioFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        values = getWaveFormData();
    }

    private int getAverage(byte[] audioBytes) {
        int total = 0;
        for (byte val : audioBytes) {
            if(val>0) {
                total += (int) val;
            }
            if(val<0) {
                total -= (int) val;
            }
        }
        return total / audioBytes.length;
    }

    public int[] getWaveFormData() {
        int ret = 0;
        int[] sampleAverage = new int[numSamples];
        for (int n = 0; n < numSamples; n++) {
            try {
                in.read(byteData, 0, (int) size / numSamples);
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

    public int[] getValues() {
        return values;
    }

    public void setValues(int[] values) {
        this.values = values;
    }
}
