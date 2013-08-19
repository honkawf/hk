package com.buyertest;

import java.util.Date;

import com.XML.XML;
import com.bluetooth.BluetoothOperation;
import com.bluetooth.BluetoothOperation.ClientThread;
import com.zxing.activity.CaptureActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class StoreInfoActivity extends Activity{
	private TextView storeInfo;
	private Button btnConfirm;
	private ProgressDialog pd;
	private String storeName,mac,type;
	private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 1:
            	pd=new ProgressDialog(StoreInfoActivity.this);
				pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pd.setCancelable(false);
				pd.setMessage((String)msg.obj); 
				pd.show();
                break;
            case 0:
                pd.dismiss();
       		 	Intent goodslist = new Intent(StoreInfoActivity.this,GoodsListActivity.class);
    			startActivity(goodslist);   
       		 	StoreInfoActivity.this.finish();
                break;
            case 2:
            	pd.dismiss();
            	Toast.makeText(StoreInfoActivity.this, "连接超时，请重试", 5000).show();
            	break;
            case 3:
            	pd.dismiss();
            	Intent intent = new Intent(StoreInfoActivity.this,GoodsListActivity.class);
    			startActivity(intent);   
       		 	StoreInfoActivity.this.finish();
            }
            super.handleMessage(msg);
        }
    };
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		 super.onCreate(savedInstanceState); 
         setContentView(R.layout.store); 
         storeInfo=(TextView)findViewById(R.id.storeInfo);
         btnConfirm=(Button)findViewById(R.id.confirm1);
         String Info[]=new String[3];
         try{
        	 Intent intent=getIntent();
        	 Info=intent.getStringExtra("scanResult").split(";");
        	 storeName=Info[0];
        	 mac=Info[1];
        	 type=Info[2];
        	 storeInfo.setText("店名："+storeName+"\n蓝牙地址："+mac+"\n类型："+type);
        	
         }
         catch(Exception e)
         {
        	 Toast.makeText(StoreInfoActivity.this, "扫描失败，请重扫二维码", Toast.LENGTH_LONG).show();
        	 Intent openCameraIntent = new Intent(StoreInfoActivity.this,CaptureActivity.class);
        	 startActivityForResult(openCameraIntent, 0);   
         }
         btnConfirm.setOnClickListener(new Button.OnClickListener(){
        	 @Override
        	 public void onClick(View v) {
        		 // TODO Auto-generated method stub
        		 if(type.equals("supermarket"))
        		 {
        			 new Thread()
        			 {
        				 public void run()
        				 {
        					 Message msg=handler.obtainMessage();
                        	 msg.what=1;
                        	 msg.obj="正在连接服务器";
                        	 msg.sendToTarget();
                        	 Date dstart=new Date();
                        	 long start=dstart.getTime()/1000;
                        	 while(BluetoothOperation.isConnected==0)
                        	 {
                        		 Date dend=new Date();
                        		 long end=dend.getTime()/1000;
                        		 if(end-start>=20)
                        		 {
                        			 msg=handler.obtainMessage();
                             		 msg.what=2;
                             		 msg.sendToTarget();
                             		 return;
                        		 }
                        	 }
                     		 BluetoothOperation.isConnected=0;
                        	 Log.d("point","4");
                     		 msg=handler.obtainMessage();
                     		 msg.what=0;
                     		 msg.sendToTarget();
        				 }
        			 }.start();
                	 BluetoothOperation.mac=mac;
                	 BluetoothOperation bo=new BluetoothOperation();
             		 BluetoothOperation.ClientThread ct=bo.new ClientThread();
             		 ct.start();
        			 Log.i("确认信息","顾客已确认");

                 }
                 else if(type.equals("privately"))
                 {
                	 new Thread()
        			 {
        				 public void run()
        				 {
        					 Message msg=handler.obtainMessage();
                        	 msg.what=1;
                        	 msg.obj="正在连接服务器";
                        	 msg.sendToTarget();
                        	 Date dstart=new Date();
                        	 long start=dstart.getTime()/1000;
                        	 while(BluetoothOperation.isConnected==0)
                        	 {
                        		 Date dend=new Date();
                        		 long end=dend.getTime()/1000;
                        		 if(end-start>=20)
                        		 {
                        			 msg=handler.obtainMessage();
                             		 msg.what=2;
                             		 msg.sendToTarget();
                             		 return;
                        		 }
                        	 }
                     		 BluetoothOperation.isConnected=0;
                        	 Log.d("point","4");
                     		 msg=handler.obtainMessage();
                     		 msg.what=0;
                     		 msg.sendToTarget();
        				 }
        			 }.start();
                	 BluetoothOperation.mac=mac;
                	 BluetoothOperation bo=new BluetoothOperation();
             		 BluetoothOperation.ClientThread ct=bo.new ClientThread();
             		 ct.start();
        			 Log.i("确认信息","顾客已确认");
                 	
                 	
                 }
			}
         });
	}
}