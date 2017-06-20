package de.tu_darmstadt.informatik.newapp.Server;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.tu_darmstadt.informatik.newapp.Server.uiHostNowPlaying;
import de.tu_darmstadt.informatik.newapp.Server.uiHostPlayLists;


/**
 * This program allows the user to slide left and right through pages of data.
 * This if the for the Host UI
 *
 * Created by Parvez on 15-11-2016.
 */


public class Pager extends FragmentStatePagerAdapter {

    int tabCount;

    public Pager(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    /**
     * Method to get Fragment of the tab
     *
     * @param position This the position of the tab
     * @return Returns the fragment of the tab
     */

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                uiManageParty tab1 = new uiManageParty();
                return tab1;

            case 1:
                uiHostPlayLists tab2 = new uiHostPlayLists();
                return tab2;

            case 2:
                uiHostNowPlaying tab3 = new uiHostNowPlaying();
                return tab3;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }

}
