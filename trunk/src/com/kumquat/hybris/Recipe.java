package com.kumquat.hybris;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * An object that represents a recipe
 */
public class Recipe {
	private Ingredient[] ingredients;
	private String[] directions;
	private String prep_time;
	private String cook_time;
	private String serving_size;
	private String type;
	private String name;
	
	/**
	 * Create a new Recipe with the given information
	 * @param name The name of the Recipe
	 * @param ingredients The ingredients in the Recipe
	 * @param directions The directions for the Recipe
	 * @param prep_time The prep time for the Recipe
	 * @param cook_time The cook time for the Recipe
	 * @param serving_size The serving size of the Recipe
	 * @param type The type of the Recipe (breakfast, snack, etc.)
	 */
	public Recipe(String name, Ingredient[] ingredients, String[] directions, String prep_time, String cook_time, String serving_size, String type) {
		this.ingredients = ingredients;
		this.directions = directions;
		this.prep_time = prep_time;
		this.cook_time = cook_time;
		this.serving_size = serving_size;
		this.type = type;
		this.name = name;
	}
	
	/**
	 * @return The number of ingredients required for this Recipe
	 */
	public int numIngredients() {
		return ingredients.length;
	}
	
	/**
	 * @param which The location in the ingredients array to return
	 * @return The requested Ingredient
	 */
	public Ingredient getIngredient(int which) {
		return ingredients[which];
	}
	
	/**
	 * @return An array of the ingredients required for this Recipe
	 */
	public Ingredient[] getIngredients() {
		return ingredients;
	}

	/**
	 * @return The number of directions in this Recipe
	 */
	public int numDirections() {
		return directions.length;
	}
	
	/**
	 * @param which The location in the directions array to return
	 * @return The requested direction
	 */
	public String getDirection(int which) {
		return directions[which];
	}

	/**
	 * @return An array of the directions for this Recipe
	 */
	public String[] getDirections() {
		return directions;
	}

	/**
	 * @return The prep time of this Recipe
	 */
	public String getPrepTime() {
		return prep_time;
	}

	/**
	 * @return The cook time of this Recipe
	 */
	public String getCookTime() {
		return cook_time;
	}

	/**
	 * @return The serving size of this Recipe
	 */
	public String getServingSize() {
		return serving_size;
	}

	/**
	 * @return The type of this Recipe
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return The name of this Recipe
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the names of all the recipes in the given database
	 * @param db The database to search for recipes
	 * @return An array containing the names of all recipes found in the database
	 */
	public static String[] getAllRecipeNames(SQLiteDatabase db) {
		String sql = "SELECT name FROM Recipes ORDER BY name";
		Cursor c = db.rawQuery(sql, null);
		
		if(c == null) { return null; }
		
		String[] res = new String[c.getCount()];
		c.moveToFirst();
		
		for(int a = 0; a < res.length; a++) {
			res[a] = c.getString(0);
			c.moveToNext();
		}
		
		c.close();
		
		return res;
	}
	
	/**
	 * Gets the names of all the recipes in the given database that can be made with the given ingredients
	 * @param ingredients The list of ingredients to search with
	 * @param db The database to search for recipes
	 * @return The names of all recipes found, null if none are found
	 */
	public static String[] getAllRecipeNamesWithIngredients(int[] ingredients, SQLiteDatabase db) {
		// Make the set to check ingredients against
		String ing = "(";
		for(int a = 0; a < ingredients.length; a++) {
			if(a != 0) { ing += ","; }
			ing += ingredients[a];
		}
		ing += ")";
		
		// Woo SQL!
		String sql = "SELECT r.name, i.recipe_id " +
					"FROM Recipes r, Ingredients i " +
					"WHERE r.id = i.recipe_id " +
					"AND i.item_id IN " + ing +
					"GROUP BY r.name " +
					"HAVING COUNT(i.recipe_id) = r.num_ing " +
					"ORDER BY r.name";
		
		Cursor c = db.rawQuery(sql, null);
		
		if(c == null) { return null; }
		
		String[] res = new String[c.getCount()];
		c.moveToFirst();
		
		for(int a = 0; a < res.length; a++) {
			res[a] = c.getString(0);
			c.moveToNext();
		}
		
		c.close();
		
		return res;
	}
	
	/**
	 * Gets a Recipe from the database by name
	 * @param name The name of the Recipe to find
	 * @param db The database to search for the Recipe
	 * @return The Recipe if found, null otherwise
	 */
	public static Recipe getFromDatabase(String name, SQLiteDatabase db) {
		// Find all the recipe info
		String rinfosql = "SELECT id, prep_time, cook_time, serving_size, type FROM Recipes WHERE name = \"" + name + "\"";
		Cursor rinfoc = db.rawQuery(rinfosql, null);
		
		if(rinfoc == null) { return null; }
		if(rinfoc.getCount() != 1) { rinfoc.close(); return null; }
		
		rinfoc.moveToFirst();
		int id = rinfoc.getInt(0);
		
		// Find all the ingredient info
		String ringsql = "SELECT item_id, qty, qty_metric FROM Ingredients WHERE recipe_id = " + id;
		Cursor ringc = db.rawQuery(ringsql, null);
		
		if(ringc == null) { rinfoc.close(); return null; }
		if(ringc.getCount() < 1) { rinfoc.close(); ringc.close(); return null; }
		
		// Find all the directions
		String rdirsql = "SELECT direction FROM Directions WHERE recipe_id = " + id + " ORDER BY dir_number";
		Cursor rdirc = db.rawQuery(rdirsql, null);
		
		if(rdirc == null) { rinfoc.close(); ringc.close(); return null; }
		if(rdirc.getCount() < 1) { rinfoc.close(); ringc.close(); rdirc.close(); return null; }
		
		// Fun stuff here
		String prep = rinfoc.getString(1);
		String cook = rinfoc.getString(2);
		String serv = rinfoc.getString(3);
		String type = rinfoc.getString(4);
		
		// Put the found directions into the Recipe
		rdirc.moveToFirst();
		String[] dirs = new String[rdirc.getCount()];
		for(int a = 0; a < dirs.length; a++) {
			dirs[a] = rdirc.getString(0);
			rdirc.moveToNext();
		}
		
		// Put the found ingredients into the Recipe
		ringc.moveToFirst();
		Ingredient[] ings = new Ingredient[ringc.getCount()];
		for(int a = 0; a < ings.length; a++) {
			int iid = ringc.getInt(0);
			double qty = ringc.getDouble(1);
			String qtymet = ringc.getString(2);
			ings[a] = new Ingredient(iid, qty, qtymet, db);
			ringc.moveToNext();
		}
		
		rinfoc.close();
		ringc.close();
		rdirc.close();
		
		return new Recipe(name, ings, dirs, prep, cook, serv, type);
	}
}
