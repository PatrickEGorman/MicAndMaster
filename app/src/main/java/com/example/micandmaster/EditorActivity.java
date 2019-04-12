package com.example.micandmaster;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Chronometer;

import com.example.micandmaster.audio.Audio;
import com.example.micandmaster.db.AudioEntity;
import com.example.micandmaster.db.AudioViewModel;

import java.io.IOException;

public class EditorActivity extends AppCompatActivity {
    Audio audio;
    MediaPlayer mediaPlayer;
    Chronometer myChronometer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String audioName = getIntent().getStringExtra(MainActivity.AUDIO_NAME);
        AudioViewModel model = new AudioViewModel(getApplication());
        MutableLiveData<AudioEntity> audioLiveData = model.findAudio(audioName);
        this.myChronometer = (Chronometer) findViewById(R.id.chronometer);
        audioLiveData.observe(this, new Observer<AudioEntity>() {
            @Override
            public void onChanged(AudioEntity audioEntity) {
                Audio audio = new Audio(audioEntity.getName(), audioEntity.getPath());
                setAudio(audio);
            }
        });
    }

    public void setAudio(Audio audio) {
        this.audio = audio;
    }

    public void playClick(View view) {
        mediaPlayer = new MediaPlayer();
        try {
            myChronometer.setBase(SystemClock.elapsedRealtime());
            mediaPlayer.setDataSource(audio.path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            myChronometer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopChronometer();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopChronometer() {
        myChronometer.stop();
    }

}
