package com.example.zxcbn.superplayer;

import android.app.Service;
import android.app.VoiceInteractor;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class Background extends Service{
    private ArrayList<Long> mid = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private boolean isPrepared;
    private int mPosition;
    private final IBinder mBinder = new AudioServiceBinder();
    //private NoficationPlayer noficationPlayer;
    int muid,aid,mduration;
    String mpath,mselect,martist;
    Music music;


    public class AudioServiceBinder extends Binder{
        Background getService(){
            return Background.this;
        }
    }


    @Override
    public void onCreate() {
        android.util.Log.i("서비스테스트","oncreate()");
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isPrepared = true;
                mp.start();
                sendBroadcast(new Intent(BroadcastActions.PREPARED));
                //updateNotificationPlayer();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPrepared = false;
                sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED));
                //updateNotificationPlayer();
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                isPrepared = false;
                sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED));
                //updateNotificationPlayer();
                return false;
            }
        });
        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {

            }
        });
        ContentResolver resolver = getApplicationContext().getContentResolver();
        music = new Music(resolver);
        //noficationPlayer = new NoficationPlayer(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        android.util.Log.i("서비스테스트","onstartcommand()");
       /* if (intent != null) {
            String action = intent.getAction();
            if (NoficationPlayer.CommandActions.TOGGLE_PLAY.equals(action)) {
                if (isPlaying()) {
                    pause();
                } else {
                    play();
                }
            } else if (NoficationPlayer.CommandActions.REWIND.equals(action)) {
                rewind();
            } else if (NoficationPlayer.CommandActions.FORWARD.equals(action)) {
                forward();
                play(mPosition);
                play();
            } else if (NoficationPlayer.CommandActions.CLOSE.equals(action)) {
                pause();
                removeNotificationPlayer();
            }
        }*/

        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        android.util.Log.i("서비스테스트","ondestory()");
        super.onDestroy();
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    /*private void updateNotificationPlayer() {
        if (noficationPlayer != null) {
            noficationPlayer.updateNotificationPlayer();
        }
    }

    private void removeNotificationPlayer() {
        if (noficationPlayer != null) {
            noficationPlayer.removeNotificationPlayer();
        }
    }*/


    public void queryAudioItem(int position){
        mPosition = position;
        mpath = music.arraypath.get(position);
        mselect = music.arrayname.get(position);
    }
    public void prepare(){
        try{
            if(mpath==null){
                Toast.makeText(this, "노래를 선택해 주세요", Toast.LENGTH_SHORT).show();
            }
            mediaPlayer.setDataSource(mpath);
            mediaPlayer.prepare();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void stop(){
        mediaPlayer.stop();
        mediaPlayer.reset();
    }

    public void play(int position){
        queryAudioItem(position);
        stop();
        prepare();
    }

    public void play(){
        if(isPrepared){
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
            sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED));
        }else{
            prepare();
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
            sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED));
        }
        //updateNotificationPlayer();
    }

    public void pause(){
        if(isPrepared){
            mediaPlayer.pause();
            sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED));
            //updateNotificationPlayer();
        }
    }

    public void forward(){
        if(music.arrayaid.size() - 1 > mPosition){
            mPosition++;
        }else{
            mPosition = 0;
        }
        play(mPosition);
        play();
    }

    public void rewind(){
        if(mPosition > 0){
            mPosition--;
        }else{
            mPosition = music.arrayaid.size() - 1;
        }
        play(mPosition);
        play();
    }

    public void seekTo(int position){
        mediaPlayer.seekTo(position);
    }

    public int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    public int getDuration(){
        return mediaPlayer.getDuration();
    }

    public int getSessionId(){
        return mediaPlayer.getAudioSessionId();
    }

    public int getmPosition() {
        return mPosition;
    }

}