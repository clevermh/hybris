package com.kumquat.hybris;

import java.util.HashMap;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kumquat.hybris.databases.UPCObject;

public class ItemLookup {
	private UPCObject getItemByDatabase(String code, SQLiteDatabase db) {
		String sql_statement = "SELECT upc_code, upc_e, ean_code, description, amount FROM upctable " +
								"WHERE upc_code = ?";
		Cursor c = db.rawQuery(sql_statement, new String[]{code});
		
		if(c == null || c.getCount() == 0) { if(c != null) { c.close(); } return null; }
		
		HashMap <String, String> upc_info = new HashMap<String, String>();
		c.moveToFirst();
		
		
		UPCObject upc_object = new UPCObject(upc_info.get("upc_code"));
        upc_object.addUPCInformation(upc_info);
		
		c.close();
		db.close();
		
		return upc_object;
	}
}
