package de.tu_darmstadt.informatik.newapp.database_activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.tu_darmstadt.informatik.newapp.MainActivity;
import de.tu_darmstadt.informatik.newapp.R;
import de.tu_darmstadt.informatik.newapp.SQLiteDBHelper;
import de.tu_darmstadt.informatik.newapp.javabeans.User;


/**
 * Created by niharika.sharma on 12-12-2016.
 */

public class UserProfileImageActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageView imageView;
    //private Button buttonSelectPicture;
    private Button buttonCreateAccount;
    Bitmap bitmap = null;
    byte img[];

    User user;
    SQLiteDBHelper sQLiteDBHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_picture);

        // get action bar
        ActionBar actionBar = getSupportActionBar();
        //getActionBar();

        // Enabling Up / Back navigation
        actionBar.setDisplayHomeAsUpEnabled(true);
        imageView = (ImageView) findViewById(android.R.id.icon);
        imageView.setImageResource(R.drawable.profile_icon);

        sQLiteDBHelper = new SQLiteDBHelper(this);

        //buttonSelectPicture = (Button)findViewById(R.id.btn_selectPhoto);
        //buttonSelectPicture.setOnClickListener(this);
        buttonCreateAccount = (Button)findViewById(R.id.btn_createAccount);
        buttonCreateAccount.setOnClickListener(this);

        imageView.setOnClickListener(this);

        Intent passedIntent = getIntent();
        user = (User) passedIntent.getSerializableExtra("UserDetails");

    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){

            //case R.id.btn_selectPhoto:
            case android.R.id.icon:

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,0);

                break;

            case R.id.btn_createAccount:
                createProfile();

                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode== Activity.RESULT_OK && data !=null)
        {
            Uri selectedImage = data.getData();
            try        {
                bitmap = MediaStore.Images.Media.getBitmap (this.getContentResolver(), selectedImage);
                ByteArrayOutputStream byteArrayOutputStreams = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStreams);
                img = byteArrayOutputStreams.toByteArray();
                imageView.setImageBitmap(bitmap);
                user.setImageData(img);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public void createProfile() {

        try {
            user.setRegistrationStatus(1);
            boolean isInserted = sQLiteDBHelper.insertToUserTable(user);
            if (isInserted == true){
                Toast.makeText(getApplicationContext(), "Profile Created", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(getBaseContext(), GameActivity.class);
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("UserDetails", user);
                startActivity(intent);
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(), "Error in Profile Creation", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), UserProfileRegistrationActivity.class);
                startActivity(intent);
                finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
