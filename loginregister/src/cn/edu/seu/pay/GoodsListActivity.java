package cn.edu.seu.pay;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.edu.seu.datatransportation.BluetoothDataTransportation;
import cn.edu.seu.main.MainActivity;
import cn.edu.seu.xml.Goods;
import cn.edu.seu.xml.XML;

import cn.edu.seu.main.R;
import com.zxing.activity.CaptureActivity;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class GoodsListActivity extends Activity{
	public static ArrayList<Map<String,Object>> goodslist;
	private ListView listview;
	private Button goon,confirm;
	private MyAdapter adapter;
    private Goods goods=new Goods();
    private ProgressDialog pd;
    private String totalprice;
    public static int flag=0;
    private int loaded=0;
    private String scanResult;
	private ArrayList<String> barcodeset=new ArrayList<String>();
	private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
         try{
        	 switch (msg.what) {
            case 0:
            	pd.dismiss();
            	break;
            case 1:
            	Log.i("case","1");
            	pd=new ProgressDialog(GoodsListActivity.this);
				pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pd.setCancelable(false);
				pd.setMessage((String)msg.obj);  
				pd.show();
                break;
            case 2:
            	Map<String,Object> map=new HashMap<String,Object>();
	 			map.put("name",goods.getName());
	 			map.put("barcode",goods.getBarcode());
	 			map.put("price",goods.getPrice());
	 			map.put("quantity",goods.getQuantity());
				goodslist.add(map);
				adapter.notifyDataSetChanged();
				listview.setSelection(goodslist.size());
				break;
            case 3:
            	Log.i("case","3");
            	Intent confirm=new Intent(GoodsListActivity.this,ConfirmListActivity.class);
	 			confirm.putExtra("totalprice", totalprice);
	            startActivity(confirm);
	            break;
            case 4:
       	     	GoodsListActivity.this.finish();    
       	     	break;
            case 5:
            	pd.dismiss();
            	Toast.makeText(GoodsListActivity.this, "连接失败，请重试", 5000).show();
            	break;
            case 6:
            	Toast.makeText(GoodsListActivity.this, "条形码不存在", 5000).show();
            	break;
            }

         }
         catch(Exception e)
         {
        	 Log.i("GoodsListActivity","提示框出错");
         }
            super.handleMessage(msg);
        }
    };
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.goodslist);
        goodslist=new  ArrayList<Map<String,Object>>();
        adapter = new MyAdapter(this);
        listview=(ListView)findViewById(R.id.listView1); 
        listview.setAdapter(adapter);
        goon=(Button)findViewById(R.id.goon);
        goon.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				goon.setText("继续购物");
				Intent openCameraIntent = new Intent(GoodsListActivity.this,CaptureActivity.class);
				startActivityForResult(openCameraIntent, 0);
			}
        	
        });
        confirm=(Button)findViewById(R.id.confirm2);
        Log.d("point","11");
        confirm.setOnClickListener(new Button.OnClickListener(){

			
			public void onClick(View arg0) {
				boolean condition1=(goodslist.size()==0);
				if(condition1)
					return;
				boolean condition2=(goodslist.size()==1&&(goodslist.get(0).get("quantity").toString().equals("0")));
				if(condition2)
					return;
				new Thread()
				{
					public void run()
					{
						Message msg=handler.obtainMessage();
						msg.what=1;
						msg.obj="正在获取总价";
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
                        		msg.what=5;
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
 						XML getPrice=new XML();
 						for(Map<String, Object> map :goodslist)
 						{
 							getPrice.addData(map.get("barcode").toString(), map.get("name").toString(), map.get("price").toString(), map.get("quantity").toString());
 						}
 						String xml=getPrice.producePriceXML("getTotalPrice");
 				        byte[] receive;
 						if(MainActivity.bdt.write(xml))
 						{
 							receive=MainActivity.bdt.read();
 							loaded=1;
 							Message msg=handler.obtainMessage();
 			 				msg.what=0;
 			 				msg.sendToTarget();
 						}
 		 				else
 		 					return;
 		 				try{
 			 				totalprice=getPrice.parseTotalPriceXML(new ByteArrayInputStream(receive));
 			 				Log.i("收到总价",totalprice);
 			 				Message msg=handler.obtainMessage();
 			 				msg.what=3;
 			 				msg.sendToTarget();
 		 				}
 		 				catch(Exception e)
 		 				{
 		 			        Log.i("info","未成功接收xml");
 		 					//Toast.makeText(GoodsListActivity.this, "未成功接收xml", Toast.LENGTH_LONG).show();
 		 				};
 					}
 				}.start();
		}
        	
    });
    new Thread()
     {
    	 public void run()
    	{
    	     while(flag==0);
    	     flag=0;
    	     Message msg=handler.obtainMessage();
    	     msg.what=4;
    	     msg.sendToTarget();
    	}
     }.start();
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 		super.onActivityResult(requestCode, resultCode, data);
 			if (resultCode == RESULT_OK) {
 			Bundle bundle = data.getExtras();
 			 scanResult = bundle.getString("result");
 			try {
 				Toast.makeText(GoodsListActivity.this, scanResult, Toast.LENGTH_LONG).show();
 				if(barcodeset.indexOf(scanResult)==-1)
 				{
 					new Thread()
 					{
 						public void run()
 						{
 							Message msg=handler.obtainMessage();
 							msg.what=1;
 							msg.obj="正在获取商品信息";
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
 	                        		msg.what=5;
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
 							XML getPrice=new XML();
 		 					getPrice.addData(scanResult, "", "", "1");
 		 					String xml=getPrice.producePriceXML("getPrice");
 		 					byte [] receive;
 		 					if(MainActivity.bdt.write(xml))
 		 					{
 		 						receive=MainActivity.bdt.read();
 		 						loaded=1;
 		 						Message msg=handler.obtainMessage();
 	 			 				msg.what=0;
 	 			 				msg.sendToTarget();
 		 					}
 		 	 				else
 		 	 					return;
 		 					try{
 		 			        	String sentence=getPrice.parseSentenceXML(new ByteArrayInputStream(receive));
 		 						if(sentence.equals("条形码不存在"))
 		 						{
 		 							Log.i("info","条形码不存在");
 		 							Message msg=handler.obtainMessage();
		 	 						msg.what=6;
		 	 						msg.sendToTarget();
		 	 	 				    barcodeset.add(goods.getBarcode());
 		 							
 		 						}
 		 						else{
 		 	 						goods=getPrice.parsePriceXML(new ByteArrayInputStream(receive));
 		 	 						try{
 		 	 							Message msg=handler.obtainMessage();
 		 	 							msg.what=2;
 		 	 							msg.sendToTarget();
 		 	 	 				        barcodeset.add(goods.getBarcode());
 		 	 	 					}
 		 	 	 					catch(Exception e)
 		 	 	 					{
 	 		 							Log.i("info","界面创建失败");
 		 	 	 						//Toast.makeText(GoodsListActivity.this, "界面创建失败", Toast.LENGTH_LONG).show();
 		 	 	 					}
 		 						}
 		 						
 		 					}
 		 					catch(Exception e)
 		 					{
 		 						Log.i("info","解析xml出错");
 		 						//Toast.makeText(GoodsListActivity.this, "解析xml出错", Toast.LENGTH_LONG).show();
 		 					}
 		 					
 						}
 					}.start();
 				}
 			} catch (Exception e) {
 				// TODO Auto-generated catch block
 				Toast.makeText(GoodsListActivity.this, "扫描失败，请重扫条形码", Toast.LENGTH_LONG).show();
 				Intent openCameraIntent = new Intent(GoodsListActivity.this,CaptureActivity.class);
 				startActivityForResult(openCameraIntent, 0);
 			}
 			}
 		}

	public final class ViewHolder{
		public TextView name;
		public TextView barcode;
		public TextView price;
		public Picker picker;
	    }
	public class MyAdapter extends BaseAdapter{
		private LayoutInflater mInflater;
		private ArrayList<Picker> pickerlist=new ArrayList<Picker>();
		public MyAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
			}
	        public int getCount() {
	            // TODO Auto-generated method stub
	             return goodslist.size();
	        }
	 
	        public Object getItem(int arg0) {
	            // TODO Auto-generated method stub
	            return null;
	        }
	 
	        public long getItemId(int arg0) {
	            // TODO Auto-generated method stub
	            return 0;
	        }
	     
	        public View getView(final int position, View convertView, ViewGroup parent) {
	 			ViewHolder holder;
	            if (convertView == null) {
	                 
	                holder=new ViewHolder();  
	                convertView = mInflater.inflate(R.layout.goodsitem, null);
	                holder.name=(TextView)convertView.findViewById(R.id.goodsinfo);
	                holder.barcode=(TextView)convertView.findViewById(R.id.barcode);
	                holder.price=(TextView)convertView.findViewById(R.id.price);
	                holder.picker=(Picker)convertView.findViewById(R.id.picker);
	                pickerlist.add(holder.picker);
	                convertView.setTag(holder);
	                 
	            }else {
	                 
	                holder = (ViewHolder)convertView.getTag();
	            }
	             
	             
	            holder.name.setText((String)goodslist.get(position).get("name"));
	            holder.barcode.setText((String)goodslist.get(position).get("barcode"));
	            holder.price.setText((String)goodslist.get(position).get("price"));
	            holder.picker.getButtonMinus().setOnClickListener(new Button.OnClickListener() {
					
					public void onClick(View v) {
						Picker getPicker=pickerlist.get(pickerlist.size()-position-1);
						//Picker getPicker=pickerlist.get(position);
						Integer quantity=Integer.parseInt(getPicker.getText().toString());
						if(quantity>=1)
						{
							quantity--;
							getPicker.setText(Integer.toString(quantity));
						}
						goodslist.get(position).put("quantity",quantity);
						Toast.makeText(GoodsListActivity.this, goodslist.get(position).values().toString() , 500).show();
					}
				});
	            holder.picker.getButtonPlus().setOnClickListener(new Button.OnClickListener() {
					
					public void onClick(View v) {
						Picker getPicker=pickerlist.get(pickerlist.size()-position-1);
						//Picker getPicker=pickerlist.get(position);
						Integer quantity=Integer.parseInt(getPicker.getText().toString())+1;
						getPicker.setText(Integer.toString(quantity));
						goodslist.get(position).put("quantity",quantity);
						Toast.makeText(GoodsListActivity.this, goodslist.get(position).values().toString() , 500).show();
					}
				});
	             
	            return convertView;
	        }
	         
	    }
	
}
