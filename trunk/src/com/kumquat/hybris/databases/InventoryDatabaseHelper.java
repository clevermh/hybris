package com.kumquat.hybris.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Controls access to the inventory database
 */
public class InventoryDatabaseHelper extends SQLiteOpenHelper {
	@SuppressWarnings("unused")
	private Context context;
	private SQLiteDatabase database;
	
	// The current version of the database structure
	// Changing this will cause the databases to be deleted and recreated
	public static final int VERSION = 1;
	
	// The CREATE TABLE statements for the Inventory table
	private static final String inventory_table = "CREATE TABLE IF NOT EXISTS Inventory (" +
												  "id INTEGER PRIMARY KEY AUTOINCREMENT," +
												  "item_id INTEGER NOT NULL," +
												  "qty REAL NOT NULL," +
												  "qty_metric varchar(20) NOT NULL default ''" +
												  ");";
	
	public InventoryDatabaseHelper(Context context) {
		super(context, "InventoryDatabase", null, VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		database = db;
		database.execSQL(inventory_table);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("InventoryDatabase", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
		
		db.execSQL("DROP TABLE IF EXISTS Inventory");
        onCreate(db);
	}
}
