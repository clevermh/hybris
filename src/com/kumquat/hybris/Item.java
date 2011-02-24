package com.kumquat.hybris;

import com.kumquat.hybris.databases.ItemDatabaseHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Item {
	private final int id;
	private final String description;
	private final String type;
	private final String sub_type;
	private final String specific_type;
	
	public Item(int i, String d, String t, String su, String sp) {
		id = i;
		description = d;
		type = t;
		sub_type = su;
		specific_type = sp;
	}
	
	public int getID() { return id; }
	public String getDescription() { return description; }
	public String getType() { return type; }
	public String getSubType() { return sub_type; }
	public String getSpecificType() { return specific_type; }
	
	public static Item getFromDatabase(Context con, int id) {
		ItemDatabaseHelper idbh = new ItemDatabaseHelper(con);
		SQLiteDatabase idb = idbh.getReadableDatabase();
		
		return getFromDatabase(idb, id);
	}
	
	public static Item getFromDatabase(SQLiteDatabase db, int id) {
		String sql_statement = "SELECT description, type, sub_type, specific_type FROM Items WHERE id = " + id;
		Cursor c = db.rawQuery(sql_statement, new String[]{});
		
		if(c == null || c.getCount() == 0) {
			if(c != null) { c.close(); }
			
			return null;
		}
		
		c.moveToFirst();
		
		Item item = new Item(id, c.getString(0), c.getString(1), c.getString(2), c.getString(3));
		
		c.close();
		
		return item;
	}
}
