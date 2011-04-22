package com.kumquat.hybris;

import com.kumquat.hybris.databases.HybrisDatabaseHelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
					String quantity = inputbox1.getText().toString();
					String unit = inputbox2.getText().toString();
					
					//do what you want with quantity and unit
					
				}});
	    	
	    	
	    	builder.setMessage("Add It!")
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
