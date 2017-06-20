package de.tu_darmstadt.informatik.newapp.database;

/**
 * Created by niharika.sharma on 26-11-2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import de.tu_darmstadt.informatik.newapp.javabeans.User;


public class UserProfile_DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "PartyBeatSQLiteDB.db";
    public static final String TABLE_NAME = "user";
    private static final int DATABASE_VERSION = 1;
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_FIRSTNAME = "FIRSTNAME";
    public static final String COLUMN_LASTNAME = "LASTNAME";
    public static final String COLUMN_USERNAME = "USERNAME";
    public static final String COLUMN_PASSWORD = "PASSWORD";
    public static final String COLUMN_EMAIL = "EMAIL";



    public UserProfile_DBHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, FIRSTNAME TEXT NOT NULL, LASTNAME TEXT NOT NULL, USERNAME TEXT NOT NULL, PASSWORD TEXT NOT NULL, EMAIL TEXT NOT NULL)");

        } catch (SQLException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
            onCreate(db);
        } catch (SQLException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public boolean insertData(User user) {
        long result = 0;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_FIRSTNAME, user.getFirstName());
            contentValues.put(COLUMN_LASTNAME, user.getLastName());
            contentValues.put(COLUMN_EMAIL, user.getEmail());
            //contentValues.put(COLUMN_GENDER, user.getGender());
            contentValues.put(COLUMN_USERNAME, user.getUserName());
            contentValues.put(COLUMN_PASSWORD, user.getPassword());

            result = db.insert(TABLE_NAME, null, contentValues);

        } catch (SQLException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == -1)
            return false;
        else
            return true;

    }

    public int validateLogin(String userName, String password){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = new String[]{userName,password};

        Cursor cursor = null;
        int cursorCount= 0;

        try{

            cursor = db.rawQuery("select * from user_profile_table where USERNAME=? and PASSWORD=?", selectionArgs);
            cursor.moveToFirst();
            cursorCount = cursor.getCount();
            cursor.close();
            return cursorCount;

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }


        return 0;



    }

    public Cursor readAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE_NAME, null);
        return result;
    }

    public boolean updateData(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_FIRSTNAME, user.getFirstName());
        contentValues.put(COLUMN_LASTNAME, user.getLastName());
        contentValues.put(COLUMN_EMAIL, user.getEmail());
        //contentValues.put(COLUMN_GENDER, user.getGender());
        db.update(TABLE_NAME, contentValues, "USERNAME = ?", new String[]{user.getUserName()});
        return true;
    }

    public Integer deleteData(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[]{user.getUserName()});

    }


}
