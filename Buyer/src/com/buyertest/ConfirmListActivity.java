package com.buyertest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.Picker.Picker;
import com.RSA.RSA;
import com.XML.Goods;
import com.XML.XML;
import com.bluetooth.BluetoothOperation;
import com.bluetooth.BluetoothOperation.ReadThread;
import com.bluetooth.BluetoothOperation.SendThread;
import com.zxing.activity.CaptureActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ConfirmListActivity extends Activity{
	private ListView listview;
	private Button confirm;
	private TextView totalView;
	private ConfirmAdapter adapter;
	private String totalprice;
    private Goods goods=new Goods();
    private ProgressDialog pd;
    private int loaded=0;
	private ArrayList<Map<String,Object>> goodslist;
	private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 1:
            	pd=new ProgressDialog(ConfirmListActivity.this);
				pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pd.setCancelable(false);
				pd.setMessage((String)msg.obj);  
				pd.show();
                break;
            case 0:
                pd.dismiss();
                break;
            case 2:
		    	AlertDialog.Builder builder = new Builder(ConfirmListActivity.this);
		    	builder.setTitle("付款结果").setMessage((String)msg.obj).setCancelable(false).setPositiveButton("确认", new OnClickListener(){

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						Intent intent=new Intent(ConfirmListActivity.this,MainActivity.class);
						startActivity(intent);
						GoodsListActivity.flag=1;
						ConfirmListActivity.this.finish();
						try {
							BluetoothOperation.socket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
		    		
		    	});
		    	builder.show();
            	break;
            case 3:
            	pd.dismiss();
            	Toast.makeText(ConfirmListActivity.this, "连接超时，请重试", 5000).show();
            	confirm.setVisibility(View.VISIBLE);
            	break;
            }
            super.handleMessage(msg);
        }
    };
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.confirmlist);
        Intent intent=getIntent();
        totalprice=intent.getStringExtra("totalprice");
        totalView=(TextView)findViewById(R.id.totalprice);
        totalView.setText(totalprice);
        goodslist=GoodsListActivity.goodslist;
        adapter = new ConfirmAdapter(this);
        listview=(ListView)findViewById(R.id.listView2); 
        listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        confirm=(Button)findViewById(R.id.confirm3);
        confirm.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//从本地文件获取随机码
				//假设随机码是2309916916024662023
				confirm.setVisibility(View.GONE);
				new Thread()
				{
					public void run()
					{
						Message msg=handler.obtainMessage();
						msg.what=1;
						msg.obj="正在确认付款";
						msg.sendToTarget();
						Date dstart=new Date();
                   	 	long start=dstart.getTime()/1000;
                   	 	while(loaded==0)
                   	 	{
                   	 		Date dend=new Date();
                   	 		long end=dend.getTime()/1000;
                   	 		if(end-start>=20)
                   	 		{
                   	 			msg=handler.obtainMessage();
                        		msg.what=3;
                        		msg.sendToTarget();
                        		return;
                   	 		}
                   	 	}
                   	 	loaded=0;
					}
				}.start();
				new Thread()
				{
					public void run()
					{

						Date dt=new Date();
						String cardnumber=MainActivity.person.getCardNum();
						String tradetime=String.valueOf(dt.getTime()/1000);
						String buyerdevice=BluetoothOperation.getLocalMac().replaceAll(":","");
						String salerdevice=BluetoothOperation.mac.replaceAll(":","");
						int totalpricefill=(int)(Double.valueOf(totalprice)*100);
						String pricefill=String.format("%08d",totalpricefill);
						String buyerdevicesub=buyerdevice.substring(buyerdevice.length()-4,buyerdevice.length());
						String salerdevicesub=salerdevice.substring(salerdevice.length()-4,salerdevice.length());
						int buyerdevicefill=Integer.parseInt(buyerdevicesub,16);
						String buyerfill=String.format("%05d",buyerdevicefill);
						int salerdevicefill=Integer.parseInt(salerdevicesub,16);
						String salerfill=String.format("%05d",salerdevicefill);
						String words=tradetime+buyerfill+salerfill+pricefill;
						Log.d("words",words);
						RSA rsa=new RSA();
						String cipher=rsa.setRSA(words);
						XML confirmTrade=new XML();
						for(Map<String, Object> map :goodslist)
						{
							confirmTrade.addData(map.get("barcode").toString(), map.get("name").toString(), map.get("price").toString(), map.get("quantity").toString());
						}
						confirmTrade.setTrade(buyerdevice, salerdevice, tradetime, totalprice, cipher,cardnumber);
						String xml=confirmTrade.produceTradeXML("confirmTrade");
						Log.d("",xml);
						if(BluetoothOperation.send(xml))
						{
							byte[] receive=BluetoothOperation.receive();
							loaded=1;
							Message msg=handler.obtainMessage();
							msg.what=0;
							msg.sendToTarget();
					    	XML confirmXML=new XML();
					    	String sentence=confirmXML.parseSentenceXML(new ByteArrayInputStream(receive));
					    	msg=handler.obtainMessage();
					    	msg.what=2;
					    	msg.obj=sentence;
					    	msg.sendToTarget();
		 					Log.d("",new String(receive));
		 					BluetoothOperation.receive=null;
		 					//更新余额
							
						}
						else
							return;
					}
				}.start();
			}
        	
        });
        
	}
	public final class ViewHolder{
		public TextView name;
		public TextView barcode;
		public TextView price;
		public TextView count;
	    }
	public class ConfirmAdapter extends BaseAdapter{
		private LayoutInflater mInflater;
		public ConfirmAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
			}
	        @Override
	        public int getCount() {
	            // TODO Auto-generated method stub
	             return goodslist.size();
	        }
	 
	        @Override
	        public Object getItem(int arg0) {
	            // TODO Auto-generated method stub
	            return null;
	        }
	 
	        @Override
	        public long getItemId(int arg0) {
	            // TODO Auto-generated method stub
	            return 0;
	        }
	     @Override
	        public View getView(final int position, View convertView, ViewGroup parent) {
	 			ViewHolder holder;
	            if (convertView == null) {
	                holder=new ViewHolder();  
	                convertView = mInflater.inflate(R.layout.confirmitem, null);
	                holder.name=(TextView)convertView.findViewById(R.id.goodsinfo1);
	                holder.barcode=(TextView)convertView.findViewById(R.id.barcode1);
	                holder.price=(TextView)convertView.findViewById(R.id.price1);
	                holder.count=(TextView)convertView.findViewById(R.id.count);
	                convertView.setTag(holder);
	                 
	            }else {
	                 
	                holder = (ViewHolder)convertView.getTag();
	            }
	    		holder.name.setText((String)goodslist.get(position).get("name"));
	            holder.barcode.setText((String)goodslist.get(position).get("barcode"));
	            holder.price.setText((String)goodslist.get(position).get("price"));
	            holder.count.setText("X"+(String)goodslist.get(position).get("quantity"));
	            return convertView;
	        }
	         
	    }
}
