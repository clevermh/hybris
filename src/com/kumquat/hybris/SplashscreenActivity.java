package com.kumquat.hybris;

import android.app.Activity;
import android.os.Bundle;
import android.database.sqlite.*;

import com.kumquat.hybris.databases.HybrisDatabaseHelper;
import com.kumquat.hybris.databases.InventoryDatabaseHelper;

public class SplashscreenActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen);
		
		new Thread(new Runnable() {
			public void run() {
				// Populate the converter
				UnitConverter.addStandard("pound");
				UnitConverter.addStandard("fluid ounce");
				UnitConverter.addStandard("whole");
				UnitConverter.addStandard("box");
				
				UnitConverter.addConversion("", "", 1);
				UnitConverter.addConversion("cup", "cup", 1);
				UnitConverter.addConversion("teaspoon", "teaspoon", 1);
				UnitConverter.addConversion("tablespoon", "tablespoon", 1);
				UnitConverter.addConversion("pound", "pound", 1);
				UnitConverter.addConversion("whole", "whole", 1);
				UnitConverter.addConversion("pinch", "pinch", 1);
				UnitConverter.addConversion("box", "box", 1);
				UnitConverter.addConversion("piece", "piece", 1);
				
				UnitConverter.addConversion("gallon", "fluid ounce", 128);
				UnitConverter.addConversion("quart", "fluid ounce", 32);
				UnitConverter.addConversion("pint", "fluid ounce", 16);
				UnitConverter.addConversion("cup", "fluid ounce", 8);
				UnitConverter.addConversion("gill", "fluid ounce", 4);
				UnitConverter.addConversion("fluid ounce", "tablespoon", 2);
				UnitConverter.addConversion("fluid ounce", "teaspoon", 6);
				
				UnitConverter.addConversion("ounce", "pound", 0.0625);
				UnitConverter.addConversion("stone", "pound", 14);
				UnitConverter.addConversion("pound", "gram", 453.6);
				UnitConverter.addConversion("pound", "kilogram", 0.454);
				UnitConverter.addConversion("half", "whole", 0.5);
				UnitConverter.addConversion("quarter", "whole", 0.25);
				UnitConverter.addConversion("whole", "third", 3);
				UnitConverter.addConversion("eighth", "whole", 0.125);
				
				UnitConverter.addConversion("liter", "fluid ounce", 33.81);
				UnitConverter.addConversion("cubic centimeter", "fluid ounce", 0.03381);
				UnitConverter.addConversion("cubic centimeter", "fluid ounce", 0.03381);
				UnitConverter.addConversion("pinch", "fluid ounce", 0.01042);
				UnitConverter.addConversion("dash", "fluid ounce", 0.012);
				UnitConverter.addConversion("smidgen", "fluid ounce", 0.005208);
				
				// Force the databases to populate now (otherwise the app crashes later)
				HybrisDatabaseHelper hdh = new HybrisDatabaseHelper(getApplicationContext());
				SQLiteDatabase db = hdh.getReadableDatabase();
				while(hdh.isPopulating()) {
					try { Thread.sleep(500); }
					catch(Exception e) { }
				}
				db.close();
				hdh.close();
				
				InventoryDatabaseHelper idh = new InventoryDatabaseHelper(getApplicationContext());
				SQLiteDatabase db2 = idh.getReadableDatabase();
				try { Thread.sleep(100); }
				catch(Exception e) { }
				db2.close();
				idh.close();
				
				finish();
			}
		}).start();
	}
}
