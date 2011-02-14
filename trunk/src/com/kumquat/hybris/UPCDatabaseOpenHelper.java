package com.kumquat.hybris;

import android.content.Context;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class UPCDatabaseOpenHelper extends SQLiteOpenHelper {
    
	private SQLiteDatabase database;
	private final Context context;
	
	private static final String createDB = "CREATE TABLE upctable (" +
											"upc			varchar(10) primary key" +
											", item			varchar(255)" +
											", amount		int" +
											", amount_type	varchar(255)" +
											");";
	
	public UPCDatabaseOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		database = db;
		database.execSQL(createDB);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.w("UPCDatabase", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS upctable");
        onCreate(db);
	}

}
