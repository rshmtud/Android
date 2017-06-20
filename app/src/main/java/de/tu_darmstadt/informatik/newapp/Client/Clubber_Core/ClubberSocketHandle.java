package de.tu_darmstadt.informatik.newapp.Client.Clubber_Core;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import de.tu_darmstadt.informatik.newapp.Client.uiClubber;
import de.tu_darmstadt.informatik.newapp.Server.Host_Core.ServerGroupOwnerSocketHandle;

/**
 * Created by manna on 25-01-2017.
 */

public class ClubberSocketHandle extends Thread {
    public static final int message_recieve_event = 100;


    private InetAddress mAddress;





    private static final int Buff = 256;
    private Handler handler;


    public static final int CL_CB = 101;
    private static final int CONN_TIMEOUT = 0;

    private Socket clubber_Socks;


    public ClubberSocketHandle(Handler handler, InetAddress groupOwnerAddress) {
        this.handler = handler;
        this.mAddress = groupOwnerAddress;
    }

    @Override
    public void run() {

        handler.obtainMessage(CL_CB, this).sendToTarget();
        connect();

        while (clubber_Socks != null) {
            try {
                InputStream input_stream = clubber_Socks.getInputStream();
                OutputStream output_stream = clubber_Socks.getOutputStream();

                byte[] buffer = new byte[Buff];
                int bytes;

                bytes = input_stream.read(buffer);
                if (bytes == -1) {
                    continue;
                }

                String recieve_Message = new String(buffer);

                String[] command_Str = recieve_Message.split(ServerGroupOwnerSocketHandle.SEPERATOR);

                if (command_Str[0].equals(ServerGroupOwnerSocketHandle.MUSIC_BAL) && command_Str.length > 1) {
                    output_stream.write(recieve_Message.getBytes());
                }

                //Vote Polling send to server: manna, march 2017

                if (command_Str[0].equals(ServerGroupOwnerSocketHandle.POLL_VOTE) && command_Str.length > 1) {
                    Log.d("Message from Client","Sending 1");
                    byte[] buffer2 = new byte[Buff];
                    String send_vote_index = new String(buffer2);
                    send_vote_index = "VOTE:" + uiClubber.getVote_index();
                    if (uiClubber.getVote_index()!=0) {
                        output_stream.write(send_vote_index.getBytes());
                        output_stream.flush();
                        Log.d("Message from Client", send_vote_index.getBytes().toString());

                    }
                }

                //Vote Polling send to server: manna, march 2017

              if (command_Str[0].equals(ServerGroupOwnerSocketHandle.NEW_SONG) && command_Str.length > 1) {
                    //Get the device own IP address from HOST
                    byte[] buffer2 = new byte[Buff];
                    String sendSongURL = new String(buffer2);
                    sendSongURL="URL:"+uiClubber.getSong_URL();
                  Log.d("NEW_SONG_send",sendSongURL);
                  Log.d("Outside_MessageClient", sendSongURL.getBytes().toString());
                    if(sendSongURL.length()>6) {
                        Log.d("Message from Client","Sending New Song");
                        output_stream.write(sendSongURL.getBytes());
                        output_stream.flush();
                        Log.d("Message from Client", sendSongURL.getBytes().toString());
                    }

                }

                handler.obtainMessage(message_recieve_event, buffer).sendToTarget();
            } catch (SocketException e) {
                leave();
            } catch (IOException e) {

                leave();
            } catch (NumberFormatException e) {

                leave();
            }
        }
    }

    public void connect() {
        if (clubber_Socks == null || clubber_Socks.isClosed()) {
            clubber_Socks = new Socket();
        }

        try {
            clubber_Socks.bind(null);

            clubber_Socks.connect(new InetSocketAddress(mAddress.getHostAddress(),
                    ServerGroupOwnerSocketHandle.PORT), CONN_TIMEOUT);


            clubber_Socks.setSoTimeout(CONN_TIMEOUT);
        } catch (IOException e) {
            leave();
        }
    }

    public void leave() {
        if (clubber_Socks == null) {
            return;
        }
        try {
            clubber_Socks.close();
            clubber_Socks = null;
        } catch (IOException e) {

            clubber_Socks = null;
        }
    }
}