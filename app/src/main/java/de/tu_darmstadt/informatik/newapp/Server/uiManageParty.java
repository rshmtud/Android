package de.tu_darmstadt.informatik.newapp.Server;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;

import de.tu_darmstadt.informatik.newapp.R;
import de.tu_darmstadt.informatik.newapp.Server.Host_Core.PeerAvailableListFragment;
import de.tu_darmstadt.informatik.newapp.Server.Host_Core.WiFiServerBroadCastReciever;

import static android.os.Looper.getMainLooper;


/**
 * This program is the 1st tab ie. 'Manage Party' of uiHost
 * Shows the Device details and connected peers
 *
 * Created by manna on 28-02-2017.
 */

public class uiManageParty extends Fragment {

    private View rView;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {


        rView = inflater.inflate(R.layout.host_resource,container,false);
        return rView;
    }


}



