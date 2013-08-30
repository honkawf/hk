package com.XML;
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
    private ArrayList<Goods> goodslist=new ArrayList<Goods>();
    private Trade trade;
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
	            xmlSerializer.endTag(null, "information");
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
	    public void setTrade(String buyerdevice, String salerdevice,String tradetime,String totalprice,String cipher,String cardnumber){ 
	    	trade=new Trade();
	    	trade.setBuyerDevice(buyerdevice);
	    	trade.setSalerDevice(salerdevice);
	    	trade.setTradeTime(tradetime);
	    	trade.setTotalPrice(totalprice);
	    	trade.setCipher(cipher);
	    	trade.setCardNumber(cardnumber);
	    }   
	

}
