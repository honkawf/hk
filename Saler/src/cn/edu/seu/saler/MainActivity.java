package cn.edu.seu.saler;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


import java.util.UUID;








import com.salertest.R;

import cn.edu.seu.datatransportation.BluetoothDataTransportation;
import cn.edu.seu.xml.PersonInfo;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
public class MainActivity extends Activity {
    /** Called when the activity is first created. */
	Button btnSearch, btnDis, btnExit,btnSend,btnOpen; 
	ToggleButton tbtnSwitch; 
	ListView lvBTDevices; 
	ArrayAdapter<String> adtDevices; 
	List<String> lstDevices = new ArrayList<String>(); 
	BluetoothAdapter btAdapt; 
	public static BluetoothSocket btSocket;
	public static final String PROTOCOL_SCHEME_RFCOMM = "btspp";
	public static  BluetoothDataTransportation bdt=new BluetoothDataTransportation();
	private TextView receive;
	public static PersonInfo person=new PersonInfo();
	private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 1:
            	Intent intent=new Intent(MainActivity.this,InputActivity.class);
            	startActivity(intent);
                break;
            case 0:
                break;
				 
            }
            super.handleMessage(msg);
        }
    };
     @Override 
     public void onCreate(Bundle savedInstanceState) { 
         super.onCreate(savedInstanceState); 
         setContentView(R.layout.main); 
         //初始化person信息
         {
        	 person.setUsername("Chris");
        	 person.setBluetoothmac(BluetoothDataTransportation.getLocalMac().replace(":", ""));
        	 person.setCardnum("1234567890123456789");
        	 person.setImei("213213");
         }
         btnExit = (Button) this.findViewById(R.id.btnExit); 
         btnExit.setOnClickListener(new ClickEvent()); 
         btnDis = (Button) this.findViewById(R.id.btnDis); 
         btnDis.setOnClickListener(new ClickEvent()); 
         btnSend=(Button)findViewById(R.id.send);
         btnSend.setOnClickListener(new ClickEvent());
         btnOpen=(Button)findViewById(R.id.open);
         btnOpen.setOnClickListener(new ClickEvent());
         receive=(TextView)findViewById(R.id.receive);
         // ToogleButton设置 
         tbtnSwitch = (ToggleButton) this.findViewById(R.id.tbtnSwitch); 
         tbtnSwitch.setOnClickListener(new ClickEvent()); 
        WaitingPayThread wpt=new WaitingPayThread(handler);
        wpt.start();
         
  
     } 
     @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		WaitingPayThread wpt=new WaitingPayThread(handler);
	    wpt.start();
	}
	class ClickEvent implements View.OnClickListener { 
         @Override 
         public void onClick(View v) { 
             if (v == btnSearch)// 搜索蓝牙设备，在BroadcastReceiver显示结果 
             { 
                 if (btAdapt.getState() == BluetoothAdapter.STATE_OFF) {// 如果蓝牙还没开启 
                     Toast.makeText(MainActivity.this, "请先打开蓝牙", 1000) 
                             .show(); 
                     return; 
                 } 
                 if (btAdapt.isDiscovering()) 
                     btAdapt.cancelDiscovery(); 
                 lstDevices.clear(); 
                 Object[] lstDevice = btAdapt.getBondedDevices().toArray(); 
                 for (int i = 0; i < lstDevice.length; i++) { 
                     BluetoothDevice device = (BluetoothDevice) lstDevice[i]; 
                     String str = "	已配对|" + device.getName() + "|" 
                             + device.getAddress(); 
                     lstDevices.add(str); // 获取设备名称和mac地址 
                     adtDevices.notifyDataSetChanged(); 
                 } 
                 setTitle("本机：" + btAdapt.getAddress()); 
                 btAdapt.startDiscovery(); 
             } else if (v == tbtnSwitch) {// 本机蓝牙启动/关闭 
                 if (tbtnSwitch.isChecked() == false) 
                     btAdapt.enable(); 
  
                 else if (tbtnSwitch.isChecked() == true) 
                     btAdapt.disable(); 
                
             } else if (v == btnDis)// 本机可以被搜索 
             { 
                 Intent discoverableIntent = new Intent( 
                         BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE); 
                 discoverableIntent.putExtra( 
                         BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0); 
                 startActivity(discoverableIntent); 
             } else if (v == btnExit) { 
                 try { 
                     if (btSocket != null) 
                         btSocket.close(); 
                 } catch (IOException e) { 
                     e.printStackTrace(); 
                 } 
                 MainActivity.this.finish(); 
             } 
             else if(v==btnSend)
             {
            	TextView sendDialog=(TextView)findViewById(R.id.message);
            	 String mes=(String)sendDialog.getText().toString();
            	 if(mes=="")
            		 Toast.makeText(MainActivity.this,"发送内容不能为空",Toast.LENGTH_SHORT).show();
            	 else
            	{
            		 sendDialog.setText("");
            	}
             }
         } 
     } 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
 	
}