package de.tu_darmstadt.informatik.newapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 *
 * Created by manna: for clubber on 28-02-2017.
 */


public class MainActivity_Host extends Fragment {


    private View rView2;
    private ViewPager vPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rView2 = inflater.inflate(R.layout.mainactivity_host,container,false);

        vPager = (ViewPager) getActivity().findViewById(R.id.mainactivity_pager);



        if (vPager != null) {

            vPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }





        return rView2;
    }




}

