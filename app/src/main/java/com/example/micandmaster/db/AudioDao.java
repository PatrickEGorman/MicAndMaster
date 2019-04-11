package com.example.micandmaster.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

@Dao
public interface AudioDao {

    @Query("SELECT * from audio_table ORDER BY name ASC")
    LiveData<List<AudioEntity>> getAudio();

    @Query("SELECT name from audio_table ORDER BY name ASC")
    LiveData<List<String>> getAudioNames();

    @Transaction
    @Query("SELECT * from audio_table WHERE audio_table.name=:name")
    AudioEntity getAudioFromName(String name);

    @Insert
    void insertAudio(AudioEntity audioEntity);

    // Make sure to also delete audioEntity file
    @Delete
    void deleteAudio(AudioEntity audioEntity);
}
