package com.example.micandmaster.audio;

import android.content.Context;

public class WaveForm {
    Audio mAudio;

    static {
        System.loadLibrary("native-lib");
    }

    public WaveForm(Audio audio, Context context){
        mAudio = audio;
        mAudio.generatePath(context);
        System.out.println(array(mAudio.path)[2]);
    }

    public native int[] array(String jstring1);
}
