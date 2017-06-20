package de.tu_darmstadt.informatik.newapp.Client;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import de.tu_darmstadt.informatik.newapp.SQLiteDBHelper;

import de.tu_darmstadt.informatik.newapp.Client.Clubber_Core.ClubberDevicesFragList;
import de.tu_darmstadt.informatik.newapp.Client.Clubber_Core.MusicPlayerFragmentClubber;
import de.tu_darmstadt.informatik.newapp.Client.Clubber_Core.WiFiClubberBroadCastReciever;
import de.tu_darmstadt.informatik.newapp.R;
import de.tu_darmstadt.informatik.newapp.javabeans.User;

/**
 * This program is the main activity for the Clubber UI
 * it includes three tabs as Fragments (Check Party, and Now Playing)
 *
 * Created by Rashmi on 11/16/2016.
 * Functionality Integration manna 02/27/2017
 */

public class uiClubber extends AppCompatActivity implements TabLayout.OnTabSelectedListener, WifiP2pManager.ChannelListener, ClubberDevicesFragList.PeerAvailable_FragListener,SensorEventListener
{
    public String MUSIC_LIST_SEPERATOR = "%%%";
    private boolean isWifiSet = false;
    public WifiP2pManager mManager;
    public WifiP2pManager.Channel mChannel;
    public WiFiClubberBroadCastReciever mReceiver;
    public IntentFilter mIntentFilter;
    ListView listView;
    //hostSongLocally(String IP);
    //Check flag for Already hosted
    private static boolean ClientHostingSongs=false;
    public static boolean isClientHostingSongs() {
        return ClientHostingSongs;
    }

    public static void setClientHostingSongs(boolean clientHostingSongs) {
        ClientHostingSongs = clientHostingSongs;
    }


    private static int vote_index;
    //Adding new song to the playList
    public static String song_URL;

    private static int HttpClient_port;
    public static int getHttpClient_port() {
        return HttpClient_port;
    }

    public static void setHttpClient_port(int httpClient_port) {
        HttpClient_port = httpClient_port;
    }

    private static File webroot;
    public static File getWebroot() {
        return webroot;
    }

    public static void setWebroot(File webroot) {
        uiClubber.webroot = webroot;
    }



    private static String Client_IP;
    public static String getClient_IP() {
        return Client_IP;
    }

    public static void setClient_IP(String client_IP) {
        Client_IP = client_IP;
    }


    public static String getSong_URL() {
        return song_URL;
    }

    public static void setSong_URL(String song_URL) {
        uiClubber.song_URL = song_URL;
    }


    //Clubber voting changes: Manna march 2017
    public static int getVote_index() {
        return vote_index;
    }

    public void setVote_index(int vote_index) {
        this.vote_index = vote_index;
    }


    private boolean chn_is_restricted = false;
    ProgressDialog prog_diag = null;


    //Tabbed Layout Details
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Pager_Client adapter;

    //Music Player
    private MusicPlayerFragmentClubber songPlay;
    private uiClientNowPlaying play_ListClubber;


    User user;
    SQLiteDBHelper sqLiteDBHelper;
    private String device_id;

    //========= Get Sensor Manager --> Tushar =================
    private String serverIP;
    private int portNumber;

    //private TextView xAxisVal, yAxisVal, zAxisVal, musicOnVal, clubberDancingVal;
    private Sensor acceleroSensor;
    private SensorManager sensorManager;
    //Audio Manager
    private android.media.AudioManager audioManager;

    //Last Time user Danced
    public String lastDanceTime = null;
    public long lastSensorReadingTime;
    //Music ON
    public boolean musicOnVal = false;

    public String songName = null; //Use this to check if the user is voting for the same track
    public List<String> lastVotedSongNamesList = new ArrayList<String>();
    public boolean songPushedByHost = false;
    //======================= Start Reading Accelerometer Data from this Activity (Tushar) =============
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        //Not currently in use
    }
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event.values.length > 0)
        {
            //Check if music is on
            audioManager = (AudioManager)this.getSystemService(android.content.Context.AUDIO_SERVICE);
            if (audioManager.isMusicActive())
            {
                musicOnVal = true; //Update var

                //Check if user is dancing
                boolean userDancing = checkIfClubberIsDancing(event.values[0], event.values[1], event.values[2]);
                if (userDancing == true)
                {
                    //Get Current Time
                    lastSensorReadingTime = System.nanoTime();//Start Time
                    lastDanceTime = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss").format(Calendar.getInstance().getTime());
                    //Toast.makeText(getActivity().getBaseContext(),"You moved at: "+ lastDanceTime +"",Toast.LENGTH_LONG).show();
                }
                else
                {
                    //Toast.makeText(getActivity().getBaseContext(),"You are not dancing",Toast.LENGTH_LONG).show();
                }

            }
            else
            {
                musicOnVal = false; //Update var
                ////Toast.makeText(getActivity().getBaseContext(),"No music is playing on your device!",Toast.LENGTH_LONG).show();
            }

        }
    }

    //Check if clubber is dancing --> Tushar
    public boolean checkIfClubberIsDancing(float x, float y, float z)
    {
        boolean result = false;
        if (x < 0){ x = x * (-1);}
        if (y < 0){ x = x * (-1);}
        if (z < 0){ x = x * (-1);}

        if (x >= 11 || y >= 11 || z >= 11){ result = true;}
        return result;
    }

