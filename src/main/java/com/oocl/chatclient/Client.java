package com.oocl.chatclient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.oocl.chatclient.protocol.Action;
import com.oocl.chatclient.protocol.Protocol;

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
		String loginResult = null;
		
		try {
			socket = new Socket(hostIp, Integer.parseInt(hostPort));
		} catch (NumberFormatException e) {
			loginResult = "NumberFormatException";
			return loginResult;
		} catch (UnknownHostException e) {
			loginResult = "UnknownHostException";
			return loginResult;
		} catch (IOException e) {
			loginResult = "IOException";
			return loginResult;
		}
		return "true";
	}

	/**
	 * 只负责接收服务端消息。
	 */
	@Override
	public void run() {
		while(flagRun){
			try {
				if(ois.available()>0){
					response = (Protocol)ois.readObject();
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
				}else if(response.getAction() == Action.List){
					//更新在线人数
				}else if(response.getAction() == Action.Login){
					//输出谁谁谁登录了
					System.out.println(response.getAction()+" "+response.getFrom());
				}else if(response.getAction() == Action.Logout){
					//客户端登出
				}else if(response.getAction() == Action.Shake){
					//振屏
				}else if(response.getAction() == Action.Exit){
					//服务器退出，全体关闭
				}

			}
		}
	}
	
	/**
	 * 登录成功，聊天框呈现
	 */
	public void showChatFrame(String username) {
		initStream();//初始化连接
//		c_chatFrame = new Client_chatFrame(this,username);
//		c_chatFrame.setVisible(true);
		flagRun = true;
		this.start();
	}
	
	private void initStream() {
		try {
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
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
		if(c.login("Abel", "localhost", "5000").equals("true")){
			System.out.println("登录成功");
		}
		c.showChatFrame("Abel");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
