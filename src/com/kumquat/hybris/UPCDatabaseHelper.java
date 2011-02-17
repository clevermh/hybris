package com.kumquat.hybris;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UPCDatabaseHelper extends SQLiteOpenHelper {
	private SQLiteDatabase upc_database;
	public static final int VERSION = 4;
	
	private static final String upc_table = "CREATE TABLE IF NOT EXISTS upctable (" +
											"upc_id INTEGER PRIMARY KEY AUTOINCREMENT," +
											"upc_code varchar(12) NOT NULL default ''," +
											"upc_e varchar(8) NOT NULL default ''," +
											"ean_code varchar(13) NOT NULL default ''," +
											"description varchar(2000) NOT NULL default ''," +
											"amount varchar(200) NOT NULL default ''," +
											"product_type varchar(30) NOT NULL default ''," +
											"sub_type varchar(200) NOT NULL default ''," +
											"specific_type varchar(200) NOT NULL default ''" +
											");";
	private static final String upc_idx = "CREATE INDEX IF NOT EXISTS upc_idx_01 ON upctable(upc_code);";
	private static final String type_idx = "CREATE INDEX IF NOT EXISTS type_idx_01 ON upctable(product_type, sub_type, specific_type);";
	
	
	public UPCDatabaseHelper(Context context) {
		super(context, "UPCDatabase", null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		upc_database = db;
		upc_database.execSQL(upc_table);
		upc_database.execSQL(upc_idx);
		upc_database.execSQL(type_idx);
		
		// populate();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("UPCDatabase", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS upctable");
        onCreate(db);
	}
	
}