//    //Get Server Connection
//    public void setServerDetails(String serverIP, int serverPort)
//    {
//        serverIP = serverIP;
//        portNumber = serverPort;
//    }
    //============================ END Sensor Reading =============================


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clubber);

      device_id = Settings.Secure.getString(this.getContentResolver(),
        Settings.Secure.ANDROID_ID);
      user = new User();
      sqLiteDBHelper = new SQLiteDBHelper(this);


      getUserDetails();
        // get action bar
        ActionBar actionBar = getSupportActionBar();
        //getActionBar();

        // Enabling Up / Back navigation
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Hello " +user.getUserName());

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarHost);
        //setSupportActionBar(toolbar);

        tabLayout = (TabLayout) findViewById(R.id.tabLayoutClubber);
        tabLayout.addTab(tabLayout.newTab().setText("Check Party"));
        tabLayout.addTab(tabLayout.newTab().setText("Now Playing"));
//        tabLayout.addTab(tabLayout.newTab().setText("Activity Area"));

      tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.vPagerClubber);
        adapter = new Pager_Client(getSupportFragmentManager(), tabLayout.getTabCount());
//        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);

        tabLayout.setOnTabSelectedListener(this);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        //WiFiDirect Clubber

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiClubberBroadCastReciever(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();


        // need these intent filters to catch the Wi-fi direct events
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        //Music Player

        songPlay = new MusicPlayerFragmentClubber();

        play_ListClubber = new uiClientNowPlaying();

        //============ Initialize Sensor Manager --> Tushar ===================
        //When the user starts playing the song, the accelerometer should start reading movement data --> Tushar
        //Create Sensor Manager
        sensorManager = (SensorManager)this.getSystemService(Context.SENSOR_SERVICE);

        //Accelerometer Sensor
        acceleroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //Register sensor listener
        sensorManager.registerListener(this, acceleroSensor, SensorManager.SENSOR_DELAY_UI);//0.06 sec delay
        //=====================================

    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    //WiFiDirect Functions : manna Feb 2017

    @Override
    public void onResume() {
        super.onResume();
        mReceiver = new WiFiClubberBroadCastReciever(mManager, mChannel, this);
        registerReceiver(mReceiver, mIntentFilter);

        searchDevices();

    }

    public void wifiEnable() {
        WifiManager wim = (WifiManager) this.getSystemService(this.WIFI_SERVICE);

        wim.setWifiEnabled(true);
    }


    public void onInitiateDiscovery() {
        if (prog_diag != null && prog_diag.isShowing()) {
            prog_diag.dismiss();
        }

        prog_diag = ProgressDialog.show(this, "Press Back to cancel",
                "finding peers", true, true,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // stop discovery
                        mManager.stopPeerDiscovery(mChannel,
                                new WifiP2pManager.ActionListener() {
                                    @Override
                                    public void onFailure(int reason) {
                                    }

                                    @Override
                                    public void onSuccess() {

                                    }
                                });
                    }
                });
    }

    @Override
    public void searchDevices() {

        wifiEnable();

        chn_is_restricted = false;

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reasonCode) {

                mManager.stopPeerDiscovery(mChannel,
                        new WifiP2pManager.ActionListener() {
                            @Override
                            public void onFailure(int reason) {

                            }

                            @Override
                            public void onSuccess() {
                                mManager.discoverPeers(mChannel,
                                        new WifiP2pManager.ActionListener() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onFailure(int reasonCode) {

                                            }
                                        });
                            }
                        });
            }
        });
    }

    @Override
    public void playtheSong(String url, long timing, int pos)
    {

        songPlay.startPlaying(url,timing,pos);

    }
    @Override
    public void createHttpServerClient(String ipaddr)
    {
        Log.d("createHttpServerClient",ipaddr);
        if(getClient_IP().length()>6) {
            webroot = getApplicationContext().getFilesDir();
            songPlay.hostSongLocally(ipaddr);
        }
    }
    @Override
    public void stop_Playing()
    {
        songPlay.stopPlaying();
    }
    @Override
    public void updatePlayList(String playList)
    {
        //===============Tushar (March 2017 ========================
        // play_ListClubber.setPlayLists(playList);
        final String[] songs = playList.split(MUSIC_LIST_SEPERATOR);
        //Click Listener for the list
        AdapterView.OnItemClickListener mMessageClickedHandler =
                new AdapterView.OnItemClickListener()
                {
                    //On item click on List View, get the song name and call Voting method --> Tushar
                    public void onItemClick(AdapterView parent, View v, int position, long id)
                    {
                        //Get the name of the song that is selected from the listview
                        songName = (String) parent.getItemAtPosition(position);
                        //Call the voting function here
                        voteSong(parent, position);
                    }
                };

        ListView listView = (ListView)findViewById(R.id.playListClubber);
        listView.setOnItemClickListener(mMessageClickedHandler);
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songs);
        listView.setAdapter(itemsAdapter);

        //==============================================================

       // play_ListClubber.setPlayLists(playList);

