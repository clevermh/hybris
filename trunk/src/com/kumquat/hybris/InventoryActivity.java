package com.kumquat.hybris;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class InventoryActivity extends ListActivity {
	private Inventory inventory;
	
	private ArrayAdapter<String> makeAdapter() {
		ArrayAdapter<String> adapter = null;
		
		String[] items = new String[inventory.getCount()];
		for(int a = 0; a < items.length; a++) {
			Ingredients i = inventory.getItem(a);
			items[a] = i.getName() + "\t" + i.getQuantity();
		}
		
		adapter = new ArrayAdapter<String>(this, R.layout.list_item, items);
		
		return adapter;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		inventory = new Inventory(getApplicationContext());

		setListAdapter(makeAdapter());
		
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
