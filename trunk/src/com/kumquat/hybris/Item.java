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
	
	/**
	 * @param i The ID of the Item
	 * @param db The database to find the name from
	 */
	public Item(int i, SQLiteDatabase db) {
		id = i;
		name = findNameFromID(db, i);
		upc_codes = new String[0];
		plu_codes = new String[0];
	}
	
	/**
	 * @param n The name of the Item
	 * @param db The database to find the ID from
	 */
	public Item(String n, SQLiteDatabase db) {
		id = findIDFromDatabase(db, n);
		name = n;
		upc_codes = new String[0];
		plu_codes = new String[0];
	}
	
	/**
	 * @param i The ID of this Item
	 * @param n The name of the Item
	 */
	public Item(int i, String n) {
		id = i;
		name = n;
		upc_codes = new String[0];
		plu_codes = new String[0];
	}
	
	/**
	 * @param i The ID of this Item
	 * @param n The name of the Item
	 * @param u The list of UPC codes for this Item
	 * @param p The list of PLU codes for this Item
	 */
	public Item(int i, String n, String[] u, String[] p) {
		id = i;
		name = n;
		upc_codes = u;
		plu_codes = p;
	}
	
	/**
	 * @return The ID of the Item
	 */
	public int getID() { return id; }
	
	/**
	 * @return The name of the Item
	 */
	public String getName() { return name; }
	
	/**
	 * This should only be used when loading an Item from the YAML since it is never populated otherwise
	 * @return How many UPC codes are stored by this Item
	 */
	public boolean hasUPCs() { return upc_codes.length > 0; }
	
	/**
	 * This should only be used when loading an Item from the YAML since it is never populated otherwise
	 * @return An array of the UPC codes stored by this Item
	 */
	public String[] getUPCs() { return upc_codes; }
	
	/**
	 * This should only be used when loading an Item from the YAML since it is never populated otherwise
	 * @return How many PLU codes are stored by this Item
	 */
	public boolean hasPLUs() { return plu_codes.length > 0; }
	
	/**
	 * This should only be used when loading an Item from the YAML since it is never populated otherwise
	 * @return An array of the PLU codes stored by this Item
	 */
	public String[] getPLUs() { return plu_codes; }
	
	/**
	 * Find an Item based on a given UPC code
	 * @param db The database containing the UPC code and Item tables
	 * @param upc The UPC code of an Item
	 * @return The Item that this UPC references if the UPC is known, null otherwise
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
	 * Find an Item based on a given name
	 * @param db The database containing the Item table
	 * @param n The name of the Item to find
	 * @return The ID for the Item if it exists, -1 otherwise
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
	 * Find the name of an Item based on a given ID
	 * @param db The database containing the Item table
	 * @param id The ID of an Item
	 * @return The name of the Item if it exists, an empty String otherwise
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
	 * Find an Item based from a given ID
	 * @param db The database containing the Item table
	 * @param id The ID of an Item
	 * @return An Item based on the given ID if it exists, null otherwise
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
	
	/**
	 * Find the name of every Item in a database
	 * @param db The database containing the Item table
	 * @return An array containing the name of every Item in the given database
	 */
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
