package cn.edu.seu.transfer;

public class Transfer {

	private String payerdevice;
	private String receiverdevice;
	private String payername;
	private String receivername;
	private String transfertime;
	private String totalprice;
	private String cipher;
	private String payercardnumber;
	private String receivercardnumber;
	public String getPayerDevice()
	{
		return payerdevice;
	}
	public String getReceiverDevice()
	{
		return receiverdevice;
	}
	public String getPayerName()
	{
		return payername;
	}
	public String getReceiverName()
	{
		return receivername;
	}
	public String getTransferTime()
	{
		return transfertime;
	}
	public String getTotalPrice()
	{
		return totalprice;
	}
	public String getCipher()
	{
		return cipher;
	}
	public String getPayCardNumber()
	{
		return payercardnumber;
	}

	public String getReceiverCardNumber()
	{
		return receivercardnumber;
	}
	public void setPayerDevice(String payerdevice)
	{
		this.payerdevice=payerdevice;
	}
	public void setReceiverDevice(String receiverdevice)
	{
		this.receiverdevice=receiverdevice;
	}
	public void setPayerName(String payername)
	{
		this.payername=payername;
	}
	public void setReceiverName(String receivername)
	{
		this.receivername=receivername;
	}
	public void setTransferTime(String transfertime)
	{
		this.transfertime=transfertime;
	}
	public void setTotalPrice(String totalprice)
	{
		this.totalprice=totalprice;
	}
	public void setCipher(String cipher)
	{
		this.cipher=cipher;
	}
	public void setPayerCardNumber(String payercardnumber)
	{
		this.payercardnumber=payercardnumber;
	}

	public void setReceiverCardNumber(String receivercardnumber)
	{
		this.receivercardnumber=receivercardnumber;
	}
}
