package com.kumquat.hybris;

import com.kumquat.hybris.databases.*;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ItemLookup {
	public static int getItemIDByDatabase(Context con, String code, String sql_statement) {
		//String sql_statement = "SELECT item_id FROM Upc_Table " + "WHERE upc_code = ?";
		
		UPCDatabaseHelper udbh = new UPCDatabaseHelper(con);
		SQLiteDatabase udb = udbh.getReadableDatabase();
		Cursor c = udb.rawQuery(sql_statement, new String[]{code});
		
		if(c == null || c.getCount() == 0) { if(c != null) { c.close(); } return -1; }
		
		c.moveToFirst();
		
		int ItemID = c.getInt(0);
		
		c.close();
		udb.close();
		
		return ItemID;
	}
}
