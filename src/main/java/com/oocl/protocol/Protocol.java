package com.oocl.protocol;

import java.io.Serializable;

public class Protocol implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Action action = null;
	private String from = null;
	private String to = null;
	private String msg = null;
	private long time;

	/**
	 * chat
	 * 
	 * @param action
	 * @param from
	 * @param to
	 * @param msg
	 * @param time
	 */

	public Protocol(Action action, String from, String to, String msg, long time) {
		super();
		this.action = action;
		this.from = from;
		this.to = to;
		this.msg = msg;
		this.time = time;
	}

	/**
	 * shake
	 * 
	 * @param action
	 * @param time
	 */

	public Protocol(Action action, String from, String to, long time) {
		super();
		this.action = action;
		this.from = from;
		this.to = to;
		this.time = time;
	}

	/**
	 * login or logout
	 * 
	 * @param action
	 * @param from
	 * @param time
	 */
	public Protocol(Action action, String from, long time) {
		super();
		this.action = action;
		this.from = from;
		this.time = time;
	}

	/**
	 * getlist
	 * 
	 * @param action
	 * @param from
	 * @param time
	 */
	public Protocol(Action action, String from) {
		super();
		this.action = action;
		this.from = from;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "Protocol [action=" + action + ", from=" + from + ", to=" + to
				+ ", msg=" + msg + ", time=" + time + "]";
	}

}
