package de.tu_darmstadt.informatik.newapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.tu_darmstadt.informatik.newapp.javabeans.User;

import static android.R.attr.name;

/**
 * Following program is a helper object to create, open, and/or manage SQLite database.
 * For first time installation of the app, it creates a SQLite db called PartyBeatSQLiteDB.db
 * Then creates tables such as Playlist, Songs, UserProfile
 * Also contains methods to insert/update into tables.
 * Created by Parvez on 20-11-2016.
 * Updated by Niharika 14-12-2016
 *
 */


public class SQLiteDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "PartyBeatSQLiteDB.db";

    public static final String PLAYLIST_TABLE_NAME = "PlayList";
    public static final String PLAYLIST_ID = "id";
    public static final String PLAYLIST_NAME = "playListName";

    public static final String SONG_TABLE_NAME = "Songs";
    public static final String SONG_ID = "id";
    public static final String SONG_PLAYLIST_ID = "playlistId";
    public static final String SONG_NAME = "songName";
    public static final String SONG_LOCATION = "songLocation";

    public static final String USER_TABLE_NAME = "UserProfile";
    private static final int DATABASE_VERSION = 1;
    public static final String USER_ID = "ID";
    public static final String USER_NAME = "name";
    public static final String USER_USERNAME = "userName";
    //public static final String USER_PASSWORD = "password";
    public static final String USER_EMAIL = "email";
    //public static final String USER_IMAGE_KEY = "imageName";
    public static final String USER_IMAGE = "imageData";
    public static final String USER_STATUS = "status";
    public static final String USER_DEVICE_ID = "deviceID";
    private final Context context;
    private String DB_PATH = "";


    public SQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
        // SQLiteDatabase db = this.getWritableDatabase();
    }

    /**
     * Called when the database is created for the first time.
     * This is where the creation of tables and the initial population of the tables happens
     * @param db first parameter to the method which is the SQLite database
     */

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("create table " + PLAYLIST_TABLE_NAME + " (" + PLAYLIST_ID + " integer primary key autoincrement, " + PLAYLIST_NAME + " text);");
            db.execSQL("create table " + SONG_TABLE_NAME + " (" + SONG_ID + " integer primary key autoincrement, " + SONG_PLAYLIST_ID + " integer, " + SONG_NAME + " text, " + SONG_LOCATION + " text);");
            db.execSQL("create table " + USER_TABLE_NAME + " (" + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + USER_DEVICE_ID + " TEXT NOT NULL UNIQUE, " + USER_NAME + " TEXT NOT NULL, " + USER_USERNAME + " TEXT NOT NULL, " + USER_EMAIL + " TEXT, " + USER_IMAGE + " BLOB, " + USER_STATUS + " INTEGER DEFAULT 0)");
            //USER_DEVICE_ID not null unique
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when the database needs to be upgraded.
     * The implementation should use this method to drop tables, add tables,
     * or do anything else it needs to upgrade to the new schema version.
     * @param db  This is the SQLite database
     * @param oldVersion This is the old schema version
     * @param newVersion This is the new schema version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + PLAYLIST_TABLE_NAME);
        db.execSQL("drop table if exists " + SONG_TABLE_NAME);
        db.execSQL("drop table if exists " + USER_TABLE_NAME);
        onCreate(db);
    }

    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();
        SQLiteDatabase db_Read = null;
        if (dbExist) {
            //do nothing - database already exist
        } else {

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            db_Read = this.getReadableDatabase();
            db_Read.close();
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        DB_PATH = this.context.getDatabasePath(DATABASE_NAME).toString();
        try {
            String myPath = DB_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    public boolean insertToSongsTable(int playlistId, String name, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SONG_PLAYLIST_ID, playlistId);
        contentValues.put(SONG_NAME, name);
        contentValues.put(SONG_LOCATION, location);
        db.insertOrThrow(SONG_TABLE_NAME, null, contentValues);
        //db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean insertToPlaylistTable(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PLAYLIST_NAME, name);
        db.insertOrThrow(PLAYLIST_TABLE_NAME, null, contentValues);
        //db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public void deletePlaylistEntry(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PLAYLIST_TABLE_NAME, PLAYLIST_ID + "=" + id, null);
    }

  public boolean insertToUserTable(User user) {
    long result = 0;
    try {
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();

      contentValues.put(USER_NAME, user.getFirstName().trim());
      contentValues.put(USER_USERNAME, user.getUserName().trim());
      //contentValues.put(USER_PASSWORD, user.getPassword());
      contentValues.put(USER_EMAIL, user.getEmail().trim());
      contentValues.put(USER_IMAGE, user.getImageData());
      contentValues.put(USER_STATUS, user.getRegistrationStatus());
      contentValues.put(USER_DEVICE_ID, user.getDeviceId().trim());

      //result = db.insertOrThrow(USER_TABLE_NAME, null, contentValues);
      result = db.insert(USER_TABLE_NAME, null, contentValues);
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

    public Cursor getIdOfPlaylist(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select id from " + PLAYLIST_TABLE_NAME + " where playListName='" + name + "'", null);
        return res;
    }

    public Cursor getAllSongsFromPlaylist(int playlistId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + SONG_TABLE_NAME + " where playlistId='" + playlistId + "'", null);
        return res;
    }

    public Cursor getAllPlaylist() {
      try{
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + PLAYLIST_TABLE_NAME, null);
        return res;

      }
      catch(Exception e){
        Log.d("myTag", "This is my message");
        return null;
      }

    }

    public Cursor readUserData(String deviceId) {
        Cursor result = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            result = db.rawQuery("select * from " + USER_TABLE_NAME + " where deviceID='" + deviceId + "'", null);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

  public boolean updateUserData(User user) {
    int updatedRowCount = 0;
    try {
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();
      contentValues.put(USER_NAME, user.getFirstName().trim());
      contentValues.put(USER_USERNAME, user.getUserName().trim());
      //contentValues.put(USER_PASSWORD, user.getPassword());
      contentValues.put(USER_EMAIL, user.getEmail().trim());
      contentValues.put(USER_IMAGE, user.getImageData());
      String deviceId= user.getDeviceId().trim();
      contentValues.put(USER_STATUS, user.getRegistrationStatus());
      //updatedRowCount =db.update(USER_TABLE_NAME, contentValues, "deviceID="+deviceId, null);
      updatedRowCount =db.update(USER_TABLE_NAME, contentValues, "deviceID = ?", new String[]{deviceId});

      //db.update(USER_TABLE_NAME, contentValues, "deviceID = ?", new String[]{user.getDeviceId()});
      return updatedRowCount > 0;

    } catch (Exception e) {
      e.printStackTrace();
    }
    return updatedRowCount > 0;

  }

    public int validateLogin(String userName, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = new String[]{userName, password};

        Cursor cursor = null;
        int cursorCount = 0;

        try {

            cursor = db.rawQuery("select * from " + USER_TABLE_NAME + " where USERNAME=? and PASSWORD=?", selectionArgs);
            cursor.moveToFirst();
            cursorCount = cursor.getCount();
            cursor.close();
            return cursorCount;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void eraseAllItems(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + tableName);
    }
}

