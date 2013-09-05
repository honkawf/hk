package cn.edu.seu.saler;

import java.io.ByteArrayInputStream;
import java.util.Date;







import java.util.Properties;

import cn.edu.seu.datadeal.PropertyInfo;
import cn.edu.seu.datatransportation.LocalInfoIO;
import cn.edu.seu.datatransportation.NetDataTransportation;
import cn.edu.seu.record.Record;
import cn.edu.seu.record.Recorddh;
import cn.edu.seu.saler.TimeOutProgressDialog.OnTimeOutListener;
import cn.edu.seu.xml.Trade;
import cn.edu.seu.xml.XML;

import com.salertest.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class InputActivity extends Activity {

	private Button b1;
	private EditText ed;
    private TimeOutProgressDialog pd;
    private static final String TAG="InputActivity";
    private Thread sendAndReceiveThread;
	private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 0:
                pd.dismiss();
                break;
            case 1:
            	pd=TimeOutProgressDialog.createProgressDialog(InputActivity.this, 50000, new OnTimeOutListener(){

					@Override
					public void onTimeOut(TimeOutProgressDialog dialog) {
						// TODO Auto-generated method stub
						b1.setVisibility(View.VISIBLE);
		            	try
		            	{
		            		sendAndReceiveThread.interrupt();
		            	}
		            	catch(Exception e)
		            	{
		            		e.printStackTrace();
		            		Log.i(TAG,"打断线程失败");
		            	}
					    AlertDialog.Builder builder = new Builder(InputActivity.this);
				    	builder.setTitle("付款结果").setMessage("连接超时").setCancelable(false).setPositiveButton("确认", new DialogInterface.OnClickListener(){
				    		
				    		public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								Intent intent=new Intent(InputActivity.this,MainActivity.class);
								startActivity(intent);
								InputActivity.this.finish();
								Log.i(TAG, "跳转完成");
								MainActivity.bdt.close();
				    		}
				    	});
				    	builder.show();
		            	
					}
            		
            	});
				pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pd.setCancelable(false);
				pd.setMessage((String)msg.obj);  
				pd.show();
                break;
            case 2:
                AlertDialog.Builder builder = new Builder(InputActivity.this);
		    	builder.setTitle("收款结果").setMessage((String)msg.obj).setCancelable(false).setPositiveButton("确认", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						Intent intent=new Intent(InputActivity.this,MainActivity.class);
						startActivity(intent);
						InputActivity.this.finish();
						MainActivity.bdt.close();
						
					}
		    		
		    	});
		    	builder.show();
            	break;
				 
            }
            super.handleMessage(msg);
        }
    };
	public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.input);
        b1 = (Button)this.findViewById(R.id.button1);
        ed = (EditText)this.findViewById(R.id.editText1);
        b1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				XML info = new XML();
				b1.setVisibility(View.GONE);
				Trade trade=new Trade();
				trade.setReceiverCardNumber(MainActivity.person.getCardnum());
				trade.setReceiverDevice(MainActivity.person.getBluetoothmac());
				trade.setReceiverIMEI(MainActivity.person.getImei());
				trade.setReceiverName(MainActivity.person.getUsername());
				trade.setTotalPrice(ed.getText().toString());
				info.setTrade(trade);
				MainActivity.bdt.write(info.produceIndividualTradeXML("individualTrade"));
				Message msg=handler.obtainMessage();
				msg.what=1;
				msg.obj="正在处理";
				msg.sendToTarget();
				Thread sendAndReceiveThread=new Thread()
				{
					public void run()
					{
						try
						{

							XML info=new XML();
							byte[] receive=MainActivity.bdt.read();
							Trade trade=info.parseIndividualTradeXML(new ByteArrayInputStream(receive));
							Properties server=PropertyInfo.getProperties();
							NetDataTransportation ndt=new NetDataTransportation();
							ndt.connect(server.getProperty("serverAddress"),Integer.parseInt(server.getProperty("serverPort")));
							ndt.write(new String(receive));
							byte[] receive1=ndt.read();
							Message msg=handler.obtainMessage();
							msg.what=0;
							msg.sendToTarget();
							String sentence=info.parseSentenceXML(new ByteArrayInputStream(receive1));
							msg=handler.obtainMessage();
							msg.what=2;
							if(sentence.equals(""))
							{
								byte[] receive2=ndt.read();
								sentence=info.parseBalanceXML(new ByteArrayInputStream(receive1));
								msg.obj="收款成功";
								if(sentence.equals(""))
								{
									MainActivity.bdt.write(new String(receive1));
									sentence=info.parseBalanceXML(new ByteArrayInputStream(receive2));
								}
								else
								{
									MainActivity.bdt.write(new String(receive2));
								}
								Log.i("sentence","test"+sentence);
								String balance = sentence;
								LocalInfoIO lio = new LocalInfoIO("sdcard/data" , "local.dat");
								lio.modifyBalance(balance);
								//给交易记录赋值
								Record record = new Record( 0 ,trade.getPayerName(),trade.getPayerDevice(),trade.getPayerIMEI(),trade.getReceiverName(),trade.getReceiverDevice(),trade.getReceiverIMEI(),Double.parseDouble(trade.getTotalPrice()),"收款", trade.getTradeTime());
								Recorddh rdh = new Recorddh(InputActivity.this , "recorddb" , null , 1);
								rdh.insert(record);
							}
							else
							{
								msg.obj="收款失败";
							}
							msg.sendToTarget();
						}
						catch(Exception e)
						{
							e.printStackTrace();
							Log.i(TAG, "操作失败");
						}
					}
				};
				sendAndReceiveThread.start();
			}
        });
	}
}
