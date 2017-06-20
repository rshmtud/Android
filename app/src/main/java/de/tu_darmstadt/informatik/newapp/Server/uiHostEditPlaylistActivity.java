package de.tu_darmstadt.informatik.newapp.Server;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.tu_darmstadt.informatik.newapp.R;
import de.tu_darmstadt.informatik.newapp.SQLiteDBHelper;

/**
 * Created by Parvez on 04-03-2017.
 */

public class uiHostEditPlaylistActivity extends ActionBarActivity {

    private ListView lView;
    private Button btnUpdatePlaylist, btnCancel;

    private String playListName;
    private ArrayList<Song> songsList;
    private SongCustomAdapter songCustomAdapter;
    private SQLiteDBHelper dbHelper;
    private HashMap<String, String> choosenSongs;
    private int playlistId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uihost_allsongs_sdcard);

        lView = (ListView) findViewById(R.id.songList);
        btnUpdatePlaylist = (Button) findViewById(R.id.btnCreatePlaylist);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        btnUpdatePlaylist.setText("Update Playlist");

        choosenSongs = new HashMap<String, String>();

        uiHostPlayLists uiHostNowPlaying = uiHostPlayLists.getInstance();
        playlistId = uiHostNowPlaying.getplayListId();

        displaySongsList();

        btnUpdatePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                choosenSongs = songCustomAdapter.getSelectedSongs();

                if (choosenSongs.size() == 0) {
                    Toast toast = Toast.makeText(getApplicationContext(), "No songs selected!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    updatePlaylist(playlistId);
                    uiHostEditPlaylistActivity.this.finish();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uiHostEditPlaylistActivity.this.finish();
            }
        });
    }

    private void displaySongsList() {

        songsList = new ArrayList<Song>();
        ArrayList<HashMap<String, String>> allSongsList = new ArrayList<HashMap<String, String>>();
        SongsManager sm = new SongsManager();
        allSongsList = sm.getPlayList(getApplicationContext());

        dbHelper = new SQLiteDBHelper(getApplicationContext());
        Cursor res = dbHelper.getAllSongsFromPlaylist(playlistId);
        final ArrayList<HashMap<String, String>> allSongsFromDB = new ArrayList<HashMap<String, String>>();

        if (res.getCount()!=0) {
            while (res.moveToNext()) {

                HashMap<String, String> song = new HashMap<String, String>();
                song.put("songPath", res.getString(2));
                song.put("songTitle", res.getString(3));
                allSongsFromDB.add(song);
            }
        }

        //Remove playlist songs from all songs from SD card to avoid adding duplicate songs to the playlist
        for (HashMap<String, String> songsFromPlaylist : allSongsFromDB) {

            allSongsList.remove(songsFromPlaylist);
        }

        if (allSongsList != null && allSongsList.size() > 0) {
            for (int i = 0; i < allSongsList.size(); i++) {
                HashMap<String, String> songs = allSongsList.get(i);

                if (songs != null && songs.size() > 0) {
                    songsList.add(new Song(songs.get("songTitle").toString(), songs.get("songPath").toString()));
                }
            }
        }

        songCustomAdapter = new SongCustomAdapter(songsList, this);
        lView.setAdapter(songCustomAdapter);
    }

    public void updatePlaylist(int id) {

        if (songCustomAdapter != null && id != 0) {
            if (choosenSongs != null) {
                if (choosenSongs.size() > 0) {

                    Set set = choosenSongs.entrySet();
                    Iterator itr = set.iterator();

                    while (itr.hasNext()) {

                        Map.Entry mEntry = (Map.Entry) itr.next();
                        try {

                            dbHelper.insertToSongsTable(id, mEntry.getKey().toString(), mEntry.getValue().toString());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    if(uiHost.remote_Songs.size()>0)
                    {
                        for(String path:uiHost.remote_Songs)
                        {
                            try {
                                int start =path.lastIndexOf(':');
                                String name=path.substring(start+1);
                                Log.d("SongName",name);

                                dbHelper.insertToSongsTable(id, path.toString(), name);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            }
        }
    }
}