package de.tu_darmstadt.informatik.newapp.Client;



  import android.Manifest;
  import android.content.Intent;
  import android.content.pm.PackageManager;
  import android.database.Cursor;
  import android.os.Bundle;
  import android.support.annotation.Nullable;
  import android.support.design.widget.TabLayout;
  import android.support.v4.app.Fragment;
  import android.support.v4.app.FragmentTransaction;
  import android.support.v4.content.ContextCompat;
  import android.support.v4.view.ViewPager;
  import android.support.v4.widget.SwipeRefreshLayout;
  import android.util.Log;
  import android.view.LayoutInflater;
  import android.view.View;
  import android.view.ViewGroup;
  import android.widget.AdapterView;
  import android.widget.ListAdapter;
  import android.widget.ListView;
  import android.widget.SimpleAdapter;
  import android.widget.TextView;

  import java.util.ArrayList;
  import java.util.HashMap;

  import de.tu_darmstadt.informatik.newapp.R;
  import de.tu_darmstadt.informatik.newapp.SQLiteDBHelper;

/**
 * Created by Parvez on 27-11-2016.
 */

public class uiClubberAddSongs extends Fragment {

  private ListView lvPlaylist;
  private View rView;
  private ListAdapter adapter;
  private TextView songsFromSDCard, totalSongsFromSDCard, createPlaylist;
  private ViewPager vPager;

  private ArrayList<HashMap<String, String>> allSongsList;
  private ArrayList<HashMap<String, String>> playLists;

  private int playListId, READ_EXTERNAL_STORAGE_PERMISSION_CODE;

  private static uiClubberAddSongs instance;

  public uiClubberAddSongs(){}

  public void setplayListId(int d){
    this.playListId = d;
  }
  public int getplayListId(){
    return this.playListId;
  }

  public static synchronized uiClubberAddSongs getInstance(){
    if(instance == null){
      instance = new uiClubberAddSongs();
    }
    return instance;    }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


    rView = inflater.inflate(R.layout.uihost_playlists,container,false);

    songsFromSDCard = (TextView)rView.findViewById(R.id.songsFromSDCard);
    totalSongsFromSDCard = (TextView)rView.findViewById(R.id.totalSongsFromSDCard);
    createPlaylist = (TextView)rView.findViewById(R.id.createPlaylist);
    vPager = (ViewPager) getActivity().findViewById(R.id.vPagerHost);

    boolean hasPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
      == PackageManager.PERMISSION_GRANTED;

    if(!hasPermission) {
      requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION_CODE);
    }

    allSongsList = new ArrayList<HashMap<String, String>>();
    SongsManager songMgr = new SongsManager();
    allSongsList = songMgr.getPlayList(getActivity());

    songsFromSDCard.setText("Local Songs");
    totalSongsFromSDCard.setText(allSongsList.size()+" songs");

    songsFromSDCard.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        Intent intent = new Intent(getContext(), uiClubberReadSDCardActivity.class);
        startActivity(intent);
      }
    });

    totalSongsFromSDCard.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        Intent intent = new Intent(getContext(), uiClubberReadSDCardActivity.class);
        startActivity(intent);
      }
    });

    createPlaylist.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        Intent intent = new Intent(getContext(), uiClubberReadSDCardActivity.class);
        startActivity(intent);

      }
    });

    adapter = new SimpleAdapter(getContext(), getPlaylists(),
      R.layout.uiclubber_playlist_item, new String[] {"playlistName",
      "songCount"}, new int[] { R.id.playListName,
      R.id.noOfSongs });

    lvPlaylist = (ListView)rView.findViewById(R.id.playList);
    lvPlaylist.setAdapter(adapter);

    lvPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        uiClubberAddSongs uiClubber = uiClubberAddSongs.getInstance();
        SQLiteDBHelper dbHelp = new SQLiteDBHelper(getContext());
        Cursor cur = dbHelp.getIdOfPlaylist(playLists.get(i).get("playlistName"));

        int playlistId = 0;

        if (cur.getCount()!=0) {
          while (cur.moveToNext()) {
            playlistId = cur.getInt(0);
          }
        }

        uiClubber.setplayListId(playlistId);

//        TabLayout tabLOut = (TabLayout) getActivity().findViewById(R.id.tabLayoutClubber);
//        tabLOut.getTabAt(2).select();
      }
    });

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

    return rView;
  }

  @Override
  public void onResume() {
    super.onResume();

    adapter = new SimpleAdapter(getContext(), getPlaylists(),
      R.layout.uiclubber_playlist_item, new String[] {"playlistName",
      "songCount"}, new int[] { R.id.playListName,
      R.id.noOfSongs });

    lvPlaylist = (ListView)rView.findViewById(R.id.playList);
    lvPlaylist.setAdapter(adapter);
  }

  public ArrayList<HashMap<String, String>> getPlaylists() {

    final SQLiteDBHelper dbHelper = new SQLiteDBHelper(this.getContext());
    final Cursor res = dbHelper.getAllPlaylist();
    playLists =  new ArrayList<HashMap<String, String>>();

    if (res != null) {
      if (res.getCount()!=0) {

        while (res.moveToNext()) {
          HashMap<String, String> playListItem = new HashMap<String, String>();
          playListItem.put("playlistName", res.getString(1));
          SQLiteDBHelper dbHelperObj = new SQLiteDBHelper(this.getContext());
          Cursor cur = dbHelperObj.getIdOfPlaylist(res.getString(1));

          int playlistId = 0;

          if (cur.getCount()!=0) {
            while (cur.moveToNext()) {
              playlistId = cur.getInt(0);
            }
          }
          Cursor curs = dbHelperObj.getAllSongsFromPlaylist(playlistId);
          playListItem.put("songCount", Integer.toString(curs.getCount())+ " songs");

          playLists.add(playListItem);
        }
      }
    }

    return playLists;
  }
}
