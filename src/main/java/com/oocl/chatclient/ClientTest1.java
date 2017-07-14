package com.oocl.chatclient;

import java.util.Date;

import com.oocl.protocol.Action;
import com.oocl.protocol.Protocol;

public class ClientTest1 {

	public static void main(String[] args) {
		Client c1 = new Client();
		String result1 = c1.login("Alince", "127.0.0.1", "5000");
		System.out.println("登录结果："+result1);
		c1.setFlagRun(true);
		c1.start();
		
		//Shack
		c1.sendMessage(new Protocol(Action.Shake, "Alince","Abel", null ,new Date().getTime()));
		
		try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
