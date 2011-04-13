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