package com.kumquat.hybris.databases;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;

import com.kumquat.hybris.Ingredient;
import com.kumquat.hybris.Item;
import com.kumquat.hybris.Recipe;

/**
 * A class that allows for YAML files to be parsed into certain things
 */
public class YAMLParser {
	/**
	 * Gets a list of Recipes that are in the given YAML resource file
	 * @param resources the resources to get the file from
	 * @param resid the id of the resource to load
	 * @param db the database to find item IDs from
	 * @return an array containing the Recipes from the file
	 */
	@SuppressWarnings("unchecked")
	public static Recipe[] parseRecipesFromRes(final Resources resources, int resid, SQLiteDatabase db) {
		Yaml yaml = new Yaml();
		InputStream inputStream = resources.openRawResource(resid);
		List<Object> result = (List<Object>)yaml.load(inputStream);
		Recipe[] recipes = new Recipe[result.size()];
		int count = 0;
		
		for(Object o : result) {
			//Map<String, Object> data = (Map<String, Object>)result.get(k);
			Map<String, Object> data = (Map<String, Object>)o;

			// Basic info
			String name = data.get("name").toString();
			String prep = data.get("prep").toString();
			String cook = data.get("cook").toString();
			String size = data.get("size").toString();
			String type = data.get("type").toString();
			
			// Directions
			List<String> alldirs = (List<String>)data.get("directions");
			String[] dirs = new String[alldirs.size()];
			alldirs.toArray(dirs);
			
			// Ingredients
			List<Object> allings = (List<Object>)data.get("ingredients");
			Ingredient[] ings = new Ingredient[allings.size()];
			for(int a = 0; a < ings.length; a++) {
				Map<String, Object> thising = (Map<String, Object>)allings.get(a);
				double qty = Double.parseDouble(thising.get("qty").toString());
				String qtymet = thising.get("qty_metric").toString();
				String ing_name = thising.get("ing_name").toString();
				ings[a] = new Ingredient(ing_name, qty, qtymet, db);
			}
			
			recipes[count] = new Recipe(name, ings, dirs, prep, cook, size, type);
			count++;
		}
		
		return recipes;
	}
	
	/**
	 * Get a list of Items that are in the given YAML resource file
	 * @param resources the resources to get the file from
	 * @param resid the id of the resource to load
	 * @return an array containing the Items from the file
	 */
	public static Item[] parseItemsFromRes(final Resources resources, int resid) {
		
		return null;
	}
}
