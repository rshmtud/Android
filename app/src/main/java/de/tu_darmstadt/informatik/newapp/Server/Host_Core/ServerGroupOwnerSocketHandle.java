package de.tu_darmstadt.informatik.newapp.Server.Host_Core;



import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.tu_darmstadt.informatik.newapp.R;
import de.tu_darmstadt.informatik.newapp.Server.uiHost;
import de.tu_darmstadt.informatik.newapp.Server.uiHostNowPlaying;
import de.tu_darmstadt.informatik.newapp.Utils.Util;
import de.tu_darmstadt.informatik.newapp.WebServer.NanoHTTPD;

import static de.tu_darmstadt.informatik.newapp.Server.Host_Core.PeerAvailableListFragment.Server_Port;


/**
 * Created by manna on 24-01-2017.
 */

public class ServerGroupOwnerSocketHandle extends Thread {


    private boolean reEstablishConnection=true;
    public static final String SEPERATOR = "##";
    private static final int BUFF_SIZE = 256;
    public static final String MUSIC_PLAY = "SIGNAL_PLAY";
    public static final String MUSIC_STOP = "SIGNAL_STOP";
    public static final String MUSIC_BAL = "SIGNAL_BAL";
    public static final String MUSIC_LIST = "MUSIC_LIST";
    public static final String CREATE_CLIENT_HTTP = "CREATE_CLIENT_HTTP";
    public static final String NEW_SONG = "NEW_SONG";
    //Changes for Voting : manna March 2017
    public static final String POLL_VOTE = "POLL_VOTE";
    //Changes for new songs added: manna March 2017
    public ArrayList<String> more_songs;
    public ArrayList<String> vote_Counter;
    public static final int PORT = 7950;
    public ServerSocket sockHost = null;
    private Handler handler;
    public ArrayList<Socket> clientSocketList;;
    public static final int CB_SVR = 112;
    public boolean recieved_Vote=false;

    private uiHost mActivity;



    //Constructor for initialization

    public ServerGroupOwnerSocketHandle(Handler handler) throws IOException
    {
        this.handler = handler;
        vote_Counter = new ArrayList<String>();
        clientSocketList = new ArrayList<Socket>();
        more_songs = new ArrayList<String>();
        HostCreateSocket();
    }


