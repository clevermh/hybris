package com.kumquat.hybris.databases;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.kumquat.hybris.Ingredients;
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
											   "name varchar(20) NOT NULL default ''," +
											   "prep_time varchar(20) not NULL default ''," +
											   "cook_time varchar(20) not NULL default ''," +
											   "serving_size varchar(20) not NULL default ''," +
											   "type varchar(20) not NULL default ''" +
											   ");";
	
	private static final String ingredient_table = "CREATE TABLE IF NOT EXISTS Ingredients (" +
												   "recipe_id INTEGER NOT NULL," +
												   "item_id INTEGER NOT NULL," +
												   "qty INTEGER NOT NULL," +
												   "qty_metric varchar(20) NOT NULL default ''," +
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
		recipe_database.execSQL(ingredient_table);
		recipe_database.execSQL(direction_table);
		
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
		db.execSQL("DROP TABLE IF EXISTS Recipes");
		db.execSQL("DROP TABLE IF EXISTS Ingredients");
		db.execSQL("DROP TABLE IF EXISTS Directions");
        onCreate(db);
	}
	
	public boolean addRecipe(String name, Ingredients[] ingredients, String[] directions, String prepTime, 
			String cookTime, String servingSize, String type) {
		ContentValues recipe_value = new ContentValues();
		ContentValues ingredient_value = new ContentValues();
		ContentValues direction_value = new ContentValues();
		
		recipe_value.put("name", name);
		recipe_value.put("prep_time", prepTime);
		recipe_value.put("cook_time", cookTime);
		recipe_value.put("serving_size", servingSize);
		recipe_value.put("type", type);
		int recipe_id = recipe_value.getAsInteger("id");
		
		for (int i = 0; i < ingredients.length; i++) {
			Ingredients ing = ingredients[i];
			ingredient_value.put("recipe_id", recipe_id);
			ingredient_value.put("item_id", ing.getItemId());
			ingredient_value.put("qty", ing.getQuantity());
			ingredient_value.put("qty_metric", ing.getQuantityMetric());
		}
		
		for (int i = 0; i < directions.length; i++) {
			direction_value.put("recipe_id", recipe_id);
			direction_value.put("direction", directions[i]);
		}
		
		long id = recipe_database.insertOrThrow("Recipes", null, recipe_value);
		if (id == -1) { return false; }

		id = recipe_database.insertOrThrow("Ingredients", null, ingredient_value);
		if (id == -1) { return false; }
		
		id = recipe_database.insertOrThrow("Directions", null, direction_value);
		if (id == -1) { return false; }
		
		return true;
	}
}




