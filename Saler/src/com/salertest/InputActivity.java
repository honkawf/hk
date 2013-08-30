package com.salertest;

import com.XML.XML;
import com.bluetooth.BluetoothOperation;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class InputActivity extends Activity {

	Button b1;
	EditText ed;
	public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.input);
        b1 = (Button)this.findViewById(R.id.button1);
        ed = (EditText)this.findViewById(R.id.editText1);
        b1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				XML x = new XML();
				BluetoothOperation.send(x.productSentenceXML(ed.getText().toString()));
			}
        });
	}
}
