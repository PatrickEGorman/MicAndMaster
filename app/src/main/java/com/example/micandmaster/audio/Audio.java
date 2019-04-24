package com.example.micandmaster.audio;

import android.app.Application;
import android.content.Context;

import com.example.micandmaster.db.AudioViewModel;

import java.io.File;

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

    public void saveToDb(Application application) {
        AudioViewModel model = new AudioViewModel(application);
        model.insertAudio(this);
    }

    public void deleteAudio(Application application) {
        File audiOFile = new File(this.path);
        if (audiOFile.delete()) {
            System.out.println(this.path + " deleted");
        }
        AudioViewModel model = new AudioViewModel(application);
        model.deleteAudio(this);
    }

    public void generatePath(Context context) {
        this.path = context.getFilesDir().getAbsolutePath() + "/" + name + ".pcm";
    }
}
