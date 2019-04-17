package com.example.micandmaster;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.micandmaster.Util.FileUtilities;
import com.example.micandmaster.audio.Audio;
import com.example.micandmaster.db.AudioViewModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class RecordActivity extends AppCompatActivity {

    private AudioRecord record;
    private File file;
    private AudioTrack audioPlayer;
    private Chronometer myChronometer;
    private LayoutInflater inflater;
    private View popupView;
    private Thread recordingThread = null;
    int ret = 0;
    private boolean isRecording;
    public static final String AUDIO_NAME = "com.example.micandmaster.AUDIO_NAME";
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(44100,
            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
    private AudioTrack track;
    private byte[] byteData = null;
    private FileInputStream in;
    private int size;
    private int count = 512 * 1024;
    private Thread mThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String path = this.getFilesDir().getAbsolutePath();
        this.file = new File(path + "audio.pcm");
        this.myChronometer = (Chronometer) findViewById(R.id.chronometer);
        track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE, AudioTrack.MODE_STREAM);

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
        final String name = nameInput.getText().toString();
        Boolean isUnique = Audio.checkNameUnique(name, this);
        AudioViewModel viewModel = new AudioViewModel(this.getApplication());
        LiveData<List<String>> namesLiveData = viewModel.getAudioNames();
        namesLiveData.observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> names) {
                saveFile(names, name);
            }
        });
    }

    public void saveFile(List<String> names, String name) {
        boolean isUnique = true;
        for (String audioName : names) {
            if (name.equals(audioName)) {
                isUnique = false;
            }
        }
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
        } else {
            TextView indicator = findViewById(R.id.warningIndicator);
            String text = "Name " + name + " is already taken";
            indicator.setText(text);
        }
    }

    public File getFile() {
        return file;
    }

    public void recordClick(View view) {

        String curText = (String) ((TextView) view).getText();

        if (curText.equals("New Recording")) {
            ((TextView) view).setText("Stop");
            int intSize = android.media.AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT);
            record = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100,
                    AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, intSize);
            record.startRecording();
            myChronometer.setBase(SystemClock.elapsedRealtime());
            myChronometer.start();
            isRecording = true;
            recordingThread = new Thread(new RecordingRunnable(), "Recording Thread");
            recordingThread.start();
            findViewById(R.id.play).setVisibility(View.INVISIBLE);
            findViewById(R.id.save).setVisibility(View.INVISIBLE);
        }

        if (curText.equals("Stop")) {
            ((TextView) view).setText("New Recording");
            myChronometer.stop();
            isRecording = false;
            record.stop();
            record.release();
            findViewById(R.id.play).setVisibility(View.VISIBLE);
            findViewById(R.id.save).setVisibility(View.VISIBLE);
            record = null;
            recordingThread = null;
        }
    }

    private class RecordingRunnable implements Runnable {

        @Override
        public void run() {
            final File file = getFile();
            final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

            try (final FileOutputStream outStream = new FileOutputStream(file)) {
                while (isRecording) {
                    int result = record.read(buffer, BUFFER_SIZE);
                    if (result < 0) {
                        throw new RuntimeException("Reading of audio buffer failed: " +
                                getBufferReadFailureReason(result));
                    }
                    outStream.write(buffer.array(), 0, BUFFER_SIZE);
                    track.write(buffer.array(), 0, BUFFER_SIZE);
                    buffer.clear();
                }
            } catch (IOException e) {
                throw new RuntimeException("Writing of recorded audio failed", e);
            }
        }

        private String getBufferReadFailureReason(int errorCode) {
            switch (errorCode) {
                case AudioRecord.ERROR_INVALID_OPERATION:
                    return "ERROR_INVALID_OPERATION";
                case AudioRecord.ERROR_BAD_VALUE:
                    return "ERROR_BAD_VALUE";
                case AudioRecord.ERROR_DEAD_OBJECT:
                    return "ERROR_DEAD_OBJECT";
                case AudioRecord.ERROR:
                    return "ERROR";
                default:
                    return "Unknown (" + errorCode + ")";
            }
        }
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
        if (audioTrack == null) {
            Log.d("TCAudio", "audio track is not initialised ");
            return null;
        }

        byteData = new byte[(int) count];
        try {
            in = new FileInputStream(this.file);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        size = (int) file.length();
        return audioTrack;
    }

    private class PlayerProcess implements Runnable {

        @Override
        public void run() {
            int bytesread = 0;
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

}
