package de.tu_darmstadt.informatik.newapp.Client.Clubber_Core;

import android.app.Activity;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.tu_darmstadt.informatik.newapp.Client.uiClubber;
import de.tu_darmstadt.informatik.newapp.R;
import de.tu_darmstadt.informatik.newapp.Server.Host_Core.ServerGroupOwnerSocketHandle;
import de.tu_darmstadt.informatik.newapp.SQLiteDBHelper;
import de.tu_darmstadt.informatik.newapp.javabeans.User;

/**
 * Created by manna on 25-01-2017.
 */

public class ClubberDevicesFragList extends ListFragment implements WifiP2pManager.PeerListListener, WifiP2pManager.ConnectionInfoListener, Handler.Callback {


  private ClubberSocketHandle thread_Clubber;
  private View mContentView = null;


  private Activity mActivity = null;


  private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

  private ProgressDialog p_diag = null;
  private WifiP2pDevice dev_details;

  private final Handler h = new Handler(this);

  //Defining Interface for Linking Events : manna Dec 2016

  public interface PeerAvailable_FragListener {

    void musicPlayerDetails(WifiP2pDevice device);

    void musicPlayerInfo(WifiP2pInfo info);

    void stopDisconnect();

    void connect(WifiP2pConfig config);

    void leave();

    void searchDevices();
    void playtheSong(String url, long startTime, int position);
    void stop_Playing();
    void updatePlayList(String playList);
    public void createHttpServerClient(String ipaddr);
  }


  //Handling request from Sockets Logic from Android Developer forum for UI and String Tokenizers : manna Dec 2016
  @Override
  public boolean handleMessage(Message message) {

    switch (message.what)
    {
      case ClubberSocketHandle.message_recieve_event:
        byte[] buff_store = (byte[]) message.obj;
        String str = new String(buff_store);

        String[] orderfromHost = str.split(ServerGroupOwnerSocketHandle.SEPERATOR);

        if (orderfromHost[0].equals(ServerGroupOwnerSocketHandle.MUSIC_PLAY) && orderfromHost.length > 3)
        {
          try
          {
            ((PeerAvailable_FragListener) getActivity()).playtheSong(orderfromHost[1], Long.parseLong(orderfromHost[2]), Integer.parseInt(orderfromHost[3]));
          }
          catch (NumberFormatException e)
          {

          }
        }
        if (orderfromHost[0].equals(ServerGroupOwnerSocketHandle.MUSIC_STOP) && orderfromHost.length > 0)
        {
          ((PeerAvailable_FragListener) getActivity()).stop_Playing();
        }
        if (orderfromHost[0].equals(ServerGroupOwnerSocketHandle.MUSIC_LIST) && orderfromHost.length > 0) {
          ((PeerAvailable_FragListener) getActivity()).updatePlayList(orderfromHost[1]);
        }
        if (orderfromHost[0].equals(ServerGroupOwnerSocketHandle.CREATE_CLIENT_HTTP) && orderfromHost.length > 0) {

          Log.d("Msg",orderfromHost[1].substring(2));
          String clientIP=orderfromHost[1].toString();
          int start=clientIP.indexOf('/');
          int end = clientIP.indexOf(',');
          uiClubber.setClient_IP(clientIP.substring(start+1,end));
          ((PeerAvailable_FragListener) getActivity()).createHttpServerClient(uiClubber.getClient_IP());
        }

        break;

      case ClubberSocketHandle.CL_CB:
        thread_Clubber = (ClubberSocketHandle) message.obj;

        break;

      default:

        break;
    }
    return true;
  }


  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    this.setListAdapter(new ClubberDevicesFragList.WiFiPeerListAdapter(getActivity(),
      R.layout.connected_devices__details_clubbers, peers));
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    mContentView = inflater.inflate(R.layout.already_connected_peers_clubbers, null);

