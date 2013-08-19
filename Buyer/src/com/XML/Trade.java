package com.XML;

public class Trade {

	private String buyerdevice;
	private String salerdevice;
	private String tradetime;
	private String totalprice;
	private String cipher;
	private String cardnumber;
	public String getBuyerDevice()
	{
		return buyerdevice;
	}
	public String getSalerDevice()
	{
		return salerdevice;
	}
	public String getTradeTime()
	{
		return tradetime;
	}
	public String getTotalPrice()
	{
		return totalprice;
	}
	public String getCipher()
	{
		return cipher;
	}
	public String getCardNumber()
	{
		return cardnumber;
	}
	public void setBuyerDevice(String buyerdevice)
	{
		this.buyerdevice=buyerdevice;
	}
	public void setSalerDevice(String salerdevice)
	{
		this.salerdevice=salerdevice;
	}
	public void setTradeTime(String tradetime)
	{
		this.tradetime=tradetime;
	}
	public void setTotalPrice(String totalprice)
	{
		this.totalprice=totalprice;
	}
	public void setCipher(String cipher)
	{
		this.cipher=cipher;
	}
	public void setCardNumber(String cardnumber)
	{
		this.cardnumber=cardnumber;
	}
}
