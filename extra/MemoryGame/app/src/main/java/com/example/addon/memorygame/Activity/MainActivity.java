package com.example.addon.memorygame.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.addon.memorygame.Adapter.AdapterCard;
import com.example.addon.memorygame.DataBase.DataBaseHandler;
import com.example.addon.memorygame.Fragment.HighSoreListDialog;
import com.example.addon.memorygame.Model.CardData;
import com.example.addon.memorygame.Model.UserProfile;
import com.example.addon.memorygame.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements AdapterCard.OnItemClickListenerTest,View.OnClickListener {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.my_toolbar)
    Toolbar toolbar;
//    @BindView(R.id.reset)
//    TextView reset;
    private ImageView imgToolbar;
    private TextView txtTitle;
    private TextView txtTitleRight;
    ArrayList<String> colourList = new ArrayList<>();
    List<CardData> cardDataList = new ArrayList<>();
    AdapterCard adapterCard;
    public static int SCORE = 0;
    private AdapterCard.OnItemClickListenerTest clickListenerTest;
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int columnCount = 4;
    EditText edtTxtName;
    String playerName="";
    DataBaseHandler dataBaseHandler;
    List<UserProfile> dataUserList;
    int highScore=Integer.MIN_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        dataBaseHandler = new DataBaseHandler(this);
//        toolbar.setNavigationIcon(R.drawable.logo);
//        toolbar.setTitle(""+AdapterCard.score);
        setToolBar();
        getListOFColor();
        Collections.shuffle(colourList);
        setCardDataForGame();
        initView();
        SCORE = 0;
        updateHighestScore();


    }

    private void setToolBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(false);
        getSupportActionBar().setCustomView(R.layout.toolbar_lyt);
     //   View view = getSupportActionBar().getCustomView();
        imgToolbar = (ImageView) toolbar.findViewById(R.id.imgToolbar);
        txtTitle = (TextView) toolbar.findViewById(R.id.title_center);
        txtTitleRight = (TextView) toolbar.findViewById(R.id.title_right);
        imgToolbar.setImageResource(R.drawable.logo);
        txtTitle.setText("-");
        txtTitleRight.setOnClickListener(this);

    }

    private void setCardDataForGame() {
        int count = 0;
      //  cardDataList = new ArrayList<>();
        for (String val : colourList) {
            CardData cardData = new CardData();
            cardData.setCardName(val);
            cardData.setFlipped(false);
            cardData.setId(count++);
            cardDataList.add(cardData);
        }
    }

    private void initView() {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), columnCount);
        recyclerView.setLayoutManager(layoutManager);
        adapterCard = new AdapterCard(this);
        adapterCard.clear();
        adapterCard.setmOnItemClickListener(this);
        recyclerView.setAdapter(adapterCard);
        adapterCard.addAll(cardDataList);
    }

    private void getListOFColor() {
        colourList= new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            colourList.add("colour" + i);
            colourList.add("colour" + i);

        }
    }

    @Override
    public void onItemClick(CardData item, boolean status) {
        if (status) {
            Toast.makeText(this, "Game over " + MainActivity.SCORE, Toast.LENGTH_SHORT).show();
            showChangeLangDialog(this);
        }
        txtTitle.setText("" + MainActivity.SCORE);


    }


    public void  showChangeLangDialog(Context context) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

         edtTxtName = (EditText) dialogView.findViewById(R.id.nameEdt);

        dialogBuilder.setTitle("Custom dialog");
        dialogBuilder.setMessage("Enter text below");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edtTxtName.getText().toString();
                if(!TextUtils.isEmpty(edtTxtName.getText().toString()))
                {
                    playerName= edtTxtName.getText().toString();
                        UserProfile userProfile = new UserProfile(playerName,SCORE);
                        dataBaseHandler.addScore(userProfile);
                    dialog.dismiss();
                    dataUserList =  dataBaseHandler.getAllProfiles();
                    updateHighestScore();
                }else {
                    Toast.makeText(MainActivity.this, "Enter valid name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
                dialog.dismiss();
            }
        });
        if (SCORE>highScore) {
            AlertDialog b = dialogBuilder.create();
            b.show();
        }
    }

    private String updateHighestScore() {
        String val ="Top Score";
        try {
            dataUserList= dataBaseHandler.getAllProfiles();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(dataUserList!=null && dataUserList.size()>0)
        {
            txtTitleRight.setText("Highest : "+dataUserList.get(0).getScore()+"");
            val=dataUserList.get(0).getScore()+"";
            highScore=dataUserList.get(0).getScore();
        }

        return val;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id)
        {

            case R.id.title_right:
                clickEvent();
                break;

        }
    }

    private void clickEvent() {

        if (dataUserList!=null && dataUserList.size()>0) {
            HighSoreListDialog highSoreListDialog =  HighSoreListDialog.newInstance();
            highSoreListDialog.show(getSupportFragmentManager(),"High Score");
        }else {
        Toast.makeText(this, "No top score" , Toast.LENGTH_SHORT).show();
        }
    }


}
