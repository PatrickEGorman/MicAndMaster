package com.example.micandmaster;

import android.app.Activity;
import android.app.Service;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.micandmaster.audio.Audio;
import com.example.micandmaster.db.AudioDao;
import com.example.micandmaster.db.AudioDatabase;
import com.example.micandmaster.db.AudioEntity;
import com.example.micandmaster.db.AudioViewModel;

import java.io.IOException;

import javax.xml.transform.Result;

public class EditorActivity extends AppCompatActivity {
    Audio audio;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String audioName = getIntent().getStringExtra(MainActivity.AUDIO_NAME);
        AudioViewModel model = new AudioViewModel(getApplication());
        MutableLiveData<AudioEntity> audioLiveData = model.findAudio(audioName);
        audioLiveData.observe(this, new Observer<AudioEntity>(){
            @Override
            public void onChanged(AudioEntity audioEntity){
                Audio audio = new Audio(audioEntity.getName(), audioEntity.getPath());
                setAudio(audio);
            }
        });
    }

    public void setAudio(Audio audio){
        this.audio = audio;
    }

    public void playClick(View view){
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audio.path);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

}
