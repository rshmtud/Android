package de.tu_darmstadt.informatik.newapp.Server;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import de.tu_darmstadt.informatik.newapp.R;
import de.tu_darmstadt.informatik.newapp.SQLiteDBHelper;

/**
 * This program is the 3rd tab ie. 'Now Playing' of uiHost
 * It populates the songs from the selected Playlist
 * Has necessary functionalities for the Media Player
 * Also synchronizes the song in Clubber's UI
 *
 * Created by Parvez on 15-11-2016.
 * Functionality Integrated for sending song to peers: manna Feb 2017
 */

public class uiHostNowPlaying extends Fragment {

  public static final String MUSIC_LIST_SEPERATOR = "%%%";
  private ArrayList<HashMap<String, String>> allSongsFromDB = new ArrayList<HashMap<String, String>>();
  private String playListToClient="";

  private SQLiteDBHelper dbHelper;
  private View rView2;
  private uiHost mActivity = null;

  private String song_Path="";
  private MediaPlayer mPlayer;
  private TextView mp3Title, duration;
  private double curPosition = 0, totalDuration = 0;
  public int songIndex;
  private Handler dHandler = new Handler();
  private SeekBar seekBar;

  private ListView lvNowPlaying;
  private Button btnPause, btnAddSongs, btnDeletePlaylist;
  private ImageButton btnNext, btnPrevious;
  private ViewPager vPager;



  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    mActivity = (uiHost)getActivity();//Without this we will get null pointer exception : manna Feb 2017

    rView2 = inflater.inflate(R.layout.uihost_nowplaying,container,false);
    //UI changes manna: Mar 2017
    btnPause = (Button)rView2.findViewById(R.id.play_button);

    btnNext = (ImageButton)rView2.findViewById(R.id.next_button);
    btnPrevious = (ImageButton)rView2.findViewById(R.id.prev_button);
    btnAddSongs = (Button)rView2.findViewById(R.id.btnAddSongs);
    btnDeletePlaylist = (Button)rView2.findViewById(R.id.btnDeletePlaylist);
    vPager = (ViewPager) getActivity().findViewById(R.id.vPagerHost);

    startPage();

