package com.sqlite;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {  
    private static final int VERSION = 1;  
  
    /** 
     * 在SQLiteOpenHelper的子类当中，必须有该构造函数 
     * @param context   上下文对象 
     * @param name      数据库名称 
     * @param factory 
     * @param version   当前数据库的版本，值必须是整数并且是递增的状态 
     */  
    public DatabaseHelper(Context context, String name, CursorFactory factory,  
            int version) {  
        //必须通过super调用父类当中的构造函数  
        super(context, name, factory, version);  
    }  
      
    public DatabaseHelper(Context context, String name, int version){  
        this(context,name,null,version);  
    }  
  
    public DatabaseHelper(Context context, String name){  
        this(context,name,VERSION);  
    }  
  
    //该函数是在第一次创建的时候执行，实际上是第一次得到SQLiteDatabase对象的时候才会调用这个方法  
    @Override  
    public void onCreate(SQLiteDatabase db) { 
        //execSQL用于执行SQL语句  
        db.execSQL("create table if not exists record(id integer primary key autoincrement not null , username varchar not null , price varchar not null , type varchar not null , time varchar not null)");  
    }
  
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
        // TODO Auto-generated method stub  
        System.out.println("upgrade a database");  
    }
    
	public void insert(String name , String price , String type , String time){
    	String sql = "insert into record(username,price,type,time) values('" +name+ "' , '" +price+ "' , '" +type+ "' , " +time+ ")";
    	getWritableDatabase().execSQL(sql);
    }
    
    public Records [] query(){
    	getWritableDatabase().execSQL("create table if not exists record(id integer primary key autoincrement not null , username varchar not null , price varchar not null , type varchar not null , time varchar not null)");
    	Cursor c = getReadableDatabase().query("record", new String[] { "username",  
        "price" , "type" , "time" }, null , null , null, null, null);
    	Records [] list = new Records[c.getCount()];
    	if(c != null &&c.moveToFirst()){
    		for(int i = 0 ; i < c.getCount(); i++){
				list[i] = new Records(c.getString(0) , c.getString(1) , c.getString(2) , c.getString(3));
    			c.moveToNext();
    		}
    		c.close();
    	}
    	else{
    		list = null;
    	}
    	return list;
    }
}  