package com.kumquat.hybris;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UPCDatabaseHelper extends SQLiteOpenHelper {
	private SQLiteDatabase database;
	private final Context context;
	public static final int VERSION = 1;
	
	private static final String createDB = "CREATE TABLE upctable (" +
											"upc			varchar(10) primary key" +
											", item			varchar(255)" +
											", amount		int" +
											", amount_type	varchar(255)" +
											");";
	
	public UPCDatabaseHelper(Context context) {
		super(context, "UPCDatabase", null, VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		database = db;
		database.execSQL(createDB);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("UPCDatabase", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS upctable");
        onCreate(db);
	}
}
