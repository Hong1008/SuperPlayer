package com.example.zxcbn.superplayer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by zxcbn on 2018-05-29.
 */

public class MusicAdapter extends BaseAdapter {
    Context context;
    Music music;

    public MusicAdapter(Context context, Music music) {
        this.context = context;
        this.music = music;
    }

    @Override
    public int getCount() {
        return music.arrayaid.size();
    }

    @Override
    public Object getItem(int position) {
        return music.arrayaid.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = View.inflate(context,R.layout.custom,null);
        }
        TextView songtitle = (TextView)convertView.findViewById(R.id.songtitle);
        TextView songartist = (TextView)convertView.findViewById(R.id.songartist);
        TextView songduration = (TextView)convertView.findViewById(R.id.songduration);
        SimpleDateFormat time = new SimpleDateFormat("mm:ss");
        songtitle.setText(music.arrayname.get(position));
        songartist.setText(music.arrayartist.get(position));
        String du = time.format(Integer.parseInt(music.arrayduration.get(position)));
        songduration.setText(du);
        return convertView;
    }
}
