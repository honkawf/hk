package com.sqlite;

public class Check {

	private String payername;
	private String payercardnum;
	private String totalprice;
	private String transfertime;
	private String xml;
	private String cash;
	
	public Check(){
		
	}
	public Check(String pn , String pd , String tp , String tt , String x ,String c){
		setpn(pn);
		setpd(pd);
		settp(tp);
		settt(tt);
		setx(x);
		setc(c);
	}
	
	public void setpn(String pn){
		payername = pn;
	}
	public String getpn(){
		return payername;
	}
	public void setpd(String pd){
		payercardnum = pd;
	}
	public String getpd(){
		return payercardnum;
	}
	public void settp(String tp){
		totalprice = tp;
	}
	public String gettp(){
		return totalprice;
	}
	public void settt(String tt){
		transfertime = tt;
	}
	public String gettt(){
		return transfertime;
	}
	public void setx(String x){
		xml = x;
	}
	public String getx(){
		return xml;
	}
	public void setc(String c){
		cash = c;
	}
	public String getc(){
		return cash;
	}
}
