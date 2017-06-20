package com.example.addon.memorygame.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.addon.memorygame.Model.UserProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by addon on 12/19/2016.
 */

public class DataBaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "ScoreDb";
   // private static final String DATABASE_NAME_EXTERNAL = "/sdcard/ScoreDb";

    // Contacts table name
    private static final String TABLE_GAME_SCORE = "GameScore ";

    // Contacts Table Columns names
    private static final String FULL_NAME = "Name";
    private static final String SCORE = "score";


    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_GAME_SCORE + "("
                + FULL_NAME + " TEXT PRIMARY KEY,"
                + SCORE + " INT" +
                ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME_SCORE);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    public void addScore(UserProfile userData) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(FULL_NAME, userData.getUserName()); // Contact Name
            values.put(SCORE, userData.getScore()); // Contact Name


            // Inserting Row
            db.insert(TABLE_GAME_SCORE, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close(); // Closing database connection
    }

    // Getting single contact
    public UserProfile getProfileData(String emailId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_GAME_SCORE, new String[]{FULL_NAME,
                        SCORE}, FULL_NAME + "=?",
                new String[]{emailId}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        UserProfile userProfile = null;
        try {
            userProfile = new UserProfile(cursor.getString(0),
                    cursor.getInt(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // return contact
        return userProfile;
    }

    // Getting All Contacts
    public List<UserProfile> getAllProfiles() {
        List<UserProfile> listProfile = new ArrayList<UserProfile>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_GAME_SCORE +" ORDER BY "+SCORE+" DESC;";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                UserProfile contact = new UserProfile();
                contact.setUserName(cursor.getString(0));
                contact.setScore(cursor.getInt(1));

                // Adding contact to list
                listProfile.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return listProfile;
    }

    // Updating single contact
    public int updateScore(UserProfile userProfile) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FULL_NAME, userProfile.getUserName());

        // updating row

        int res=db.update(TABLE_GAME_SCORE, values, SCORE + " = ?",
                new String[]{String.valueOf(userProfile.getScore())});
        return res;
    }

    // Deleting single contact
    public void deleteContact(UserProfile userProfile) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GAME_SCORE, FULL_NAME + " = ?",
                new String[]{String.valueOf(userProfile.getUserName())});
        db.close();
    }


    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_GAME_SCORE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}