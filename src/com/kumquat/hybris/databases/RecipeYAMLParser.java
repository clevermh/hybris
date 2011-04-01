package com.kumquat.hybris.databases;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kumquat.hybris.Ingredient;
import com.kumquat.hybris.R;
import com.kumquat.hybris.Recipe;

public class RecipeYAMLParser {
	public static Recipe[] parseRecipesFromRes(final Resources resources, int resid, SQLiteDatabase db) {
		Yaml yaml = new Yaml();
		InputStream inputStream = resources.openRawResource(resid);
		Map<String, Object> result = (Map<String, Object>)yaml.load(inputStream);
		Recipe[] recipes = new Recipe[result.size()];
		int count = 0;
		
		for(String k : result.keySet()) {
			Map<String, Object> data = (Map<String, Object>)result.get(k);

			// Basic info
			String name = k;
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
}
