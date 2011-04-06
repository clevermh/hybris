package com.kumquat.hybris;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Recipe {
	private Ingredient[] ingredients;
	private String[] directions;
	private String prep_time;
	private String cook_time;
	private String serving_size;
	private String type;
	private String name;
	
	/**
	 * Create a new recipe with the given information
	 * @param name the name of the recipe
	 * @param ingredients the ingredients in the recipe
	 * @param directions the directions for the recipe
	 * @param prep_time the prep time for the recipe
	 * @param cook_time the cook time for the recipe
	 * @param serving_size the serving size of the recipe
	 * @param type the type of the recipe
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
	 * @return the number of ingredients
	 */
	public int numIngredients() {
		return ingredients.length;
	}
	
	/**
	 * @param which the location in the ingredients array to return
	 * @return the requested ingredient
	 */
	public Ingredient getIngredient(int which) {
		return ingredients[which];
	}
	
	/**
	 * @return the ingredients
	 */
	public Ingredient[] getIngredients() {
		return ingredients;
	}

	/**
	 * @return the number of directions
	 */
	public int numDirections() {
		return directions.length;
	}
	
	/**
	 * @param which the location in the directions array to return
	 * @return the requested direction
	 */
	public String getDirection(int which) {
		return directions[which];
	}

	/**
	 * @return the directions
	 */
	public String[] getDirections() {
		return directions;
	}

	/**
	 * @return the prep time
	 */
	public String getPrepTime() {
		return prep_time;
	}

	/**
	 * @return the cook time
	 */
	public String getCookTime() {
		return cook_time;
	}

	/**
	 * @return the serving size
	 */
	public String getServingSize() {
		return serving_size;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the names of all the recipes in the given database
	 * @param db the database to search for recipes
	 * @return the names of all recipes found
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
	 * @param ingredients the list of ingredients to search with
	 * @param db the database to search for recipes
	 * @return the names of all recipes found
	 */
	public static String[] getAllRecipeNamesWithIngredients(int[] ingredients, SQLiteDatabase db) {
		String ing = "(";
		for(int a = 0; a < ingredients.length; a++) {
			if(a != 0) { ing += ","; }
			ing += ingredients[a];
		}
		ing += ")";
		
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
	 * Gets a recipe from the database by name
	 * @param name the name of the recipe to find
	 * @param db the database to search for the recipe
	 * @return the recipe if found, null otherwise
	 */
	public static Recipe getFromDatabase(String name, SQLiteDatabase db) {
		String rinfosql = "SELECT id, prep_time, cook_time, serving_size, type FROM Recipes WHERE name = \"" + name + "\"";
		Cursor rinfoc = db.rawQuery(rinfosql, null);
		
		if(rinfoc == null) { return null; }
		if(rinfoc.getCount() != 1) { rinfoc.close(); return null; }
		
		rinfoc.moveToFirst();
		int id = rinfoc.getInt(0);
		
		String ringsql = "SELECT item_id, qty, qty_metric FROM Ingredients WHERE recipe_id = " + id;
		Cursor ringc = db.rawQuery(ringsql, null);
		
		if(ringc == null) { rinfoc.close(); return null; }
		if(ringc.getCount() < 1) { rinfoc.close(); ringc.close(); return null; }
		
		String rdirsql = "SELECT direction FROM Directions WHERE recipe_id = " + id + " ORDER BY dir_number";
		Cursor rdirc = db.rawQuery(rdirsql, null);
		
		if(rdirc == null) { rinfoc.close(); ringc.close(); return null; }
		if(rdirc.getCount() < 1) { rinfoc.close(); ringc.close(); rdirc.close(); return null; }
		
		// Fun stuff here
		String prep = rinfoc.getString(1);
		String cook = rinfoc.getString(2);
		String serv = rinfoc.getString(3);
		String type = rinfoc.getString(4);
		
		rdirc.moveToFirst();
		String[] dirs = new String[rdirc.getCount()];
		for(int a = 0; a < dirs.length; a++) {
			dirs[a] = rdirc.getString(0);
			rdirc.moveToNext();
		}
		
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
