package com.kumquat.hybris;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ItemLookup {
	
	public static int getItemIDfromCode(String code, SQLiteDatabase db, String sql_statement) {
		//String sql_statement = "SELECT upc_code, upc_e, ean_code, description, amount FROM upctable " + "WHERE upc_code = ?";
		Cursor c = db.rawQuery(sql_statement, new String[]{code});
		
		if(c == null || c.getCount() == 0) { if(c != null) { c.close(); } return -1; }
		
		c.moveToFirst();
		
		int ItemID = c.getInt(0);
		
		c.close();
		db.close();
		
		return ItemID;
	}
}
