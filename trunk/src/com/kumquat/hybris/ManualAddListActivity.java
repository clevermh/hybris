package com.kumquat.hybris;

import com.kumquat.hybris.databases.HybrisDatabaseHelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.NumberKeyListener;
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

	    	final String initial_text1 = "Quantity";
	    	final String initial_text2 = "Unit";
	    	LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
	    	View layout = inflater.inflate(R.layout.twoinputdialog,
	    	                               (ViewGroup) findViewById(R.id.layout_root));
	    	
	    	final EditText inputbox1 = (EditText) layout.findViewById(R.id.dialog_input_one);
	    	inputbox1.setText(initial_text1);
	    	final EditText inputbox2 = (EditText) layout.findViewById(R.id.dialog_input_two);
	    	inputbox2.setText(initial_text2);
	    	
	    	inputbox1.setKeyListener(new NumberKeyListener(){

				@Override
				public int getInputType() {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				protected char[] getAcceptedChars() {
					char[] numberChars = {'1','2','3','4','5','6','7','8','9','0','.'};
				    return numberChars;
				}});
	    	
	    	inputbox1.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					String nowtext = inputbox1.getText().toString();
					if (nowtext.equals(initial_text1)){
						inputbox1.setText("");
					}
				}});
	    	
	    	inputbox2.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					String nowtext = inputbox2.getText().toString();
					if (nowtext.equals(initial_text2)){
						inputbox2.setText("");
					}
				}});
	    	
	    	TextView label1 = (TextView) layout.findViewById(R.id.dialog_label_one);
	    	label1.setText("");
	    	TextView label2 = (TextView) layout.findViewById(R.id.dialog_label_two);
	    	label2.setText("");
	    	
	    	Button dialogOK = (Button) layout.findViewById(R.id.dialog_ok_button);
	    	
	    	dialogOK.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					if(!selectedItem.equals("")) {
						String quantity = inputbox1.getText().toString().trim();
						String unit = inputbox2.getText().toString().trim().toLowerCase();

						if(UnitConverter.knownUnit(unit)) {
							double number = Double.parseDouble(quantity);
							HybrisDatabaseHelper hdh = new HybrisDatabaseHelper(getApplicationContext());
							SQLiteDatabase db = hdh.getReadableDatabase();
							
							Ingredient addition = new Ingredient(selectedItem, number, unit, db);
							if(inventory.updateItem(addition)) {
								Toast.makeText(getApplicationContext(), selectedItem + " added", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(getApplicationContext(), "Error adding " + selectedItem, Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(getApplicationContext(), "Unknown unit", Toast.LENGTH_SHORT).show();
						}
					}
					
					selectedItem = "";
					dismissDialog(DIALOG_ADD_ITEM);
				}});
	    	
	    	
	    	builder.setMessage("Amount of Item")
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