    @Override
    public void run()
    {
        while (sockHost != null)
        {
            try
            {
                handler.obtainMessage(CB_SVR, this).sendToTarget();
                HostCreateSocket();
                //Accept Client connections
                Socket Client_Socket = sockHost.accept();
                //For vote testing: manna Mar, 2017
                //String toClient = POLL_VOTE+SEPERATOR;

                //orderClient(Client_Socket, toClient);
                //InputfromClient(Client_Socket);
                clientSocketList.add(Client_Socket);
                //Logic for Voting: Manna March, 2017
                AsyncTaskRunner runner = new AsyncTaskRunner(Client_Socket);
                String sleepTime = "5000";
                runner.execute(sleepTime);

            }
            catch (IOException e)
            {

                try
                {
                    if (sockHost != null && !sockHost.isClosed())
                    {
                        reEstablishConnection = true;


                        for (Socket clientsocket : clientSocketList)
                        {
                            clientsocket.close();
                        }

                        clientSocketList = new ArrayList<Socket>();
                    }
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
                break;
            }
        }
    }


    public class AsyncTaskRunner extends AsyncTask<String, String, String> {
        private Socket Client_Socket;
        AsyncTaskRunner(Socket Client_Socket)
        {
            this.Client_Socket=Client_Socket;
        }

        private String resp;

        @Override
        protected String doInBackground(String... params) {
            while (Client_Socket != null) {
                //Opening input and output streams: manna march 2017

                Log.d("Message from Client", "This is might be vote or new URL");
                //Changes for voting : manna 2017
                try {
                    InputStream input_stream = Client_Socket.getInputStream();
                    //OutputStream output_stream = Client_Socket.getOutputStream();

                    byte[] buffer = new byte[BUFF_SIZE];
                    int bytes;

                    bytes = input_stream.read(buffer);
                    if (bytes == -1) {
                        //continue;
                    }

                    String recieve_Message = new String(buffer);
                    Log.d("Client has sent", recieve_Message.trim());
                    String classification = recieve_Message.trim();
                    if (classification.contains("VOTE")) {
                        Log.d("Voting", classification);
                        vote_Counter.add(classification);
                    }
                    //Changes for the Recieved URL from Clients: manna March 2017
                    if (classification.contains("URL")) {
                        if(classification.length()>12) {
                            Log.d("New Song", classification);
                            more_songs.add(classification);
                            uiHost.remote_Songs.add(classification);

                        }
                    }

                }
                catch(IOException e){e.printStackTrace();}
            }
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                int time = Integer.parseInt(params[0])*1000;

                Thread.sleep(time);
                resp = "Slept for " + params[0] + " seconds";
            } catch (InterruptedException e) {
                e.printStackTrace();
                resp = e.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {

        }


        @Override
        protected void onPreExecute() {

        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
    }

  /*  public void InputfromClient(Socket Client_Socket) throws IOException
    {
       while (Client_Socket != null) {
        //Opening input and output streams: manna march 2017

                Log.d("Message from Client", "This is new");
                //Changes for voting : manna 2017

                Log.d("Message from Client", "This is not null");

            InputStream input_stream = Client_Socket.getInputStream();
            OutputStream output_stream = Client_Socket.getOutputStream();

            byte[] buffer = new byte[BUFF_SIZE];
            int bytes;

            bytes = input_stream.read(buffer);
            if (bytes == -1) {
                continue;
            }

            String recieve_Message = new String(buffer);
           Log.d("Client has sent", recieve_Message);

              if(recieve_Message!=null)
                {
                    recieved_Vote=true;
                }
            }
        }*/




    public void HostCreateSocket()
    {
        if (!reEstablishConnection)
        {
            return;
        }

        try
        {
            if (sockHost != null && !sockHost.isClosed())
            {
                sockHost.close();
                sockHost = null;
            }

            sockHost = new ServerSocket(); // <-- create an unbound socket first
            sockHost.setReuseAddress(true);
            sockHost.bind(new InetSocketAddress(PORT)); // <-- now bind it

            //sockHost.setReuseAddress(true);
            //sockHost = new ServerSocket(PORT);

            reEstablishConnection = false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //Sending Music Message Order to Client to the Client Socket : manna Dec 2016

    private void orderClient(Socket Client_Socket, String toClient)
    {
        if (Client_Socket == null)
        {
            return;
        }

        if (Client_Socket.isClosed())
        {
            clientSocketList.remove(Client_Socket);
            Client_Socket = null;
            return;
        }

        try
        {
            OutputStream os = Client_Socket.getOutputStream();
            os.write(toClient.getBytes());

        }
        catch (IOException ex1)
        {
            try
            {

                Client_Socket.close();
                clientSocketList.remove(Client_Socket);
                Client_Socket = null;
            }
            catch (IOException ex2)
            {
                ex2.printStackTrace();
            }

        }
    }

    public void serverDisconnect()
    {
        reEstablishConnection = true;


        peersdisconnect();

        clientSocketList = null;
        clientSocketList = new ArrayList<Socket>();

        try
        {
            if (sockHost != null && !sockHost.isClosed())
            {
                sockHost.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        sockHost = null;
    }

    public void peersdisconnect()
    {

        for (Socket peer: clientSocketList)
        {
            try
            {
                if (peer != null && !peer.isClosed())
                {
                    peer.close();
                    peer = null;
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }


        clientSocketList = new ArrayList<Socket>();
    }
    //Check for new Music

    public void getNew_Music()
    {

        for (Socket Client_Socket : clientSocketList)
        {
            String toClient = NEW_SONG+SEPERATOR;
            orderClient(Client_Socket, toClient);
        }
    }

    public void clientCanPlay(String songName, long timing, int pos)
    {

        if (songName == null)
        {
            return;
        }

        String toClient = MUSIC_PLAY + SEPERATOR + songName + SEPERATOR
                + String.valueOf(timing) + SEPERATOR
                + String.valueOf(pos) + SEPERATOR;


        for (Socket Client_Socket : clientSocketList)
        {
            orderClient(Client_Socket, toClient);
        }
    }
    //Send Current playlist to Client : manna Jan 2017
    public void sendClientPlaylist(String songList)
    {
        if (songList == null)
        {
            return;
        }

        String toClient = MUSIC_LIST + SEPERATOR + songList + SEPERATOR;


        for (Socket Client_Socket : clientSocketList)
        {
            orderClient(Client_Socket, toClient);

        }
    }
    //Poll vote from Clients : manna Jan 2017
    public void getVote_FromClient()
    {
        String toClient = POLL_VOTE+SEPERATOR;


        for (Socket Client_Socket : clientSocketList)
        {
            orderClient(Client_Socket, toClient);
        }

        //Voting Algorithm : manna March 2017
        if(vote_Counter.size()>0){
            uiHost.vote_index_available=true;
            for(String votes:vote_Counter){
                Log.d("Show Votes",votes);

            }
            String mAX_Vote_Index="";
            //Creating a new List removing duplicates
            List<String> al = new ArrayList<String>();
            al=vote_Counter;
            // add elements to al, including duplicates
            Set<String> hs = new HashSet<>();
            hs.addAll(al);
            al.clear();
            al.addAll(hs);
            int maxCount=0;
            for(String key:al)
            {
                int counter=0;
                for(String value:vote_Counter)
                {
                    if(key.equals(value)){
                        counter++;
                    }
                }
                if(counter>maxCount){
                    maxCount=counter;
                    mAX_Vote_Index=key;


                }

            }
            Log.d("Maximum Votes By",mAX_Vote_Index);
            uiHost.vote_index=Integer.parseInt(mAX_Vote_Index.substring(5));




        }
        else{
            uiHost.vote_index_available=false;
        }



        //Resetting the vote counter for new Polling: manna Jan 2017

        vote_Counter = new ArrayList<String>();


    }
    public void clientCanStop()
    {
        String toClient = MUSIC_STOP + SEPERATOR;


        for (Socket Client_Socket : clientSocketList)
        {
            orderClient(Client_Socket, toClient);
        }
    }

    //Intialize Http Server on all Clients
    public void createClientHttp()
    {

        for (Socket Client_Socket : clientSocketList)
        {
            String toClient = CREATE_CLIENT_HTTP + SEPERATOR+Client_Socket.toString()+SEPERATOR;
            orderClient(Client_Socket, toClient);
        }
    }
}
