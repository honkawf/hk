package com.buyertest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cn.edu.seu.datatransportation.BluetoothDataTransportation;
import cn.edu.seu.datatransportation.BluetoothReadThread;
import cn.edu.seu.datatransportation.BluetoothServerThread;
import cn.edu.seu.datatransportation.BluetoothWriteThread;
import cn.edu.seu.datatransportation.ClsUtils;
import cn.edu.seu.transfer.Transfer;
import cn.edu.seu.transfer.TransferActivity;
import cn.edu.seu.transfer.TransferWaitingThread;

import com.XML.PersonInfo;
import com.XML.XML;
import com.sqlite.Checkdh;
import com.zxing.activity.CaptureActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
public class MainActivity extends Activity {
    /** Called when the activity is first created. */
	private Button btnClo, btnExit,btnSta,btnTran,btnCheck; 
	private BluetoothAdapter btAdapt; 
	private String scanResult;
	private Transfer transfer;
	public static PersonInfo person=new PersonInfo();;//这里写person的初始化
	
	public static BluetoothDataTransportation bdt=new BluetoothDataTransportation();
	private String mac;
	private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 0:
				 Toast.makeText(MainActivity.this, "转账失败", 2000).show();
				 break;
            case 1:
				 Toast.makeText(MainActivity.this, "收到转账,您可以到我的支票页面进行兑现或转发", 5000).show();
				 try
			     {
					 byte[]receive=(byte[])msg.obj;
					 Checkdh cdh = new Checkdh(MainActivity.this, "recorddb" , null, 1);
					 cdh.insert(transfer.getPayerName(), transfer.getPayCardNumber(), transfer.getTotalPrice(), transfer.getTransferTime(), receive , "0"); 
			     }
			     catch(Exception e)
			     {
			     	Log.e("数据库操作","失败");
			     }	
				 break;
            case 2:
            	 Toast.makeText(MainActivity.this, "连接服务器失败", 2000).show();
				 break;
            }
            super.handleMessage(msg);
        }
    };
     @Override 
     public void onCreate(Bundle savedInstanceState) { 
         super.onCreate(savedInstanceState); 
         setContentView(R.layout.main); 
         {
     		person.setUserName("payer");
     		person.setCustomerName("付款方");
     		person.setCardNum("4816057396530741749");
     	}
         btAdapt = BluetoothAdapter.getDefaultAdapter();// 初始化本机蓝牙功能 
         // Button 设置 
         btnSta=(Button)findViewById(R.id.btnSta);
         btnTran=(Button)findViewById(R.id.btnTran);
         btnCheck=(Button)findViewById(R.id.btnCheck);
         btnClo = (Button) this.findViewById(R.id.btnClo); 
         btnExit = (Button) this.findViewById(R.id.btnExit); 
         btnExit.setOnClickListener(new ClickEvent());
         btnTran.setOnClickListener(new ClickEvent());
         btnSta.setOnClickListener(new ClickEvent());
         btnClo.setOnClickListener(new ClickEvent());
         btnCheck.setOnClickListener(new ClickEvent()); 
         if(btAdapt==null)
         {
        	 Toast.makeText(MainActivity.this, "设备不支持蓝牙", Toast.LENGTH_LONG).show();
         }
         else
         {
        	 if(!btAdapt.isEnabled())
        	 {
        		 btAdapt.enable();
            	 Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            	 intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
            	 startActivity(intent);
        	 }
		 }
         //服务器与转账线程
         TransferWaitingThread twt=new TransferWaitingThread(handler);
         twt.start();
      
     } 
  
  
     @Override 
     protected void onDestroy() { 
         super.onDestroy(); 
         android.os.Process.killProcess(android.os.Process.myPid()); 
     } 

     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.main, menu);
         return true;
     }
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 		super.onActivityResult(requestCode, resultCode, data);
 			if (resultCode == RESULT_OK) {
 			Bundle bundle = data.getExtras();
 			scanResult = bundle.getString("result");
 			mac=scanResult.split(";")[1];
 			try {
 				bdt.connect(mac);
 				Toast.makeText(MainActivity.this, scanResult, Toast.LENGTH_LONG).show();
 				Intent store=new Intent(MainActivity.this,StoreInfoActivity.class);
 				store.putExtra("scanResult", scanResult);
 				startActivity(store);
 			} catch (Exception e) {
 				// TODO Auto-generated catch block
 				Toast.makeText(MainActivity.this, "扫描失败，请重扫二维码", Toast.LENGTH_LONG).show();
 				Intent openCameraIntent = new Intent(MainActivity.this,CaptureActivity.class);
 				startActivityForResult(openCameraIntent, 0);
 			}
 			}
 	}
     class ClickEvent implements View.OnClickListener { 
         @Override 
         public void onClick(View v) { 
        	 if (v == btnClo)
             {
            	 
            		 Set<BluetoothDevice> btDev=btAdapt.getBondedDevices();
            		 Iterator it=btDev.iterator();
            		 BluetoothDevice dev;
            		 while(it.hasNext())
            		 {
            			 dev=(BluetoothDevice) it.next();
            			 try {
							ClsUtils.removeBond(dev.getClass(), dev);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
            		 }
             }
             else if (v == btnExit) { 
            	 	if(bdt.isConnected())
                	 bdt.close();
              
                 MainActivity.this.finish(); 
             }
             else if(v==btnSta)
             {
            	 if(btAdapt==null)
  				{
  					Toast.makeText(MainActivity.this, "设备不支持蓝牙", Toast.LENGTH_LONG).show();
  				}
  				else
  				{
  					if(!btAdapt.isEnabled())
  					{
  						btAdapt.enable();
  					}
  					Intent openCameraIntent = new Intent(MainActivity.this,CaptureActivity.class);
  					startActivityForResult(openCameraIntent, 0);
  				}
  			}
            else if(v==btnTran)
            {
            	Intent intent = new Intent(MainActivity.this,TransferActivity.class);
				startActivity(intent);
            }
            else if(v==btnCheck)
            {
            	Intent intent = new Intent(MainActivity.this,CheckActivity.class);
				startActivity(intent);
            }
         }
     } 

}