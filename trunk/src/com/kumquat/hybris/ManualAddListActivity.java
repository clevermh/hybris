package com.kumquat.hybris;

import com.kumquat.hybris.databases.HybrisDatabaseHelper;

import android.app.ListActivity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ManualAddListActivity extends ListActivity {
	private ArrayAdapter<String> makeAdapter(String[] arr) {
		ArrayAdapter<String> adapter;
		
		if(arr == null) {
			adapter = new ArrayAdapter<String>(this, R.layout.list_item, new String[] { });
		} else {
			adapter = new ArrayAdapter<String>(this, R.layout.list_item, arr);
		}
		
		return adapter;
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		HybrisDatabaseHelper hdh = new HybrisDatabaseHelper(getApplicationContext());
		SQLiteDatabase db = hdh.getReadableDatabase();
		
		String[] allitems = Item.getAllSpecificTypes(db);
		setListAdapter(makeAdapter(allitems));
		
		db.close();
		
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// When clicked, show a toast with the TextView text
				Toast.makeText(getApplicationContext(),
						((TextView) view).getText(), Toast.LENGTH_SHORT).show();
			}
		});
	}
}
