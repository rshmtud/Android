package de.tu_darmstadt.informatik.newapp.Client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import de.tu_darmstadt.informatik.newapp.R;

/**
 * This program is used to get the selected songs from Listview
 *
 *
 * Created by Parvez on 23-11-2016.
 */

class Song {

    String title;
    String location;
    boolean selected = false;

    public Song(String title, String location) {
        super();
        this.title = title;
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

public class SongCustomAdapter extends ArrayAdapter<Song> {

    private List<Song> songsList;
    private Context context;
    private HashMap<String, String> selectedSongs;
    boolean[] chkBoxState;

    public HashMap<String, String> getSelectedSongs() {
        return selectedSongs;
    }

    public void setSelectedSongs(HashMap<String, String> selectedSongs) {
        this.selectedSongs = selectedSongs;
    }

    public SongCustomAdapter(List<Song> songsList, Context context) {
        super(context, R.layout.uiclubber_allsongs_sdcard, songsList);
        this.songsList = songsList;
        this.context = context;
        chkBoxState = new boolean[songsList.size()];
    }

    private static class SongsHolder {
        public TextView songName;
        public RadioButton chkBox;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        View view = convertView;
        final SongsHolder holder;

        selectedSongs = new HashMap<String, String>();

        if (convertView == null) {

            LayoutInflater infl = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infl.inflate(R.layout.uiclubber_allsongs_playlist_item, null);
            holder = new SongsHolder();
            holder.songName = (TextView) view.findViewById(R.id.songTitle);
            holder.chkBox = (RadioButton) view.findViewById(R.id.selectSongCheckBox);

            if (holder.chkBox.getVisibility() != View.VISIBLE) {
                holder.chkBox.setVisibility(View.VISIBLE);
            }

            view.setTag(holder);
            view.setTag(R.id.selectSongCheckBox, holder.chkBox);
        } else {

            holder = (SongsHolder) view.getTag();
        }

        Song song = songsList.get(pos);
        holder.songName.setText(song.getTitle());
        holder.chkBox.setTag(pos);

        holder.chkBox.setChecked(chkBoxState[pos]);

        final int flag = pos;

        holder.chkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Song song = songsList.get(flag);

                if(((RadioButton)v).isChecked()) {

                    chkBoxState[flag] = true;
                    selectedSongs.put(song.getLocation().toString(), song.getTitle().toString());
                    Toast.makeText(getContext(), ""+song.getTitle() +" added.", Toast.LENGTH_SHORT).show();

                } else {

                    chkBoxState[flag] = false;
                    selectedSongs.remove(song.getLocation().toString());
                    Toast.makeText(getContext(), ""+song.getTitle() +" removed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
