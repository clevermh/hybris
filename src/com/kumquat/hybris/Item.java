package com.kumquat.hybris;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * The Item class defines a food item
 *
 */
public class Item {
	private final int id;
	private final String name;
	private final String[] upc_codes;
	private final String[] plu_codes;
	
	public Item(int i, SQLiteDatabase db) {
		id = i;
		name = findNameFromID(db, i);
		upc_codes = new String[0];
		plu_codes = new String[0];
	}
	
	public Item(String n, SQLiteDatabase db) {
		id = findIDFromDatabase(db, n);
		name = n;
		upc_codes = new String[0];
		plu_codes = new String[0];
	}
	
	/**
	 * @param i the unique identification number for this ingredient
	 * @param n the name of the ingredient
	 */
	public Item(int i, String n) {
		id = i;
		name = n;
		upc_codes = new String[0];
		plu_codes = new String[0];
	}
	
	/**
	 * @param i the unique identification number for this ingredient
	 * @param n the name of the ingredient
	 * @param u the list of UPC codes for this item
	 * @param p the list of PLU codes for this item
	 */
	public Item(int i, String n, String[] u, String[] p) {
		id = i;
		name = n;
		upc_codes = u;
		plu_codes = p;
	}
	
	/**
	 * 
	 * @return the unique ID number of the item
	 */
	public int getID() { return id; }
	
	public String getName() { return name; }
	
	public boolean hasUPCs() { return upc_codes.length > 0; }
	public String[] getUPCs() { return upc_codes; }
	
	public boolean hasPLUs() { return plu_codes.length > 0; }
	public String[] getPLUs() { return plu_codes; }
	
	/**
	 * 
	 * @param db an SQLite database relating UPC codes to unique item IDs of ingredients
	 * @param upc the UPC code of an item
	 * @return the item ID for the item
	 */
	public static Item findItemFromUPC(SQLiteDatabase db, String upc) {
		String sql = "SELECT item_id FROM Upc WHERE upc_code = '" + upc + "'";
		Cursor c = db.rawQuery(sql, null);
		
		if(c == null) { return null; }
		if(c.getCount() != 1) { c.close(); return null; }
		
		c.moveToFirst();
		int id = c.getInt(0);
		c.close();
		
		return findItemFromID(db, id);
	}
	
	/**
	 * 
	 * @param db SQLite database relating UPC codes to item information
	 * @param spec the specific name of the ingredient
	 * @return the item ID for the item
	 */
	public static int findIDFromDatabase(SQLiteDatabase db, String n) {
		String sql = "SELECT id FROM Items WHERE name = '" + n + "'";
		Cursor c = db.rawQuery(sql, null);
		
		if(c == null || c.getCount() == 0) {
			if(c != null) { c.close(); }
			
			return -1;
		}
		
		c.moveToFirst();
		
		int ret = c.getInt(0);
		c.close();
		
		return ret;
	}
	
	/**
	 * 
	 * @param db SQLite database containing item information
	 * @param id the item ID of the ingredient
	 * @return the specific name of the item
	 */
	public static String findNameFromID(SQLiteDatabase db, int id) {
		String sql = "SELECT name FROM Items WHERE id = " + id;
		Cursor c = db.rawQuery(sql, null);
		
		if(c == null) { return ""; }
		if(c.getCount() != 1) { c.close(); return ""; }
		
		c.moveToFirst();
		
		String ret = c.getString(0);
		c.close();
		
		return ret;
	}
	
	/**
	 * 
	 * @param db SQLite database containing item information
	 * @param id the item ID of the ingredient
	 * @return Item object representing the ingredient
	 */
	public static Item findItemFromID(SQLiteDatabase db, int id) {
		String sql_statement = "SELECT name FROM Items WHERE id = " + id;
		Cursor c = db.rawQuery(sql_statement, null);
		
		if(c == null || c.getCount() == 0) {
			if(c != null) { c.close(); }
			
			return null;
		}
		
		c.moveToFirst();
		
		Item item = new Item(id, c.getString(0));
		
		c.close();
		
		return item;
	}
	
	public static String[] getAllItemNames(SQLiteDatabase db) {
		String sql = "SELECT name FROM Items ORDER BY name";
		Cursor c = db.rawQuery(sql, null);
	
		if(c == null) { return null; }
		if(c.getCount() == 0) { c.close(); return new String[0]; }
		
		String[] res = new String[c.getCount()];
		c.moveToFirst();
		int n = 0;
		while(!c.isAfterLast()) {
			res[n] = c.getString(0);
			n++;
			c.moveToNext();
		}
		
		c.close();
		return res;
	}
}
