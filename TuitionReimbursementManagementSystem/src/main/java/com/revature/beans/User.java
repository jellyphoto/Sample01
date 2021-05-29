package com.revature.beans;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

//Comparable<User>?
//Serializable?

public class User {
	private static Logger log = LogManager.getLogger(User.class);
	
	public static enum Role {
		NULL, ADMIN, EMP, BENCO, DIRSUP, DEPTHEAD
	}
	private String username = null;
	private String password = null;
	private Role userRole = null;
	private int nextMessageId = 0;
	
	public User(){
		super();
		log.trace("constuctor "+this.getClass()+"() invoked");	//log flag
		this.setUserRole(Role.NULL);
		this.setUsername(null);
		this.setPassword(null);
		this.setNextMessageId(1);
		log.trace("new "+this.getClass()+" instantiated\n");	//log flag
		log.trace(this.toString());	//log flag
	}

	public String getUsername() {
		if(username == null) {
			return "";
		}
		return username;
	}

	//LOGGED
	public void setUsername(String username) {
		log.trace("setUsername("+username+") invoked");	//log flag
		this.username = username;
		log.trace(this.toString());	//log flag
	}
	
	//LOGGED
	public String getPassword() {
		if(password == null) {
			return "";
		}
		return password;
	}

	public void setPassword(String password) {
		log.trace("setPassword("+password+") invoked");	//log flag
		this.password = password;
		log.trace(this.toString());	//log flag
	}

	public Role getUserRole() {
		return userRole;
	}

	public void setUserRole(Role userRole) {
		this.userRole = userRole;
	}

	public int getNextMessageId() {
		return nextMessageId;
	}

	public void setNextMessageId(int nextMessageId) {
		this.nextMessageId = nextMessageId;
	}
	
	public void incrementNextMessageId() {
		setNextMessageId(getNextMessageId() + 1);
	}
	
	@Override
	public String toString() {
		return getUsername() + "\t"
				+ getUserRole().toString() + "\n";
	}
}
