package de.tu_darmstadt.informatik.newapp.Client;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.tu_darmstadt.informatik.newapp.R;

/**
 * This program is the 1st tab ie. 'Check Party' of uiClubber
 * Shows the Device details and connected host
 *
 * Created by manna on 28-02-2017.
 */

public class uiManageHosts extends Fragment {

    private View rView;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {


        rView = inflater.inflate(R.layout.clubber_resource,container,false);





        return rView;
    }


}



