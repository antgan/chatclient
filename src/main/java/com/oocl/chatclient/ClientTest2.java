package com.oocl.chatclient;

import java.util.Date;

import com.oocl.protocol.Action;
import com.oocl.protocol.Protocol;

public class ClientTest2 {

	public static void main(String[] args) {
		Client c1 = new Client();
		String result1 = c1.login("Zero", "127.0.0.1", "5000");
		System.out.println("登录结果："+result1);
		c1.setFlagRun(true);
		c1.start();
		
		//测试私聊
		c1.sendMessage(new Protocol(Action.Chat, "Zero","Abel", "hi",new Date().getTime()));
		//测试群聊
		c1.sendMessage(new Protocol(Action.Chat, "Zero","all", "hi",new Date().getTime()));
		
		try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
