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

public class UPCDatabaseHelper extends SQLiteOpenHelper {
	private Context context;
	private SQLiteDatabase upc_database;
	public static final int VERSION = 3;
	
	private static final String upc_table = "CREATE TABLE IF NOT EXISTS Upc_Table (" +
											"id INTEGER PRIMARY KEY AUTOINCREMENT," +
											"upc_code varchar(12) NOT NULL default ''," +
											"item_id INTEGER NOT NULL," +
											"user_added INTEGER default 0," +
											"FOREIGN KEY (item_id) REFERENCES Items(id)" +
											");";
	
	public UPCDatabaseHelper(Context context) {
		super(context, "UPCDatabase", null, VERSION);
		
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		upc_database = db;
		upc_database.execSQL(upc_table);
		
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
		Log.d("UPCDatabase", "Loading items.");
		
		final Resources resources = context.getResources();
        InputStream inputStream = resources.openRawResource(R.raw.upcs);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        
        try {
        	String line;
        	while((line = reader.readLine()) != null) {
        		if(line.startsWith("#")) { continue; }
        		
        		String[] split = line.split("\t");
        		if(split.length != 2) { continue; }
        		
        		ContentValues item = new ContentValues();
        		item.put("upc_code", split[0].trim());
        		item.put("item_id", split[1].trim());
        		
        		long id = upc_database.insertOrThrow("Upc_Table", null, item);
        		
        		if(id == -1) {
        			Log.e("UPCDatabase", "Unable to add: " + line.trim());
        		}
        	}
        } finally {
        	reader.close();
        }
		
		Log.d("UPCDatabase", "Done loading items.");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("UPCDatabase", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS Upc_Table");
        onCreate(db);
	}
	
}
