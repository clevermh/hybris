package com.kumquat.hybris;

import com.kumquat.hybris.databases.HybrisDatabaseHelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ManualAddListActivity extends ListActivity {
	static final int DIALOG_ADD_ITEM = 0;
	
	private Inventory inventory;
	private String selectedItem;
	
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
		
		String[] allitems = Item.getAllItemNames(db);
		setListAdapter(makeAdapter(allitems));
		
		db.close();
		
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		
		inventory = new Inventory(getApplicationContext());
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectedItem = (String)((TextView) view).getText();
				showDialog(DIALOG_ADD_ITEM);
			}
		});
	}
	
	/**
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	protected Dialog onCreateDialog(int id) {
	    Dialog dialog = null;
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    switch(id) {
	    case DIALOG_ADD_ITEM:
	    	// LayoutInflaters are awesome!
	    	LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
	    	View layout = inflater.inflate(R.layout.twoinputdialog, (ViewGroup) findViewById(R.id.layout_root));
	    	
	    	final EditText quantity_input = (EditText) layout.findViewById(R.id.dialog_input_one);
	    	final EditText metric_input = (EditText) layout.findViewById(R.id.dialog_input_two);
	    	Button dialogOK = (Button) layout.findViewById(R.id.dialog_ok_button);
	    	
	    	// Set the hint text
	    	quantity_input.setHint("Quantity");
	    	metric_input.setHint("Unit");
	    	
	    	// The quantity should be numeric only
	    	quantity_input.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
	    	
	    	// When the OK button is pressed, probably remove the item
	    	dialogOK.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					if(!selectedItem.equals("")) {
						// Get the given quantity and metric
						String quantity = quantity_input.getText().toString().trim().replaceAll("\\D", "");
						String unit = metric_input.getText().toString().trim().toLowerCase();

						if(!quantity.equals("")) {
							// If the unit is known then try to add the item
							if(UnitConverter.knownUnit(unit)) {
								double number = Double.parseDouble(quantity);
								HybrisDatabaseHelper hdh = new HybrisDatabaseHelper(getApplicationContext());
								SQLiteDatabase db = hdh.getReadableDatabase();
								
								Ingredient addition = new Ingredient(selectedItem, number, unit, db);
								// Try to add the ingredient, let the user know if it works
								if(inventory.updateItem(addition)) {
									Toast.makeText(getApplicationContext(), selectedItem + " added", Toast.LENGTH_SHORT).show();
								} else { // If it fails, let the user know
									Toast.makeText(getApplicationContext(), "Error adding " + selectedItem, Toast.LENGTH_SHORT).show();
								}
							} else { // Otherwise, let the user know that we have no idea what metric that is
								Toast.makeText(getApplicationContext(), "Unknown unit (" + unit + ")", Toast.LENGTH_SHORT).show();
							}
						}
						
						quantity_input.setText("");
						metric_input.setText("");
					}
					
					// The dialog should go away when the button is pressed
					selectedItem = "";
					dismissDialog(DIALOG_ADD_ITEM);
				}});
	    	
	    	
	    	builder.setMessage("Add item")
	    	       .setView(layout)
	    	       .setCancelable(true);
	    	dialog = builder.create();
	    		
	        break;
	    default:
	        dialog = null;
	    }
	    
	    return dialog;
	}
}
