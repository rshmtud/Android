package de.tu_darmstadt.informatik.newapp.Client.Clubber_Core;

import android.app.Activity;
import android.app.Fragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import de.tu_darmstadt.informatik.newapp.Client.uiClubber;
import de.tu_darmstadt.informatik.newapp.R;
import de.tu_darmstadt.informatik.newapp.WebServer.NanoHTTPD;
import de.tu_darmstadt.informatik.newapp.WebServer.SimpleWebServer;

/**
 * Created by manna on 26-01-2017.
 */

public class MusicPlayerFragmentClubber {

    private String serverIP_Http = null;


    private NanoHTTPD nanoWebServerhttp = null;
    //Actually this is Client Local hosting port named as Server_Port: manna March 2017
    public static final int Server_Port = 1612;

    private MediaPlayer mediaPlayer = new MediaPlayer();

    /*@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mContentView = inflater.inflate(R.layout.music_player_clubber, null);
        mediaPlayer = new MediaPlayer();

        return mContentView;

    }*/

    public  void hostSongLocally(String IP)
    {
        if (uiClubber.getWebroot() != null&&(!uiClubber.isClientHostingSongs()))
        {
            Log.d("createHttpServerClient",uiClubber.getWebroot().toString());
            if (nanoWebServerhttp == null)
            {
                serverIP_Http = IP;

                boolean quiet = false;

                nanoWebServerhttp = new SimpleWebServer(serverIP_Http, Server_Port, uiClubber.getWebroot(), quiet);
                try
                {
                    nanoWebServerhttp.start();
                    uiClubber.setClientHostingSongs(true);
                    uiClubber.setHttpClient_port(Server_Port);

                    Log.d("Client Music","Hosted");
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }
        }

    }

    public void stopPlaying(){

        try {

            if(mediaPlayer.isPlaying())
            {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();


        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void startPlaying(String url, long startingtime, int startingPosition){

        try {

                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();
                //mediaPlayer.setDataSource("http://www.stephaniequinn.com/Music/Canon.mp3");
                //mediaPlayer.setDataSource("http://192.168.49.1:1512/1012.mp3");

                mediaPlayer.setDataSource(url);

                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                mediaPlayer.prepare();
                mediaPlayer.start();

        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
