package com.example.micandmaster;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.micandmaster.audio.Audio;
import com.example.micandmaster.db.AudioViewModel;

import java.util.List;

public class LoadExistingActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {

    String current_selection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_existing);
        AudioViewModel model = new AudioViewModel(getApplication());
        LiveData<List<String>> names = model.getAudioNames();
        names.observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> names) {
                setList(names);
            }
        });
    }

    protected void setList(List<String> names) {
        Spinner spinner = (Spinner) findViewById(R.id.audio_selector);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.current_selection = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void loadButtonClick(View view) {
        Intent intent = new Intent(this, EditorActivity.class);
        intent.putExtra(MainActivity.AUDIO_NAME, this.current_selection);
        startActivity(intent);
    }

    public void deleteButtonClick(View view) {
        Audio audio = new Audio(this.current_selection);
        audio.generatePath(this);
        audio.deleteAudio(getApplication());
    }
}
