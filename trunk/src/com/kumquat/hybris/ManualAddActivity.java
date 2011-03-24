package com.kumquat.hybris;

import com.kumquat.hybris.databases.ItemDatabaseHelper;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;

public class ManualAddActivity extends Activity {
	private ItemDatabaseHelper idh;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manualadd);
		
		idh = new ItemDatabaseHelper(getApplicationContext());
		
		Spinner typeSpinner = (Spinner)findViewById(R.id.manualadd_type1);
		String[] alltypes = idh.getAllTypes();
		
		Spinner subSpinner = (Spinner)findViewById(R.id.manualadd_type2);
		
		Spinner specSpinner = (Spinner)findViewById(R.id.manualadd_type3);
		
		Button ok = (Button)findViewById(R.id.manualadd_ok);
		
		Button cancel = (Button)findViewById(R.id.manualadd_cancel);
	}
}
