package com.XML;

public class PersonInfo {
	private String userName;
	private String customerName;
	private String cardNum;
	private String password;
	private String bluetoothMac;
	private String dynamicPasswordNum;
	private String identificationCardNum;
	private String phoneNum;
	private String balance;
	
	public PersonInfo(){
		this.userName = "";
		this.customerName = "";
		this.cardNum = "";
		this.bluetoothMac = "";
		this.dynamicPasswordNum = "";
		this.identificationCardNum = "";
		this.phoneNum = "";
		this.password = "";
		this.balance = "0";
		
	}
	
	public PersonInfo(String userName,String customerName,String cardNum,
			String password,String bluetoothMac, String dynamicPasswordNum,
			String identificationCardNum,String phoneNum,String balance){
		this.userName = userName;
		this.customerName = customerName;
		this.userName = cardNum;
		this.bluetoothMac = bluetoothMac;
		this.dynamicPasswordNum = dynamicPasswordNum;
		this.identificationCardNum = identificationCardNum;
		this.phoneNum = phoneNum;
		this.password = password;
		this.balance = balance;
	}
	
	public void setUserName(String userName){
		this.userName = userName;
	}
	
	public String getUserName(){
		return userName;
	}
	
	public void setCustomerName(String customerName){
		this.customerName = customerName;
	}
	
	public String getCustomerName(){
		return customerName;
	}
	
	public void setCardNum(String cardNum){
		this.cardNum = cardNum;
	}
	
	public String getCardNum(){
		return cardNum;
	}
	
	public void setBluetoothMac(String bluetoothMac){
		this.bluetoothMac = bluetoothMac;
	}
	
	public String getBluetoothMac(){
		return bluetoothMac;
	}
	
	public void setDynamicPasswordNum(String dynamicPasswordNum){
		this.dynamicPasswordNum = dynamicPasswordNum;
	}
	
	public String getDynamicPasswordNum(){
		return dynamicPasswordNum;
	}
	
	public void setIdentificationCardNum(String identificationCardNum){
		this.identificationCardNum = identificationCardNum;
	}
	
	public String getIdentificationCardNum(){
		return identificationCardNum;
	}
	
	public void setPhoneNum(String phoneNum){
		this.phoneNum = phoneNum;
	}
	
	public String getPhoneNum(){
		return phoneNum;
	}
	
	public void setPassword(String password){
		this.password = password;
	}
	
	public String getPassword(){
		return password;
	}
	
	public void setBalance(String balance){
		this.balance = balance;
	}
	
	public String getBalance(){
		return balance;
	}
	
}
