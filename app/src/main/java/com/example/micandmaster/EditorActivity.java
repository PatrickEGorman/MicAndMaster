package com.example.micandmaster;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.pm.ActivityInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Chronometer;

import com.example.micandmaster.audio.Audio;
import com.example.micandmaster.audio.WaveForm;
import com.example.micandmaster.db.AudioEntity;
import com.example.micandmaster.db.AudioViewModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class EditorActivity extends AppCompatActivity {
    private Audio audio;
    private Chronometer myChronometer;
    private AudioTrack audioPlayer;
    private Thread mThread;
    private FileInputStream in;
    private File file;
    private byte[] byteData;
    private int count = 512 * 1024;
    private long size;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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
        this.file = new File(audio.path);
        generateWaveform();
    }

    public void playClick(View view) {
        audioPlayer = createAudioPlayer();
        myChronometer.setBase(SystemClock.elapsedRealtime());
        myChronometer.start();
        try {
            in = new FileInputStream(this.file);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        audioPlayer.play();
        mThread = new Thread(new PlayerProcess());
        mThread.start();
    }

    private AudioTrack createAudioPlayer() {
        int intSize = android.media.AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);
        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, intSize, AudioTrack.MODE_STREAM);
        byteData = new byte[(int) count];
        try {
            in = new FileInputStream(this.file);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        size = file.length();
        return audioTrack;
    }

    private class PlayerProcess implements Runnable {

        @Override
        public void run() {
            long bytesread = 0;
            int ret = 0;
            while (bytesread < size) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                try {
                    ret = in.read(byteData, 0, count);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (ret != -1) { // Write the byte array to the track
                    audioPlayer.write(byteData, 0, ret);
                    bytesread += ret;
                } else break;
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (audioPlayer != null) {
                if (audioPlayer.getState() != AudioTrack.PLAYSTATE_STOPPED) {
                    audioPlayer.stop();
                    audioPlayer.release();
                    mThread = null;
                }
                mThread = null;
                stopChronometer();
            }
        }
    }

    public void generateWaveform() {
        WaveForm waveForm = new WaveForm(this.audio, this.getApplicationContext());
    }

    public void stopChronometer() {
        myChronometer.stop();
    }

}
