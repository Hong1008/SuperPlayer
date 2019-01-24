package com.example.zxcbn.superplayer;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends Activity{
    CheckBox changer;
    LinearLayout lplayer,lequalizer;
    ListView musicList;
    Button eqsave,eqclear,eq0,looping;
    TextView mTitle,maintitle,playtime,fulltime,first,last,repeat,getBand1,getBand2,getBand3,getBand4,getBand5,range1,range2,range3,range4,range5;
    SeekBar band60,band230,band910,band3600,band14000,playstate,base;
    Equalizer equalizer;
    int mid;
    int sessionid;
    short level0,level1,level2,level3,level4;
    OptionDB optionDB;
    SQLiteDatabase sql;
    PlayThread pt;
    SimpleDateFormat time;
    RepeatThread rt;
    int firstTime, lastTime;
    Intent intent;
    BassBoost bass;
    ImageButton play,forward,rewind;
    Music music;
    MusicAdapter adapter;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI();
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkpermission();
        changer = (CheckBox)findViewById(R.id.changer);
        lequalizer = (LinearLayout)findViewById(R.id.equalizer);
        lplayer = (LinearLayout)findViewById(R.id.player);
        musicList = (ListView)findViewById(R.id.lv1);
        play = (ImageButton) findViewById(R.id.play);
        forward = (ImageButton)findViewById(R.id.forward);
        rewind = (ImageButton)findViewById(R.id.rewind);
        maintitle = (TextView)findViewById(R.id.maintitle);
        mTitle = (TextView)findViewById(R.id.songtitle);
        playtime = (TextView)findViewById(R.id.playtime);
        fulltime = (TextView)findViewById(R.id.fulltime);
        first = (TextView)findViewById(R.id.firstTime);
        last = (TextView)findViewById(R.id.lastTime); 
        repeat = (TextView)findViewById(R.id.repeat); 
        playstate = (SeekBar)findViewById(R.id.playstate);
        eqclear = (Button)findViewById(R.id.eqclear);
        eqsave = (Button)findViewById(R.id.eqsave);
        eq0 = (Button)findViewById(R.id.eq0);
        looping = (Button)findViewById(R.id.looping);
        getBand1 = (TextView)findViewById(R.id.tv1);
        getBand2 = (TextView)findViewById(R.id.tv3);
        getBand3 = (TextView)findViewById(R.id.tv5);
        getBand4 = (TextView)findViewById(R.id.tv7);
        getBand5 = (TextView)findViewById(R.id.tv9);
        range1 = (TextView)findViewById(R.id.tv2);
        range2 = (TextView)findViewById(R.id.tv4);
        range3 = (TextView)findViewById(R.id.tv6);
        range4 = (TextView)findViewById(R.id.tv8);
        range5 = (TextView)findViewById(R.id.tv10);
        band60 = (SeekBar)findViewById(R.id.sk1);
        band230 = (SeekBar)findViewById(R.id.sk2);
        band910 = (SeekBar)findViewById(R.id.sk3);
        band3600 = (SeekBar)findViewById(R.id.sk4);
        band14000 = (SeekBar)findViewById(R.id.sk5);
        base = (SeekBar)findViewById(R.id.base);
        ContentResolver resolver = getContentResolver();
        music = new Music(resolver);
        adapter = new MusicAdapter(this,music);
        musicList.setAdapter(adapter);
        musicList.setItemChecked(0,true);
        time = new SimpleDateFormat("mm:ss");
        musicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AudioApp.getInstance().getServiceInterface().item(i);
                if(AudioApp.getInstance().getServiceInterface().isPlaying()==true){
                    AudioApp.getInstance().getServiceInterface().play(i);
                }
                AudioApp.getInstance().getServiceInterface().play(i);
                loadEq();
                AudioApp.getInstance().getServiceInterface().play();
                pt = new PlayThread(playstate, playtime,maintitle,looping,forward,rewind);
                fulltime.setText(time.format(AudioApp.getInstance().getServiceInterface().getDuration()));
            }
        });
        optionDB = new OptionDB(MainActivity.this);
        try {
            equalizer = null;
            equalizer = new Equalizer(0, 0);
            equalizer.setEnabled(true);
        }catch (UnsupportedOperationException e){
            e.printStackTrace();
        }
        bass = new BassBoost(0,0);
        bass.setStrength((short) 1000);
        bass.setEnabled(true);
        base.setMax((int) bass.getRoundedStrength());
        base.setProgress((int) bass.getRoundedStrength());
        repeat.setBackgroundColor(Color.LTGRAY);
        getBand1.setText(String.valueOf(equalizer.getBandLevel((short) 0)));
        getBand2.setText(String.valueOf(equalizer.getBandLevel((short) 1)));
        getBand3.setText(String.valueOf(equalizer.getBandLevel((short) 2)));
        getBand4.setText(String.valueOf(equalizer.getBandLevel((short) 3)));
        getBand5.setText(String.valueOf(equalizer.getBandLevel((short) 4)));
        band60.setProgress(equalizer.getBandLevel((short) 0)+1500);
        band230.setProgress(equalizer.getBandLevel((short) 1)+1500);
        band910.setProgress(equalizer.getBandLevel((short) 2)+1500);
        band3600.setProgress(equalizer.getBandLevel((short) 3)+1500);
        band14000.setProgress(equalizer.getBandLevel((short) 4)+1500);
        level0 = equalizer.getBandLevel((short) 0);
        level1 = equalizer.getBandLevel((short) 1);
        level2 = equalizer.getBandLevel((short) 2);
        level3 = equalizer.getBandLevel((short) 3);
        level4 = equalizer.getBandLevel((short) 4);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AudioApp.getInstance().getServiceInterface().isPlaying()==false) {
                    loadEq();
                    AudioApp.getInstance().getServiceInterface().play();
                    pt = new PlayThread(playstate, playtime,maintitle,looping,forward,rewind);
                    fulltime.setText(time.format(AudioApp.getInstance().getServiceInterface().getDuration()));
                }else{
                    AudioApp.getInstance().getServiceInterface().pause();
                }
            }
        });
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioApp.getInstance().getServiceInterface().forward();
                loadEq();
                pt = new PlayThread(playstate, playtime,maintitle,looping,forward,rewind);
                fulltime.setText(time.format(AudioApp.getInstance().getServiceInterface().getDuration()));
            }
        });
        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioApp.getInstance().getServiceInterface().rewind();
                loadEq();
                pt = new PlayThread(playstate, playtime,maintitle,looping,forward,rewind);
                fulltime.setText(time.format(AudioApp.getInstance().getServiceInterface().getDuration()));
            }
        });
        playstate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    AudioApp.getInstance().getServiceInterface().seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstTime = AudioApp.getInstance().getServiceInterface().getCurrentPosition();
                first.setText(time.format(firstTime).toString());

            }
        });
        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastTime = AudioApp.getInstance().getServiceInterface().getCurrentPosition();
                last.setText(time.format(lastTime).toString());
            }
        });
        repeat.setTextColor(Color.BLACK);
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firstTime==0||lastTime==0){
                    Toast.makeText(MainActivity.this, "시작지점과 종료지점을 지정해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(repeat.getText().equals("구간반복")){
                    repeat.setText("반복중");
                    rt = new RepeatThread(firstTime,lastTime);
                    rt.run();
                }else if(repeat.getText().equals("반복중")) {
                    repeat.setText("구간반복");
                    rt.power();
                }
            }
        });
        band60.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                equalizer.setBandLevel((short) 0, (short) (i-1500));
                getBand1.setText(String.valueOf(equalizer.getBandLevel((short) 0)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                level0 = equalizer.getBandLevel((short) 0);
            }
        });
        band230.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                equalizer.setBandLevel((short) 1, (short) (i-1500));
                getBand2.setText(String.valueOf(equalizer.getBandLevel((short) 1)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                level1 = equalizer.getBandLevel((short) 1);
            }
        });
        band910.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                equalizer.setBandLevel((short) 2, (short) (i-1500));
                getBand3.setText(String.valueOf(equalizer.getBandLevel((short) 2)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                level2 = equalizer.getBandLevel((short) 2);
            }
        });
        band3600.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                equalizer.setBandLevel((short) 3, (short) (i-1500));
                getBand4.setText(String.valueOf(equalizer.getBandLevel((short) 3)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                level3 = equalizer.getBandLevel((short) 3);
            }
        });
        band14000.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                equalizer.setBandLevel((short) 4, (short) (i-1500));
                getBand5.setText(String.valueOf(equalizer.getBandLevel((short) 4)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                level4 = equalizer.getBandLevel((short) 4);
            }
        });
        base.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bass.setStrength((short) progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        changer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b==true){
                    lplayer.setVisibility(View.INVISIBLE);
                    lequalizer.setVisibility(View.VISIBLE);
                }else{
                    lplayer.setVisibility(View.VISIBLE);
                    lequalizer.setVisibility(View.INVISIBLE);
                }
            }
        });
        eqsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sql = optionDB.getWritableDatabase();
                Cursor cursor1;
                cursor1 = sql.rawQuery("SELECT mid FROM OptionDB;",null);
                sql.execSQL("INSERT OR REPLACE INTO OptionDB VALUES ( "+mid+" , "+level0+" , "+level1+" , "
                        +level2+" , "+level3+" , "+level4+",'"+AudioApp.getInstance().getServiceInterface().getSelect()+"' );");
                Log.i("query","INSERT OR REPLACE INTO OptionDB VALUES ( "+mid+" , "+level0+" , "+level1+" , "
                        +level2+" , "+level3+" , "+level4+" );");
                sql.close();
                Toast.makeText(MainActivity.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        eqclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sql = optionDB.getWritableDatabase();
                Toast.makeText(MainActivity.this, "모든 설정이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                optionDB.onUpgrade(sql,1,2);
                sql.close();
            }
        });
        eq0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                equalizer.setBandLevel((short) 0,(short) 0);
                equalizer.setBandLevel((short) 1,(short) 0);
                equalizer.setBandLevel((short) 2,(short) 0);
                equalizer.setBandLevel((short) 3,(short) 0);
                equalizer.setBandLevel((short) 4,(short) 0);
                getBand1.setText(String.valueOf(equalizer.getBandLevel((short) 0)));
                getBand2.setText(String.valueOf(equalizer.getBandLevel((short) 1)));
                getBand3.setText(String.valueOf(equalizer.getBandLevel((short) 2)));
                getBand4.setText(String.valueOf(equalizer.getBandLevel((short) 3)));
                getBand5.setText(String.valueOf(equalizer.getBandLevel((short) 4)));
                band60.setProgress(equalizer.getBandLevel((short) 0)+1500);
                band230.setProgress(equalizer.getBandLevel((short) 1)+1500);
                band910.setProgress(equalizer.getBandLevel((short) 2)+1500);
                band3600.setProgress(equalizer.getBandLevel((short) 3)+1500);
                band14000.setProgress(equalizer.getBandLevel((short) 4)+1500);
            }
        });
        looping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(looping.getText().toString().equals("한곡반복")){
                    looping.setText("전체반복");
                }else if(looping.getText().toString().equals("전체반복")){
                    looping.setText("뒤로반복");
                }else{
                    looping.setText("한곡반복");
                }
            }
        });
        registerBroadcast();
        updateUI();
    }

    private void loadEq(){
        sessionid = AudioApp.getInstance().getServiceInterface().getSessionId();
        try {
            equalizer = new Equalizer(0, sessionid);
            equalizer.setEnabled(true);
        }catch (UnsupportedOperationException e){
            e.printStackTrace();
        }
        sql = optionDB.getReadableDatabase();
        Cursor cursor1;
        mid = AudioApp.getInstance().getServiceInterface().getPosition();
        cursor1 = sql.rawQuery("SELECT * FROM OptionDB where mid = "+mid+";",null);
        if(cursor1.moveToFirst()){
            level0 = (short) cursor1.getInt(1);
            level1 = (short) cursor1.getInt(2);
            level2 = (short) cursor1.getInt(3);
            level3 = (short) cursor1.getInt(4);
            level4 = (short) cursor1.getInt(5);
            Log.i("load bandlevel : ",String.valueOf(level0)+","+String.valueOf(level1)+","+String.valueOf(level2)
                    +","+String.valueOf(level3)+","+String.valueOf(level4));
            cursor1.close();
            sql.close();
            equalizer.setBandLevel((short) 0,level0);
            equalizer.setBandLevel((short) 1,level1);
            equalizer.setBandLevel((short) 2,level2);
            equalizer.setBandLevel((short) 3,level3);
            equalizer.setBandLevel((short) 4,level4);
            getBand1.setText(String.valueOf(equalizer.getBandLevel((short) 0)));
            getBand2.setText(String.valueOf(equalizer.getBandLevel((short) 1)));
            getBand3.setText(String.valueOf(equalizer.getBandLevel((short) 2)));
            getBand4.setText(String.valueOf(equalizer.getBandLevel((short) 3)));
            getBand5.setText(String.valueOf(equalizer.getBandLevel((short) 4)));
            band60.setProgress(equalizer.getBandLevel((short) 0)+1500);
            band230.setProgress(equalizer.getBandLevel((short) 1)+1500);
            band910.setProgress(equalizer.getBandLevel((short) 2)+1500);
            band3600.setProgress(equalizer.getBandLevel((short) 3)+1500);
            band14000.setProgress(equalizer.getBandLevel((short) 4)+1500);
        }else{
            equalizer.setBandLevel((short) 0,(short) 0);
            equalizer.setBandLevel((short) 1,(short) 0);
            equalizer.setBandLevel((short) 2,(short) 0);
            equalizer.setBandLevel((short) 3,(short) 0);
            equalizer.setBandLevel((short) 4,(short) 0);
            getBand1.setText(String.valueOf(equalizer.getBandLevel((short) 0)));
            getBand2.setText(String.valueOf(equalizer.getBandLevel((short) 1)));
            getBand3.setText(String.valueOf(equalizer.getBandLevel((short) 2)));
            getBand4.setText(String.valueOf(equalizer.getBandLevel((short) 3)));
            getBand5.setText(String.valueOf(equalizer.getBandLevel((short) 4)));
            band60.setProgress(equalizer.getBandLevel((short) 0)+1500);
            band230.setProgress(equalizer.getBandLevel((short) 1)+1500);
            band910.setProgress(equalizer.getBandLevel((short) 2)+1500);
            band3600.setProgress(equalizer.getBandLevel((short) 3)+1500);
            band14000.setProgress(equalizer.getBandLevel((short) 4)+1500);
            level0 = (short) 0;
            level1 = (short) 0;
            level2 = (short) 0;
            level3 = (short) 0;
            level4 = (short) 0;
        }
    }

    private void checkpermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1111);
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(this).setTitle("알림").setMessage("권한 거부").setNeutralButton("설정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).create().show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1000:
                for(int i=0; i<grantResults.length; i++){
                    if(grantResults[i] < 0){
                        Toast.makeText(MainActivity.this, "저장소 권한 활성화 필요", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                break;
        }
    }

    private void registerBroadcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastActions.PREPARED);
        filter.addAction(BroadcastActions.PLAY_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver,filter);
    }

    private void unregisterBroadcast(){
        unregisterReceiver(mBroadcastReceiver);
    }

    private void updateUI() {
        if (AudioApp.getInstance().getServiceInterface().isPlaying()) {
            play.setImageResource(R.drawable.pause);
        } else {
            play.setImageResource(R.drawable.play);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
    }
}
