package cn.edu.seu.transfer;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

import com.wgs.jiesuo.R;

import cn.edu.seu.datatransportation.BluetoothDataTransportation;
import cn.edu.seu.main.MainActivity;
import cn.edu.seu.pay.GoodsListActivity;
import cn.edu.seu.pay.RSA;
import cn.edu.seu.xml.PersonInfo;
import cn.edu.seu.xml.XML;


import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TransferPriceActivity extends Activity {
	private TextView textView1;
	private EditText editText1;
	private Button btnConfirm;
	private ProgressDialog pd;
	private boolean loaded=false;
	private PersonInfo receiver;
	private Handler handler = new Handler() {
	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
	         case 1:
	        	 pd=new ProgressDialog(TransferPriceActivity.this);
	        	 pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        	 pd.setCancelable(false);
	        	 pd.setMessage((String)msg.obj);  
	        	 pd.show();
	             break;
	         case 2:
	        	 AlertDialog.Builder alertDialog = new Builder(TransferPriceActivity.this);
	        	 alertDialog.setTitle("转账结果").setMessage((String)msg.obj).setCancelable(false);
	        	 alertDialog.setPositiveButton("确认", new DialogInterface.OnClickListener(){


						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							TransferPriceActivity.this.finish();
							Intent intent=new Intent(TransferPriceActivity.this,MainActivity.class);
							startActivity(intent);
							GoodsListActivity.flag=1;
							TransferActivity.bdt.close();
							
						}
			    		
			    	});
					alertDialog.show();
					break;
	         case 3:
	        	 pd.dismiss();
				 Toast.makeText(TransferPriceActivity.this, "连接超时", 2000).show();
	             break;
	     }
	     super.handleMessage(msg);
	  }
	};

    
	 public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.transferprice);
        btnConfirm=(Button) findViewById(R.id.btnConfirm);
        editText1=(EditText) findViewById(R.id.editText1);
        btnConfirm.setOnClickListener(new OnClickListener(){


			public void onClick(View v) {
				// TODO Auto-generated method stub

				new Thread()
				{
					public void run()
					{
						Message msg=handler.obtainMessage();
						msg.what=1;
						msg.obj="正在发送电子支票";
						msg.sendToTarget();
						Date d=new Date();
						long start=d.getTime()/1000;
						while(loaded==false)
						{
							long end=d.getTime()/1000;
							if(end-start>120)
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

						XML transfer=new XML();
						// 点击确认按钮后，获取用户输入金额，完成转账交易
						String totalprice=editText1.getText().toString();
						Date dt=new Date();
						String cardnumber=MainActivity.person.getCardnum();
						String username=MainActivity.person.getCustomername();
						String transfertime=String.valueOf(dt.getTime()/1000);
						String payerdevice=BluetoothDataTransportation.getLocalMac().replaceAll(":","");
						String receiverdevice=TransferActivity.bdt.getRemoteMac().replaceAll(":","");
						int totalpricefill=(int)(Double.valueOf(totalprice)*100);
						String pricefill=String.format("%08d",totalpricefill);
						String payerdevicesub=payerdevice.substring(payerdevice.length()-4,payerdevice.length());
						int payerdevicefill=Integer.parseInt(payerdevicesub,16);
						String payerfill=String.format("%05d",payerdevicefill);
						String words=transfertime+payerfill+pricefill;
						Log.d("words",words);
						RSA rsa=new RSA();
						String cipher=rsa.setRSA(words);
						transfer.setTransfer(payerdevice, "", username, "", transfertime, totalprice, cipher, cardnumber, "");
						String xml=transfer.produceTransferXML("transfer");
						TransferActivity.bdt.write(xml);
						Log.d("发送",xml);
						byte[] receive=TransferActivity.bdt.read();
						loaded=true;
						String sentence=transfer.parseSentenceXML(new ByteArrayInputStream(receive));
						Message msg=handler.obtainMessage();
						msg.what=2;
						msg.obj="转账失败";
						if(sentence.equals("转账成功"))
						{

							msg.obj="转账成功";
							//生成转账记录
							//扣除余额
						}
						msg.sendToTarget();
						TransferActivity.bdt.close();
								
					}
				}.start();
			}
        	
        });
        }
	     
 

}
