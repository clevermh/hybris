package com.kumquat.hybris.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PLUDatabaseHelper extends SQLiteOpenHelper {
	private SQLiteDatabase plu_database;
	public static final int VERSION = 1;
	
	private static final String plu_table = "CREATE TABLE IF NOT EXISTS Plu_Table (" +
											"plu_id INTEGER PRIMARY KEY AUTOINCREMENT," +
											"item_id INTEGER NOT NULL," +
											"plu_code varchar(5) default '0'," +
											"FOREIGN KEY (item_id) REFERENCES Items(id)" +
											");";
	
	public PLUDatabaseHelper(Context context) {
		super(context, "PLUDatabase", null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		plu_database = db;
		plu_database.execSQL(plu_table);
		// populate();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("PLUDatabase", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS upctable");
        onCreate(db);
	}
	
}
