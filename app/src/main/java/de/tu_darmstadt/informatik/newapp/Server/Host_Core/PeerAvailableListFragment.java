package de.tu_darmstadt.informatik.newapp.Server.Host_Core;

import android.app.Activity;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.tu_darmstadt.informatik.newapp.R;
import de.tu_darmstadt.informatik.newapp.SQLiteDBHelper;
import de.tu_darmstadt.informatik.newapp.Utils.Util;
import de.tu_darmstadt.informatik.newapp.WebServer.NanoHTTPD;
import de.tu_darmstadt.informatik.newapp.WebServer.SimpleWebServer;
import de.tu_darmstadt.informatik.newapp.javabeans.User;


/**
 * Created by manna on 24-01-2017.
 */

//Logic via android developer docs for WiFiDirect Connectivity @ android official site

public class PeerAvailableListFragment extends ListFragment implements PeerListListener, ConnectionInfoListener, Handler.Callback
{
    private ServerGroupOwnerSocketHandle thread_for_server;
    private String serverIP_Http = null;
    private File webroot = null;

    private View mContentView = null;
    private final Handler handler = new Handler(this);

    private Activity mActivity = null;


    private NanoHTTPD nanoWebServerhttp = null;


    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    public static final int Server_Port = 1512;
    //112 is as same as the callback in ServerGOSocketHandle.java
    public static final int CB_SVR = 112;

    private ProgressDialog p_diag = null;
    private WifiP2pDevice dev_details;

    //Defining Interface

    public interface PeerAvailable_FragListener
    {

        void musicPlayerDetails(WifiP2pDevice device);

        void musicPlayerInfo(WifiP2pInfo info);

        void stopDisconnect();

        void connect(WifiP2pConfig config);

        void leave();

        void searchDevices();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        this.setListAdapter(new WiFiPeerListAdapter(getActivity(),
                R.layout.connected_devices_details_host, peers));
        mActivity = getActivity();

        //Create a directory for the webserver

        webroot = mActivity.getApplicationContext().getFilesDir();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        mContentView = inflater.inflate(R.layout.already_connected_peers_host, null);

        return mContentView;
    }


    @Override
    public void onDestroyView()
    {
        stop_HttpServer();
        super.onDestroyView();
    }




    private static String getDeviceStatus(int deviceStatus)
    {

        switch (deviceStatus)
        {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";
        }
    }


    public WifiP2pDevice getDevice()
    {
        return dev_details;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        WifiP2pDevice wifidevice_object = (WifiP2pDevice) getListAdapter().getItem(
                position);

        switch (wifidevice_object.status)
        {
            case WifiP2pDevice.AVAILABLE:

                if (p_diag != null && p_diag.isShowing())
                {
                    p_diag.dismiss();
                }

                p_diag = ProgressDialog.show(getActivity(), "To cancel tap back", "Creating Connection: " + wifidevice_object.deviceName, true, true);
                //Logic from official andorid Developer forum
                WifiP2pConfig settings = new WifiP2pConfig();
                settings.deviceAddress = wifidevice_object.deviceAddress;
                settings.wps.setup = WpsInfo.PBC;
                //The Server doesnt need to connect to Clients. Need to comment out this
                ((PeerAvailable_FragListener) getActivity()).connect(settings);
                break;

            case WifiP2pDevice.INVITED:
                if (p_diag != null && p_diag.isShowing())
                {
                    p_diag.dismiss();
                }

                p_diag = ProgressDialog.show(getActivity(), "To cancel tap back", "Cancelling Connection: " + wifidevice_object.deviceName, true, true);

                ((PeerAvailable_FragListener) getActivity()).stopDisconnect();

                ((PeerAvailable_FragListener) getActivity()).searchDevices();
                break;

            case WifiP2pDevice.CONNECTED:
                if (p_diag != null && p_diag.isShowing())
                {
                    p_diag.dismiss();
                }

                p_diag = ProgressDialog.show(getActivity(),
                        "To cancel tap back", "Cancelling Connection: "
                                + wifidevice_object.deviceName, true, true);

                ((PeerAvailable_FragListener) getActivity()).leave();

                ((PeerAvailable_FragListener) getActivity()).searchDevices();
                break;


            case WifiP2pDevice.FAILED:
            case WifiP2pDevice.UNAVAILABLE:
            default:
                ((PeerAvailable_FragListener) getActivity()).searchDevices();
                break;
        }

        ((PeerAvailable_FragListener) getActivity()).musicPlayerDetails(wifidevice_object);
    }


    private class WiFiPeerListAdapter extends ArrayAdapter<WifiP2pDevice>
    {
        private List<WifiP2pDevice> items;


        public WiFiPeerListAdapter(Context context, int textViewResourceId,
                                   List<WifiP2pDevice> objects)
        {
            super(context, textViewResourceId, objects);
            items = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View v = convertView;

            if (v == null)
            {
                LayoutInflater vi = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.connected_devices_details_host, null);
            }

            WifiP2pDevice device = items.get(position);


            if (device != null)
            {
                TextView top = (TextView) v.findViewById(R.id.peer_name);
                TextView bottom = (TextView) v
                        .findViewById(R.id.peer_details);
                if (top != null)
                {
                    top.setText(device.deviceName);
                }
                if (bottom != null)
                {

                    if (device.status == WifiP2pDevice.INVITED)
                    {
                        if (p_diag != null
                                && p_diag.isShowing())
                        {
                            p_diag.dismiss();
                        }

                        p_diag = ProgressDialog.show(getActivity(),
                                        "Inviting peer",
                                        "Sent invitation to: "
                                                + device.deviceName
                                                + "\n\nTap on peer to revoke invitation.",
                                        true, true);
                    }
                    bottom.setText(getDeviceStatus(device.status));
                }
            }

