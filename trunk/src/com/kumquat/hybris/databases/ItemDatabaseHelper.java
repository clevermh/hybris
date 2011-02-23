package com.kumquat.hybris.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ItemDatabaseHelper extends SQLiteOpenHelper {
	private SQLiteDatabase item_database;
	public static final int VERSION = 1;
	
	private static final String item_table = "CREATE TABLE IF NOT EXISTS Items (" +
											"id INTEGER PRIMARY KEY AUTOINCREMENT," +
											"description varchar(2000) NOT NULL default ''," +
											"type varchar(100) NOT NULL default ''," +
											"sub_type varchar(100) NOT NULL default ''," +
											"specific_type varchar(100) NOT NULL default ''" +
											");";
	
	private static final String item_idx = "CREATE INDEX IF NOT EXISTS item_idx_01 ON Items(type, sub_type, specific_type);";
	
	public ItemDatabaseHelper(Context context) {
		super(context, "ItemDatabase", null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		item_database = db;
		item_database.execSQL(item_table);
		item_database.execSQL(item_idx);
		// populate();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("ItemDatabase", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS upctable");
        onCreate(db);
	}
	
}
