package com.example.zxcbn.superplayer;

import android.app.Application;

/**
 * Created by zxcbn on 2018-06-09.
 */

public class AudioApp extends Application {
        private static AudioApp mInstance;
        private AudioServiceInterface mInterface;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mInterface = new AudioServiceInterface(getApplicationContext());
    }

    public static AudioApp getInstance() {
            return mInstance;
        }

        public AudioServiceInterface getServiceInterface() {
            return mInterface;
        }
}
