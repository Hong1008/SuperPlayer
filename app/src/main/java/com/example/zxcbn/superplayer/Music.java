package com.example.zxcbn.superplayer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * Created by zxcbn on 2018-05-29.
 */

public class Music {
    ArrayList<String> arrayid;
    ArrayList<String> arrayname;
    ArrayList<String> arraypath;
    ArrayList<String> arrayaid;
    ArrayList<String> arrayartist;
    ArrayList<String> arrayduration;

    public Music(ContentResolver resolver) {
        arrayid = new ArrayList<String>();
        arrayname = new ArrayList<String>();
        arraypath = new ArrayList<String>();
        arrayaid = new ArrayList<String>();
        arrayartist = new ArrayList<String>();
        arrayduration = new ArrayList<String>();
        String GENRE_ID      = MediaStore.Audio.Genres._ID;
        String GENRE_NAME    = MediaStore.Audio.Genres.NAME;
        String SONG_ID       = android.provider.MediaStore.Audio.Media._ID;
        String SONG_TITLE    = android.provider.MediaStore.Audio.Media.TITLE;
        String SONG_ARTIST   = android.provider.MediaStore.Audio.Media.ARTIST;
        String SONG_ALBUM    = android.provider.MediaStore.Audio.Media.ALBUM;
        String SONG_YEAR     = android.provider.MediaStore.Audio.Media.YEAR;
        String SONG_TRACK_NO = android.provider.MediaStore.Audio.Media.TRACK;
        String SONG_FILEPATH = android.provider.MediaStore.Audio.Media.DATA;
        String SONG_DURATION = android.provider.MediaStore.Audio.Media.DURATION;
        String SONG_ALBUMID = MediaStore.Audio.Media.ALBUM_ID;
        String ALBUM_ART = MediaStore.Audio.Albums.ALBUM_ART;
        String ALBUM_ID = MediaStore.Audio.Albums.ALBUM_ID;

        String[] albumColumns = {
                ALBUM_ART,
                ALBUM_ID
        };

        String[] genreColumns = {
                SONG_ID,
                SONG_TITLE,
                SONG_FILEPATH,
                SONG_ALBUMID,
                SONG_ARTIST,
                SONG_DURATION
        };

        final Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,genreColumns, null,null,null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            arrayid.add(cursor.getString(0));
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            arrayname.add(cursor.getString(1));
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            arraypath.add(cursor.getString(2));
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            arrayaid.add(cursor.getString(3));
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            arrayartist.add(cursor.getString(4));
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            arrayduration.add(cursor.getString(5));
        cursor.close();
    }
}
