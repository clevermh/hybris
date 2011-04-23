package com.kumquat.hybris;

import com.kumquat.hybris.databases.HybrisDatabaseHelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class InventoryActivity extends ListActivity {
	static final int DIALOG_ADD_ITEM = 0;
	
	private Inventory inventory;
	private String selectedItem;
	
	private ArrayAdapter<String> makeAdapter() {
		String[] items = new String[inventory.getCount()];
		for(int a = 0; a < items.length; a++) {
			Ingredient i = inventory.getItem(a);
			items[a] = i.getName() + "\t(" + i.getQuantity() + " " + i.getQuantityMetric() + ")";
		}
		
		return new ArrayAdapter<String>(this, R.layout.list_item, items);
	}
	
	/**
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	protected Dialog onCreateDialog(int id) {
	    Dialog dialog = null;
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    switch(id) {
	    case DIALOG_ADD_ITEM:

	    	LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
	    	View layout = inflater.inflate(R.layout.twoinputdialog,
	    	                               (ViewGroup) findViewById(R.id.layout_root));
	    	
	    	final EditText inputbox1 = (EditText) layout.findViewById(R.id.dialog_input_one);
	    	inputbox1.setText("");
	    	final EditText inputbox2 = (EditText) layout.findViewById(R.id.dialog_input_two);
	    	inputbox2.setText("");
	    	
	    	TextView label1 = (TextView) layout.findViewById(R.id.dialog_label_one);
	    	label1.setText("Quantity");
	    	TextView label2 = (TextView) layout.findViewById(R.id.dialog_label_two);
	    	label2.setText("Units");
	    	
	    	Button dialogOK = (Button) layout.findViewById(R.id.dialog_ok_button);
	    	
	    	dialogOK.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					if(!selectedItem.equals("")) {
						String quantity = inputbox1.getText().toString().trim();
						String unit = inputbox2.getText().toString().trim().toLowerCase();
	
						if(UnitConverter.knownUnit(unit)) {
							double number = -Double.parseDouble(quantity);
							HybrisDatabaseHelper hdh = new HybrisDatabaseHelper(getApplicationContext());
							SQLiteDatabase db = hdh.getReadableDatabase();
							
							Ingredient addition = new Ingredient(selectedItem, number, unit, db);
							if(inventory.updateItem(addition)) {
								Toast.makeText(getApplicationContext(), selectedItem + " removed", Toast.LENGTH_SHORT).show();
								refreshList();
							} else {
								Toast.makeText(getApplicationContext(), "Error removing " + selectedItem, Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(getApplicationContext(), "Unknown unit: " + unit, Toast.LENGTH_SHORT).show();
						}
					}
					
					selectedItem = "";
					dismissDialog(DIALOG_ADD_ITEM);
				}});
	    	
	    	
	    	builder.setMessage("Remove It!")
	    	       .setView(layout)
	    	       .setCancelable(true);
	    	dialog = builder.create();
	    		
	        break;
	    default:
	        dialog = null;
	    }
	    return dialog;
	}
	
	private void refreshList() {
		inventory = new Inventory(getApplicationContext());
		
		setListAdapter(makeAdapter());
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		refreshList();
		
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectedItem = inventory.getItem(position).getName();
				showDialog(DIALOG_ADD_ITEM);
			}
		});

	}
}
