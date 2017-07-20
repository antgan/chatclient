package com.oocl.chatclient.frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.oocl.chatclient.util.ClientConfig;
import com.oocl.protocol.Action;
import com.oocl.protocol.Protocol;

/**
 * Register frame
 * 
 * @author GANAB,ZERO
 * 
 */
public class RegisterFrame extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	public static final String TITLE_HEAD = "[Register]"; 
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private LoginFrame loginFrame;
	private JLabel nameLb;
	private JTextField nameTf;
	private JLabel pwdLb;
	private JTextField pwdTf;
	private JButton submitBtn;
	private JButton cancelBtn;
	
	public RegisterFrame(LoginFrame loginFrame) throws UnknownHostException, IOException {
		this.socket = new Socket(ClientConfig.getInstance().REGISTER_SERVER_HOST, 
				Integer.parseInt(ClientConfig.getInstance().REGISTER_SERVER_PORT));
		oos = new ObjectOutputStream(socket.getOutputStream());
		ois = new ObjectInputStream(socket.getInputStream());
		this.loginFrame = loginFrame;
		init();
		addEvent();
	}


	private void init() {
		//this
		this.setTitle(TITLE_HEAD);
		this.setSize(300,240);
		this.setResizable(false);
		this.setLocationRelativeTo(null);//绝对布局
	
		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				closeWindow();
			}
		});
		this.setLayout(null);
		
		nameLb = new JLabel("UserName: ");
		nameTf = new JTextField();
		pwdLb=new JLabel("Password:");
		pwdTf=new JTextField();
		submitBtn = new JButton("Register");
		cancelBtn = new JButton("Cancel");
		
		nameLb.setBounds(30, 30, 100, 30);
		nameTf.setBounds(110, 30, 150, 30);
		pwdLb.setBounds(30, 70, 100, 30);
		pwdTf.setBounds(110, 70, 150, 30);
		submitBtn.setBounds(30, 130, 100, 30);
		cancelBtn.setBounds(160, 130, 100, 30);
		
		this.add(nameLb);
		this.add(nameTf);
		this.add(pwdLb);
		this.add(pwdTf);
		this.add(submitBtn);
		this.add(cancelBtn);
	}
	
	private void addEvent() {
		submitBtn.addActionListener(this);
		cancelBtn.addActionListener(this);
	}
	
	public void clearInput(){
		nameTf.setText("");
		pwdTf.setText("");
	}


	public void actionPerformed(ActionEvent e) {
		//resetBtn ActionEvent
		if(e.getSource()==submitBtn){
			String name = nameTf.getText().trim();
			String pwd = pwdTf.getText().trim();
			if("".equals(name)){
				JOptionPane.showMessageDialog(this, "User name cannot be empty! " );
				return;
			}
			if("all".equals(name)){
				JOptionPane.showMessageDialog(this, "User name cannot be 'all' ! " );
				return;
			}
			if("".equals(pwd)){
				JOptionPane.showMessageDialog(this, "Password cannot be empty! " );
				return;
			}
			try {
				//注册
				register(name, pwd);
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		//resetBtn ActionEvent
		if(e.getSource()==cancelBtn){
			closeWindow();
		}
	}
	
	private void register(String name, String pwd) throws IOException, ClassNotFoundException{
		Protocol request = new Protocol(Action.Register, name, pwd);
		oos.writeObject(request.toJson());
		oos.flush();
		
		Protocol response = Protocol.fromJson((String)ois.readObject());
		if("success".equals(response.getMsg())){
			JOptionPane.showMessageDialog(this, "Register success!");
			this.closeWindow();
			return;
		}else{
			JOptionPane.showMessageDialog(this, "Register failure! The user name already exists!");
			return;
		}
	}

	public void closeWindow(){
		try {
			this.oos.close();
			this.ois.close();
			this.socket.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		this.clearInput();
		this.setVisible(false);
		loginFrame.setVisible(true);
	}

}
