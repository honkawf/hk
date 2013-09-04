package cn.edu.seu.xml;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

public class XML {
 
	private Trade trade;
	 public String parseBalanceXML(InputStream is)
	    {
	    	String sentence="";
	        XmlPullParser xpp = Xml.newPullParser(); 
	        try {
				xpp.setInput(is, "utf-8");
				for (int i = xpp.getEventType(); i != XmlPullParser.END_DOCUMENT; i = xpp.next())
				{        	
				    if (i==XmlPullParser.START_TAG)
				    {
				    	if(xpp.getName().equals("information"))
				    	{

		            		if(!xpp.getAttributeValue(null,"event").equals("localbalance"))
		            			return sentence;
		            		else
		            		{	
		            			sentence=xpp.nextText();
		            		}

		            	}
				    }
				}

			} catch (XmlPullParserException e) {
					Log.e("错误","未成功接收xml");
			} catch (IOException e) {
				e.printStackTrace();
			}
	        return sentence;
	    }
    public String parseSentenceXML(InputStream is)
    {
    	String sentence="";
        XmlPullParser xpp = Xml.newPullParser(); 
        try {
			xpp.setInput(is, "utf-8");
			for (int i = xpp.getEventType(); i != XmlPullParser.END_DOCUMENT; i = xpp.next())
			{        	
			    if (i==XmlPullParser.START_TAG)
			    {
			    	if(xpp.getName().equals("information"))
			    	{

	            		if(!xpp.getAttributeValue(null,"event").equals("sentence"))
	            			return sentence;
	            		else
	            		{	
	            			sentence=xpp.nextText();
	            		}

	            	}
			    }
			}

		} catch (XmlPullParserException e) {
				Log.e("错误","未成功接收xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
        return sentence;
    }
    public String productSentenceXML(String info)
    {
    	 StringWriter stringWriter = new StringWriter();  
	        try {  
	            // 获取XmlSerializer对象  
	            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();  
	            XmlSerializer xmlSerializer = factory.newSerializer();  
	            // 设置输出流对象  
	            xmlSerializer.setOutput(stringWriter);  
	         
	            xmlSerializer.startDocument("utf-8", true);
	            xmlSerializer.startTag(null, "information");
	            xmlSerializer.attribute(null, "event", "sentence");
	            xmlSerializer.text(info);
	            xmlSerializer.endTag(null, "information");
	            xmlSerializer.endDocument();
    		} catch (Exception e) {  
    			e.printStackTrace();  
    		}  
    		return stringWriter.toString();  
    }
	public Trade parseIndividualTradeXML(InputStream is)
	{
		Trade trade=new Trade();
        XmlPullParser xpp = Xml.newPullParser(); 
        try {
			xpp.setInput(is, "utf-8");
			for (int i = xpp.getEventType(); i != XmlPullParser.END_DOCUMENT; i = xpp.next())
			{        	
			    if (i==XmlPullParser.START_TAG)
			    {
			    	if(xpp.getName().equals("information"))
			    	{

	            		if(!xpp.getAttributeValue(null,"event").equals("individualTrade"))
	            			return null;
	            		else
	            			continue;

	            	}
			    	if (xpp.getName().equals("payername"))
	                	trade.setPayerName(xpp.nextText());
			    	else if (xpp.getName().equals("payerdevice"))
	                	trade.setPayerDevice(xpp.nextText());
			    	else if (xpp.getName().equals("payerimei"))
	                	trade.setPayerIMEI(xpp.nextText());
			    	else if (xpp.getName().equals("payercardnumber")) 
	                	trade.setPayerCardNumber(xpp.nextText());
	                else if (xpp.getName().equals("receivername")) 
	                	trade.setReceiverName(xpp.nextText());
	                else if (xpp.getName().equals("receiverdevice")) 
	                	trade.setReceiverDevice(xpp.nextText());
	                else if (xpp.getName().equals("receiverimei")) 
	                	trade.setReceiverIMEI(xpp.nextText());
	                else if (xpp.getName().equals("receivercardnumber")) 
	                	trade.setReceiverCardNumber(xpp.nextText());
	                else if(xpp.getName().equals("tradetime"))
	                	trade.setTradeTime(xpp.nextText());
	                else if(xpp.getName().equals("totalprice"))
	                	trade.setTotalPrice(xpp.nextText());
	                else if(xpp.getName().equals("cipher"))
	                	trade.setCipher(xpp.nextText());
	                
			    }
			}

		} catch (XmlPullParserException e) {
				Log.e("错误","未成功接收xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
        return trade;
	}
	public void setTrade(Trade trade){ 
    	this.trade=trade;
 }
	 public void setTrade(String payerdevice,String payername,String payerimei, String payercardnumber,String receiverdevice,String receivername,
				String receiverimei,String receivercardnumber,String tradetime,String totalprice,String cipher){ 
	    	trade=new Trade( payerdevice, payername, payerimei,  payercardnumber, receiverdevice, receivername,
	    			 receiverimei, receivercardnumber, tradetime, totalprice, cipher);
	 }
	 public String produceIndividualTradeXML(String event)
	    {
	    	 StringWriter stringWriter = new StringWriter();  
		        try {  
		            // 获取XmlSerializer对象  
		            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();  
		            XmlSerializer xmlSerializer = factory.newSerializer();  
		            // 设置输出流对象  
		            xmlSerializer.setOutput(stringWriter);  
		         
		            xmlSerializer.startDocument("utf-8", true);
		            xmlSerializer.startTag(null, "information");
		            xmlSerializer.attribute(null, "event", event);
		            xmlSerializer.startTag(null, "trade");
		            
		            xmlSerializer.startTag(null, "payername");  
	                xmlSerializer.text(trade.getPayerName());  
	                xmlSerializer.endTag(null, "payername");  
	                
		            xmlSerializer.startTag(null, "payerdevice");  
	                xmlSerializer.text(trade.getPayerDevice());  
	                xmlSerializer.endTag(null, "payerdevice");  
	                

		            xmlSerializer.startTag(null, "payerimei");  
	                xmlSerializer.text(trade.getPayerIMEI());  
	                xmlSerializer.endTag(null, "payerimei");  
	                
	                xmlSerializer.startTag(null, "payercardnumber");  
	                xmlSerializer.text(trade.getPayCardNumber());  
	                xmlSerializer.endTag(null, "payercardnumber");  
	               
	                
	                xmlSerializer.startTag(null, "receivername");  
	                xmlSerializer.text(trade.getReceiverName());  
	                xmlSerializer.endTag(null, "receivername");  
	                
		            xmlSerializer.startTag(null, "receiverdevice");  
	                xmlSerializer.text(trade.getReceiverDevice());  
	                xmlSerializer.endTag(null, "receiverdevice");  
	                

		            xmlSerializer.startTag(null, "receiverimei");  
	                xmlSerializer.text(trade.getReceiverIMEI());  
	                xmlSerializer.endTag(null, "receiverimei");  
	                
	                
		            xmlSerializer.startTag(null, "receivercardnumber");  
	                xmlSerializer.text(trade.getReceiverCardNumber());  
	                xmlSerializer.endTag(null, "receivercardnumber");  
	                
	                xmlSerializer.startTag(null, "tradetime");  
	                xmlSerializer.text(trade.getTradeTime());  
	                xmlSerializer.endTag(null, "tradetime");  
	                
	                xmlSerializer.startTag(null, "totalprice");  
	                xmlSerializer.text(trade.getTotalPrice());  
	                xmlSerializer.endTag(null, "totalprice");
	                
	                xmlSerializer.startTag(null, "cipher");  
	                xmlSerializer.text(trade.getCipher());  
	                xmlSerializer.endTag(null, "cipher");  
	                
	                
		            xmlSerializer.endTag(null, "trade");
		            xmlSerializer.endTag(null, "information");
		            xmlSerializer.endDocument();  
		        } catch (Exception e) {  
		            e.printStackTrace();  
		        }  
		        return stringWriter.toString();  
	    }
}
