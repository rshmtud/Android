package de.tu_darmstadt.informatik.newapp.database_activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.Toast;

import de.tu_darmstadt.informatik.newapp.R;
import de.tu_darmstadt.informatik.newapp.SQLiteDBHelper;
import de.tu_darmstadt.informatik.newapp.database.UserProfile_DBHelper;
import de.tu_darmstadt.informatik.newapp.javabeans.User;

/**
 * Created by niharika.sharma on 07-12-2016.
 */

public class LoginActivity extends Activity implements View.OnClickListener {

    Button buttonLogin;
    Button buttonCancel;
    EditText editUserName;
    EditText editPassword;
    User user;
    SQLiteDBHelper userProfileDbHelper;

    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userProfileDbHelper= new SQLiteDBHelper(this);
        buttonLogin = (Button)findViewById(R.id.btnLogin);
        buttonLogin.setOnClickListener(this);
        buttonCancel = (Button)findViewById(R.id.btnLoginCancel);
        buttonCancel.setOnClickListener(this);
        editUserName = (EditText)findViewById(R.id.editLoginUserName);
        editPassword = (EditText)findViewById(R.id.editLoginPassword);

    }
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnLoginCancel:
                Intent i = new Intent(getBaseContext(), UserProfileRegistrationActivity.class);
                //is passed to this method to launch a new activity or get an existing activity to do something new.
                startActivity(i);
                break;

            case R.id.btnLogin:


                String username = editUserName.getText().toString();
                String password = editPassword.getText().toString();

                if (username.equals("") || username == null) {
                    Toast.makeText(getApplicationContext(), "Please enter User Name", Toast.LENGTH_SHORT).show();
                } else if (password.equals("") || password == null) {
                    Toast.makeText(getApplicationContext(), "Please enter your Password", Toast.LENGTH_SHORT).show();
                } else {
                    boolean isValidLogin = validateLogin(username, password );
                    if (isValidLogin) {
                        Toast.makeText(getApplicationContext(), "valid Login \nPlease Try Again", Toast.LENGTH_LONG).show();

                        //Intent in = new Intent(getBaseContext(), Welcome1.class);
                        //in.putExtra("UserName", editUserName.getText().toString());
                       // startActivity(in);
                        // finish();
                    }
                }
                break;

        }
    }

    private boolean validateLogin(String username, String password)
    {


        int numberOfRows = userProfileDbHelper.validateLogin(username, password);

        if(numberOfRows <= 0)
        {

            Toast.makeText(getApplicationContext(), "Invalid Login \nPlease Try Again", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
            return false;
        }


        return true;

    }


    public void onDestroy()
    {
        super.onDestroy();
        userProfileDbHelper.close();
    }

}
