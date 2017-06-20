package de.tu_darmstadt.informatik.newapp.Server;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import de.tu_darmstadt.informatik.newapp.R;
import de.tu_darmstadt.informatik.newapp.SQLiteDBHelper;
import de.tu_darmstadt.informatik.newapp.Server.Host_Core.PeerAvailableListFragment;
import de.tu_darmstadt.informatik.newapp.Server.Host_Core.WiFiServerBroadCastReciever;
import de.tu_darmstadt.informatik.newapp.javabeans.User;

/**
 * This program is the main activity for the Host UI
 * it includes three tabs as Fragments (Manage Party, Playlists and Now Playing)
 *
 * Created by Rashmi on 11/16/2016.
 * Updated by Parvez on 11/27/2016.
 * Functionality Integration manna 02/27/2017
 */


public class uiHost extends AppCompatActivity implements TabLayout.OnTabSelectedListener, WifiP2pManager.ChannelListener, PeerAvailableListFragment.PeerAvailable_FragListener{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Pager adapter;

    //WiFiDirect members
    private boolean isWifiSet = false;
    public WifiP2pManager mManager;
    public WifiP2pManager.Channel mChannel;
    public WiFiServerBroadCastReciever mReceiver;
    public IntentFilter mIntentFilter;
    private boolean chn_is_restricted = false;
    ProgressDialog prog_diag = null;

    //Voting Members: manna march 2017
    public static boolean vote_index_available=false;
    public static int vote_index=0;

public static ArrayList<String> remote_Songs= new ArrayList<String>();
  User user;
  SQLiteDBHelper sqLiteDBHelper;
  private String device_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
      device_id = Settings.Secure.getString(this.getContentResolver(),
        Settings.Secure.ANDROID_ID);
      user = new User();
      sqLiteDBHelper = new SQLiteDBHelper(this);

      getUserDetails();

        // get action bar
        final ActionBar actionBar = getSupportActionBar();
      getSupportActionBar().setTitle("Hello " + user.getUserName());
        //getActionBar();

