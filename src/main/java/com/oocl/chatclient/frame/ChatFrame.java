package com.oocl.chatclient.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import sun.awt.AWTAccessor.WindowAccessor;

import com.oocl.chatclient.Client;

/**
 * Chat frame
 * 
 * @author GANAB
 * 
 */
public class ChatFrame extends JFrame implements ActionListener,
		ListSelectionListener {
	private JTextArea chatTa;
//	private JTextArea friendsTa;
	private JTextField inputTf;
	private JButton sendBtn;
	private JButton shakeBtn;
	private Client client;
	private String username;
	private JButton allBtn;
	private JScrollPane users_JScrollPane;
	private JList<String> jlt_disUsers;
	public String toWho = "all";

	public ChatFrame(Client client, String username) {
		this.client = client;
		this.username = username;
		this.setTitle("hello " + username);

		this.setSize(400, 300);
		this.setResizable(false);
		class MyWindowAdapter extends WindowAdapter{
			public Client client;
			
			public MyWindowAdapter(Client client) {
				this.client = client;
			}
			@Override
			public void windowClosing(WindowEvent e) {
				if(client!=null){
					client.exitChat();
				}
			}
		};
//		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(new MyWindowAdapter(client));
		this.setLocationRelativeTo(null);
		init();
		addEvent();
	}

	private void init() {
		this.setLayout(new BorderLayout());
		JPanel panLeft = new JPanel(new BorderLayout());
		JPanel panRight = new JPanel(new BorderLayout());
		JPanel panLeftBottom = new JPanel(new BorderLayout());
		JPanel panBtnGroup = new JPanel(new BorderLayout());
		chatTa = new JTextArea(10, 20);

		allBtn = new JButton("ALL");

		jlt_disUsers = new JList<String>();
		jlt_disUsers.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		users_JScrollPane = new JScrollPane(jlt_disUsers);

		// friendsTa=new JTextArea(10,10);

		inputTf = new JTextField();
		sendBtn = new JButton("send");
		shakeBtn = new JButton("shake");

		inputTf.setFont(new Font("宋体", Font.BOLD, 18));
		inputTf.setForeground(Color.red);

		chatTa.setFont(new Font("宋体", Font.BOLD, 18));
		chatTa.setForeground(Color.blue);

		panRight.add(allBtn, BorderLayout.NORTH);
		panRight.add(users_JScrollPane, BorderLayout.SOUTH);

		panLeft.add(new JScrollPane(chatTa), BorderLayout.CENTER);
		panLeft.add(panLeftBottom, BorderLayout.SOUTH);
		panRight.setBorder(new LineBorder(Color.red));

		this.add(panLeft, BorderLayout.CENTER);
		this.add(panRight, BorderLayout.EAST);
		panBtnGroup.add(shakeBtn, BorderLayout.NORTH);
		panBtnGroup.add(sendBtn, BorderLayout.SOUTH);
		panLeftBottom.add(inputTf, BorderLayout.CENTER);
		panLeftBottom.add(panBtnGroup, BorderLayout.EAST);

	}
	

	private void addEvent() {
		sendBtn.addActionListener(this);
		shakeBtn.addActionListener(this);
		allBtn.addActionListener(this);
		jlt_disUsers.addListSelectionListener(this);
	}

	public void actionPerformed(ActionEvent e) {

		// allBtn ActionEvent
		if (e.getSource() == allBtn) {
			toWho = "all";
		}
		// sendBtn ActionEvent
		if (e.getSource() == sendBtn) {
			String msg = inputTf.getText().trim();
			inputTf.setText("");
			if ("".equals(msg)) {
				JOptionPane.showMessageDialog(this,
						"Cannot send an empty message!");
			} else {
				client.sendMsg(msg, toWho);
				appendDisMsg(client.getUserName(), toWho, msg,
						new Date().getTime());
			}
		}
		// shakeBtn ActionEvent
		if (e.getSource() == shakeBtn) {
			long time = new Date().getTime();
			client.shake(toWho, time);
			appendShakeMsg(null, ("all".equals(toWho)) ? "所有人" : toWho, time);
		}
	

	}

	/**
	 * 格式化时间
	 * 
	 * @param time
	 * @return
	 */
	private String getTime(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date(time));
	}

	/**
	 * 追加聊天记录
	 * 
	 * @param from
	 * @param to
	 * @param msg
	 * @param time
	 */
	public void appendDisMsg(String from, String to, String msg, long time) {
		if ("all".equals(to)) {
			chatTa.append("[群聊] " + from + " " + getTime(time)
					+ " \n" + "\t" + msg + "\n");
		} else {
			if(from.equals(client.getUserName())){
				chatTa.append("[对"+to+"私聊]" + from + " " + getTime(time)
						+ " \n" + "\t" + msg + "\n");
			}else{
				chatTa.append("[私聊]" + from + " " + getTime(time)
						+ " \n" + "\t" + msg + "\n");
			}
		}
		chatTa.setCaretPosition(chatTa.getText().length());
	}

	/**
	 * 追加震动提醒
	 * 
	 * @param from
	 * @param to
	 * @param time
	 */
	public void appendShakeMsg(String from, String to, long time) {
		if (to != null) {
			chatTa.append("[震动提醒]你震了" + to + "一下! " + getTime(time) + "\n");
		} else {
			chatTa.append("[震动提醒] " + from + "震了你一下! " + getTime(time) + "\n");
		}
		chatTa.setCaretPosition(chatTa.getText().length());
	}

	/**
	 * 震动窗口
	 * 
	 * @param shakeMan
	 * @param time
	 */
	public void shakeWindow(String shakeMan, long time) {
		//
		int x = this.getX();
		int y = this.getY();
		try {
			for (int i = 0; i < 2; i++) {
				this.setLocation(x + 20, y + 20);
				Thread.sleep(50);
				this.setLocation(x, y);
				Thread.sleep(50);
			}
			appendShakeMsg(shakeMan, null, time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			this.setLocation(x, y);
		}
	}

	public void updateUserOnline(Vector<String> userNames) {	
		jlt_disUsers.removeAll();
		if (userNames != null && userNames.size() > 0) {	
			jlt_disUsers.setListData(userNames);
		}
	}
	
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == jlt_disUsers) {
			toWho = jlt_disUsers.getSelectedValue();
			if(toWho==null){
				toWho = "all";
			}
		}
	}
}
