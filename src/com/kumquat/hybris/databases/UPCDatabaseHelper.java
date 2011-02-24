package com.kumquat.hybris.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UPCDatabaseHelper extends SQLiteOpenHelper {
	private SQLiteDatabase upc_database;
	public static final int VERSION = 2;
	
	private static final String upc_table = "CREATE TABLE IF NOT EXISTS Upc_Table (" +
											"id INTEGER PRIMARY KEY AUTOINCREMENT," +
											"upc_code varchar(12) NOT NULL default ''," +
											"upc_e varchar(8) NOT NULL default ''," +
											"ean_code varchar(13) NOT NULL default ''," +
											"item_id INTEGER NOT NULL," +
											"FOREIGN KEY (item_id) REFERENCES Items(id)" +
											");";
	
	public UPCDatabaseHelper(Context context) {
		super(context, "UPCDatabase", null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		upc_database = db;
		upc_database.execSQL(upc_table);
		
		// populate();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("UPCDatabase", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS Upc_Table");
        onCreate(db);
	}
	
}
