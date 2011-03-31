package com.kumquat.hybris;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import android.widget.ZoomControls;

public class RecipeActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recipedisplay);
		
		final ZoomControls zoomer = (ZoomControls)findViewById(R.id.zoom_on_text);
		final TextView txt_instruction = (TextView)findViewById(R.id.text_instruct);
		zoomer.setOnZoomInClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Go to the remove page
				txt_instruction.setTextSize(txt_instruction.getTextSize());
			}
		});
		zoomer.setOnZoomOutClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Go to the remove page
				txt_instruction.setTextSize((float)txt_instruction.getTextSize() * (float)0.5);
				txt_instruction.setText("text");
			}
		});
		
		
	}
}
