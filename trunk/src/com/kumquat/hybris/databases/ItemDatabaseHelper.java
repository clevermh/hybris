package com.kumquat.hybris.databases;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.kumquat.hybris.R;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ItemDatabaseHelper extends SQLiteOpenHelper {
	private Context context;
	private SQLiteDatabase item_database;
	public static final int VERSION = 2;
	
	private static final String item_table = "CREATE TABLE IF NOT EXISTS Items (" +
											"id INTEGER PRIMARY KEY AUTOINCREMENT," +
											"type varchar(100) NOT NULL default ''," +
											"sub_type varchar(100) NOT NULL default ''," +
											"specific_type varchar(100) NOT NULL default ''" +
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
		/*new Thread(new Runnable() {
            public void run() {
                try {
                    loadItems();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();*/
		
		try {
            loadItems();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        		
        		ContentValues item = new ContentValues();
        		item.put("type", split[0].trim());
        		item.put("sub_type", split[1].trim());
        		item.put("specific_type", split[2].trim());
        		
        		long id = item_database.insertOrThrow("Items", null, item);
        		
        		if(id == -1) {
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
	
}
