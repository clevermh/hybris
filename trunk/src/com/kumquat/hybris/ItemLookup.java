package com.kumquat.hybris;

import java.util.HashMap;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kumquat.hybris.databases.UPCObject;

public static class ItemLookup {
	private int getItemByDatabase(String code, SQLiteDatabase db) {
		String sql_statement = "SELECT upc_code, upc_e, ean_code, description, amount FROM upctable " +
								"WHERE upc_code = ?";
		Cursor c = db.rawQuery(sql_statement, new String[]{code});
		
		if(c == null || c.getCount() == 0) { if(c != null) { c.close(); } return -1; }
		
		HashMap <String, String> upc_info = new HashMap<String, String>();
		c.moveToFirst();
		
		int ItemID = c.getInt(0);
		
		c.close();
		db.close();
		
		return ItemID;
	}
}
