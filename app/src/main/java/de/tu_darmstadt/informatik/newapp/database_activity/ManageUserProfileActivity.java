package de.tu_darmstadt.informatik.newapp.database_activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import de.tu_darmstadt.informatik.newapp.R;
import de.tu_darmstadt.informatik.newapp.SQLiteDBHelper;
import de.tu_darmstadt.informatik.newapp.javabeans.User;

import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.provider.Settings.Secure;


/**
 * Created by niharika.sharma on 13-12-2016.
 */

public class ManageUserProfileActivity extends AppCompatActivity implements OnClickListener {

    private Button buttonUpdateProfile;
    //private Button buttonSelectPic;
    private ImageView imageView;

    private EditText userFirstName;
    private EditText userName;
    private EditText userPassword;
    private EditText userEmail;

    User user, updatedUser;
    SQLiteDBHelper sqLiteDBHelper;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private static final int PIC_SELECTED = 0;
    private String device_id;
    Bitmap bitmap = null;
    byte img[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_profile);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);

        device_id = Secure.getString(this.getContentResolver(),
                Secure.ANDROID_ID);
        user = new User();
        updatedUser = new User();
        sqLiteDBHelper = new SQLiteDBHelper(this);

        getUserDetails();

        imageView = (ImageView) findViewById(android.R.id.icon);
        if(user.getImageData() !=null){
           showCurrentUserProfilePic(user);
        }else{
            imageView.setImageResource(R.drawable.profile_icon);
        }


        imageView.setOnClickListener(this);
        userFirstName = (EditText) findViewById(R.id.updateName);
        userName = (EditText) findViewById(R.id.updateUserName);
        userEmail = (EditText) findViewById(R.id.updateEmail);
        userFirstName.setText(user.getFirstName());
        userName.setText(user.getUserName());
        userEmail.setText(user.getEmail());

        buttonUpdateProfile = (Button) findViewById(R.id.btn_updateProfile);
        buttonUpdateProfile.setOnClickListener(this);

    }

    public void getUserDetails() {
        try{
            Cursor res = sqLiteDBHelper.readUserData(device_id);
            if (res.getCount() > 0) {
                while (res.moveToNext()) {
                    user.setFirstName(res.getString(2));
                    user.setUserName(res.getString(3));
                    user.setEmail(res.getString(4));
                    user.setImageData(res.getBlob(5));
                    user.setRegistrationStatus(res.getInt(6));
                }


            }
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public void onClick(View v) {

        switch (v.getId()) {

            case android.R.id.icon:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,0);

                break;

            case R.id.btn_updateProfile:


                updatedUser.setFirstName(userFirstName.getText().toString());
                updatedUser.setUserName(userName.getText().toString());
                updatedUser.setEmail(userEmail.getText().toString());
                updatedUser.setImageData(img);
                //updatedUser.setImageData(user.getImageData());


                boolean invalid = false;

                if (user.getFirstName().equals("")) {
                    invalid = true;
                    Toast.makeText(getApplicationContext(), "Enter your First name", Toast.LENGTH_SHORT).show();
                } else {
                    if (user.getUserName().equals("")) {
                        invalid = true;
                        Toast.makeText(getApplicationContext(), "Please enter your Username", Toast.LENGTH_SHORT).show();
                    } else

                    {
                        if (user.getEmail().equals("")) {
                            invalid = true;
                            Toast.makeText(getApplicationContext(), "Please enter your Email ID", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!(user.getEmail().matches(emailPattern))) {
                                invalid = true;
                                Toast.makeText(getApplicationContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                }
                if (invalid == false) {
                    updateProfile();

                }

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
                updatedUser.setImageData(img);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }




    public void showCurrentUserProfilePic(User currentUser)
    {
        Bitmap currentBitmap=BitmapFactory.decodeByteArray(currentUser.getImageData(), 0, currentUser.getImageData().length);
        imageView.setImageBitmap(currentBitmap);

    }

    public void updateProfile() {

        try {
            updatedUser.setRegistrationStatus(1);
            updatedUser.setDeviceId(device_id);
            boolean isUpdated = sqLiteDBHelper.updateUserData(updatedUser);
            if (isUpdated == true) {
                Toast.makeText(getApplicationContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                userFirstName.setText(updatedUser.getFirstName());
                userName.setText(updatedUser.getUserName());
                userEmail.setText(updatedUser.getEmail());
                showCurrentUserProfilePic(updatedUser);
                user=updatedUser;
                Intent intent = new Intent(getBaseContext(), ManageUserProfileActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Error in Updating Profile", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), ManageUserProfileActivity.class);
                startActivity(intent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