//        String[] songs = playList.split(MUSIC_LIST_SEPERATOR);
//       // String listforadapter[] = new String[songs.length];
//        //for(int i=0;i<songs.length;i++)
//        //{
//         //   listforadapter[i]=songs[i];
//            //Log.d("SongName:",songs[i]);
//        //}
//
//        listView = (ListView)findViewById(R.id.playListClubber);
//        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songs);
//        listView.setAdapter(itemsAdapter);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            try {
//                setVote_index(position);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    });
    }

    // -- Tushar (February 2017)
    public void voteSong(AdapterView parent, int currentSongPosition)
    {
        respondToClickVoting(parent, currentSongPosition);//Pass in Current View
    }

    //Respond to voting request for the next track --> Tushar
    public void respondToClickVoting(AdapterView parent, int CurrentPosition)
    {
        //Calculate Elapsed Time from last dance moves
        long elapsedTime = System.nanoTime() - lastSensorReadingTime;
        double elapsedSeconds = (double)elapsedTime / 1000000000.0;
        if (elapsedSeconds < 60)
        {
            //Check if the clubber has already voted for the track!
            if (songName != null && !lastVotedSongNamesList.contains(songName))
            {
                //At this stage, we have to send the position of the song back to HOST
                //Implement Server Code Here
                try
                {
                    setVote_index(CurrentPosition);//Set the position and wait for server to poll
                }
                catch (Exception e)
                {
                e.printStackTrace();
                }

                Toast.makeText(this,"Request sent to Host for : "+ songName +"",
                        Toast.LENGTH_SHORT).show();


                lastVotedSongNamesList.add(songName); //Add current song to last voted songs list
                //FFEDFF
                ////parent.getChildAt(currentSongPositionInList).setBackgroundColor(Color.parseColor("#FFEDFF"));//Change Row Colour
                ////parent.getChildAt(currentSongPositionInList).setBackgroundColor(Color.BLUE);//Change Row Colour
            }
            else
            {
                Toast.makeText(this,"You already voted for "+ songName+"!",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this,"You have to move your body to Vote! \n Keep dancing :)",
                    Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);

    }

    @Override
    public void onDestroy() {
        leave();

        super.onDestroy();
    }


    public void resetData() {
        ClubberDevicesFragList fragmentList = (ClubberDevicesFragList) getFragmentManager()
                .findFragmentById(R.id.device_list_frag_clubber);

        if (fragmentList != null) {
            fragmentList.clearPeers();
        }
    }

    @Override
    public void onChannelDisconnected() {

        if (mManager != null && !chn_is_restricted) {
            Toast.makeText(this, "Wi-fi Direct Channel lost. Trying again...",
                    Toast.LENGTH_LONG).show();
            resetData();

            chn_is_restricted = true;
            mManager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this, "Channel not found. Enable Disable WiFi", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void musicPlayerDetails(WifiP2pDevice device) {

        //Do mUsic stuff here later

    }

    @Override
    public void musicPlayerInfo(WifiP2pInfo info) {

        //Do music stuff here later

    }

    @Override
    public void stopDisconnect() {

        if (mManager != null) {


            final ClubberDevicesFragList fragment = (ClubberDevicesFragList) getFragmentManager()
                    .findFragmentById(R.id.device_list_frag_clubber);

            if (fragment.getDevice() == null
                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {

            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
                    || fragment.getDevice().status == WifiP2pDevice.INVITED
                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
                mManager.cancelConnect(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(int reasonCode) {

                    }
                });
            }
        }
    }


    @Override
    public void connect(WifiP2pConfig config) {
        if (mManager == null) {
            return;
        }

        WifiP2pConfig newConfig = config;
        newConfig.groupOwnerIntent = 1;

        mManager.connect(mChannel, newConfig, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(uiClubber.this,
                        "Retrying Connection", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void leave() {
        if (mManager == null) {
            return;
        }

        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onFailure(int reasonCode) {


            }

            @Override
            public void onSuccess() {
                Toast.makeText(uiClubber.this, "One Device Disconnected",
                        Toast.LENGTH_SHORT).show();

            }
        });
    }


    public void setIsWifiSet(boolean isWifiSet) {
        this.isWifiSet = isWifiSet;
    }

  public void getUserDetails() {
    try{
      Cursor res = sqLiteDBHelper.readUserData(device_id);
      if (res.getCount() > 0) {
        while (res.moveToNext()) {
          user.setFirstName(res.getString(2));
          user.setUserName(res.getString(3));
          user.setEmail(res.getString(4));
          user.setImageData(res.getBlob(5));
          user.setRegistrationStatus(res.getInt(6));
        }


      }
    }catch(Exception e){
      e.printStackTrace();
    }


  }
}

