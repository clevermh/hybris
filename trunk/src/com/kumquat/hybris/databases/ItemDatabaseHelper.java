package com.kumquat.hybris.databases;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.kumquat.hybris.R;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ItemDatabaseHelper extends SQLiteOpenHelper {
	private Context context;
	private SQLiteDatabase item_database;
	public static final int VERSION = 3;
	
	private static final String item_table = "CREATE TABLE IF NOT EXISTS Items (" +
											"id INTEGER PRIMARY KEY AUTOINCREMENT," +
											"type varchar(100) NOT NULL default ''," +
											"sub_type varchar(100) NOT NULL default ''," +
											"specific_type varchar(100) NOT NULL default ''," +
											"user_added INTEGER default 0" +
											");";
	
	private static final String item_idx = "CREATE INDEX IF NOT EXISTS item_idx_01 ON Items(type, sub_type, specific_type, id);";
	
	public ItemDatabaseHelper(Context context) {
		super(context, "ItemDatabase", null, VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		item_database = db;
		item_database.execSQL(item_table);
		item_database.execSQL(item_idx);
		
		populate();
	}
	
	private void populate() {
		new Thread(new Runnable() {
            public void run() {
                try {
                    loadItems();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
	}
	
	private void loadItems() throws IOException {
		Log.d("ItemDatabase", "Loading items.");
		
		final Resources resources = context.getResources();
        InputStream inputStream = resources.openRawResource(R.raw.items);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        
        try {
        	String line;
        	while((line = reader.readLine()) != null) {
        		if(line.startsWith("#")) { continue; }
        		
        		String[] split = line.split("\t");
        		if(split.length != 3) { continue; }
        		
        		if(!addItem(split[0].trim(), split[1].trim(), split[2].trim(), false)) {
        			Log.e("ItemDatabase", "Unable to add: " + line.trim());
        		}
        	}
        } finally {
        	reader.close();
        }
		
		Log.d("ItemDatabase", "Done loading items.");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("ItemDatabase", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS Items");
		db.execSQL("DROP INDEX IF EXISTS item_idx_01;");
        onCreate(db);
	}
	
	public String[] getAllTypes() {
		String sql = "SELECT DISTINCT type FROM Items SORT BY type";
		Cursor c = item_database.rawQuery(sql, null);
		
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
	
	public String[] getAllSubTypes(String type) {
		String sql = "SELECT DISTINCT sub_type FROM Items WHERE type = " + type + " SORT BY type";
		Cursor c = item_database.rawQuery(sql, null);
		
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
	
	public String[] getAllSpecificTypes(String type, String subtype) {
		String sql = "SELECT DISTINCT specific_type FROM Items WHERE type = " + type + " AND sub_type = " + subtype + " SORT BY type";
		Cursor c = item_database.rawQuery(sql, null);
		
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
	
	public boolean addItem(String type, String sub, String spec, boolean user) {
		ContentValues item = new ContentValues();
		item.put("type", type);
		item.put("sub_type", sub);
		item.put("specific_type", spec);
		item.put("user_added", (user ? 1 : 0));
		
		long id = item_database.insertOrThrow("Items", null, item);
		
		return id != -1;
	}
}
