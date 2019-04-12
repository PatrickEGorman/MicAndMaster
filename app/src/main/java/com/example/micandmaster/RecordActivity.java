package com.example.micandmaster;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.micandmaster.Util.FileUtilities;
import com.example.micandmaster.audio.Audio;

import java.io.File;
import java.io.IOException;

public class RecordActivity extends AppCompatActivity {

    private MediaRecorder mediaRecorder;
    private File file;
    private MediaPlayer mediaPlayer;
    private Chronometer myChronometer;
    private LayoutInflater inflater;
    private View popupView;
    public static final String AUDIO_NAME = "com.example.micandmaster.AUDIO_NAME";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String path = this.getFilesDir().getAbsolutePath();
        this.file = new File(path + "audio.aac");
        this.myChronometer = (Chronometer) findViewById(R.id.chronometer);
    }

    public void recordClick(View view) {
        String curText = (String) ((TextView) view).getText();

        if (curText.equals("New Recording")) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            mediaRecorder.setOutputFile(this.file.getAbsolutePath());
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            try {
                mediaRecorder.prepare();
                ((TextView) view).setText("Stop");
            } catch (IOException E) {
                E.printStackTrace();
            }
            myChronometer.setBase(SystemClock.elapsedRealtime());
            myChronometer.start();
            mediaRecorder.start();
        }

        if (curText.equals("Stop")) {
            ((TextView) view).setText("New Recording");
            myChronometer.stop();
            mediaRecorder.stop();
            Button playButton = (Button) findViewById(R.id.play);
            playButton.setVisibility(View.VISIBLE);
            Button saveButton = (Button) findViewById(R.id.save);
            saveButton.setVisibility(View.VISIBLE);
        }
    }

    public void playClick(View view) {
        mediaPlayer = new MediaPlayer();
        try {
            myChronometer.setBase(SystemClock.elapsedRealtime());
            mediaPlayer.setDataSource(this.file.getPath());
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

    private void stopChronometer() {
        this.myChronometer.stop();
    }

    public void saveClick(View view) {
        this.inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        this.popupView = this.inflater.inflate(R.layout.save_as_window, null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focasable = true;
        final PopupWindow popupWindow = new PopupWindow(this.popupView, width, height, focasable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    public void savePopupClick(View view) {
        View layout = RecordActivity.this.inflater.inflate(R.layout.activity_record,
                (ViewGroup) this.findViewById(R.id.save_as_window));
        EditText nameInput = (EditText) this.popupView.findViewById(R.id.name_input);
        String name = nameInput.getText().toString();
        Boolean isUnique = Audio.checkNameUnique(name, this);
        if (isUnique) {
            Audio audio = new Audio(name);
            audio.generatePath(this);
            File audioSaved = new File(audio.path);
            try {
                FileUtilities.copyFileUsingStream(this.file, audioSaved);
                audio.saveToDb(getApplication());
                Intent intent = new Intent(this, EditorActivity.class);
                intent.putExtra(AUDIO_NAME, audio.name);
                startActivity(intent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
