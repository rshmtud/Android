package de.tu_darmstadt.informatik.newapp;

/**
 * Created by Rashmi on 11/16/2016.
 */
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class myAlert extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);


        // get action bar
        ActionBar actionBar = getSupportActionBar();
        //getActionBar();

        // Enabling Up / Back navigation
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
