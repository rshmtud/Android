package de.tu_darmstadt.informatik.newapp.Client;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.tu_darmstadt.informatik.newapp.Server.uiHostPlayLists;


/**
 * Created by manna: for clubber on 28-02-2017.
 */


public class Pager_Client extends FragmentStatePagerAdapter {

    int tabCount;

    public Pager_Client(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                uiManageHosts tab1 = new uiManageHosts();
                return tab1;

            case 1:
                uiClientNowPlaying tab2 = new uiClientNowPlaying();
                return tab2;
//            case 2:
//              uiClubberAddSongs tab3= new uiClubberAddSongs();
//              return tab3;

          default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }

}
