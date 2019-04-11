package com.example.micandmaster.db;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;

import com.example.micandmaster.audio.Audio;

public class AudioViewModel extends AndroidViewModel {
    public AudioRepository repository;
    private MutableLiveData<String> searchResults;

    public AudioViewModel (Application application) {
        super(application);
        this.repository = new AudioRepository(application);
    }
    public void insertAudio(Audio audio){
        AudioEntity audioEntity = new AudioEntity(audio.name, audio.path);
        this.repository.insertAudio(audioEntity);
    }

    public MutableLiveData<AudioEntity> findAudio(String name) {
        this.repository.getAudioFromName(name);
        return this.repository.searchByNameResult;
    }

}
