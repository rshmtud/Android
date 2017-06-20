package de.tu_darmstadt.informatik.newapp.Client.Clubber_Core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;

import java.util.ArrayList;

import de.tu_darmstadt.informatik.newapp.Client.uiClubber;
import de.tu_darmstadt.informatik.newapp.R;

/**
 * Created by manna on 25-01-2017.
 */

public class WiFiClubberBroadCastReciever extends BroadcastReceiver {

    public ArrayList<String> dnames = new ArrayList<String>();
    public ArrayList<String> daddresses = new ArrayList<String>();
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private uiClubber mActivity;
    private String status = "";


    public WiFiClubberBroadCastReciever(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                        uiClubber activity) {
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
                mManager.requestPeers(mChannel, (WifiP2pManager.PeerListListener) mActivity.getFragmentManager().findFragmentById(R.id.device_list_frag_clubber));
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            if (mManager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {



                if (networkInfo.isConnected()) {


                    ClubberDevicesFragList salf = (ClubberDevicesFragList) mActivity.getFragmentManager().findFragmentById(R.id.device_list_frag_clubber);
                    mManager.requestConnectionInfo(mChannel, salf);
                } else {

                    mActivity.resetData();
                }
            }
        }
        else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            ClubberDevicesFragList calf = (ClubberDevicesFragList) mActivity.getFragmentManager().findFragmentById(R.id.device_list_frag_clubber);
            calf.myDeviceDetails((WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));

        }

    }

}
