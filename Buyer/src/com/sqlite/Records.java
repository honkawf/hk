package com.sqlite;

import java.util.Date;

public class Records {

	private String username;
	private String price;
	private String type;
	private String time;

	public Records(String username , String price , String type , String time){
		setname(username);
		setprice(price);
		settype(type);
		settime(time);
	}
	
	public void setname(String n){
		username = n;
	}
	
	public String getname(){
		return username;
	}
	
	public void setprice(String p){
		price = p;
	}
	
	public String getprice(){
		return price;
	}
	
	public void settype(String t){
		type = t;
	}
	
	public String gettype(){
		return type;
	}
	
	public void settime(String d){
		time = d;
	}
	
	public String gettime(){
		return time;
	}
}
