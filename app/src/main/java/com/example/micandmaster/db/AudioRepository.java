package com.example.micandmaster.db;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import java.util.List;


public class AudioRepository {

    public MutableLiveData<AudioEntity> searchByNameResult =
            new MutableLiveData<>();
    private AudioDao audioDao;

    public AudioRepository(Application application) {
        AudioDatabase db;
        db = AudioDatabase.getDatabase(application);
        audioDao = db.audioDao();
    }

    public void insertAudio(AudioEntity audio) {
        InsertAsyncTask asyncTask = new InsertAsyncTask(audio, this.audioDao);
        asyncTask.execute();
    }

    public void deleteAudio(AudioEntity audio) {
        DeleteAsyncTask asyncTask = new DeleteAsyncTask(audio, this.audioDao);
        asyncTask.execute();
    }

    public void getAudioFromName(String name) {
        GetByNameAsyncTask asyncTask = new GetByNameAsyncTask(name, this.audioDao, this);
        asyncTask.execute();
    }

    public LiveData<List<String>> getAudioNames() {
        return audioDao.getAudioNames();
    }

    private void asyncNameFinished(AudioEntity results) {
        searchByNameResult.setValue(results);
    }

    private static class InsertAsyncTask extends AsyncTask<Void, Void, Integer> {

        private AudioDao audioDao;
        private AudioEntity audioEntity;

        public InsertAsyncTask(AudioEntity audio, AudioDao audioDao) {
            this.audioDao = audioDao;
            this.audioEntity = audio;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            this.audioDao.insertAudio(this.audioEntity);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<Void, Void, Integer> {

        private AudioDao audioDao;
        private AudioEntity audioEntity;

        public DeleteAsyncTask(AudioEntity audio, AudioDao audioDao) {
            this.audioDao = audioDao;
            this.audioEntity = audio;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            this.audioDao.deleteAudio(this.audioEntity);
            return null;
        }
    }

    private static class GetByNameAsyncTask extends AsyncTask<Void, Void, AudioEntity> {

        private String name;
        private AudioDao audioDao;
        private AudioRepository delegate;

        public GetByNameAsyncTask(String name, AudioDao audioDao, AudioRepository delegate) {
            this.name = name;
            this.audioDao = audioDao;
            this.delegate = delegate;
        }

        @Override
        protected AudioEntity doInBackground(Void... params) {
            return this.audioDao.getAudioFromName(this.name);
        }

        @Override
        protected void onPostExecute(AudioEntity result) {
            this.delegate.asyncNameFinished(result);
        }
    }
}
