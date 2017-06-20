package de.tu_darmstadt.informatik.newapp;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by manna on 28-02-2017.
 */

public class MainActivity_Clubber extends Fragment {

    private View rView;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {


        rView = inflater.inflate(R.layout.mainactivity_clubber,container,false);


        return rView;
    }


}



