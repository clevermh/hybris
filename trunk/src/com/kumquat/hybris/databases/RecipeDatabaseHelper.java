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

public class RecipeDatabaseHelper extends SQLiteOpenHelper {
	private Context context;
	private SQLiteDatabase recipe_database;
	public static final int VERSION = 1;
	
	private static final String recipe_table = "CREATE TABLE IF NOT EXISTS Recipes (" +
											"id INTEGER PRIMARY KEY AUTOINCREMENT," +
											"name varchar(20) NOT NULL default ''" +
											");";
	
	private static final String igredient_table = "CREATE TABLE IF NOT EXISTS Ingredients (" +
												"recipe_id INTEGER NOT NULL," +
												"item_id INTEGER NOT NULL," +
												"qty INTEGER NOT NULL," +
												"FOREIGN KEY (recipe_id) REFERENCES Recipes(id)," +
												"FOREIGN KEY (item_id) REFERENCES Items(id)" +
												")";
	
	private static final String direction_table = "CREATE TABLE IF NOT EXISTS Directions (" +
												"recipe_id INTEGER NOT NULL," +
												"direction VARCHAR(1000) NOT NULL," +
												"FOREIGN KEY (recipe_id) REFERENCES Recipes(id)" +
												");";
	
	public RecipeDatabaseHelper(Context context) {
		super(context, "RecipeDatabase", null, VERSION);
		
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		recipe_database = db;
		recipe_database.execSQL(recipe_table);
		
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
		Log.d("RecipeDatabase", "Loading items.");
		
		/*final Resources resources = context.getResources();
        InputStream inputStream = resources.openRawResource(R.raw.upcs);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        
        try {
        	String line;
        	while((line = reader.readLine()) != null) {
        		if(line.startsWith("#")) { continue; }
        		
        		String[] split = line.split("\t");
        		if(split.length != 2) { continue; }
        		
        		if(!addUPC(split[0].trim(), Integer.parseInt(split[1].trim()), false)) {
        			Log.e("UPCDatabase", "Unable to add: " + line.trim());
        		}
        	}
        } finally {
        	reader.close();
        }*/
		
		Log.d("RecipeDatabase", "Done loading items.");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("RecipeDatabase", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS Recipe_Table");
        onCreate(db);
	}
	
	/*public boolean addUPC(String code, int iid, boolean user) {
		ContentValues item = new ContentValues();
		item.put("upc_code", code);
		item.put("item_id", iid);
		item.put("user_added", (user ? 1 : 0));
		
		long id = upc_database.insertOrThrow("Upc_Table", null, item);
		
		return id != -1;
	}*/
}
