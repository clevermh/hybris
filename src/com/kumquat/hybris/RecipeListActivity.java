package com.kumquat.hybris;

import android.app.ListActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.kumquat.hybris.databases.HybrisDatabaseHelper;

import java.util.Vector;

public class RecipeListActivity extends ListActivity {
	Recipe[] recipes;
	String[] recipeNames;
	
	/**
	 * @param useInvent Whether or not we should only display recipes that can be made with
	 * 			the ingredients in the inventory
	 * @return An ArrayAdapter populated with the recipe names
	 */
	private ArrayAdapter<String> makeAdapter(boolean useInvent) {
		HybrisDatabaseHelper hdh = new HybrisDatabaseHelper(getApplicationContext());
		SQLiteDatabase db = hdh.getReadableDatabase();
		
		if(useInvent) {
			Inventory inv = new Inventory(getApplicationContext());
			// Get all the recipes that use ingredients in the inventory
			String[] allMaybePossible = Recipe.getAllRecipeNamesWithIngredients(inv.getAllItemIDs(), db);
			Vector<String> recipes = new Vector<String>();
			// Check each of the returned recipes to see if the inventory can make that recipe
			for(int a = 0; a < allMaybePossible.length; a++) {
				Recipe r = Recipe.getFromDatabase(allMaybePossible[a], db);
				if(inv.canMake(r)) { recipes.add(allMaybePossible[a]); }
			}
			recipeNames = recipes.toArray(new String[0]);
		} else {
			recipeNames = Recipe.getAllRecipeNames(db);
		}
		db.close();

		return new ArrayAdapter<String>(this, R.layout.list_item, recipeNames);
	}

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();

		// Make the list adapter
		setListAdapter(makeAdapter(intent.getBooleanExtra("com.kumquat.hybris.useInventory", false)));

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		// When an item in the list is clicked, open the RecipeActivity with the selected recipe
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent viewer = new Intent(getApplicationContext(), RecipeActivity.class);
				viewer.putExtra("com.kumquat.hybris.rName", recipeNames[position]);
				startActivity(viewer);
			}
		});
	}
}
