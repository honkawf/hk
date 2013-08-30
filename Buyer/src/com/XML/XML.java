package com.XML;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import cn.edu.seu.transfer.Transfer;
import android.util.Log;
import android.util.Xml;

public class XML {
    private ArrayList<Goods> goodslist=new ArrayList<Goods>();
    private Trade trade;
    private PersonInfo person;
    private Transfer transfer;
    public Goods parsePriceXML(InputStream is)
    {
    	Goods goods=new Goods();
        XmlPullParser xpp = Xml.newPullParser(); 
        try {
			xpp.setInput(is, "utf-8");
			for (int i = xpp.getEventType(); i != XmlPullParser.END_DOCUMENT; i = xpp.next())
			{
			    switch (i) {  
	            case XmlPullParser.START_TAG:  
	                if (xpp.getName().equals("barcode"))
	                	goods.setBarcode(xpp.nextText());
	                else if (xpp.getName().equals("name")) 
	                	goods.setName(xpp.nextText());
	                else if (xpp.getName().equals("price")) 
	                	goods.setPrice(xpp.nextText());
	                else if(xpp.getName().equals("quantity"))
	                	goods.setQuantity(xpp.nextText());
	                break;
	            }
			}
		} catch (XmlPullParserException e) {
				Log.e("错误","未成功接收xml");
		} catch (IOException e) {
			e.printStackTrace();
		} 
        return goods;
    }
    public String parseTotalPriceXML(InputStream is)
    {
    	String totalprice="";
        XmlPullParser xpp = Xml.newPullParser(); 
        try {
			xpp.setInput(is, "utf-8");
			for (int i = xpp.getEventType(); i != XmlPullParser.END_DOCUMENT; i = xpp.next())
			{

			    switch (i) {  
	            case XmlPullParser.START_TAG: 
	            	if(xpp.getName().equals("information"))
	            	{

	            		if(!xpp.getAttributeValue(null,"event").equals("sendTotalPrice"))
	            			return totalprice;
	            	}
	            	if (xpp.getName().equals("totalprice"))
	            	{
	            		totalprice=xpp.nextText();
	            	}
	            	break;
	                
	            }
			}

		} catch (XmlPullParserException e) {
				Log.e("错误","未成功接收xml");
		} catch (IOException e) {
			e.printStackTrace();
		} 
        return totalprice;
    }
    public String parseSentenceXML(InputStream is)
    {
    	String sentence="";
        XmlPullParser xpp = Xml.newPullParser(); 
        try {
			xpp.setInput(is, "utf-8");
			for (int i = xpp.getEventType(); i != XmlPullParser.END_DOCUMENT; i = xpp.next())
			{        	
			    switch (i) {  
	            case XmlPullParser.START_TAG: 
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

    public Transfer parseTransferXML(InputStream is){

        transfer=new Transfer();
        XmlPullParser xpp = Xml.newPullParser(); 
        try{
        	xpp.setInput(is, "utf-8");
    		for (int i = xpp.getEventType(); i != XmlPullParser.END_DOCUMENT; i = xpp.next())
    		{        	
    		    if (i==XmlPullParser.START_TAG )
    		    {
    		    		if (xpp.getName().equals("payerdevice"))
    	                	transfer.setPayerDevice(xpp.nextText());
    	                else if (xpp.getName().equals("receiverdevice")) 
    	                	transfer.setReceiverDevice(xpp.nextText());
    	                else if (xpp.getName().equals("payername")) 
    	                	transfer.setPayerName(xpp.nextText());
    	                else if(xpp.getName().equals("receivername"))
    	                	transfer.setReceiverName(xpp.nextText());
    	                else if(xpp.getName().equals("payercardnumber"))
    	                	transfer.setPayerCardNumber(xpp.nextText());
    	                else if(xpp.getName().equals("receivercardnumber"))
    	                	transfer.setReceiverCardNumber(xpp.nextText());
    	                else if(xpp.getName().equals("transfertime"))
    	                	transfer.setTransferTime(xpp.nextText());
    	                else if(xpp.getName().equals("cipher"))
    	                	transfer.setCipher(xpp.nextText());
    	                else if(xpp.getName().equals("totalprice"))
    	                	transfer.setTotalPrice(xpp.nextText());

                    
                }	
    		}
        }
        catch(Exception e)
        {
			e.printStackTrace();  
        }
        return transfer;
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
	public String producePriceXML(String event){  
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
	            xmlSerializer.startTag(null, "goodslist");  
	            for(Goods Goods:goodslist){  
	               
	                xmlSerializer.startTag(null, "goods");  
	                xmlSerializer.startTag(null, "barcode");  
	                xmlSerializer.text(Goods.getBarcode());  
	                xmlSerializer.endTag(null, "barcode"); 
	                
	                xmlSerializer.startTag(null, "name");  
	                xmlSerializer.text(Goods.getName());  
	                xmlSerializer.endTag(null, "name");  
	                  
	                xmlSerializer.startTag(null, "price");  
	                xmlSerializer.text(Goods.getPrice());  
	                xmlSerializer.endTag(null, "price");  
	                
	                xmlSerializer.startTag(null, "quantity");  
	                xmlSerializer.text(Goods.getQuantity());  
	                xmlSerializer.endTag(null, "quantity");  
	                  
	                xmlSerializer.endTag(null, "goods");  
	            }  
	            xmlSerializer.endTag(null, "goodslist");
	            xmlSerializer.endTag(null, "information");
	            xmlSerializer.endDocument();  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	        return stringWriter.toString();  
	  
	    }  
	    public void addData(String barcode,String name, String price,String quantity){  
	        Goods newGoods =new Goods(barcode, name,price,quantity);
	        goodslist.add(newGoods); 
	    }  
	    public String produceTradeXML(String event){  
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
	            xmlSerializer.startTag(null, "goodslist");  
	            for(Goods Goods:goodslist){  
	               
	                xmlSerializer.startTag(null, "goods");  
	                xmlSerializer.startTag(null, "barcode");  
	                xmlSerializer.text(Goods.getBarcode());  
	                xmlSerializer.endTag(null, "barcode"); 
	                
	                xmlSerializer.startTag(null, "name");  
	                xmlSerializer.text(Goods.getName());  
	                xmlSerializer.endTag(null, "name");  
	                  
	                xmlSerializer.startTag(null, "price");  
	                xmlSerializer.text(Goods.getPrice());  
	                xmlSerializer.endTag(null, "price");  
	                
	                xmlSerializer.startTag(null, "quantity");  
	                xmlSerializer.text(Goods.getQuantity());  
	                xmlSerializer.endTag(null, "quantity");  
	                  
	                xmlSerializer.endTag(null, "goods");  
	            }  
	            xmlSerializer.endTag(null, "goodslist");
	            xmlSerializer.startTag(null, "trade");
	            
	            xmlSerializer.startTag(null, "buyerdevice");  
                xmlSerializer.text(trade.getBuyerDevice());  
                xmlSerializer.endTag(null, "buyerdevice");  
                
                xmlSerializer.startTag(null, "salerdevice");  
                xmlSerializer.text(trade.getSalerDevice());  
                xmlSerializer.endTag(null, "salerdevice");  
                
                xmlSerializer.startTag(null, "tradetime");  
                xmlSerializer.text(trade.getTradeTime());  
                xmlSerializer.endTag(null, "tradetime");  
                
                xmlSerializer.startTag(null, "totalprice");  
                xmlSerializer.text(trade.getTotalPrice());  
                xmlSerializer.endTag(null, "totalprice");
                
                xmlSerializer.startTag(null, "cipher");  
                xmlSerializer.text(trade.getCipher());  
                xmlSerializer.endTag(null, "cipher");  
                
                xmlSerializer.startTag(null, "cardnumber");  
                xmlSerializer.text(trade.getCardNumber());  
                xmlSerializer.endTag(null, "cardnumber");  
                
	            xmlSerializer.endTag(null, "trade");
	            xmlSerializer.endTag(null, "information");
	            xmlSerializer.endDocument();  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	        return stringWriter.toString();  
	  
	    }
	    public String produceTransferXML(String event){  
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
	            xmlSerializer.startTag(null, "transfer");
	            
	            xmlSerializer.startTag(null, "payerdevice");  
                xmlSerializer.text(transfer.getPayerDevice());  
                xmlSerializer.endTag(null, "payerdevice");  
                
                xmlSerializer.startTag(null, "receiverdevice");  
                xmlSerializer.text(transfer.getReceiverDevice());  
                xmlSerializer.endTag(null, "receiverdevice");  
                
                xmlSerializer.startTag(null, "payername");  
                xmlSerializer.text(transfer.getPayerName());  
                xmlSerializer.endTag(null, "payername");
                
                xmlSerializer.startTag(null, "receivername");  
                xmlSerializer.text(transfer.getReceiverName());  
                xmlSerializer.endTag(null, "receivername");
                
                xmlSerializer.startTag(null, "transfertime");  
                xmlSerializer.text(transfer.getTransferTime());  
                xmlSerializer.endTag(null, "transfertime");  
                
                xmlSerializer.startTag(null, "totalprice");  
                xmlSerializer.text(transfer.getTotalPrice());  
                xmlSerializer.endTag(null, "totalprice");
                
                xmlSerializer.startTag(null, "cipher");  
                xmlSerializer.text(transfer.getCipher());  
                xmlSerializer.endTag(null, "cipher");  
                
                xmlSerializer.startTag(null, "payercardnumber");  
                xmlSerializer.text(transfer.getPayCardNumber());  
                xmlSerializer.endTag(null, "payercardnumber");  

                xmlSerializer.startTag(null, "receivercardnumber");  
                xmlSerializer.text(transfer.getReceiverCardNumber());  
                xmlSerializer.endTag(null, "receivercardnumber");  
                
	            xmlSerializer.endTag(null, "transfer");
	            xmlSerializer.endTag(null, "information");
	            xmlSerializer.endDocument();  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	        return stringWriter.toString();  
	  
	    }
	    public void setTrade(String buyerdevice, String salerdevice,String tradetime,String totalprice,String cipher,String cardnumber){ 
	    	trade=new Trade();
	    	trade.setBuyerDevice(buyerdevice);
	    	trade.setSalerDevice(salerdevice);
	    	trade.setTradeTime(tradetime);
	    	trade.setTotalPrice(totalprice);
	    	trade.setCipher(cipher);
	    	trade.setCardNumber(cardnumber);
	    }  
	    public PersonInfo parsePersonXML(InputStream is){
			PersonInfo person = new PersonInfo();
			XmlPullParser xpp = Xml.newPullParser();
			try {
				xpp.setInput(is, "utf-8");
				for (int i = xpp.getEventType(); i != XmlPullParser.END_DOCUMENT; i = xpp.next())
				{
				    switch (i) {  
		            case XmlPullParser.START_TAG:  
		                if (xpp.getName().equals("userName"))
		                	person.setUserName(xpp.nextText());
		                else if (xpp.getName().equals("customerName")) 
		                	person.setCustomerName(xpp.nextText());
		                else if (xpp.getName().equals("cardNum")) 
		                	person.setCardNum(xpp.nextText());
		                else if(xpp.getName().equals("bluetoothMac"))
		                	person.setBluetoothMac(xpp.nextText());
		                else if(xpp.getName().equals("dynamicPasswordNum"))
		                	person.setDynamicPasswordNum(xpp.nextText());
		                else if(xpp.getName().equals("identificationCardNum"))
		                	person.setIdentificationCardNum(xpp.nextText());
		                else if(xpp.getName().equals("phoneNum"))
		                	person.setPhoneNum(xpp.nextText());
		                break;
		            }
				}
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
					Log.e("错误","未成功接收xml");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	        return person;
	    }
		
		public String producePersonXML(String event){  
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
	                xmlSerializer.startTag(null, "personInfo");  
	                xmlSerializer.startTag(null, "userName");  
	                xmlSerializer.text(person.getUserName());  
	                xmlSerializer.endTag(null, "userName"); 
	                
	                xmlSerializer.startTag(null, "customerName");  
	                xmlSerializer.text(person.getCustomerName());  
	                xmlSerializer.endTag(null, "customerName");  
	                  
	                xmlSerializer.startTag(null, "cardNum");  
	                xmlSerializer.text(person.getCardNum());  
	                xmlSerializer.endTag(null, "cardNum");  
	                
	                xmlSerializer.startTag(null, "password");  
	                xmlSerializer.text(person.getPassword());  
	                xmlSerializer.endTag(null, "password");  
	                
	                xmlSerializer.startTag(null, "bluetoothMac");  
	                xmlSerializer.text(person.getBluetoothMac());  
	                xmlSerializer.endTag(null, "bluetoothMac"); 
	                
	                xmlSerializer.startTag(null, "dynamicPasswordNum");  
	                xmlSerializer.text(person.getDynamicPasswordNum());  
	                xmlSerializer.endTag(null, "dynamicPasswordNum"); 
	                
	                xmlSerializer.startTag(null, "identificationCardNum");  
	                xmlSerializer.text(person.getIdentificationCardNum());  
	                xmlSerializer.endTag(null, "identificationCardNum"); 
	                
	                xmlSerializer.startTag(null, "phoneNum");  
	                xmlSerializer.text(person.getPhoneNum());  
	                xmlSerializer.endTag(null, "phoneNum"); 
	                
	                xmlSerializer.startTag(null, "balance");  
	                xmlSerializer.text(person.getBalance());  
	                xmlSerializer.endTag(null, "balance");  
	                  
	                xmlSerializer.endTag(null, "personInfo");
	            xmlSerializer.endTag(null, "information");
	            xmlSerializer.endDocument(); 
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	        return stringWriter.toString();  
	    }  

	    public void addPersonData(String userName, String customerName, String cardNum,String password,String bluetoothMac, 
	    	String dynamicPasswordNum, String identificationCardNum, String phoneNum, String balance){  
	        PersonInfo newPerson =new PersonInfo(userName, customerName, cardNum,password, bluetoothMac, dynamicPasswordNum, identificationCardNum, phoneNum,balance);
	        person=newPerson;
	    } 
	    public void setTransfer(String payerdevice,String receiverdevice,String payername,String receivername,String transfertime,String totalprice,String cipher,String payercardnumber,String receivercardnumber)
	    {
	    	transfer=new Transfer();
	    	transfer.setPayerDevice(payerdevice);
	    	transfer.setReceiverDevice(receiverdevice);
	    	transfer.setPayerName(payername);
	    	transfer.setReceiverName(receivername);
	    	transfer.setTotalPrice(totalprice);
	    	transfer.setTransferTime(transfertime);
	    	transfer.setCipher(cipher);
	    	transfer.setPayerCardNumber(payercardnumber);
	    	transfer.setReceiverCardNumber(receivercardnumber);
	    }
	    public void setTransfer(Transfer transfer)
	    {
	    	this.transfer=transfer;
	    }

}
