package com.example.memorygame;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

  public static final String TABLE_NAME = "GameData";
  public static final String COLUMN_USERNAME = "playerName";
  public static final String COLUMN_RANK = "playerRank";
  public static final String COLUMN_SCORE = "playerScore";
  public static final String COLUMN_ID = "playerID";

  private static final String DATABASE_NAME = "commments.db";
  private static final int DATABASE_VERSION = 1;

  // Database creation sql statement
  private static final String DATABASE_CREATE = "create table "
      + TABLE_NAME + "(" + COLUMN_ID +" integer primary key autoincrement ,"+COLUMN_USERNAME
      + " text  ," + COLUMN_RANK
      + " integer,"+ COLUMN_SCORE
      + " integer);";

  public MySQLiteHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase database) {
    database.execSQL(DATABASE_CREATE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(MySQLiteHelper.class.getName(),
        "Upgrading database from version " + oldVersion + " to "
            + newVersion + ", which will destroy all old data");
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    onCreate(db);
  }

}