package com.example.zxcbn.superplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;


/**
 * Created by zxcbn on 2018-06-09.
 */

public class AudioServiceInterface {
        private ServiceConnection mServiceConnection;
        private Background mService;

        public AudioServiceInterface(Context context) {
            mServiceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    mService = ((Background.AudioServiceBinder) service).getService();
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    mServiceConnection = null;
                    mService = null;
                }
            };
            context.bindService(new Intent(context, Background.class)
                    .setPackage(context.getPackageName()), mServiceConnection, Context.BIND_AUTO_CREATE);
        }

        public void swPlay(){
            if (isPlaying()){
                mService.pause();
            }else {
                mService.play();
            }
        }

        public boolean isPlaying(){
            if(mService != null){
                return mService.isPlaying();
            }
            return false;
        }

        public void play(int position) {
            if (mService != null) {
                mService.play(position);
            }
        }

        public void play() {
            if (mService != null) {
                mService.play();
            }
        }

        public void pause() {
            if (mService != null) {
                mService.pause();
            }
        }

        public void forward() {
            if (mService != null) {
                mService.forward();
            }
        }

        public void rewind() {
            if (mService != null) {
                mService.rewind();
            }
        }

        public void seekTo(int position){
            if (mService != null){
                mService.seekTo(position);
            }
        }

        public int getCurrentPosition(){
            if (mService != null){
                return mService.getCurrentPosition();
            }
            return 0;
        }

        public void item(int position){
            if (mService != null){
                mService.queryAudioItem(position);
            }
        }

        public int getDuration(){
            if (mService != null){
                return mService.getDuration();
            }
            return 0;
        }

        public int getSessionId(){
            if (mService != null){
                return mService.getSessionId();
            }
            return 0;
        }

    public int getPosition(){
        if (mService != null){
            return mService.getmPosition();
        }
        return 0;
    }

    public String getSelect(){
        if (mService != null){
            return mService.mselect;
        }
        return null;
    }


}
