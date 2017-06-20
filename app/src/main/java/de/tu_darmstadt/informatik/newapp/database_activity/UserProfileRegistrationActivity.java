package de.tu_darmstadt.informatik.newapp.database_activity;

/**
 * Created by niharika.sharma on 27-11-2016.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.provider.Settings.Secure;

import de.tu_darmstadt.informatik.newapp.SQLiteDBHelper;
import de.tu_darmstadt.informatik.newapp.R;
import de.tu_darmstadt.informatik.newapp.javabeans.User;

public class UserProfileRegistrationActivity extends Activity implements OnClickListener {

    private Button buttonCreate;
    private Button buttonCancel;


    private EditText userFirstName;
    //private EditText userLastName;
    private EditText userName;
    private EditText userPassword;
    private EditText userEmail;
    private String device_id;

    User user;
    SQLiteDBHelper userProfileDbHelper;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        userProfileDbHelper = new SQLiteDBHelper(this);
        //Assignment of UI fields to the variables
        buttonCreate = (Button) findViewById(R.id.btnNext);
        buttonCreate.setOnClickListener(this);
        buttonCancel = (Button) findViewById(R.id.btnCancel);
        buttonCancel.setOnClickListener(this);
        device_id= Secure.getString(this.getContentResolver(),
                Secure.ANDROID_ID);


    }


    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnCancel:
                Intent i = new Intent(getBaseContext(), UserProfileRegistrationActivity.class);
                startActivity(i);
                break;

            case R.id.btnNext:

                userFirstName = (EditText) findViewById(R.id.editName);
                userName = (EditText) findViewById(R.id.editUserName);
                userEmail = (EditText) findViewById(R.id.editEmail);
                user = new User();
                user.setFirstName(userFirstName.getText().toString());
                user.setUserName(userName.getText().toString());
                user.setEmail(userEmail.getText().toString());


                boolean invalid = false;

                if (user.getFirstName().equals("")) {
                    invalid = true;
                    Toast.makeText(getApplicationContext(), "Enter your First name", Toast.LENGTH_SHORT).show();
                } else if (user.getUserName().equals("")) {
                    invalid = true;
                    Toast.makeText(getApplicationContext(), "Please enter your Username", Toast.LENGTH_SHORT).show();
                } else

                    if (user.getEmail().equals("")) {
                        invalid = true;
                        Toast.makeText(getApplicationContext(), "Please enter your Email ID", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!(user.getEmail().matches(emailPattern))) {
                            invalid = true;
                            Toast.makeText(getApplicationContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show();

                        }
                    }
                if (invalid == false) {
                    //createProfile();
                    user.setDeviceId(device_id);
                    Intent newIntent = new Intent(UserProfileRegistrationActivity.this, UserProfileImageActivity.class);
                    newIntent.putExtra("UserDetails", user);
                    startActivity(newIntent);
                    finish();
                }

                break;
        }
    }





    public void onDestroy() {
        super.onDestroy();
        userProfileDbHelper.close();
    }


}
