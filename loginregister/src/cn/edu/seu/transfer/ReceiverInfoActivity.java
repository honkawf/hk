package cn.edu.seu.transfer;

import cn.edu.seu.main.R;

import cn.edu.seu.main.MainActivity;
import cn.edu.seu.pay.TimeOutProgressDialog;
import cn.edu.seu.pay.TimeOutProgressDialog.OnTimeOutListener;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ReceiverInfoActivity extends Activity{
	private TextView storeInfo;
	private Button btnConfirm;
	private TimeOutProgressDialog pd;
	private Thread sendAndReceiveThread;
	private final static String TAG="ReceiverInfoActivity";
		
	private String name,mac;
	private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 0:
                pd.dismiss();
                Intent intent=new Intent(ReceiverInfoActivity.this,TransferPriceActivity.class);
                startActivity(intent);
                ReceiverInfoActivity.this.finish();
                break;
            case 1:
	        	 pd=TimeOutProgressDialog.createProgressDialog(ReceiverInfoActivity.this,50000,new OnTimeOutListener(){

						public void onTimeOut(TimeOutProgressDialog dialog) {
							// TODO Auto-generated method stub
							AlertDialog.Builder builder = new Builder(ReceiverInfoActivity.this);
					    	builder.setTitle("连接信息").setMessage("连接失败").setCancelable(false).setPositiveButton("确认", new DialogInterface.OnClickListener(){

								public void onClick(DialogInterface arg0, int arg1) {
									// TODO Auto-generated method stub
									Intent intent=new Intent(ReceiverInfoActivity.this,MainActivity.class);
									startActivity(intent);
									ReceiverInfoActivity.this.finish();
									try
									{
										MainActivity.bdt.close();
									}
									catch(Exception e)
									{
										
									}
									
								}
					    		
					    	});
					    	builder.show();
						}
	            		
	            });
				pd.setProgressStyle(TimeOutProgressDialog.STYLE_SPINNER);
				pd.setCancelable(false);
				pd.setMessage((String)msg.obj); 
				pd.show();
	            break;
            case 2:
            	AlertDialog.Builder builder = new Builder(ReceiverInfoActivity.this);
		    	builder.setTitle("连接信息").setMessage("连接失败").setCancelable(false).setPositiveButton("确认", new OnClickListener(){

					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						Intent intent=new Intent(ReceiverInfoActivity.this,MainActivity.class);
						startActivity(intent);
						ReceiverInfoActivity.this.finish();
						MainActivity.bdt.close();
						
					}
		    		
		    	});
		    	builder.show();
             	break;
            }
            super.handleMessage(msg);
        }
    };
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		 super.onCreate(savedInstanceState); 
         setContentView(R.layout.receiver); 
         storeInfo=(TextView)findViewById(R.id.receiverInfo);
         btnConfirm=(Button)findViewById(R.id.confirm1);
         Intent intent=getIntent();
         name=intent.getStringExtra("name");
         mac=intent.getStringExtra("mac");
         storeInfo.setText("收款方："+name+"\n蓝牙地址："+mac);
         btnConfirm.setOnClickListener(new Button.OnClickListener(){
        	 public void onClick(View v) {
        		 // TODO Auto-generated method stub
        		 Message msg=handler.obtainMessage();
        		 msg.what=1;
        		 msg.obj="正在连接";
        		 msg.sendToTarget();
        		 sendAndReceiveThread=new Thread()
        		 {
        			 public void run()
        			 {
        				 try
                		 {
        					 TransferActivity.bdt.createSocket();
        					 Message msg=handler.obtainMessage();
        					 msg=handler.obtainMessage();
        					 msg.what=0;
        					 msg.sendToTarget();
                		 }
        				 catch(Exception e)
        				 {
        					 Message msg=handler.obtainMessage();
        					 Log.i(TAG,"连接失败");
        					 msg=handler.obtainMessage();
        					 msg.what=2;
        					 msg.sendToTarget();
        				 }

        			 }
        		 };
        		 sendAndReceiveThread.start();
			}
         });
	}
}
