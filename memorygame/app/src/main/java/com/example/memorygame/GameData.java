package com.example.memorygame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GameData {

	// Database fields
	private SQLiteDatabase database ;

	private MySQLiteHelper dbHelper;

	private String[] allColumns = { MySQLiteHelper.COLUMN_USERNAME,
			MySQLiteHelper.COLUMN_RANK, MySQLiteHelper.COLUMN_SCORE };

	public GameData(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void saveUserData(ContentValues values) {
		// Insert the new row, returning the primary key value of the new row
		long newRowId;
		newRowId = database.insert(MySQLiteHelper.TABLE_NAME, null, values);

	}

	public Cursor readSavedData() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(MySQLiteHelper.TABLE_NAME, allColumns, null,
				null, null, null, MySQLiteHelper.COLUMN_SCORE + " DESC");
		return cursor;

	}

	public int readUserRank(String name) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = MainActivity.gameData.readSavedData();
		int i = 1;
		if (c.moveToFirst()) {
			do {
				if (c.getString(
						c.getColumnIndex(MySQLiteHelper.COLUMN_USERNAME))
						.equals(name)) {
					return i;
				}
				i++;
			} while (c.moveToNext());
		}
		return -100; // unreachable;
	}

	public int readUserScore(String name) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String q = "SELECT playerScore FROM GameData where playerName = ?";
		Cursor mCursor = db.rawQuery(q, new String[] { name });
		System.out.println("Phani readUserScore OK  " + mCursor.moveToFirst());
		System.out.println("Phani readUserScore OK "
				+ mCursor.getInt(mCursor
						.getColumnIndex(MySQLiteHelper.COLUMN_SCORE)));

		return mCursor.getInt(mCursor
				.getColumnIndex(MySQLiteHelper.COLUMN_SCORE)); // unreachable;
	}

	public boolean checkUserExits(String name) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String q = "SELECT * FROM GameData where playerName = ?";
		Cursor mCursor = db.rawQuery(q, new String[] { name });
		// System.out.println("Phani data user present name "+mCursor.moveToFirst());
		// System.out.println("Phani data user present name "+mCursor.getString(mCursor.getColumnIndex(MySQLiteHelper.COLUMN_RANK)));
		// mCursor.getString(mCursor.getColumnIndex(MySQLiteHelper.COLUMN_RANK));
		return mCursor.moveToFirst();

	}

	public int updateExistingData(ContentValues values2, String name) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		// sqLiteDatabase.update(MYDATABASE_TABLE, values,
		// MySQLiteHelper.COLUMN_USERNAME+"="+id, null);

		// updating row
		return db.update(MySQLiteHelper.TABLE_NAME, values2,
				MySQLiteHelper.COLUMN_USERNAME + " = ?",
				new String[] { name /* String.valueOf(name) */});

	}

	public HashMap<String, Integer> getAllRanks() {

		HashMap<String, Integer> map = new HashMap<String, Integer>();
		String[] allColumns = { MySQLiteHelper.COLUMN_USERNAME,
				MySQLiteHelper.COLUMN_SCORE, MySQLiteHelper.COLUMN_RANK };
		Cursor c = MainActivity.gameData.readSavedData();
		int i = 1;
		if (c.moveToFirst()) {
			do {
				String name = null;
				int score = 0;
				for (int j = 0; j < allColumns.length; j++) {
					if (allColumns[j].equals(MySQLiteHelper.COLUMN_SCORE)) {
						score = c.getInt(c.getColumnIndex(allColumns[j]));
					}
					if (allColumns[j].equals(MySQLiteHelper.COLUMN_USERNAME)) {
						name = c.getString(c.getColumnIndex(allColumns[j]));
					}

				}
				// System.out.println("Phani --------Name-----"+name);
				map.put(name, score);
				i++;
			} while (c.moveToNext());
		}
		// map.put(mUserName,userScore);
		map = sortPlayerByScore(map);
		// System.out.println("Phani --------Map-----"+map);
		return map;

	}

	@SuppressWarnings("unchecked")
	LinkedHashMap sortPlayerByScore(HashMap passedMap) {
		List mapKeys = new ArrayList(passedMap.keySet());
		List mapValues = new ArrayList(passedMap.values());
		Collections.sort(mapValues);
		Collections.reverse(mapValues);
		LinkedHashMap sortedMap = new LinkedHashMap();

		Iterator valueIt = mapValues.iterator();
		while (valueIt.hasNext()) {
			Object val = valueIt.next();
			Iterator keyIt = mapKeys.iterator();

			while (keyIt.hasNext()) {
				Object key = keyIt.next();
				String comp1 = passedMap.get(key).toString();
				String comp2 = val.toString();

				if (comp1.equals(comp2)) {
					passedMap.remove(key);
					mapKeys.remove(key);
					sortedMap.put((String) key, (Integer) val);
					break;
				}

			}

		}

		int rank = 0;
		int lastVotes = -500;
		LinkedHashMap finalsortedMap = new LinkedHashMap();
		Iterator it = sortedMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			System.out.println("Phaniiiiiiiiiiiii" + pairs.getKey() + " = "
					+ pairs.getValue());
			if (Integer.parseInt(pairs.getValue().toString()) != lastVotes) {
				rank += 1;
			}
			finalsortedMap.put(pairs.getKey(), rank);
			lastVotes = Integer.parseInt(pairs.getValue().toString());
			it.remove(); 
		}
		System.out
				.println("PhaniiiiiiiiiiiiifinalsortedMap =" + finalsortedMap);

		return finalsortedMap;
	}

}
