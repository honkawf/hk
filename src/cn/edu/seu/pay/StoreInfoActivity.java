package cn.edu.seu.pay;

import java.io.ByteArrayInputStream;
import java.util.Date;

import cn.edu.seu.datatransportation.BluetoothClientThread;
import cn.edu.seu.datatransportation.BluetoothDataTransportation;
import cn.edu.seu.main.MainActivity;
import cn.edu.seu.main.R;
import cn.edu.seu.xml.XML;

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
	private boolean loaded=false;
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
            	Intent intent = new Intent(StoreInfoActivity.this,ConfirmPriceActivity.class);
            	intent.putExtra("receive", (byte[])msg.obj);
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
        			 Log.i("蓝牙地址",mac);
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
                        	 while(!MainActivity.bdt.isConnected())
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
                        	 Log.d("point","4");
                     		 msg=handler.obtainMessage();
                     		 msg.what=0;
                     		 msg.sendToTarget();
        				 }
        			 }.start();
             		new Thread()
             		{
             			public void run()
             			{
             				 MainActivity.bdt.createSocket();
                			 Log.i("确认信息","顾客已确认");
             			}
             		}.start();

                 }
                 else if(type.equals("individual"))
                 {
                	 new Thread()
        			 {
        				 public void run()
        				 {
        					 Log.i("point","1");
        					 Message msg=handler.obtainMessage();
                        	 msg.what=1;
                        	 msg.obj="正在连接服务器";
                        	 msg.sendToTarget();
                        	 Date dstart=new Date();
                        	 long start=dstart.getTime()/1000;
                        	 while(!loaded)
                        	 {
                        		 Date dend=new Date();
                        		 long end=dend.getTime()/1000;
                        		 if(end-start>=20)
                        		 {
                        			 msg=handler.obtainMessage();
                             		 msg.what=2;
                             		 msg.obj=storeName;
                             		 msg.sendToTarget();
                             		 return;
                        		 }
                        	 }
                        	 loaded=false;
                        	
        				 }
        			 }.start();
        			new Thread()
        			{
        				public void run()
        				{
        					 Log.i("确认信息","顾客已确认");
                			 XML info=new XML();
                			 String xml=info.productSentenceXML("我要付款");
                			 MainActivity.bdt.createSocket();
                			 MainActivity.bdt.write(xml);
                			 byte[] receive=MainActivity.bdt.read();
                			 Log.d("point","4");
                			 loaded=true;
                			 Message msg=handler.obtainMessage();
                     		 msg=handler.obtainMessage();
                     		 msg.what=3;
                     		 msg.obj=receive;
                     		 msg.sendToTarget();
        				}
        			}.start();
                 	
                 	
                 }
			}
         });
	}
}
