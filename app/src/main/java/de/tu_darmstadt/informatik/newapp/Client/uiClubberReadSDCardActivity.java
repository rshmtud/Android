package de.tu_darmstadt.informatik.newapp.Client;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.tu_darmstadt.informatik.newapp.R;
import de.tu_darmstadt.informatik.newapp.SQLiteDBHelper;
import de.tu_darmstadt.informatik.newapp.Client.SongCustomAdapter;
import de.tu_darmstadt.informatik.newapp.Client.SongsManager;
import de.tu_darmstadt.informatik.newapp.Server.uiHostReadSDCardActivity;
import de.tu_darmstadt.informatik.newapp.Utils.Util;

/**
 * Activity for reading all the songs from SD card
 * And populating them in Listview
 *
 * Created by Manna on 08-02-2017.
 */

public class uiClubberReadSDCardActivity extends ActionBarActivity {

  private ListView lView;
  private Button btnSelectSong, btnCancel;


  private ArrayList<Song> songsList;
  private SongCustomAdapter songCustomAdapter;
  private HashMap<String, String> choosenSongs;
  private String songPath;
  private String songName;



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.uiclubber_allsongs_sdcard);

    lView = (ListView) findViewById(R.id.songList);
    btnSelectSong = (Button) findViewById(R.id.btnCreatePlaylist);
    btnCancel = (Button) findViewById(R.id.btnCancel);



    btnSelectSong.setText("Select Song");

    choosenSongs = new HashMap<String, String>();


    displaySongsList();

    btnSelectSong.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(final View view) {

        choosenSongs = songCustomAdapter.getSelectedSongs();

        if (choosenSongs.size() == 0) {

          Toast toast = Toast.makeText(getApplicationContext(), "No songs selected!", Toast.LENGTH_LONG);
          toast.setGravity(Gravity.CENTER, 0, 0);
          toast.show();
        }

        Log.d("hashMap content",choosenSongs.toString());

        for(Map.Entry m: choosenSongs.entrySet()) {
          Log.d("hashMap PATH",m.getKey().toString());
          Log.d("hashMap NAME",m.getValue().toString());
          songPath = m.getKey().toString();
          songName = m.getValue().toString();
        }
        if(songPath.length()>0){
          if(uiClubber.isClientHostingSongs()){
    try {
      Log.d("ClubberHostingSong","Logger");
      File webFile = new File(uiClubber.getWebroot(), songName);
      //copy file to hosting directory of WebServer : manna Jan 2017
      File song = new File(songPath);
      Util.makeFileCopy(song, webFile);
      String name = webFile.getName().toString().replaceAll("\\s", "");
      Uri webMusicURI = Uri.parse("http://" + uiClubber.getClient_IP() + ":"
              + String.valueOf(uiClubber.getHttpClient_port()) + "/" + name);

      uiClubber.setSong_URL(webMusicURI.toString());

      Log.d("song Url", webMusicURI.toString());

      uiClubberReadSDCardActivity.this.finish();
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }

          }


        }


      }
      });


    btnCancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        uiClubberReadSDCardActivity.this.finish();
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


}
