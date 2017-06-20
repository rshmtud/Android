package de.tu_darmstadt.informatik.newapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


/**
 * Created by manna: for clubber on 28-02-2017.
 */


public class MainActivity_Pager extends FragmentStatePagerAdapter {

    int tabCount;

    public MainActivity_Pager(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MainActivity_Clubber tab1 = new MainActivity_Clubber();
                return tab1;

            case 1:
                MainActivity_Host tab2 = new MainActivity_Host();
                return tab2;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }

}