    return mContentView;
  }


  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }


  private static String getDeviceStatus(int deviceStatus) {

    switch (deviceStatus) {
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


  public WifiP2pDevice getDevice() {
    return dev_details;
  }


  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    WifiP2pDevice wifidevice_object = (WifiP2pDevice) getListAdapter().getItem(
      position);

    switch (wifidevice_object.status) {
      case WifiP2pDevice.AVAILABLE:

        if (p_diag != null && p_diag.isShowing()) {
          p_diag.dismiss();
        }

        p_diag = ProgressDialog.show(getActivity(), "To cancel tap back", "Creating Connection: " + wifidevice_object.deviceName, true, true);
        //Logic from official andorid Developer forum
        WifiP2pConfig settings = new WifiP2pConfig();
        settings.deviceAddress = wifidevice_object.deviceAddress;
        settings.wps.setup = WpsInfo.PBC;
        ((ClubberDevicesFragList.PeerAvailable_FragListener) getActivity()).connect(settings);
        break;

      case WifiP2pDevice.INVITED:
        if (p_diag != null && p_diag.isShowing()) {
          p_diag.dismiss();
        }

        p_diag = ProgressDialog.show(getActivity(), "To cancel tap back", "Cancelling Connection: " + wifidevice_object.deviceName, true, true);

        ((ClubberDevicesFragList.PeerAvailable_FragListener) getActivity()).stopDisconnect();

        ((ClubberDevicesFragList.PeerAvailable_FragListener) getActivity()).searchDevices();
        break;

      case WifiP2pDevice.CONNECTED:
        if (p_diag != null && p_diag.isShowing()) {
          p_diag.dismiss();
        }

        p_diag = ProgressDialog.show(getActivity(),
          "To cancel tap back", "Cancelling Connection: "
            + wifidevice_object.deviceName, true, true);

        ((ClubberDevicesFragList.PeerAvailable_FragListener) getActivity()).leave();

        ((ClubberDevicesFragList.PeerAvailable_FragListener) getActivity()).searchDevices();
        break;


      case WifiP2pDevice.FAILED:
      case WifiP2pDevice.UNAVAILABLE:
      default:
        ((ClubberDevicesFragList.PeerAvailable_FragListener) getActivity()).searchDevices();
        break;
    }

    ((ClubberDevicesFragList.PeerAvailable_FragListener) getActivity()).musicPlayerDetails(wifidevice_object);
  }


  private class WiFiPeerListAdapter extends ArrayAdapter<WifiP2pDevice> {
    private List<WifiP2pDevice> items;


    public WiFiPeerListAdapter(Context context, int textViewResourceId,
                               List<WifiP2pDevice> objects) {
      super(context, textViewResourceId, objects);
      items = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View v = convertView;

      if (v == null) {
        LayoutInflater vi = (LayoutInflater) getActivity()
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(R.layout.connected_devices_details_host, null);
      }

      WifiP2pDevice device = items.get(position);


      if (device != null) {
        TextView top = (TextView) v.findViewById(R.id.peer_name);
        TextView bottom = (TextView) v
          .findViewById(R.id.peer_details);
        if (top != null) {
          top.setText(device.deviceName);
        }
        if (bottom != null) {

          if (device.status == WifiP2pDevice.INVITED) {
            if (p_diag != null
              && p_diag.isShowing()) {
              p_diag.dismiss();
            }

            p_diag = ProgressDialog.show(getActivity(),
              "Connecting to",
              "Device: "
                + device.deviceName
                + "\n\nTouch to cancel",
              true, true);
          }
          bottom.setText(getDeviceStatus(device.status));
        }
      }

      return v;
    }


  }
  public void myDeviceDetails(WifiP2pDevice details) {
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
  public void onConnectionInfoAvailable(WifiP2pInfo wifi_p2p) {
    if (p_diag != null && p_diag.isShowing()) {
      p_diag.dismiss();
    }


    ((PeerAvailable_FragListener) getActivity()).musicPlayerInfo(wifi_p2p);

    if (wifi_p2p.groupFormed && wifi_p2p.isGroupOwner) {


      Toast.makeText(mContentView.getContext(),
        "Clubber is Group Owner",
        Toast.LENGTH_SHORT).show();
    } else if (wifi_p2p.groupFormed) {
      if (this.thread_Clubber == null) {
        Thread clubber = new ClubberSocketHandle(this.h, wifi_p2p.groupOwnerAddress);
        clubber.start();
      }

      Toast.makeText(mContentView.getContext(),
        "Clubber Ready", Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void onPeersAvailable(WifiP2pDeviceList peerList) {
    if (p_diag != null && p_diag.isShowing()) {
      p_diag.dismiss();
    }
    peers.clear();
    peers.addAll(peerList.getDeviceList());
    ((ClubberDevicesFragList.WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
    if (peers.size() == 0) {

      return;
    }
  }

  public void clearPeers() {
    peers.clear();
    ((ClubberDevicesFragList.WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
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

}

