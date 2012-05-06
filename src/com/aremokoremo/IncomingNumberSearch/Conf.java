package com.aremokoremo.IncomingNumberSearch;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Context;

public class Conf{	
	private String confFilePath = "";

	//Constructor
	public Conf(Context context) {
		confFilePath = context.getFilesDir()+"/conf.txt";
	}

	//on:true, off:false
	public void saveOnOffStatus(boolean status) throws Exception
	{
		String str="";
		if(true==status)
		{
			str="ON";
		}
		else
		{
			str="OFF";
		}
		FileOutputStream fos=null;
		try {
			fos=new FileOutputStream(confFilePath);
			fos.write(str.getBytes());
			fos.close();
		} catch (Exception e) {
			if (fos!=null) fos.close();
			throw e;
		}
	}

	public boolean readOnOffStatus() throws Exception
	{
		boolean ret = false;

		int size;
		byte[] w=new byte[1024];
		FileInputStream fin=null;
		ByteArrayOutputStream out=null;
		try {
			fin=new FileInputStream(confFilePath);
			out=new ByteArrayOutputStream();
			while (true) {
				size=fin.read(w);
				if (size<=0) break;
				out.write(w,0,size);
			}
			fin.close();
			out.close();

			String str = new String(out.toByteArray());
			if(str.equals("ON"))
			{
				ret = true;
			}
			else
			{
				ret = false;
			}

			return ret;
		} catch (Exception e) {
			try {
				if (fin!=null) fin.close();
				if (out!=null) out.close();
			} catch (Exception e2) {
			}
			throw e;
		}

	}


}