    if (vPager != null) {

      vPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
          onResume();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
      });
    }

    return rView2;
  }

  @Override
  public void onResume() {
    super.onResume();

    beginPage();
  }

  public void initializeVariables() {

    mp3Title = (TextView) rView2.findViewById(R.id.mp3Title);
    mPlayer = new MediaPlayer();
    duration = (TextView) rView2.findViewById(R.id.mp3Duration);
    seekBar = (SeekBar) rView2.findViewById(R.id.seekBar);
    seekBar.setMax((int) totalDuration);
    seekBar.setClickable(false);
  }

  private Runnable updateSeekBarPos = new Runnable() {
    public void run() {
      curPosition = mPlayer.getCurrentPosition();
      seekBar.setProgress((int) curPosition);
      double remainingTime = totalDuration - curPosition;
      duration.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes((long) remainingTime),
        TimeUnit.MILLISECONDS.toSeconds((long) remainingTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) remainingTime))));
      dHandler.postDelayed(this, 100);
    }
  };

  public void startPage() {
    initializeVariables();
    beginPage();
  }

  //initialize variables only once at startPage()
  public void beginPage() {

    playListToClient ="";
    dbHelper = new SQLiteDBHelper(this.getContext());
    uiHostPlayLists uiHostNowPlaying = uiHostPlayLists.getInstance();
    Cursor res = dbHelper.getAllSongsFromPlaylist(uiHostNowPlaying.getplayListId());
    final ArrayList<HashMap<String, String>> allSongsFromDB = new ArrayList<HashMap<String, String>>();

    if (res.getCount()!=0) {
      while (res.moveToNext()) {

        HashMap<String, String> song = new HashMap<String, String>();
        song.put("songPath", res.getString(2));
        song.put("songTitle", res.getString(3));
        //Adding songs to PlayList to be sent to Client : manna Feb 2017
        playListToClient = playListToClient+res.getString(3) + MUSIC_LIST_SEPERATOR;
        allSongsFromDB.add(song);
      }
    }

    ArrayList<HashMap<String, String>> songsListDataTab2 = new ArrayList<HashMap<String, String>>();

    if (allSongsFromDB!= null && allSongsFromDB.size() !=0 ) {
      for (int i = 0; i < allSongsFromDB.size(); i++) {
        HashMap<String, String> song = allSongsFromDB.get(i);
        songsListDataTab2.add(song);
      }
    }


    //Position of this call can change depending upon how often the playlist should be updated : manna Feb, 2017
    mActivity.sendPlayList(playListToClient);

    ListAdapter adapterTab2 = new SimpleAdapter(getContext(), songsListDataTab2,
      R.layout.uihost_nowplaying_playlist_item, new String[]{"songTitle"}, new int[]{
      R.id.songTitleNowPlaying});

    lvNowPlaying = (ListView) rView2.findViewById(R.id.playListNowPlaying);
    lvNowPlaying.setAdapter(adapterTab2);

    lvNowPlaying.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
          songIndex = position;
          if(mPlayer.isPlaying()) {
            btnPause.setBackgroundResource(R.drawable.play_button);
            mPlayer.stop();
          }
          //Calling the peers connected to play songs : manna Feb 2017
          song_Path = allSongsFromDB.get(songIndex).get("songPath");
          mActivity.playMusicOnPeers(song_Path, 0, 0);

          //Playing on local device
          mPlayer.reset();
          mPlayer.setDataSource(allSongsFromDB.get(songIndex).get("songPath"));
          mp3Title.setText(allSongsFromDB.get(songIndex).get("songTitle"));
          mPlayer.prepare();
          totalDuration = (int) mPlayer.getDuration();
          mPlayer.start();
          btnPause.setBackgroundResource(R.drawable.pause_button);
          curPosition = (int) mPlayer.getCurrentPosition();
          seekBar.setMax((int) totalDuration);
          seekBar.setProgress((int) curPosition);
          dHandler.postDelayed(updateSeekBarPos, 100);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    //Changes for Vote Polling and updating playlist: manna 2017

    mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
      public void onCompletion(MediaPlayer mp) {

        Toast.makeText(getActivity(), "Current Playback Complete. Playing Most Voted Song", Toast.LENGTH_SHORT).show();
        mActivity.pollVote();
        if(mActivity.vote_index_available)
        {
          Log.d("Vote available","True");
          //Calling the peers connected to play songs : manna Feb 2017
          songIndex=mActivity.vote_index;
          song_Path = allSongsFromDB.get(songIndex).get("songPath");
          mActivity.playMusicOnPeers(song_Path, 0, 0);

          try {

            //Playing on local device
            mPlayer.reset();
            mPlayer.setDataSource(allSongsFromDB.get(songIndex).get("songPath"));
            mp3Title.setText(allSongsFromDB.get(songIndex).get("songTitle"));
            mPlayer.prepare();
            totalDuration = (int) mPlayer.getDuration();
            mPlayer.start();
            btnPause.setBackgroundResource(R.drawable.pause_button);
            curPosition = (int) mPlayer.getCurrentPosition();
            seekBar.setMax((int) totalDuration);
            seekBar.setProgress((int) curPosition);
            dHandler.postDelayed(updateSeekBarPos, 100);

          } catch (Exception e) {}

        }
        //Log.d("MediaPlayer","Completed Playing");
      }


    });

    btnPause.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        if(mPlayer.isPlaying()) {
          btnPause.setBackgroundResource(R.drawable.play_button);
          btnPause.animate();
          mActivity.stopMusicOnPeers();
          mPlayer.pause();
        } else {
          mActivity.playMusicOnPeers(song_Path, mPlayer.getDuration(), mPlayer.getCurrentPosition());
          btnPause.animate();
          mPlayer.start();
          btnPause.setBackgroundResource(R.drawable.pause_button);

        }

      }
    });

    btnNext.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        try {
          if(mPlayer.isPlaying()) {
            mActivity.stopMusicOnPeers();
            mPlayer.stop();
          }
          mPlayer.reset();
          mPlayer.setDataSource(allSongsFromDB.get(songIndex+1).get("songPath"));
          mp3Title.setText(allSongsFromDB.get(songIndex+1).get("songTitle"));
          songIndex = songIndex+1;
          mPlayer.prepare();
          totalDuration = (int) mPlayer.getDuration();
          mPlayer.start();
          btnPause.setBackgroundResource(R.drawable.pause_button);
          curPosition = (int) mPlayer.getCurrentPosition();
          seekBar.setMax((int) totalDuration);
          seekBar.setProgress((int) curPosition);
          dHandler.postDelayed(updateSeekBarPos, 100);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    btnPrevious.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        try {
          if(mPlayer.isPlaying()) {
            mActivity.stopMusicOnPeers();
            mPlayer.stop();
          }
          mPlayer.reset();
          mPlayer.setDataSource(allSongsFromDB.get(songIndex-1).get("songPath"));
          mp3Title.setText(allSongsFromDB.get(songIndex-1).get("songTitle"));
          songIndex = songIndex-1;
          mPlayer.prepare();
          totalDuration = (int) mPlayer.getDuration();
          mPlayer.start();
          btnPause.setBackgroundResource(R.drawable.pause_button);
          curPosition = (int) mPlayer.getCurrentPosition();
          seekBar.setMax((int) totalDuration);
          seekBar.setProgress((int) curPosition);
          dHandler.postDelayed(updateSeekBarPos, 100);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    btnAddSongs.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), uiHostEditPlaylistActivity.class);
        startActivity(intent);
      }
    });

    btnDeletePlaylist.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(final View view) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(view.getContext());
        dialogBuilder.setTitle("Are you sure to delete this playlist?");

        dialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

            try {
              if(mPlayer.isPlaying()) {
                mActivity.stopMusicOnPeers();
                mPlayer.stop();
              }
            } catch (Exception e) {
              e.printStackTrace();
            }

            uiHostPlayLists uiHostPlaylist = uiHostPlayLists.getInstance();
            dbHelper.deletePlaylistEntry(uiHostPlaylist.getplayListId());

            //After deleting the playlist, go to tab 2 ie. Playlists tab
            TabLayout tabLOut = (TabLayout) getActivity().findViewById(R.id.tabLayoutHost);
            tabLOut.getTabAt(1).select();
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
    });
  }

}
