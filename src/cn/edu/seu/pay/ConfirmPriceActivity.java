package cn.edu.seu.pay;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Map;

import cn.edu.seu.datatransportation.BluetoothDataTransportation;
import cn.edu.seu.datatransportation.LocalInfoIO;
import cn.edu.seu.main.MainActivity;
import cn.edu.seu.main.R;
import cn.edu.seu.record.Record;
import cn.edu.seu.record.Recorddh;
import cn.edu.seu.xml.Trade;
import cn.edu.seu.xml.XML;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.Toast;

public class ConfirmPriceActivity extends Activity{
	private TextView price,receivername;
	private Button confirm;
    private ProgressDialog pd;
    private boolean loaded=false;
    private byte[] receive;
    private Trade trade;
	private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 1:
            	pd=new ProgressDialog(ConfirmPriceActivity.this);
				pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pd.setCancelable(false);
				pd.setMessage((String)msg.obj);  
				pd.show();
                break;
            case 0:
                pd.dismiss();
                break;
            case 2:
		    	AlertDialog.Builder builder = new Builder(ConfirmPriceActivity.this);
		    	builder.setTitle("付款结果").setMessage((String)msg.obj).setCancelable(false).setPositiveButton("确认", new OnClickListener(){

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						Intent intent=new Intent(ConfirmPriceActivity.this,MainActivity.class);
						startActivity(intent);
						GoodsListActivity.flag=1;
						ConfirmPriceActivity.this.finish();
						MainActivity.bdt.close();
						
					}
		    		
		    	});
		    	builder.show();
            	break;
            case 3:
            	pd.dismiss();
            	Toast.makeText(ConfirmPriceActivity.this, "连接超时，请重试", 5000).show();
            	confirm.setVisibility(View.VISIBLE);
            	break;
            }
            super.handleMessage(msg);
        }
    };
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.confirmprice);
        confirm=(Button) findViewById(R.id.confirm4);
        price=(TextView)findViewById(R.id.price);
        receivername=(TextView)findViewById(R.id.receivername);
        Intent intent=getIntent();
        receive=intent.getByteArrayExtra("receive");
        XML info =new XML();
        info.setTrade(trade);
        trade=info.parseIndividualTradeXML(new ByteArrayInputStream(receive));
        price.setText(trade.getTotalPrice()+"元");
        receivername.setText("收款人:"+trade.getReceiverName());
        confirm.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Date dt=new Date();
				String cardnumber=MainActivity.person.getCardnum();
				String tradetime=String.valueOf(dt.getTime()/1000);
				String buyerimei=MainActivity.person.getImei();
				String username=MainActivity.person.getUsername();
				String buyerdevice=BluetoothDataTransportation.getLocalMac().replaceAll(":","");
				String salerdevice=MainActivity.bdt.getRemoteMac().replaceAll(":","");
				int totalpricefill=(int)(Double.valueOf(trade.getTotalPrice())*100);
				String pricefill=String.format("%08d",totalpricefill);
				String buyerdevicesub=buyerdevice.substring(buyerdevice.length()-4,buyerdevice.length());
				String salerdevicesub=salerdevice.substring(salerdevice.length()-4,salerdevice.length());
				int buyerdevicefill=Integer.parseInt(buyerdevicesub,16);
				String buyerfill=String.format("%05d",buyerdevicefill);
				int salerdevicefill=Integer.parseInt(salerdevicesub,16);
				String salerfill=String.format("%05d",salerdevicefill);
				String words=tradetime+buyerfill+salerfill+pricefill;
				Log.d("words",words);
				RSA rsa=new RSA();
				String cipher=rsa.setRSA(words);
				XML confirmTrade=new XML();
				trade.setPayerDevice(buyerdevice);
				trade.setPayerName(username);
				trade.setPayerIMEI(buyerimei);
				trade.setPayerCardNumber(cardnumber);
				trade.setTradeTime(tradetime);
				trade.setCipher(cipher);
				confirmTrade.setTrade(trade);
				String xml=confirmTrade.produceIndividualTradeXML("individualTrade");
				MainActivity.bdt.write(xml);
				new Thread()
				{
					public void run()
					{
						Message msg=handler.obtainMessage();
						msg.what=1;
						msg.obj="正在确认付款";
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
						byte receive[]=MainActivity.bdt.read();
						XML payResult=new XML();
						String sentence=payResult.parseSentenceXML(new ByteArrayInputStream(receive));
						if(sentence=="")
						{
							//更新余额,交易记录
		 					//给余额赋值
							sentence=payResult.parseBalanceXML(new ByteArrayInputStream(receive));
							String balance = sentence;
							LocalInfoIO lio = new LocalInfoIO("sdcard/data" , "local.data");
							lio.modifyBalance(balance);
							//给交易记录赋值
							Record record = new Record( 0 ,trade.getPayerName(),trade.getPayerDevice(),trade.getPayerIMEI(),trade.getReceiverName(),trade.getReceiverDevice(),trade.getReceiverIMEI(),Double.parseDouble(trade.getTotalPrice()),"收款", trade.getTradeTime());
							Recorddh rdh = new Recorddh(ConfirmPriceActivity.this , "recorddb" , null , 1);
							rdh.insert(record);
							Message msg=handler.obtainMessage();
		            		msg.what=2;
		            		msg.obj="付款成功";
		            		msg.sendToTarget();
						}
						else
						{
							Message msg=handler.obtainMessage();
		            		msg.what=2;
		            		msg.obj="付款失败";
		            		msg.sendToTarget();
						}
					}
				}.start();
				
			}
        	
        });
	}
}
