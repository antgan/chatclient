package com.oocl.chatclient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Vector;

import com.oocl.chatclient.frame.ChatFrame;
import com.oocl.chatclient.frame.LoginFrame;
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
	private ChatFrame chatFrame;
	private String userName = null;
	private Protocol response;

	public Client() {}
	
	/**
	 * 拿Token 登录聊天服务器
	 * @param userName
	 * @param token
	 * @param hostIp
	 * @param hostPort
	 * @return
	 */
	public String login(String userName, String token, String hostIp, String hostPort) {
		Protocol loginRequest = null;
		String loginResult = null;
		try {
			socket = new Socket(hostIp, Integer.parseInt(hostPort));
			initStream();//初始化连接
			loginRequest = new Protocol(Action.Login, this.userName, new Date().getTime());
			loginRequest.setMsg(token);
			//发送登录请求
			this.sendMessage(this.oos, loginRequest);
			Protocol response = Protocol.fromJson((String)ois.readObject());
			//接收登录结果
			return response.getMsg();
		} catch (NumberFormatException e) {
			loginResult = "请求参数不正确";
			return loginResult;
		} catch (UnknownHostException e) {
			loginResult = "无效主机Host";
			return loginResult;
		} catch (IOException e) {
			loginResult = "程序错误：[无法连接到主机HOST]";
			return loginResult;
		} catch (ClassNotFoundException e) {
			loginResult = "程序错误：[ClassNotFoundException]";
			return loginResult;
		}
	}
	

	public String validate(String userName, String pwd, String hostIp, String hostPort) {
		this.userName = userName;
		Protocol validateRequest = null;
		String loginResult = null;
		Socket validateSocket = null;
		ObjectOutputStream validateOos = null;
		ObjectInputStream validateOis = null;
		try {
			//与登录中心建立短链接，校验用户，获取token
			validateSocket = new Socket(hostIp, Integer.parseInt(hostPort));
			validateOos = new ObjectOutputStream(validateSocket.getOutputStream());
			validateOis = new ObjectInputStream(validateSocket.getInputStream());
			
			validateRequest = new Protocol(Action.Login, this.userName, new Date().getTime());
			validateRequest.setPwd(pwd);
			//发送登录请求
			this.sendMessage(validateOos, validateRequest);
			
			Protocol response = Protocol.fromJson((String)validateOis.readObject());
			//接收令牌
			if("failure".equals(response.getMsg())){
				return "InvalidToken";
			}else{
				String token = response.getMsg();
				return token;
			}
		} catch (NumberFormatException e) {
			loginResult = "请求参数不正确";
			return loginResult;
		} catch (UnknownHostException e) {
			loginResult = "无效主机Host";
			return loginResult;
		} catch (IOException e) {
			loginResult = "程序错误：[无法连接到主机HOST]";
			return loginResult;
		} catch (ClassNotFoundException e) {
			loginResult = "程序错误：[ClassNotFoundException]";
			return loginResult;
		} finally {
			try {
				validateOos.close();
				validateOis.close();
				validateSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 只负责接收服务端消息。 
	 */
	@Override
	public void run() {
		if(socket.isClosed()){
			flagRun =false;
		}
		while(flagRun){
			try {
				Object o = ois.readObject();
				if(o!=null){
					response = Protocol.fromJson((String)o);
				}else{
					continue;
				}
			} catch (ClassNotFoundException e) {
				flagRun = false;
			} catch (IOException e) {
				flagRun = false;
			}
			
			//接收服务器
			if(response!=null){
				if(response.getAction() == Action.Chat){
					if(!response.getFrom().equals(this.userName)){
						this.chatFrame.appendDisMsg(response.getFrom(), response.getTo(), response.getMsg(), response.getTime());	
					}
				}else if(response.getAction() == Action.NotifyLogin){
					this.chatFrame.appendLoginLogoutMsg(response.getFrom(), response.getTime(), true);
				}else if(response.getAction() == Action.NotifyLogout){
					this.chatFrame.appendLoginLogoutMsg(response.getFrom(), response.getTime(), false);
				}else if(response.getAction() == Action.Shake){
					if(!response.getFrom().equals(this.userName)){
						this.chatFrame.shakeWindow(response.getFrom(), response.getTime());
					}
				}else if(response.getAction() == Action.List){
					String[] users = response.getMsg().split(",");
					Vector<String> userList = new Vector<String>();
					for(String s: users){
						if(!userName.equals(s)){
							userList.add(s);
						}
					}
					this.chatFrame.updateUserOnline(userList);
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
		chatFrame=new ChatFrame(this, username);
		chatFrame.setVisible(true);
		
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
	public void sendMessage(ObjectOutputStream oos, Protocol request) {
		try {
			oos.writeObject(request.toJson());
			oos.flush();
		} catch (IOException e) {
			chatFrame.alertMsg("Server off line. ");
			System.exit(0);
		}
	}
	
	/**
	 * 退出客户端
	 * @return
	 */
	public void exitChat() {
		try {
			Protocol protocol = new Protocol(Action.Logout, this.userName, "server", userName+"退出登录", new Date().getTime());
			oos.writeObject(protocol.toJson());
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

	/**
	 * send to person
	 * @param msg
	 * @param toWho
	 */
	public void sendMsg(String msg, String toWho) {
		Protocol request = new Protocol(Action.Chat, this.userName, toWho, msg, new Date().getTime());
		this.sendMessage(this.oos,request);
	}

	/**
	 * shake person
	 * @param toWho
	 */
	public void shake(String toWho, long time) {
		Protocol request = new Protocol(Action.Shake, this.userName, toWho, null, time);
		this.sendMessage(this.oos,request);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public static void main(String[] args) {
		Client client = new Client();
		LoginFrame loginFrame = new LoginFrame(client);
		loginFrame.setVisible(true);
	}
}
