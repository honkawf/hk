package com.buyertest;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends Activity{

	private TextView name;
	private TextView price;
	private TextView time;
	private TextView cash;
	private TextView cardnum;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listdetail);
		
		name = (TextView)findViewById(R.id.name);
		price = (TextView)findViewById(R.id.price);
		time = (TextView)findViewById(R.id.time);
		cash = (TextView)findViewById(R.id.cash);
		cardnum = (TextView)findViewById(R.id.cardnum);
		
		name.setText(this.getIntent().getStringExtra("name"));
		price.setText(this.getIntent().getStringExtra("price"));
		time.setText(this.getIntent().getStringExtra("time"));
		cash.setText(this.getIntent().getStringExtra("cash"));
		cardnum.setText(this.getIntent().getStringExtra("cardnum"));
	}
}
