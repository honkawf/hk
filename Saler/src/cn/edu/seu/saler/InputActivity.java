package cn.edu.seu.saler;

import java.io.ByteArrayInputStream;
import java.util.Date;







import java.util.Properties;

import cn.edu.seu.datadeal.PropertyInfo;
import cn.edu.seu.datatransportation.LocalInfoIO;
import cn.edu.seu.datatransportation.NetDataTransportation;
import cn.edu.seu.record.Record;
import cn.edu.seu.record.Recorddh;
import cn.edu.seu.xml.Trade;
import cn.edu.seu.xml.XML;

import com.salertest.R;

import android.app.Activity;
import android.app.ProgressDialog;
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
    private ProgressDialog pd;
    private boolean loaded=false;
	private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 1:
            	pd=new ProgressDialog(InputActivity.this);
				pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pd.setCancelable(false);
				pd.setMessage((String)msg.obj);  
				pd.show();
                break;
            case 0:
                pd.dismiss();
                break;
            case 3:
            	pd.dismiss();
            	Toast.makeText(InputActivity.this, "连接超时，请重试", 5000).show();
            	b1.setVisibility(View.VISIBLE);
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
				new Thread()
				{
					public void run()
					{
						Message msg=handler.obtainMessage();
						msg.what=1;
						msg.obj="正在处理";
						msg.sendToTarget();
						Date dstart=new Date();
                   	 	long start=dstart.getTime()/1000;
                   	 	while(!loaded)
                   	 	{
                   	 		Date dend=new Date();
                   	 		long end=dend.getTime()/1000;
                   	 		if(end-start>=50)
                   	 		{
                   	 			msg=handler.obtainMessage();
                        		msg.what=3;
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
						XML info=new XML();
						byte[] receive=MainActivity.bdt.read();
						Trade trade=info.parseIndividualTradeXML(new ByteArrayInputStream(receive));
						Properties server=PropertyInfo.getProperties();
						NetDataTransportation ndt=new NetDataTransportation();
						ndt.connect(server.getProperty("serverAddress"),Integer.parseInt(server.getProperty("serverPort")));
						ndt.write(new String(receive));
						byte[] receive1=ndt.read();
						byte[] receive2=ndt.read();
						Message msg=handler.obtainMessage();
						msg.what=0;
						msg.sendToTarget();
						loaded=true;
						String sentence=info.parseBalanceXML(new ByteArrayInputStream(receive1));
						Log.i("sentence",sentence);
						if(sentence.equals(""))
						{
							MainActivity.bdt.write(new String(receive1));
							sentence=info.parseBalanceXML(new ByteArrayInputStream(receive2));
						}
						else
						{
							MainActivity.bdt.write(new String(receive2));
						}
						String balance = sentence;
						LocalInfoIO lio = new LocalInfoIO("sdcard/data" , "local.data");
						lio.modifyBalance(balance);
						//给交易记录赋值
						Record record = new Record( 0 ,trade.getPayerName(),trade.getPayerDevice(),trade.getPayerIMEI(),trade.getReceiverName(),trade.getReceiverDevice(),trade.getReceiverIMEI(),Double.parseDouble(trade.getTotalPrice()),"收款", trade.getTradeTime());
						Recorddh rdh = new Recorddh(InputActivity.this , "recorddb" , null , 1);
						rdh.insert(record);
						
					}
				}.start();
			}
        });
	}
}
