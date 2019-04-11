package com.example.micandmaster;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.micandmaster.Util.FileUtilities;
import com.example.micandmaster.audio.Audio;
import com.example.micandmaster.db.AudioDatabase;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class RecordActivity extends AppCompatActivity {

    private MediaRecorder mediaRecorder;
    private File file;
    private MediaPlayer mediaPlayer;
    private Button playButton;
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
    }

    public void recordClick(View view){
        String curText = (String) ((TextView)view).getText();

        if(curText.equals("New Recording")){
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            mediaRecorder.setOutputFile(this.file.getAbsolutePath());
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            try {
                mediaRecorder.prepare();
                ((TextView)view).setText("Stop");
            }
            catch(IOException E){
                E.printStackTrace();
            }
            mediaRecorder.start();
        }

        if(curText.equals("Stop")){
            ((TextView)view).setText("New Recording");
            mediaRecorder.stop();
            Button playButton = (Button) findViewById(R.id.play);
            playButton.setVisibility(View.VISIBLE);
            Button saveButton = (Button) findViewById(R.id.save);
            saveButton.setVisibility(View.VISIBLE);
        }
    }

    public void playClick(View view){
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this.file.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void saveClick(View view){
        this.inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        this.popupView = this.inflater.inflate(R.layout.save_as_window, null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focasable = true;
        final PopupWindow popupWindow = new PopupWindow(this.popupView, width, height, focasable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    public void savePopupClick(View view){
        View layout = RecordActivity.this.inflater.inflate(R.layout.activity_record,
                (ViewGroup) this.findViewById(R.id.save_as_window));
        EditText nameInput = (EditText) this.popupView.findViewById(R.id.name_input);
        String name = nameInput.getText().toString();
        Boolean isUnique = Audio.checkNameUnique(name, this);
        if(isUnique){
            Audio audio = new Audio(name);
            audio.generatePath(this);
            File audioSaved = new File(audio.path);
            try {
                FileUtilities.copyFileUsingStream(this.file, audioSaved);
                audio.saveToDb(getApplication());
                Intent intent = new Intent(this, EditorActivity.class);
                intent.putExtra(AUDIO_NAME, audio.name);
                startActivity(intent);
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
