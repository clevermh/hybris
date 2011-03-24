package com.kumquat.hybris;

import com.kumquat.hybris.databases.ItemDatabaseHelper;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class ManualAddActivity extends Activity {
	//private ItemDatabaseHelper idh;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manualadd);
		
		ItemDatabaseHelper idh = new ItemDatabaseHelper(getApplicationContext());
		
		Spinner typeSpinner = (Spinner)findViewById(R.id.manualadd_type1);
		
		Spinner subSpinner = (Spinner)findViewById(R.id.manualadd_type2);
		
		Spinner specSpinner = (Spinner)findViewById(R.id.manualadd_type3);
		
		Button ok = (Button)findViewById(R.id.manualadd_ok);
		
		Button cancel = (Button)findViewById(R.id.manualadd_cancel);
		
		SQLiteDatabase idb = idh.getReadableDatabase();
		String[] alltypes = Item.getAllTypes(idb);
		if(alltypes != null) {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, alltypes);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			typeSpinner.setAdapter(adapter);
		}
		idb.close();
	}
}
