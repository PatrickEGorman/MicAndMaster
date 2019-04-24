package com.example.micandmaster.audio;

import android.content.Context;

public class WaveForm {
    Audio mAudio;

    public WaveForm(Audio audio, Context context) {
        mAudio = audio;
        mAudio.generatePath(context);
    }
}
