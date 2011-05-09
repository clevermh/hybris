package com.kumquat.hybris;

import com.kumquat.hybris.databases.HybrisDatabaseHelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class InventoryActivity extends ListActivity {
	static final int DIALOG_ADD_ITEM = 0;
	
	private Inventory inventory;
	private String selectedItem;
	
	/**
	 * @return An ArrayAdapter containing the information stored in the inventory
	 */
	private ArrayAdapter<String> makeAdapter() {
		DecimalFormat df = new DecimalFormat("0.###");
		String[] items = new String[inventory.getCount()];
		for(int a = 0; a < items.length; a++) {
			Ingredient i = inventory.getItem(a);
			items[a] = i.getName() + "\t(" + df.format(i.getQuantity()) + " " + i.getQuantityMetric() + ")";
		}
		
		return new ArrayAdapter<String>(this, R.layout.list_item, items);
	}
	
	/**
	 * Makes the dialog for adding to the inventory
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
							// If the unit is known then try to remove the item
							if(UnitConverter.knownUnit(unit)) {
								double number = -Double.parseDouble(quantity);
								HybrisDatabaseHelper hdh = new HybrisDatabaseHelper(getApplicationContext());
								SQLiteDatabase db = hdh.getReadableDatabase();
								
								Ingredient addition = new Ingredient(selectedItem, number, unit, db);
								// Try to remove the ingredient, let the user know if it works
								if(inventory.updateItem(addition)) {
									Toast.makeText(getApplicationContext(), selectedItem + " removed", Toast.LENGTH_SHORT).show();
									refreshList();
								} else { // If it fails, let the user know
									Toast.makeText(getApplicationContext(), "Error removing " + selectedItem, Toast.LENGTH_SHORT).show();
								}
							} else { // Otherwise, let the user know that we have no idea what metric that is
								Toast.makeText(getApplicationContext(), "Unknown unit: " + unit, Toast.LENGTH_SHORT).show();
							}
						}
						
						quantity_input.setText("");
						metric_input.setText("");
					}
					
					// The dialog should go away when the button is pressed
					selectedItem = "";
					dismissDialog(DIALOG_ADD_ITEM);
				}});
	    	
	    	
	    	builder.setMessage("Remove item")
	    	       .setView(layout)
	    	       .setCancelable(true);
	    	dialog = builder.create();
	    		
	        break;
	    default:
	        dialog = null;
	    }
	    
	    return dialog;
	}
	
	// When you remove an item you may have removed it from the inventory entirely
	// so make sure to update the list
	private void refreshList() {
		inventory = new Inventory(getApplicationContext());
		
		setListAdapter(makeAdapter());
	}

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Populate the ListView
		refreshList();
		
		// Set it to filter text (this needs to be done or something bad happens)
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		// When an item is clicked on, open the dialog
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectedItem = inventory.getItem(position).getName();
				showDialog(DIALOG_ADD_ITEM);
			}
		});

	}
}