            return v;
        }
    }


    public void myDeviceDetails(WifiP2pDevice details)
    {
        this.dev_details = details;
        TextView view = (TextView) mContentView.findViewById(R.id.my_name);
        view.setText(dev_details.deviceName);
        view = (TextView) mContentView.findViewById(R.id.my_status);
        view.setText(getDeviceStatus(dev_details.status));
        ImageView imageView = (ImageView) mContentView.findViewById(android.R.id.icon);

        String device_id = Settings.Secure.getString(this.getActivity().getContentResolver(),Settings.Secure.ANDROID_ID);
        SQLiteDBHelper sqLiteDBHelper = new SQLiteDBHelper(this.getActivity());
        User user=new User();
        user=getUserDetails(device_id, sqLiteDBHelper);

        if(user != null && user.getImageData() !=null){
            Bitmap currentBitmap= BitmapFactory.decodeByteArray(user.getImageData(), 0, user.getImageData().length);
            imageView.setImageBitmap(currentBitmap);
        }else{
            imageView.setImageResource(R.drawable.profile_icon);
        }


    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList)
    {
        if (p_diag != null && p_diag.isShowing())
        {
            p_diag.dismiss();
        }
        peers.clear();
        peers.addAll(peerList.getDeviceList());
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
        if (peers.size() == 0)
        {

            return;
        }
    }

    public void clearPeers()
    {
        peers.clear();
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public boolean handleMessage(Message message)
    {
        switch (message.what)
        {
            case CB_SVR:
                thread_for_server = (ServerGroupOwnerSocketHandle) message.obj;

                break;

            default:

                break;
        }
        return true;
    }


    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifi_p2p)
    {
        if (p_diag != null && p_diag.isShowing())
        {
            p_diag.dismiss();
        }


        ((PeerAvailable_FragListener) getActivity()).musicPlayerInfo(wifi_p2p);


        if (wifi_p2p.groupFormed && wifi_p2p.isGroupOwner)
        {
            try
            {

                if (this.thread_for_server == null)
                {
                    Thread host = new ServerGroupOwnerSocketHandle(this.handler);
                    host.start();

                    if (webroot != null)
                    {
                        if (nanoWebServerhttp == null)
                        {
                            serverIP_Http = wifi_p2p.groupOwnerAddress.getHostAddress();

                            boolean quiet = false;

                            nanoWebServerhttp = new SimpleWebServer(serverIP_Http, Server_Port, webroot, quiet);
                            try
                            {
                                nanoWebServerhttp.start();

                                Toast.makeText(mContentView.getContext(),
                                        "Host Started",
                                        Toast.LENGTH_SHORT).show();
                            }
                            catch (IOException ioe)
                            {
                                ioe.printStackTrace();
                            }
                        }
                    }
                    else
                    {

                    }
                }
            }
            catch (IOException e)
            {

            }
        }
        else if (wifi_p2p.groupFormed)
        {


            Toast.makeText(mContentView.getContext(),
                    "Host is not group owner",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void stop_HttpServer()
    {
        if (thread_for_server != null)
        {
            thread_for_server.serverDisconnect();
            thread_for_server = null;
        }
        if (nanoWebServerhttp != null)
        {
            nanoWebServerhttp.stop();
            nanoWebServerhttp = null;
        }
    }
    public void sendPlayListtoClient(String playList)
    {
        if (thread_for_server != null)
        {

            thread_for_server.sendClientPlaylist(playList);
        }

    }

    //Changes for voting manna: March 2017
    public void getVotefromClient()
    {
        if (thread_for_server != null)
        {

            thread_for_server.getVote_FromClient();
        }

    }

    //Change for checking new Music : March 2017

    public void checkClientMusicAdd()
    {
        if (thread_for_server != null)
        {

            thread_for_server.getNew_Music();
        }

    }

    public void OrderClientMusicPlay(File song, long beginTime, int beginPosition)
    {
        if (thread_for_server == null)
        {

            return;
        }

        try
        {


            File webFile = new File(webroot, song.getName());
            //copy file to hosting directory of WebServer : manna Jan 2017
            Util.makeFileCopy(song, webFile);
            String name = webFile.getName().toString().replaceAll("\\s","");
            Uri webMusicURI = Uri.parse("http://" + serverIP_Http + ":"
                    + String.valueOf(Server_Port) + "/" + name);

            thread_for_server.clientCanPlay(webMusicURI.toString(), beginTime, beginPosition);
        }
        catch (IOException e1)
        {

        }
    }

    public void OrderClientMusicPlay2(String clientHosting, long beginTime, int beginPosition)
    {
        if (thread_for_server == null)
        {

            return;
        }






            thread_for_server.clientCanPlay(clientHosting, beginTime, beginPosition);



    }

    public void orderClientMusicStop()
    {
        if (thread_for_server != null)
        {
            thread_for_server.clientCanStop();
        }
    }
    public User getUserDetails(String device_id, SQLiteDBHelper sqLiteDBHelper) {
        User user=new User();
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

        return user;
    }

 public void create_ClientHttp()
    {
        if (thread_for_server != null)
        {
            thread_for_server.createClientHttp();
        }
    }
}
