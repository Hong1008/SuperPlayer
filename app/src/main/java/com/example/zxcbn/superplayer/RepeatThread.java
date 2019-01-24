package com.example.zxcbn.superplayer;

import android.media.MediaPlayer;
import android.os.SystemClock;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by zxcbn on 2018-05-10.
 */

public class RepeatThread extends Thread {
    int power = 0;
    int current;
    boolean isPlaying;
    public RepeatThread(final int firstTime, final int lastTime){
        final SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
        new Thread(){
            @Override
            public void run() {
                try{
                    isPlaying = AudioApp.getInstance().getServiceInterface().isPlaying();
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(isPlaying==false) return;
                try{
                    AudioApp.getInstance().getServiceInterface().seekTo(firstTime);
                }catch (Exception e){
                    e.printStackTrace();
                }
                while (isPlaying==true){
                    if(power==1) return;
                    try{
                        current = AudioApp.getInstance().getServiceInterface().getCurrentPosition();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if (timeFormat.format(current).equals(timeFormat.format(lastTime))){
                        try{
                            AudioApp.getInstance().getServiceInterface().seekTo(firstTime);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }SystemClock.sleep(200);
            }
        }.start();
    }
    public void power(){
        power = 1;
    }
}
