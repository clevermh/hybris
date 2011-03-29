package com.kumquat.hybris;

import com.kumquat.hybris.databases.HybrisDatabaseHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Item {
	private final int id;
	private final String type;
	private final String sub_type;
	private final String specific_type;
	
	public Item(int i, String t, String su, String sp) {
		id = i;
		type = t;
		sub_type = su;
		specific_type = sp;
	}
	
	public int getID() { return id; }
	public String getType() { return type; }
	public String getSubType() { return sub_type; }
	public String getSpecificType() { return specific_type; }
	
	public static int findIDFromDatabase(SQLiteDatabase db, String type, String sub, String spec) {
		String sql = "SELECT id FROM Items WHERE type = '" + type + "' AND sub_type = '" + sub + 
						"' AND specific_type = '" + spec + "'";
		Cursor c = db.rawQuery(sql, null);
		
		if(c == null || c.getCount() == 0) {
			if(c != null) { c.close(); }
			
			return -1;
		}
		
		c.moveToFirst();
		return c.getInt(0);
	}
	
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
	
	public static String[] getAllTypes(SQLiteDatabase db) {
		if(db == null) { return null; }
		String sql = "SELECT DISTINCT type FROM Items ORDER BY type";
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
		
		return res;
	}
	
	public static String[] getAllSubTypes(SQLiteDatabase db, String type) {
		String sql = "SELECT DISTINCT sub_type FROM Items WHERE type = '" + type + "' ORDER BY sub_type";
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
		
		return res;
	}
	
	public static String[] getAllSpecificTypes(SQLiteDatabase db, String type, String subtype) {
		String sql = "SELECT DISTINCT specific_type FROM Items WHERE type = '" + type + "' AND sub_type = '" + subtype +
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
		
		return res;
	}
}