package cn.edu.seu.saler;

import java.io.ByteArrayInputStream;

import cn.edu.seu.xml.XML;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class WaitingPayThread extends Thread{

	private Handler handler;
	public WaitingPayThread(Handler handler)
	{
		this.handler=handler;
	}
	public void run()
	{
		 while(true)
		 {
			 try
			 {
				 MainActivity.bdt.createServer();
				 Message msg=handler.obtainMessage();
				 msg.what=1;
				 while(!MainActivity.bdt.isConnected());
				 byte[]receive=MainActivity.bdt.read();
				 XML info=new XML();
				 String sentence=info.parseSentenceXML(new ByteArrayInputStream(receive));
				 if(sentence.equals("我要付款"))
					 msg.sendToTarget();
				 else
					 MainActivity.bdt.close();
			 }
			 catch(Exception e)
			 {
				 MainActivity.bdt.close();
			 }
		 }
			 
	}
}
