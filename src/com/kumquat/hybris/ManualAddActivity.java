package com.kumquat.hybris;

import com.kumquat.hybris.databases.HybrisDatabaseHelper;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class ManualAddActivity extends Activity {
	private Inventory inventory;
	private String selType;
	private String selSub;
	private String selSpec;
	private int selID;
	
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
		setContentView(R.layout.manualadd);
		
		final HybrisDatabaseHelper dbhelper = new HybrisDatabaseHelper(getApplicationContext());
		
		inventory = new Inventory(getApplicationContext());
		
		Spinner typeSpinner = (Spinner)findViewById(R.id.manualadd_type1);
		
		Spinner subSpinner = (Spinner)findViewById(R.id.manualadd_type2);
		
		Spinner specSpinner = (Spinner)findViewById(R.id.manualadd_type3);
		
		Button ok = (Button)findViewById(R.id.manualadd_ok);
		ok.setEnabled(false);
		
		Button cancel = (Button)findViewById(R.id.manualadd_cancel);
		
		// Get the items for the top level spinner
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		String[] alltypes = Item.getAllTypes(db);
		typeSpinner.setAdapter(makeAdapter(alltypes));
		db.close();
		
		// When you select something in the top level it should populate the second level
		typeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				selType = parent.getItemAtPosition(pos).toString();
				Log.d("DBG_OUT", "Type selected: " + selType);
				SQLiteDatabase db = dbhelper.getReadableDatabase();
				String[] subtypes = Item.getAllSubTypes(db, selType);
				Spinner spin = (Spinner)findViewById(R.id.manualadd_type2);
				spin.setAdapter(makeAdapter(subtypes));
				db.close();
			}

			public void onNothingSelected(AdapterView<?> parent) { }
		});
		
		// When you select something in the second level it should populate the bottom level
		subSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				selSub = parent.getItemAtPosition(pos).toString();
				Log.d("DBG_OUT", "Subtype selected: " + selSub);
				SQLiteDatabase db = dbhelper.getReadableDatabase();
				String[] spectypes = Item.getAllSpecificTypes(db, selType, selSub);
				Spinner spin = (Spinner)findViewById(R.id.manualadd_type3);
				spin.setAdapter(makeAdapter(spectypes));
				db.close();
			}

			public void onNothingSelected(AdapterView<?> parent) { }
		});
		
		// When you select something in the bottom level it should find out its quantity metric and let you enter
		// the quantity to add
		specSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				selSpec = parent.getItemAtPosition(pos).toString();
				Log.d("DBG_OUT", "Spectype selected: " + selSpec);
				Button btn = (Button)findViewById(R.id.manualadd_ok);
				btn.setEnabled(true);
			}

			public void onNothingSelected(AdapterView<?> parent) { }
		});
		
		// The ok button should do something
		ok.setOnClickListener(new OnClickListener() {
			public void onClick(View parent) {
				// TODO do something here
				SQLiteDatabase db = dbhelper.getReadableDatabase();
				selID = Item.findIDFromDatabase(db, selType, selSub, selSpec);
				Log.d("DBG_OUT", "Selected ID: " + selID);
				db.close();
				
				Ingredient ni = new Ingredient(selID, selSpec, 1, "units");
				if(inventory.addItem(ni)) {
					Log.d("DBG_OUT", "Item added to inventory");
				}
			}
		});
		
		// The cancel button should do something
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View parent) {
				// TODO do something here
				finish();
			}
		});
	}
}
