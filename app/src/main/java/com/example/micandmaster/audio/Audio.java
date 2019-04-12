package com.example.micandmaster.audio;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.example.micandmaster.db.AudioDatabase;
import com.example.micandmaster.db.AudioViewModel;

import java.io.File;
import java.util.List;

public class Audio {
    public String path;
    public String name;

    public Audio(String name) {
        this.name = name;
    }


    public Audio(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public static boolean checkNameUnique(String name, Context context) {
        AudioDatabase db = AudioDatabase.getDatabase(context);
        LiveData<List<String>> namesLiveData = db.audioDao().getAudioNames();
        Boolean unique = true;
        try {
            List<String> names = namesLiveData.getValue();
            for (int i = 0; i < names.size(); i++) {
                if (name.equals(names.get(i))) {
                    unique = false;
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return unique;
    }

    public void saveToDb(Application application) {
        AudioViewModel model = new AudioViewModel(application);
        model.insertAudio(this);
    }

    public void deleteAudio(Application application) {
        File audiOFile = new File(this.path);
        audiOFile.delete();
        AudioViewModel model = new AudioViewModel(application);
        model.deleteAudio(this);
    }

    public void generatePath(Context context) {
        this.path = context.getFilesDir().getAbsolutePath() + name + ".aac";
    }
}
