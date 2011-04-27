package com.kumquat.hybris;

import com.kumquat.hybris.databases.HybrisDatabaseHelper;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.ZoomControls;

/**
 * This Activity displays a specified recipe to the user
 */
public class RecipeActivity extends Activity {
	private Recipe self;
	private String[] labels;
	private String[] content;
	private int curPage;
	
	/**
	 * @param arr The array to make the adapter out of
	 * @return An ArrayAdapter with the given items
	 */
	private ArrayAdapter<String> makeAdapter(String[] arr) {
		ArrayAdapter<String> adapter;
		
		if(arr == null) {
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[] { });
		} else {
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arr);
		}
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		return adapter;
	}
	
	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recipedisplay);
		
		// Get the name of the recipe to display, if it does not exist then we should exit this Activity
		Intent in = getIntent();
		String recipename = in.getStringExtra("com.kumquat.hybris.rName");
		if(recipename == null) { finish(); return; }
		
		// Get the recipe info from the database, if it does not exist then we should exit this Activity
		HybrisDatabaseHelper hdh = new HybrisDatabaseHelper(getApplicationContext());
		SQLiteDatabase db = hdh.getReadableDatabase();
		self = Recipe.getFromDatabase(recipename, db);
		db.close();
		if(self == null) { finish(); return; }
		
		// Setup all the pages of info
		// First page is the basic recipe info including ingredients
		// Second page is the list of all directions
		// The rest of the pages are each direction on a separate page
		curPage = 0;
		labels = new String[self.numDirections() + 2];
		content = new String[labels.length];
		
		labels[1] = "All Directions";
		content[1] = "";
		
		for(int a = 0; a < self.numDirections(); a++) {
			content[1] += (a + 1) + ". " + self.getDirection(a) + "\n";
			
			labels[a + 2] = "Directions - " + (a + 1);
			content[a + 2] = self.getDirection(a);
		}
		
		String inf = recipename + "\n-----------\n";
		inf += "Prep time: " + self.getPrepTime() + "\n";
		inf += "Cook time: " + self.getCookTime() + "\n";
		inf += "Serves: " + self.getServingSize() + "\n";
		inf += "Type: " + self.getType() + "\n";
		inf += "-----------\n";
		for(int a = 0; a < self.numIngredients(); a++) {
			inf += self.getIngredient(a).getQuantity() + "  ";
			inf += self.getIngredient(a).getQuantityMetric() + "  ";
			inf += self.getIngredient(a).getName() + "\n";
		}
		
		labels[0] = "Recipe Info";
		content[0] = inf;
		
		// Setup all the button press stuff
		final Button back = (Button)findViewById(R.id.recipe_back);
		final Button next = (Button)findViewById(R.id.recipe_next);
		final Button done = (Button)findViewById(R.id.recipe_done);
		final Spinner spinner = (Spinner)findViewById(R.id.recipe_step);
		final ZoomControls zoomer = (ZoomControls)findViewById(R.id.recipe_zoom);
		final TextView info = (TextView)findViewById(R.id.recipe_info);
		
		// When a page in the Spinner is selected, display that page
		spinner.setAdapter(makeAdapter(labels));
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				TextView tv = (TextView)findViewById(R.id.recipe_info);
				curPage = pos;
				tv.setText(content[curPage]);
				
				if(curPage == 0) {
					back.setEnabled(false);
					next.setEnabled(true);
				} else if(curPage == labels.length - 1) {
					back.setEnabled(true);
					next.setEnabled(false);
				} else {
					back.setEnabled(true);
					next.setEnabled(true);
				}
			}

			public void onNothingSelected(AdapterView<?> parent) { }
		});
		
		// Go back a page
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				spinner.setSelection(curPage - 1);
			}
		});
		
		// Go forward a page
		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				spinner.setSelection(curPage + 1);
			}
		});
		
		// Try to remove the ingredients for this Recipe from the inventory
		done.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Inventory invent = new Inventory(getApplicationContext());
				// If I can make this recipe, remove each ingredient from the inventory
				if(invent.canMake(self)) {
					for(int a = 0; a < self.numIngredients(); a++) {
						Ingredient ing = self.getIngredient(a);
						Ingredient rem = new Ingredient(ing.getItemId(), ing.getName(), -ing.getQuantity(), ing.getQuantityMetric());
						
						if(!invent.updateItem(rem)) {
							Log.e("RecipeActivity", "Failed to remove ingredient " + ing);
						}
					}
					
					// Let the user know things were removed from the inventory
					Toast.makeText(getApplicationContext(), "Ingredients removed from inventory", Toast.LENGTH_SHORT).show();
				} else {
					// If you can't make the recipe do nothing
				}
				
				finish();
			}
		});
		
		// Zoom controls, now functionally correct!
		zoomer.setOnZoomInClickListener(new OnClickListener() {
			public void onClick(View v) {
				info.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, info.getTextSize() * 1.5f);
			}
		});
		zoomer.setOnZoomOutClickListener(new OnClickListener() {
			public void onClick(View v) {
				info.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, info.getTextSize() / 1.5f);
			}
		});
	}
}
