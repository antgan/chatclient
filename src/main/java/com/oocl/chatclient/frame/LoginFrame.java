package com.oocl.chatclient.frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.oocl.chatclient.Client;
import com.oocl.chatclient.util.ClientConfig;

public class LoginFrame extends JFrame implements ActionListener{
	private JLabel nameLb;
	private JTextField nameTf;
	private JTextField serverIpTf;
	private JTextField serverPortTf;
	private JLabel serverLb;
	private JLabel pwdLb;
	private JLabel ipLb;
	private JPasswordField pwdTf;
	private JButton loginBtn;
	private JButton registerBtn;
	private Client client;
	private String ip;
	public LoginFrame(Client client) {
		this.client=client;
		init();
		addEvent();
	}


	private void init() {
		//找IP
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		//this
		this.setTitle("WTIP Chat");
		this.setSize(300,240);
		this.setResizable(false);
		this.setLocationRelativeTo(null);//绝对布局
		
		WindowListener l=new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		};
		this.addWindowListener(l);
		
		this.setLayout(null);
		
		serverLb = new JLabel("Server host:");
		serverIpTf = new JTextField(ClientConfig.getInstance().CHAT_SERVER_HOST);
		serverPortTf = new JTextField(ClientConfig.getInstance().CHAT_SERVER_PORT);
		nameLb=new JLabel("UserName:");
		nameTf=new JTextField();
		pwdLb=new JLabel("Password:");
		pwdTf=new JPasswordField();
		
		ipLb = new JLabel("本机IP为 ： "+ip);
		ipLb.setBounds(75,170,150,30);
		
		loginBtn=new JButton("Login");
		registerBtn=new JButton("Register");
		serverLb.setBounds(30, 10, 100, 30);
		serverIpTf.setBounds(120, 10, 100, 30);
		serverPortTf.setBounds(230, 10, 40, 30);
		
		nameLb.setBounds(30, 50, 100,30);
		nameTf.setBounds(120, 50, 150,30);
		
		pwdLb.setBounds(30, 90, 100,30);
		pwdTf.setBounds(120, 90, 150,30);
		
		loginBtn.setBounds(60, 130, 90, 30);
		registerBtn.setBounds(150, 130, 90, 30);
		
		this.add(serverLb);
		this.add(serverIpTf);
		this.add(serverPortTf);
		this.add(nameLb);
		this.add(nameTf);
		this.add(pwdLb);
		this.add(pwdTf);
		this.add(loginBtn);
		this.add(registerBtn);
		this.add(ipLb);
	}
	
	private void addEvent() {
		loginBtn.addActionListener(this);
		registerBtn.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		//resetBtn ActionEvent
		if(e.getSource()==loginBtn){
			String userName = nameTf.getText().trim();
			String pwd = new String(pwdTf.getPassword()).trim();
			String hostIp = serverIpTf.getText().trim();
			String hostPort = serverPortTf.getText().trim();
			if("".equals(hostIp) ||"".equals(hostPort)){
				JOptionPane.showMessageDialog(this, "Server Host cannot be empty! " );
				return;
			}
			
			if( !"".equals(userName) ){
				if("all".equals(userName)){
					JOptionPane.showMessageDialog(this, "User name cannot be 'all' ! " );
					return;
				}
				if(!"".equals(pwd)){
					//登录中心校验，获取token
					String respMsg = client.validate(userName, pwd, ClientConfig.getInstance().LOGIN_SERVER_HOST , ClientConfig.getInstance().LOGIN_SERVER_PORT);
					if("InvalidToken".equals(respMsg)){
						JOptionPane.showMessageDialog(this, "User invalid. [Invalid token]");
					}else{
						String result = client.login(userName, respMsg, hostIp, hostPort);
						if("success".equals(result)){
							this.setVisible(false);
							client.showChatFrame(userName);
						}else{
							JOptionPane.showMessageDialog(this, result);
						}
					}
				}else{
					JOptionPane.showMessageDialog(this, "Password cannot be empty" );
				}
			}else{
				JOptionPane.showMessageDialog(this, "User name cannot be empty" );
			}
		}
		
		//resetBtn ActionEvent
		if(e.getSource()==registerBtn){
			RegisterFrame registerFrame;
			try {
				registerFrame = new RegisterFrame(this);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(this, "Register Server cannot be connect!" );
				return;
			}
			registerFrame.setVisible(true);
			this.setVisible(false);
		}
	}
	
	

}
