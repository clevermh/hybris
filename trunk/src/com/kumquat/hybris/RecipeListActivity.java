package com.kumquat.hybris;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.kumquat.hybris.databases.HybrisDatabaseHelper;

public class RecipeListActivity extends ListActivity {
	Recipe[] recipes;
	String[] recipeNames;
	
	private ArrayAdapter<String> makeAdapter() {
		HybrisDatabaseHelper hdh = new HybrisDatabaseHelper(getApplicationContext());
		SQLiteDatabase db = hdh.getReadableDatabase();
		
		recipeNames = Recipe.getAllRecipeNames(db);
		db.close();

		return new ArrayAdapter<String>(this, R.layout.list_item, recipeNames);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final Context context = this;

		setListAdapter(makeAdapter());

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent viewer = new Intent(context, RecipeActivity.class);
				viewer.putExtra("com.kumquat.hybris.rName", recipeNames[position]);
				startActivity(viewer);
			}
		});
	}
}
