package com.kumquat.hybris.databases;

import java.io.IOException;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kumquat.hybris.Ingredient;
import com.kumquat.hybris.Item;
import com.kumquat.hybris.R;
import com.kumquat.hybris.Recipe;

/**
 * A helper that returns back a reference to the Hybris Database.
 */
public class HybrisDatabaseHelper extends SQLiteOpenHelper  {
	private Context context;
	private SQLiteDatabase database;
	private boolean populating = false;
	
	// The current version of the database structure
	// Changing this will cause the databases to be deleted and recreated
	public static final int VERSION = 1;
	
	// The CREATE TABLE statements for all the tables
	private static final String item_table = "CREATE TABLE IF NOT EXISTS Items (" +
											"id INTEGER PRIMARY KEY AUTOINCREMENT," +
											"name varchar(100) NOT NULL default ''" +
											");";
	
	private static final String plu_table = "CREATE TABLE IF NOT EXISTS Plu (" +
											"id INTEGER PRIMARY KEY AUTOINCREMENT," +
											"item_id INTEGER NOT NULL," +
											"plu_code varchar(5) default '0'," +
											"FOREIGN KEY (item_id) REFERENCES Items(id)" +
											");";
	
	private static final String upc_table = "CREATE TABLE IF NOT EXISTS Upc (" +
											"id INTEGER PRIMARY KEY AUTOINCREMENT," +
											"upc_code varchar(12) NOT NULL default ''," +
											"item_id INTEGER NOT NULL," +
											"FOREIGN KEY (item_id) REFERENCES Items(id)" +
											");";
	
	private static final String recipe_table = "CREATE TABLE IF NOT EXISTS Recipes (" +
											   "id INTEGER PRIMARY KEY AUTOINCREMENT," +
											   "name varchar(20) NOT NULL default ''," +
											   "prep_time varchar(20) not NULL default ''," +
											   "cook_time varchar(20) not NULL default ''," +
											   "serving_size varchar(20) not NULL default ''," +
											   "type varchar(20) not NULL default ''," +
											   "num_ing INTEGER NOT NULL" +
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
												  "dir_number INTEGER NOT NULL," +
												  "direction VARCHAR(1000) NOT NULL," +
												  "FOREIGN KEY (recipe_id) REFERENCES Recipes(id)" +
												  ");";
	
