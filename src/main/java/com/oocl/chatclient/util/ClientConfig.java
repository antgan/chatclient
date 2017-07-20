package com.oocl.chatclient.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;



/**
 * 客户端的配置
 * @author GANAB
 *
 */
public class ClientConfig {
	public String LOGIN_SERVER_HOST;
	public String LOGIN_SERVER_PORT;
	public String REGISTER_SERVER_HOST;
	public String REGISTER_SERVER_PORT;
	public String CHAT_SERVER_HOST;
	public String CHAT_SERVER_PORT;
	private static ClientConfig instance;  
	
	private ClientConfig() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("client.properties"));
			LOGIN_SERVER_HOST = properties.getProperty("LOGIN_SERVER_HOST");
			LOGIN_SERVER_PORT = properties.getProperty("LOGIN_SERVER_PORT");
			REGISTER_SERVER_HOST = properties.getProperty("REGISTER_SERVER_HOST");
			REGISTER_SERVER_PORT = properties.getProperty("REGISTER_SERVER_PORT");
			CHAT_SERVER_HOST = properties.getProperty("CHAT_SERVER_HOST");
			CHAT_SERVER_PORT = properties.getProperty("CHAT_SERVER_PORT");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static synchronized ClientConfig getInstance(){
		if(instance == null){
			instance = new ClientConfig();
		}
		return instance;
	}
}
