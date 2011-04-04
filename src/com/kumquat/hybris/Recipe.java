package com.kumquat.hybris;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Recipe {
	public Ingredient[] ingredients;
	public String[] directions;
	public String prep_time;
	public String cook_time;
	public String serving_size;
	public String type;
	public String name;
	
	public Recipe(String name, Ingredient[] ingredients, String[] directions, String prep_time, String cook_time, String serving_size, String type) {
		this.ingredients = ingredients;
		this.directions = directions;
		this.prep_time = prep_time;
		this.cook_time = cook_time;
		this.serving_size = serving_size;
		this.type = type;
		this.name = name;
	}
	
	public void printToDebug() {
		Log.d("DBG_OUT", "******************************");
		Log.d("DBG_OUT", "Name: " + name);
		Log.d("DBG_OUT", "PTime: " + prep_time);
		Log.d("DBG_OUT", "CTime: " + cook_time);
		for(int a = 0; a < ingredients.length; a++) {
			ingredients[a].printToDebug();
		}
		for(int a = 0; a < directions.length; a++) {
			Log.d("DBG_OUT", a + ") " + directions[a]);
		}
		
	}
	
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
