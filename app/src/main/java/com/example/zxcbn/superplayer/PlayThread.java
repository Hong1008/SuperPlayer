package com.example.zxcbn.superplayer;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;

/**
 * Created by zxcbn on 2018-05-09.
 */

public class PlayThread extends Activity{
    int duration, current;
    boolean isPlaying;

    public PlayThread(final SeekBar seekBar, final TextView textView,final TextView title, final Button button, final ImageButton forward, final ImageButton rewind){
        new Thread(){
            SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
            @Override
            public void run() {
                try{
                    duration = AudioApp.getInstance().getServiceInterface().getDuration();
                    isPlaying = AudioApp.getInstance().getServiceInterface().isPlaying();
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (isPlaying==false) return;
                seekBar.setMax(duration);
                while(isPlaying==true){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                current = AudioApp.getInstance().getServiceInterface().getCurrentPosition();
                                title.setText(AudioApp.getInstance().getServiceInterface().getSelect());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            seekBar.setProgress(current);
                            String s = timeFormat.format(current).toString();
                            textView.setText(s);
                            if (seekBar.getProgress()==seekBar.getMax()){
                                if(button.getText().toString().equals("전체반복")) {
                                    forward.callOnClick();
                                }else if(button.getText().toString().equals("뒤로반복")){
                                    rewind.callOnClick();
                                }
                            }
                        }
                    });SystemClock.sleep(50);
                }
            }
        }.start();
    }
}
