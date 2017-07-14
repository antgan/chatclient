package com.oocl.chatclient.frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.oocl.chatclient.Client;

public class LoginFrame extends JFrame implements ActionListener{
	private JLabel nameLb;
	private JTextField nameTf;
	private JLabel pwdLb;
	private JPasswordField pwdTf;
	private JButton loginBtn;
	private JButton resetBtn;
	//service
	private Client client;
	public LoginFrame(Client client) {
		this.client=client;
		init();
		addEvent();
		this.setVisible(true);
	}


	private void init() {
		//this
		this.setTitle("login");
		this.setSize(400,260);
		this.setResizable(false);
		this.setLocationRelativeTo(null);//绝对布局
		
		WindowListener l=new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		};
		this.addWindowListener(l);
		//		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.setLayout(null);
		
		nameLb=new JLabel("username");
		nameTf=new JTextField();
		pwdLb=new JLabel("password");
		pwdTf=new JPasswordField();
		
		loginBtn=new JButton("login");
		resetBtn=new JButton("reset");
		nameLb.setBounds(20, 20, 100,40);
		nameTf.setBounds(120, 20, 200,40);
		
		pwdLb.setBounds(20, 80, 100,40);
		pwdTf.setBounds(120, 80, 200,40);
		
		loginBtn.setBounds(60, 140, 90, 40);
		resetBtn.setBounds(200, 140, 90, 40);
		
		this.add(nameLb);
		this.add(nameTf);
		this.add(pwdLb);
		this.add(pwdTf);
		this.add(loginBtn);
		this.add(resetBtn);
	}
	
	private void addEvent() {
		loginBtn.addActionListener(this);
		resetBtn.addActionListener(this);
	}
	

	public void actionPerformed(ActionEvent e) {
		//resetBtn ActionEvent
		if(e.getSource()==loginBtn){
			String username=nameTf.getText().trim();
			String pwd=new String(pwdTf.getPassword()).trim();
			if( !"".equals(username) ){
				if("all".equals(username)){
					JOptionPane.showMessageDialog(this, "User name cannot be 'all' ! " );
					return;
				}
				if(!"".equals(pwd)){
					String respMsg=client.login(username, pwd);
					if("true".equals(respMsg)){
						this.setVisible(false);
						client.showChatFrame(username);
					}else{
						JOptionPane.showMessageDialog(this, respMsg );
					}
				}else{
					JOptionPane.showMessageDialog(this, "Password cannot be empty" );
				}
			}else{
				JOptionPane.showMessageDialog(this, "User name cannot be empty" );
			}
		}
		//resetBtn ActionEvent
		if(e.getSource()==resetBtn){
			nameTf.setText("");
			pwdTf.setText("");
		}
	}
}
