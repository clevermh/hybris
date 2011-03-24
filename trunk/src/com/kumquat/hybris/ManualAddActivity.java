package com.kumquat.hybris;

import com.kumquat.hybris.databases.ItemDatabaseHelper;

import android.app.Activity;
import android.content.Context;
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
import android.widget.Toast;

public class ManualAddActivity extends Activity {
	private String selType;
	private String selSub;
	private String selSpec;
	private int selID;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manualadd);
		
		final ItemDatabaseHelper idh = new ItemDatabaseHelper(getApplicationContext());
		final Context context = this;
		
		Spinner typeSpinner = (Spinner)findViewById(R.id.manualadd_type1);
		
		Spinner subSpinner = (Spinner)findViewById(R.id.manualadd_type2);
		
		Spinner specSpinner = (Spinner)findViewById(R.id.manualadd_type3);
		
		Button ok = (Button)findViewById(R.id.manualadd_ok);
		ok.setEnabled(false);
		
		Button cancel = (Button)findViewById(R.id.manualadd_cancel);
		
		// Get the items for the top level spinner
		SQLiteDatabase idb = idh.getReadableDatabase();
		String[] alltypes = Item.getAllTypes(idb);
		if(alltypes != null) {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, alltypes);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			typeSpinner.setAdapter(adapter);
		}
		idb.close();
		
		// When you select something in the top level it should populate the second level
		typeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				selType = parent.getItemAtPosition(pos).toString();
				Log.d("DBG_OUT", "Type selected: " + selType);
				SQLiteDatabase db = idh.getReadableDatabase();
				String[] subtypes = Item.getAllSubTypes(db, selType);
				Spinner spin = (Spinner)findViewById(R.id.manualadd_type2);
				if(subtypes != null) {
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, subtypes);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spin.setAdapter(adapter);
				}
				db.close();
			}

			public void onNothingSelected(AdapterView<?> parent) { }
		});
		
		// When you select something in the second level it should populate the bottom level
		subSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				selSub = parent.getItemAtPosition(pos).toString();
				Log.d("DBG_OUT", "Subtype selected: " + selSub);
				SQLiteDatabase db = idh.getReadableDatabase();
				String[] spectypes = Item.getAllSpecificTypes(db, selType, selSub);
				Spinner spin = (Spinner)findViewById(R.id.manualadd_type3);
				if(spectypes != null) {
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, spectypes);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spin.setAdapter(adapter);
				}
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
				SQLiteDatabase db = idh.getReadableDatabase();
				selID = Item.findIDFromDatabase(db, selType, selSub, selSpec);
				Log.d("DBG_OUT", "Selected ID: " + selID);
				db.close();
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
