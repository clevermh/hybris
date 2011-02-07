package com.kumquat.hybris;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.hardware.*;

public class Test extends Activity {
	
	private OnClickListener buttonClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Context con = getApplicationContext();
			CharSequence test = "Button pressed!";
			int dur = Toast.LENGTH_SHORT;
			
			Toast tst = Toast.makeText(con, test, dur);
			tst.show();
			
			Camera cam = Camera.open();
			//cam.setParameters()
		}
		
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        
        Button b = (Button)findViewById(R.id.buttongo);
        b.setOnClickListener(buttonClick);
    }
}