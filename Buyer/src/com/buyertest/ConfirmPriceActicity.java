package com.buyertest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class ConfirmPriceActicity extends Activity{
	private TextView price;
	private Button confirm;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.confirmprice);
        confirm=(Button) findViewById(R.id.confirm4);
        price=(TextView)findViewById(R.id.price);
        Intent intent=getIntent();
        String pri=intent.getStringExtra("price");
        price.setText(pri+"元");
        
	}
}