        // Enabling Up / Back navigation
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarHost);
        //setSupportActionBar(toolbar);

        tabLayout = (TabLayout) findViewById(R.id.tabLayoutHost);
        tabLayout.addTab(tabLayout.newTab().setText("Manage Party"));
        tabLayout.addTab(tabLayout.newTab().setText("Playlists"));
        tabLayout.addTab(tabLayout.newTab().setText("Now Playing"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.vPagerHost);
        adapter = new Pager(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);

        tabLayout.setOnTabSelectedListener(this);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //WiFiDirect Init : manna Feb 2017

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiServerBroadCastReciever(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();

        // need these intent filters to catch the Wi-fi direct events
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
    }

  //Tabbed Layout Settings updated : manna Feb 2017
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


    //WiFiDirect Integration : manna Feb 2017

    @Override
    public void onResume()
    {
        super.onResume();
        mReceiver = new WiFiServerBroadCastReciever(mManager, mChannel, this);
        registerReceiver(mReceiver, mIntentFilter);

        searchDevices();

    }

    public void wifiEnable()
    {
        WifiManager wim = (WifiManager) this.getSystemService(this.WIFI_SERVICE);

        wim.setWifiEnabled(true);
    }


    public void onInitiateDiscovery()
    {
        if (prog_diag != null && prog_diag.isShowing())
        {
            prog_diag.dismiss();
        }

        prog_diag = ProgressDialog.show(this, "Press Back to cancel",
                "finding peers", true, true,
                new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        // stop discovery
                        mManager.stopPeerDiscovery(mChannel,
                                new WifiP2pManager.ActionListener()
                                {
                                    @Override
                                    public void onFailure(int reason)
                                    {
                                    }

                                    @Override
                                    public void onSuccess()
                                    {

                                    }
                                });
                    }
                });
    }

    @Override
    public void searchDevices()
    {

        wifiEnable();

        chn_is_restricted = false;

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener()
        {
            @Override
            public void onSuccess()
            {

            }

            @Override
            public void onFailure(int reasonCode)
            {

                mManager.stopPeerDiscovery(mChannel,
                        new WifiP2pManager.ActionListener()
                        {
                            @Override
                            public void onFailure(int reason)
                            {

                            }

                            @Override
                            public void onSuccess()
                            {
                                mManager.discoverPeers(mChannel,
                                        new WifiP2pManager.ActionListener()
                                        {
                                            @Override
                                            public void onSuccess()
                                            {

                                            }

                                            @Override
                                            public void onFailure(int reasonCode)
                                            {

                                            }
                                        });
                            }
                        });
            }
        });
    }

    @Override
    public void onPause()
    {
        super.onPause();
        unregisterReceiver(mReceiver);

    }

    @Override
    public void onDestroy()
    {
        leave();

        super.onDestroy();
    }


    public void resetData()
    {
        PeerAvailableListFragment fragmentList = (PeerAvailableListFragment) getFragmentManager().findFragmentById(R.id.device_list_frag_host);

        if (fragmentList != null)
        {
            fragmentList.clearPeers();
        }
    }

    @Override
    public void onChannelDisconnected()
    {

        if (mManager != null && !chn_is_restricted)
        {
            Toast.makeText(this, "Wi-fi Direct Channel lost. Trying again...",
                    Toast.LENGTH_LONG).show();
            resetData();

            chn_is_restricted = true;
            mManager.initialize(this, getMainLooper(), this);
        }
        else
        {
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
    public void stopDisconnect()
    {

        if (mManager != null)
        {


            final PeerAvailableListFragment fragment = (PeerAvailableListFragment) getFragmentManager().findFragmentById(R.id.device_list_frag_host);

            if (fragment.getDevice() == null
                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED)
            {

            }
            else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
                    || fragment.getDevice().status == WifiP2pDevice.INVITED
                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED)
            {
                mManager.cancelConnect(mChannel, new WifiP2pManager.ActionListener()
                {
                    @Override
                    public void onSuccess()
                    {

                    }

                    @Override
                    public void onFailure(int reasonCode)
                    {

                    }
                });
            }
        }
    }


    @Override
    public void connect(WifiP2pConfig config)
    {
        if (mManager == null)
        {
            return;
        }

        WifiP2pConfig newConfig = config;
        newConfig.groupOwnerIntent = 15;

        mManager.connect(mChannel, newConfig, new WifiP2pManager.ActionListener()
        {
            @Override
            public void onSuccess()
            {

            }

            @Override
            public void onFailure(int reason)
            {
                Toast.makeText(uiHost.this, "Retrying Connection", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void leave()
    {
        if (mManager == null)
        {
            return;
        }

        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener()
        {
            @Override
            public void onFailure(int reasonCode)
            {


            }

            @Override
            public void onSuccess()
            {
                Toast.makeText(uiHost.this, "One Device Disconnected",
                        Toast.LENGTH_SHORT).show();

            }
        });
    }


    //MusicPlayer and other Important Functions : manna Dec 2016
    public void sendPlayList(String playList)
    {
        PeerAvailableListFragment palf = (PeerAvailableListFragment) getFragmentManager().findFragmentById(R.id.device_list_frag_host);
        palf.sendPlayListtoClient(playList);
    }

    //Update Playlist with new Songs from Clients: manna March 2017
    public void getNewSongs(){
        PeerAvailableListFragment palf = (PeerAvailableListFragment) getFragmentManager().findFragmentById(R.id.device_list_frag_host);
        palf.checkClientMusicAdd();

    }

    //MusicPlayer voting function for polling : manna March 2017
    public void pollVote()
    {
        PeerAvailableListFragment palf = (PeerAvailableListFragment) getFragmentManager().findFragmentById(R.id.device_list_frag_host);
        palf.getVotefromClient();
    }

    public void playMusicOnPeers(String musicFilePath, long startTime, int startPos)
    {
        if(musicFilePath.contains("http://"))
        {
            PeerAvailableListFragment palf = (PeerAvailableListFragment) getFragmentManager().findFragmentById(R.id.device_list_frag_host);
            palf. OrderClientMusicPlay2(musicFilePath, startTime, startPos);
        }
        else {
            File song = new File(musicFilePath);
            PeerAvailableListFragment palf = (PeerAvailableListFragment) getFragmentManager().findFragmentById(R.id.device_list_frag_host);
            palf.OrderClientMusicPlay(song, startTime, startPos);
        }
    }

    public void stopMusicOnPeers()
    {
        PeerAvailableListFragment palf = (PeerAvailableListFragment) getFragmentManager().findFragmentById(R.id.device_list_frag_host);
        palf.orderClientMusicStop();
    }
    public void createClientHttpPorts()
    {
        PeerAvailableListFragment palf = (PeerAvailableListFragment) getFragmentManager().findFragmentById(R.id.device_list_frag_host);
        palf.create_ClientHttp();
    }

    public void setIsWifiSet(boolean isWifiSet)
    {
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


