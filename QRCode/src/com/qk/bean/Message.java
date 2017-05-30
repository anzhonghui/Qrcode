package com.qk.bean;

public class Message {

	private int id;
	private String meg;
	private int port;
	private int flag;
	
	public Message() {
		super();
	}
	public Message(int port, String meg) {
		super();
		this.meg = meg;
		this.port = port;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMeg() {
		return meg;
	}
	public void setMeg(String meg) {
		this.meg = meg;
	}
	
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	@Override
	public String toString() {
		return "Message [id=" + id + ", meg=" + meg + ", port=" + port + ", flag=" + flag + "]";
	}
}
