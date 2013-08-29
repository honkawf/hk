package cn.edu.seu.datatransportation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BluetoothDataTransportation {

	public static BluetoothSocket socket;
	public static int isConnected=0;
	public static String mac="";
	private ReadThread mreadThread;
	public static byte [] receive=null;
	public static int receiveConnection=0;
	public static String msg=null;
	private static InputStream is;
	public static  BluetoothServerSocket mserverSocket;
	public static BluetoothAdapter btAdapt=BluetoothAdapter.getDefaultAdapter();  
    public static void pair(String address)
    {
            if(btAdapt.isDiscovering())
            	btAdapt.cancelDiscovery();            
            BluetoothDevice btDev = btAdapt.getRemoteDevice(address); 
            try { 
                if (btDev.getBondState() == BluetoothDevice.BOND_NONE) { 
                   Log.i("pair蓝牙状态","远程设备发送蓝牙配对请求"); 
                   Log.i("蓝牙地址",address);
                    //这里只需要createBond就行了
					//ClsUtils.setPin(btDev.getClass(), btDev, "0000"); // 手机和蓝牙采集器配对
					ClsUtils.createBond(btDev.getClass(), btDev);   
					//ClsUtils.cancelPairingUserInput(btDev.getClass(),btDev);
                }else if(btDev.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.i("pair蓝牙状态","已经配对"); 
                } 
            } catch (Exception e) { 
                Log.e("pair错误","配对出错");
            } 
    }
       
	public class ClientThread extends Thread { 		
		public void run() {
			try {
				BluetoothDevice device=btAdapt.getRemoteDevice(mac);
				socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
				//连接
				Log.i("connect", "请稍候，正在连接服务器:"+mac);
				socket.connect();
				isConnected=1;
				Log.i("connect", "已经连接上服务端！");
			} 
			catch (IOException e) 
			{
				Log.e("connect", "", e);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				run();
			} 
		}
	};
	public class ServerThread extends Thread { 
		public void run() {
					
			try {
				/* 创建一个蓝牙服务器 
				 * 参数分别：服务器名称、UUID	 */	
				mserverSocket = btAdapt.listenUsingRfcommWithServiceRecord("btspp",
						UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));		
				
				Log.d("server", "wait cilent connect...");
				
				socket = mserverSocket.accept();
		         Log.d("point","4");

				receiveConnection=1;
				Log.d("server", "accept success !");
				//启动接受数据
				mreadThread = new ReadThread();
				mreadThread.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	public class SendThread extends Thread
	{		
		 public void run() {
			 if (socket == null) 
			 {
				 Log.e("警告","没有连接");
				 return;
			 }
			 try {				
				 OutputStream os = socket.getOutputStream();
				 byte [] send=plusHead(msg.getBytes().length);
				 os.write(send);
				 Log.i("发送长度",String.valueOf(send));
				 os.write(msg.getBytes());
				 Log.i("发送",msg);
				 os.flush();
			 } catch (IOException e) {
				 // TODO Auto-generated catch block
				 Log.e("sendMessageHandle警告","连接失败");
				 e.printStackTrace();
			 }	
		 }
	}
	public static byte[] plusHead(int length)
    {
    	String head=Integer.toString(length);
		byte [] temp=head.getBytes();
		byte [] send=new byte[16];
		for(int i=16-temp.length,j=0;j<temp.length;i++,j++)
		{
			send[i]=temp[j];
		}
		return send;
    }   
    public class ReadThread extends Thread { 
        public void run() {
        	
            byte[] buffer = new byte[16];
            
			try {
				is = socket.getInputStream();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	
			try {
				int total=0;
		        is.read(buffer);
		        total=readHead(buffer);
		        byte [] receiveTemp=new byte[total];
				is.read(receiveTemp);
				receive=receiveTemp;
				String s = new String(receive);
				Log.i("收到",s);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
    public static int readHead(byte [] buffer)
    {
		int total=0;
		int counter=0;
        for(counter=0;counter<16;counter++)
        {
        	if(buffer[counter]!='\0')
        		break;
        }
        byte []tmp=new byte[16-counter];
        System.arraycopy(buffer, counter, tmp, 0, 16-counter);
        try
        {
        	total=Integer.parseInt(new String(tmp));
        }
        catch(Exception e)
        {
        	Log.e("错误","数据传输丢失");
        }
        return total;
        
    }
    public static InputStream  Read() {
    	
        byte[] buffer = new byte[1024];
        int bytes;
        byte [] newbuffer = null;
        InputStream mmInStream = null;
        
		try {
			mmInStream = socket.getInputStream();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		try {
            // Read from the InputStream
            if( (bytes = mmInStream.read(buffer)) > 0 )
            {
            	newbuffer = new byte[bytes];
		    	for(int i=0; i<bytes; i++)
		    	{
		    		newbuffer[i] = buffer[i];
		    	}
		    	Log.e("receive",new String(newbuffer));
            }
        } catch (IOException e) {
        	
        }
		return mmInStream;
    }    
    public static String getLocalMac()
    {
    	String localmac=btAdapt.getAddress();
    	return localmac;
    }
    public static boolean send(String xml)
	{
		if(xml.equals(""))
			return false;
		BluetoothDataTransportation bo=new BluetoothDataTransportation();
		if(socket==null)
		{
			BluetoothDataTransportation.ClientThread ct=bo.new ClientThread();
			ct.start();
			while(isConnected==0);
			isConnected=0;
		}
		BluetoothDataTransportation.msg=xml;
		BluetoothDataTransportation.SendThread st=bo.new SendThread();
		st.start();
		return true;
	}
	public  static byte[] receive()
	{
		BluetoothDataTransportation bo=new BluetoothDataTransportation();
		BluetoothDataTransportation.ReadThread rt=bo.new ReadThread();
		rt.start();
		while(receive==null);
		return receive;

	}
}
