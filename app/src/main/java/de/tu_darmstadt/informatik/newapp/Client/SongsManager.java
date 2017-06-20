package de.tu_darmstadt.informatik.newapp.Client;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Parvez on 15-11-2016.
 * Updated by manna 27/02/2017
 */

public class SongsManager {

   // final String MEDIA_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString();

    //Please dont change "Data Structure" : manna Dec 2016
    private ArrayList<HashMap<String, String>> listOfSongs = new ArrayList<HashMap<String, String>>();

    public SongsManager(){

    }
    /**
     * Function to fetch all mp3 files from sdcard
     * and store their name and location in ArrayList
     * */



    //Loigc for path in internal storage reffered from Android Developer forums : manna Dec 2016
    public ArrayList<HashMap<String, String>> getPlayList(Context c)
    {
        final Cursor mCursor = c.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,new String[] { MediaStore.MediaColumns.TITLE, MediaStore.MediaColumns.DATA, MediaStore.Audio.AudioColumns.ALBUM }, null, null,
                "LOWER(" + MediaStore.MediaColumns.TITLE + ") ASC");


        String songTitle = "";
        String songPath = "";
        if (mCursor.moveToFirst()) {
            do {
                songTitle = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE));
                songPath = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                HashMap<String, String> song = new HashMap<String, String>();
                song.put("songTitle", songTitle);
                song.put("songPath", songPath);
                listOfSongs.add(song);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return listOfSongs;
    }


    /**
     * Filter files with .mp3 extension
     * */
    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }
}
