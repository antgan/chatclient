package com.oocl.chatclient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.oocl.protocol.Action;
import com.oocl.protocol.Protocol;

/**
 * 用户
 * 
 * @author GANAB
 * 
 */
public class Client extends Thread {
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private boolean flagRun = false;
	/**
	 * 在线人数
	 */
	private List<String> userOnline;
	private String userName = null;
	private Protocol response;

	public Client() {
		userOnline = new ArrayList<String>();
	}
	
	public String login(String userName, String hostIp, String hostPort) {
		this.userName = userName;
		Protocol loginRequest = null;
		String loginResult = null;
		try {
			socket = new Socket(hostIp, Integer.parseInt(hostPort));
			initStream();//初始化连接
			loginRequest = new Protocol(Action.Login, this.userName, new Date().getTime());
			//发送登录请求
			this.sendMessage(loginRequest);
			Protocol response = (Protocol)ois.readObject();
			if("success".equals(response.getMsg())){
				return "true";
			}else if("exist".equals(response.getMsg())){
				return "用户已存在";
			}
		} catch (NumberFormatException e) {
			loginResult = "请求参数不正确";
			return loginResult;
		} catch (UnknownHostException e) {
			loginResult = "无效主机Host";
			return loginResult;
		} catch (IOException e) {
			loginResult = "程序错误：[IOException]";
			return loginResult;
		} catch (ClassNotFoundException e) {
			loginResult = "程序错误：[ClassNotFoundException]";
			return loginResult;
		}
		return "false";
	}

	/**
	 * 只负责接收服务端消息。
	 */
	@Override
	public void run() {
		while(flagRun){
			try {
				Object o = ois.readObject();
				if(o!=null){
					response = (Protocol)o;
				}else{
					continue;
				}
			} catch (ClassNotFoundException e) {
				flagRun = false;
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//接收服务器
			if(response!=null){
				if(response.getAction() == Action.Chat){
					//处理聊天记录
					System.out.println(response);
				}else if(response.getAction() == Action.NotifyLogin){
					//更新在线用户
					System.out.println(response);
				}else if(response.getAction() == Action.NotifyLogout){
					//更新在线用户
					System.out.println(response);
				}else if(response.getAction() == Action.Shake){
					System.out.println(response);
					//振屏
				}else if(response.getAction() == Action.List){
					//更新列表
					System.out.println(response);
				}else if(response.getAction() == Action.Exit){
					//服务器退出，全体关闭
					System.out.println(response);
				}else{
					System.out.println("无效返回值");
				}

			}
		}
	}
	
	/**
	 * 登录成功，聊天框呈现
	 */
	public void showChatFrame(String username) {
//		c_chatFrame = new Client_chatFrame(this,username);
//		c_chatFrame.setVisible(true);
		flagRun = true;
		this.start();
	}
	
	private void initStream() {
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 发送消息
	 * @param request
	 */
	public void sendMessage(Protocol request) {
		try {
			oos.writeObject(request);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 退出客户端
	 * @return
	 */
	public void exitChat() {
		try {
			Protocol protocol = new Protocol(Action.Logout, this.userName, "server", userName+"退出登录", new Date().getTime());
			oos.writeObject(protocol);
			flagRun = false;
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ObjectInputStream getOis() {
		return ois;
	}

	public ObjectOutputStream getOos() {
		return oos;
	}

	public boolean isFlagRun() {
		return flagRun;
	}

	public void setFlagRun(boolean flagRun) {
		this.flagRun = flagRun;
	}
	
	public static void main(String[] args) {
		Client c = new Client();
		String result = c.login("Abel", "127.0.0.1", "5000");
		System.out.println("登录结果："+result);
		c.setFlagRun(true);
		c.start();
		
		try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
