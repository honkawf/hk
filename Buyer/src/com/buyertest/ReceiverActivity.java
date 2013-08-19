package com.buyertest;

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
import android.widget.TextView;
import android.widget.Toast;

public class ReceiverActivity extends Activity{
	private TextView storeInfo;
	private Button btnConfirm;
	private ProgressDialog pd;
	private String name,mac;
	private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 1:
            	pd=new ProgressDialog(ReceiverActivity.this);
				pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pd.setCancelable(false);
				pd.setMessage((String)msg.obj);  
				pd.show();
                break;
            case 0:
                pd.dismiss();
                Intent intent=new Intent(ReceiverActivity.this,TransferPriceActivity.class);
                startActivity(intent);
                ReceiverActivity.this.finish();
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
         Log.d("point","4");
         btnConfirm.setOnClickListener(new Button.OnClickListener(){
        	 @Override
        	 public void onClick(View v) {
        		 // TODO Auto-generated method stub

                 new Thread()
 				{
 					public void run()
 					{
 						Message msg=handler.obtainMessage();
 						msg.what=1;
 						msg.obj="正在连接";
 						msg.sendToTarget();
 						while(BluetoothOperation.isConnected==0);
                  		BluetoothOperation.isConnected=0;
 						msg=handler.obtainMessage();
 						msg.what=0;
 						msg.sendToTarget();
 					}
 				}.start();
                BluetoothOperation.mac=mac;
            	BluetoothOperation bo=new BluetoothOperation();
         		BluetoothOperation.ClientThread ct=bo.new ClientThread();
         		ct.start();
         		
         		Log.d("point","1");
			}
         });
	}
}
