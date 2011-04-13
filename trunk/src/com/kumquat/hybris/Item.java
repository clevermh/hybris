package com.kumquat.hybris;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * The Item class defines a food item
 *
 */
public class Item {
	private final int id;
	private final String type;
	private final String sub_type;
	private final String specific_type;
	
	/**
	 * 
	 * @param i the unique identification number for this ingredient
	 * @param t the high level category that this item belongs to
	 * @param su the medium level category that this item belongs to
	 * @param sp the specific name of the ingredient
	 */
	public Item(int i, String t, String su, String sp) {
		id = i;
		type = t;
		sub_type = su;
		specific_type = sp;
	}
	
	/**
	 * 
	 * @return the unique ID number of the item
	 */
	public int getID() { return id; }
	/**
	 * 
	 * @return high level category that this item belongs to
	 */
	public String getType() { return type; }
	/**
	 * 
	 * @return the medium level category that this item belongs to
	 */
	public String getSubType() { return sub_type; }
	/**
	 * 
	 * @return the specific name of the ingredient
	 */
	public String getSpecificType() { return specific_type; }
	
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
		
		return getFromDatabase(db, id);
	}
	
	/**
	 * 
	 * @param db SQLite database relating UPC codes to item information
	 * @param spec the specific name of the ingredient
	 * @return the item ID for the item
	 */
	public static int findIDFromDatabase(SQLiteDatabase db, String spec) {
		String sql = "SELECT id FROM Items WHERE specific_type = '" + spec + "'";
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
	 * @param type the high level category that this item belongs to
	 * @param sub the medium level category that this item belongs to
	 * @param spec the specific name of the ingredient
	 * @return the item ID for the item
	 */
	public static int findIDFromDatabase(SQLiteDatabase db, String type, String sub, String spec) {
		String sql = "SELECT id FROM Items WHERE type = '" + type + "' AND sub_type = '" + sub + 
						"' AND specific_type = '" + spec + "'";
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
		String sql = "SELECT specific_type FROM Items WHERE id = " + id;
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
	 * @return Item object represented the ingredient
	 */
	public static Item getFromDatabase(SQLiteDatabase db, int id) {
		String sql_statement = "SELECT type, sub_type, specific_type FROM Items WHERE id = " + id;
		Cursor c = db.rawQuery(sql_statement, null);
		
		if(c == null || c.getCount() == 0) {
			if(c != null) { c.close(); }
			
			return null;
		}
		
		c.moveToFirst();
		
		Item item = new Item(id, c.getString(0), c.getString(1), c.getString(2));
		
		c.close();
		
		return item;
	}
	
	/**
	 * 
	 * @param db SQLite database containing item information
	 * @return array of all high level ingredient categories
	 */
	public static String[] getAllTypes(SQLiteDatabase db) {
		if(db == null) { return null; }
		String sql = "SELECT type FROM Items ORDER BY type";
		Cursor c = db.rawQuery(sql, null);
		
		if(c == null || c.getCount() == 0) {
			if(c != null) { c.close(); }
			
			return null;
		}
		
		c.moveToFirst();
		
		String[] res = new String[c.getCount()];
		int n = 0;
		while(!c.isAfterLast()) {
			res[n] = c.getString(0);
			n++;
			c.moveToNext();
		} 
		
		c.close();
		
		return res;
	}
	
	/**
	 * 
	 * @param db SQLite database containing item information
	 * @param type a high level ingredient category
	 * @return all medium level categories that belong to this category
	 */
	public static String[] getAllSubTypes(SQLiteDatabase db, String type) {
		String sql = "SELECT sub_type FROM Items WHERE type = '" + type + "' ORDER BY sub_type";
		Cursor c = db.rawQuery(sql, null);
		
		if(c == null || c.getCount() == 0) {
			if(c != null) { c.close(); }
			
			return null;
		}
		
		c.moveToFirst();
		
		String[] res = new String[c.getCount()];
		int n = 0;
		while(!c.isAfterLast()) {
			res[n] = c.getString(0);
			n++;
			c.moveToNext();
		}
		
		c.close();
		
		return res;
	}
	
	/**
	 * 
	 * @param db SQLite database containing item information
	 * @param type a high level ingredient category
	 * @param subtype a medium level ingredient category
	 * @return array of all specific ingredient names belonging to this medium level type
	 */
	public static String[] getAllSpecificTypes(SQLiteDatabase db, String type, String subtype) {
		String sql = "SELECT specific_type FROM Items WHERE type = '" + type + "' AND sub_type = '" + subtype +
						"' ORDER BY specific_type";
		Cursor c = db.rawQuery(sql, null);
		
		if(c == null || c.getCount() == 0) {
			if(c != null) { c.close(); }
			
			return null;
		}
		
		c.moveToFirst();
		
		String[] res = new String[c.getCount()];
		int n = 0;
		while(!c.isAfterLast()) {
			res[n] = c.getString(0);
			n++;
			c.moveToNext();
		}
		
		c.close();
		
		return res;
	}
	
	/**
	 * 
	 * @param db SQLite database containing item information
	 * @return array of all specific ingredient names
	 */
	public static String[] getAllSpecificTypes(SQLiteDatabase db) {
		String sql = "SELECT specific_type FROM Items ORDER BY specific_type";
		Cursor c = db.rawQuery(sql, null);
		
		if(c == null || c.getCount() == 0) {
			if(c != null) { c.close(); }
			
			return null;
		}
		
		c.moveToFirst();
		
		String[] res = new String[c.getCount()];
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
