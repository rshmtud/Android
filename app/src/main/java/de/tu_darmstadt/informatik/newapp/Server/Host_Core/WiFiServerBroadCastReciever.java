package de.tu_darmstadt.informatik.newapp.Server.Host_Core;

/**
 * Created by manna on 04-11-2016.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;

import java.util.ArrayList;

import de.tu_darmstadt.informatik.newapp.R;
import de.tu_darmstadt.informatik.newapp.Server.uiHost;
import de.tu_darmstadt.informatik.newapp.Server.uiManageParty;


/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events. Logic provided in android WiFi Direct Developer's manual
 */


public class WiFiServerBroadCastReciever extends BroadcastReceiver {


    public ArrayList<String> dnames = new ArrayList<String>();
    public ArrayList<String> daddresses = new ArrayList<String>();
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private uiHost mActivity;
    private String status = "";


    public WiFiServerBroadCastReciever(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       uiHost activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
    }



    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {


            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {

                mActivity.setIsWifiSet(true);
            } else {
                mActivity.setIsWifiSet(false);
                mActivity.resetData();

            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {


            if (mManager != null) {
                mManager.requestPeers(mChannel, (PeerListListener) mActivity.getFragmentManager().findFragmentById(R.id.device_list_frag_host));
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            if (mManager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {



                if (networkInfo.isConnected()) {


                    PeerAvailableListFragment palf = (PeerAvailableListFragment) mActivity.getFragmentManager().findFragmentById(R.id.device_list_frag_host);
                    mManager.requestConnectionInfo(mChannel, palf);
                } else {

                    mActivity.resetData();
                }
            }
        }
        else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            PeerAvailableListFragment palf = (PeerAvailableListFragment) mActivity.getFragmentManager().findFragmentById(R.id.device_list_frag_host);
            palf.myDeviceDetails((WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));

        }

    }
}





