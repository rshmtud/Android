package de.tu_darmstadt.informatik.newapp.Server;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.tu_darmstadt.informatik.newapp.R;
import de.tu_darmstadt.informatik.newapp.SQLiteDBHelper;

/**
 * Activity for reading all the songs from SD card
 * And populating them in Listview
 *
 * Created by Parvez on 23-11-2016.
 */

public class uiHostReadSDCardActivity extends ActionBarActivity {

    private ListView lView;
    private Button btnCreatePlaylist, btnCancel;

    private String playListName;
    private ArrayList<Song> songsList;
    private SongCustomAdapter songCustomAdapter;
    private SQLiteDBHelper dbHelper;
    private HashMap<String, String> choosenSongs;
    private HashMap<String, String> choosenSongsCopy;
    private EditText inputText;
    private AlertDialog.Builder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uihost_allsongs_sdcard);

        lView = (ListView) findViewById(R.id.songList);
        btnCreatePlaylist = (Button) findViewById(R.id.btnCreatePlaylist);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        inputText = (EditText) findViewById(R.id.inputPlaylistName);
        dialogBuilder = new AlertDialog.Builder(uiHostReadSDCardActivity.this);
        dbHelper = new SQLiteDBHelper(this);

        btnCreatePlaylist.setText("Create Playlist");

        choosenSongs = new HashMap<String, String>();
        choosenSongsCopy = new HashMap<String, String>();

        displaySongsList();

        btnCreatePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                choosenSongs = songCustomAdapter.getSelectedSongs();

                if (choosenSongs.size() == 0 && playListName == null) {

                    Toast toast = Toast.makeText(getApplicationContext(), "No songs selected!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {

                    if (inputText.getVisibility() != View.VISIBLE) {
                        inputText.setVisibility(View.VISIBLE);
                    }

                    ((ViewGroup)inputText.getParent()).removeView(inputText);

                    dialogBuilder.setTitle("Playlist Name:");
                    dialogBuilder.setView(inputText);

                    dialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            playListName = inputText.getText().toString();
                            Cursor curs = dbHelper.getIdOfPlaylist(playListName);

                            if (curs.getCount() == 0) {

                                if (choosenSongsCopy.size() > 0) {
                                    choosenSongs.putAll(choosenSongsCopy);
                                }
                                dialog.dismiss();
                                createPlaylist(playListName, choosenSongs);
                                uiHostReadSDCardActivity.this.finish();
                            } else {
                                if (choosenSongs.size() > 0) {
                                    choosenSongsCopy.putAll(choosenSongs);
                                }
                                dialog.dismiss();
                                Toast toast = Toast.makeText(getApplicationContext(), "Playlist exists! Choose another name.", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        }
                    });

                    dialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    dialogBuilder.create().show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uiHostReadSDCardActivity.this.finish();
            }
        });
    }

    /**
     * Method to populate all the songs from SD card
     * and bind them into Listview
     */

    private void displaySongsList() {

        songsList = new ArrayList<Song>();
        ArrayList<HashMap<String, String>> allSongsList = new ArrayList<HashMap<String, String>>();
        SongsManager sm = new SongsManager();
        allSongsList = sm.getPlayList(getApplicationContext());

        if (allSongsList!= null && allSongsList.size() >0 ) {
            for (int i = 0; i < allSongsList.size(); i++) {
                HashMap<String, String> songs = allSongsList.get(i);

                if (songs!= null && songs.size()>0) {
                    songsList.add(new Song(songs.get("songTitle").toString(), songs.get("songPath").toString()));
                }
            }
        }

        songCustomAdapter = new SongCustomAdapter(songsList, this);
        lView.setAdapter(songCustomAdapter);
    }

    /**
     * Method to create a playlist
     * @param name This is the name of the playlist
     * @param songs List of songs to be inserted to playlist
     */

    public void createPlaylist(String name, HashMap<String, String> songs) {

        if (songCustomAdapter != null && name != null) {
            if (songs != null) {
                if (songs.size() > 0) {

                    boolean insertSuccessful = false;

                    try {
                        insertSuccessful = dbHelper.insertToPlaylistTable(name);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    int playListid = 0;

                    if (insertSuccessful) {
                        Cursor cur = dbHelper.getIdOfPlaylist(name);
                        if (cur.getCount()!=0) {
                            while (cur.moveToNext()) {
                                playListid = cur.getInt(0);
                            }
                        }
                    }

                    Set set = songs.entrySet();
                    Iterator itr = set.iterator();

                    while (itr.hasNext()) {

                        Map.Entry mEntry = (Map.Entry) itr.next();
                        try {

                            dbHelper.insertToSongsTable(playListid, mEntry.getKey().toString(), mEntry.getValue().toString());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}