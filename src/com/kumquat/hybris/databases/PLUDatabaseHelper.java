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

public class PLUDatabaseHelper extends SQLiteOpenHelper {
	private Context context;
	private SQLiteDatabase plu_database;
	public static final int VERSION = 3;
	
	private static final String plu_table = "CREATE TABLE IF NOT EXISTS Plu_Table (" +
											"id INTEGER PRIMARY KEY AUTOINCREMENT," +
											"item_id INTEGER NOT NULL," +
											"plu_code varchar(5) default '0'," +
											"user_added INTEGER default 0," +
											"FOREIGN KEY (item_id) REFERENCES Items(id)," +
											"UNIQUE (plu_code)" +
											");";
	
	public PLUDatabaseHelper(Context context) {
		super(context, "PLUDatabase", null, VERSION);
		
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		plu_database = db;
		plu_database.execSQL(plu_table);
		
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
		Log.d("PLUDatabase", "Loading items.");
		
		final Resources resources = context.getResources();
        InputStream inputStream = resources.openRawResource(R.raw.plus);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        
        try {
        	String line;
        	while((line = reader.readLine()) != null) {
        		if(line.startsWith("#")) { continue; }
        		
        		String[] split = line.split("\t");
        		if(split.length != 2) { continue; }
        		
        		if(!addPLU(split[0].trim(), Integer.parseInt(split[1].trim()), false)) {
        			Log.e("PLUDatabase", "Unable to add: " + line.trim());
        		}
        	}
        } finally {
        	reader.close();
        }
		
		Log.d("PLUDatabase", "Done loading items.");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("PLUDatabase", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS Plu_Table");
        onCreate(db);
	}
	
	public boolean addPLU(String code, int iid, boolean user) {
		ContentValues item = new ContentValues();
		item.put("plu_code", code);
		item.put("item_id", iid);
		item.put("user_added", (user ? 1 : 0));
		
		long id = plu_database.insertOrThrow("Plu_Table", null, item);
		
		return id != -1;
	}
}
