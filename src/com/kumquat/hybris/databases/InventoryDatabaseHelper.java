package com.kumquat.hybris.databases;

import android.content.ContentValues;
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
	
	public static final int VERSION = 1;
	
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

	/**
	 * Adds an item to the database given the parameters and returns true if successful
	 * @param id
	 * @param qty
	 * @param qtymet
	 * @return
	 */
	public boolean addItem(int id, int qty, String qtymet) {
		ContentValues cv = new ContentValues();
		cv.put("item_id", id);
		cv.put("qty", qty);
		cv.put("qty_metric", qtymet);
		
		long row = database.insertOrThrow("Inventory", null, cv);
		
		return row != -1;
	}
}
