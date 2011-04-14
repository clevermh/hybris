package com.kumquat.hybris;

import com.kumquat.hybris.databases.HybrisDatabaseHelper;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

import android.widget.ZoomControls;

public class RecipeActivity extends Activity {
	private Recipe self;
	private String[] labels;
	private String[] content;
	private int curPage;
	
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
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recipedisplay);
		
		Intent in = getIntent();
		String recipename = in.getStringExtra("com.kumquat.hybris.rName");
		if(recipename == null) { finish(); return; }
		
		HybrisDatabaseHelper hdh = new HybrisDatabaseHelper(getApplicationContext());
		SQLiteDatabase db = hdh.getReadableDatabase();
		self = Recipe.getFromDatabase(recipename, db);
		db.close();
		
		if(self == null) { finish(); return; }
		
		curPage = 0;
		labels = new String[self.numDirections() + 1];
		content = new String[labels.length];
		
		for(int a = 0; a < self.numDirections(); a++) {
			labels[a + 1] = "Directions - " + (a + 1);
			content[a + 1] = self.getDirection(a);
		}
		
		// Page 0 is the info
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
		
		final Button back = (Button)findViewById(R.id.recipe_back);
		final Button next = (Button)findViewById(R.id.recipe_next);
		final Button done = (Button)findViewById(R.id.recipe_done);
		final Spinner spinner = (Spinner)findViewById(R.id.recipe_step);
		final ZoomControls zoomer = (ZoomControls)findViewById(R.id.recipe_zoom);
		final TextView info = (TextView)findViewById(R.id.recipe_info);
		
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
		
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				spinner.setSelection(curPage - 1);
			}
		});
		
		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				spinner.setSelection(curPage + 1);
			}
		});
		
		done.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO magic here to remove stuff from inventory
				finish();
			}
		});
		
		
		zoomer.setOnZoomInClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Go to the remove page
				info.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,info.getTextSize() * (float)1.5);
			}
		});
		zoomer.setOnZoomOutClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Go to the remove page
				info.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,info.getTextSize() / (float)1.5);
			}
		});
	}
}
