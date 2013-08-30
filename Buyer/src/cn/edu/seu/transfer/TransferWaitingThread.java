package cn.edu.seu.transfer;

import java.io.ByteArrayInputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import cn.edu.seu.datatransportation.BluetoothReadThread;
import cn.edu.seu.datatransportation.BluetoothServerThread;
import cn.edu.seu.datatransportation.BluetoothWriteThread;

import com.XML.XML;
import com.buyertest.MainActivity;
import com.sqlite.Checkdh;

public class TransferWaitingThread extends Thread{

	private Transfer transfer;
	private boolean isReceived=false;
	private Handler handler;
	public TransferWaitingThread(Handler handler)
	{
		this.handler=handler;
	}
	public Transfer getTransfer()
	{
		return transfer;
	}
	public void run()
	{
		while(true)
		{
			try
    		{
				
    			BluetoothServerThread st=new BluetoothServerThread();
    		    st.start();
    		    BluetoothSocket socket=st.getSocket();
    		    Log.d("point","1");
    			while(!st.isConnected());
    			BluetoothReadThread brt=new BluetoothReadThread(socket);
    			brt.start();
    				 /*被连接上自动发送自己信息，用于个体户付款*/
             		/* XML info=new XML();
             		 info.addPersonData(person.getUserName(), "","", "", person.getBluetoothMac(), "", "","", "");;
             		 String xml=info.producePersonXML("sendReceiverInfo");
             		 Log.d("发送",xml);
             		 BluetoothOperation.send(xml);*/
    				 
    			XML info=new XML();
                byte[] receive=brt.getReceive();
                transfer=info.parseTransferXML(new ByteArrayInputStream(receive));
             	String xml=info.productSentenceXML("转账成功");
             	BluetoothWriteThread bwt=new BluetoothWriteThread(socket,xml);
             	bwt.start();
             	Message msg=handler.obtainMessage();
             	msg.what=1;
             	msg.obj=receive;
             	
             	msg.sendToTarget();
             	st.close();
           
    		}
    		catch(Exception e)
    		{
    			Message msg=handler.obtainMessage();
    			msg.what=0;
    			msg.sendToTarget();
    		}
         }
             
	}
}