	public HybrisDatabaseHelper(Context context) {
		super(context, "HybrisDatabase", null, VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		database = db;
		
		database.execSQL(item_table);
		
		database.execSQL(plu_table);
		
		database.execSQL(upc_table);
		
		database.execSQL(recipe_table);
		database.execSQL(ingredient_table);
		database.execSQL(direction_table);
		
		populate();
	}
	
	/**
	 * Populate the database from the files
	 */
	private void populate() {
		// Do it in a thread to avoid some dumb UI blocking
		new Thread(new Runnable() {
            public void run() {
            	populating = true;
            	final Resources resources = context.getResources();
                try {
                    loadItems(resources);
                    loadRecipes(resources);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                populating = false;
            }
        }).start();
	}
	
	/**
	 * Check if the database is busy populating
	 * @return True if the database is populating, false otherwise
	 */
	public boolean isPopulating() { return populating; }
	
	private void loadRecipes(final Resources resources) throws IOException {
		Log.d("HybrisDatabase", "Loading Recipes");
		
		// Get all the recipes from the YAML
		Recipe[] recipes = YAMLParser.parseRecipesFromRes(resources, R.raw.recipes, database);
		
		// Add each one to the DB
		for(Recipe r : recipes) {
			if(!addRecipe(r.getName(), r.getIngredients(), r.getDirections(), r.getPrepTime(), r.getCookTime(), r.getServingSize(), r.getType())) {
				Log.e("HybrisDatabase", "Unable to add: " + r.getName());
			}
		}
		
		Log.d("HybrisDatabase", "Done loading Recipes (" + recipes.length + ")");
	}
	
	private void loadItems(final Resources resources) throws IOException {
		Log.d("HybrisDatabase", "Loading Items");
		
		// Get all the items from the YAML
		Item[] items = YAMLParser.parseItemsFromRes(resources, R.raw.items);
		
		// Add each one to the DB
		for(Item i : items) {
			if(addItem(i.getID(), i.getName())) {
				if(i.hasUPCs()) {
					String[] upcs = i.getUPCs();
					for(String s : upcs) { addUPC(s, i.getID()); }
				}
				
				if(i.hasPLUs()) {
					String[] plus = i.getPLUs();
					for(String s : plus) { addPLU(s, i.getID()); }
				}
			} else {
				Log.e("HybrisDatabase", "Unable to add: " + i.getName());
			}
		}
		
		Log.d("HybrisDatabase", "Done loading Items (" + items.length + ")");
	}

	// Drop all the tables and then call onCreate(..)
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("HybrisDatabase", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
		
		db.execSQL("DROP TABLE IF EXISTS Items");
		
		db.execSQL("DROP TABLE IF EXISTS Plu");
		
		db.execSQL("DROP TABLE IF EXISTS Upc");
		
		db.execSQL("DROP TABLE IF EXISTS Recipes");
		db.execSQL("DROP TABLE IF EXISTS Ingredients");
		db.execSQL("DROP TABLE IF EXISTS Directions");
		
        onCreate(db);
	}
	
	/**
	 * Adds an item to the Items table
	 * @param iid ID of the Item
	 * @param name Name of the Item
	 * @return True if the add worked, false otherwise
	 */
	public boolean addItem(int iid, String name) {
		ContentValues item = new ContentValues();
		item.put("id", iid);
		item.put("name", name);
		
		long id = database.insertOrThrow("Items", null, item);
		
		return id != -1;
	}
	
	/**
	 * Adds a PLU code to the PLU table
	 * @param code PLU code
	 * @param iid Item ID
	 * @return True if the add worked, false otherwise
	 */
	public boolean addPLU(String code, int iid) {
		ContentValues item = new ContentValues();
		item.put("plu_code", code);
		item.put("item_id", iid);
		
		long id = database.insertOrThrow("Plu", null, item);
		
		return id != -1;
	}
	
	/**
	 * Adds a UPC code to the UPC table
	 * @param code UPC code
	 * @param iid Item ID
	 * @return True if the add worked, false otherwise
	 */
	public boolean addUPC(String code, int iid) {
		ContentValues item = new ContentValues();
		item.put("upc_code", code);
		item.put("item_id", iid);
		
		long id = database.insertOrThrow("Upc", null, item);
		
		return id != -1;
	}
	
	/**
	 * Adds a recipe into the Recipes table
	 * @param name Name of the recipe
	 * @param ingredients List of ingredients in the recipe
	 * @param directions List of directions for the recipe
	 * @param prepTime The preparation time  
	 * @param cookTime The cooking time
	 * @param servingSize The serving size
	 * @param type The recipe type
	 * @return True if the add worked, false otherwise
	 */
	public boolean addRecipe(String name, Ingredient[] ingredients, String[] directions, String prepTime, 
			String cookTime, String servingSize, String type) {
		ContentValues recipe_value = new ContentValues();
		ContentValues ingredient_value = new ContentValues();
		ContentValues direction_value = new ContentValues();
		
		recipe_value.put("name", name);
		recipe_value.put("prep_time", prepTime);
		recipe_value.put("cook_time", cookTime);
		recipe_value.put("serving_size", servingSize);
		recipe_value.put("type", type);
		recipe_value.put("num_ing", ingredients.length);
		
		// Add the recipe info to the DB
		long id = database.insertOrThrow("Recipes", null, recipe_value);
		if (id == -1) { return false; }
		int recipe_id = (int)id;
		
		// Add each ingredient to the DB
		for (int i = 0; i < ingredients.length; i++) {
			Ingredient ing = ingredients[i];
			ingredient_value.put("recipe_id", recipe_id);
			ingredient_value.put("item_id", ing.getItemId());
			ingredient_value.put("qty", ing.getQuantity());
			ingredient_value.put("qty_metric", ing.getQuantityMetric());
			
			id = database.insertOrThrow("Ingredients", null, ingredient_value);
			if (id == -1) { return false; }
		}
		
		// Add each direction to the DB
		for (int i = 0; i < directions.length; i++) {
			direction_value.put("recipe_id", recipe_id);
			direction_value.put("dir_number", i);
			direction_value.put("direction", directions[i]);
			
			id = database.insertOrThrow("Directions", null, direction_value);
			if (id == -1) { return false; }
		}
		
		return true;
	}
}
