package com.revature.beans;

import java.util.Map;

import com.revature.beans.User.Role;

public abstract class TrmsMessage {
	public static enum MessageTypes {
		INFOREQ, DENIALRES, TRRCHANGE
	}
	//protected String head;
	protected String body;
	protected String senderUsername;
	protected Role senderRole;
	protected Map<String, Role> declaredRecipients;
	protected Map<String, Role> validRecipients;
	protected MessageTypes messageType;
	protected int id;

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getSenderUsername() {
		return senderUsername;
	}

	public void setSenderUsername(String senderUsername) {
		this.senderUsername = senderUsername;
	}

	public Role getSenderRole() {
		return senderRole;
	}

	public void setSenderRole(Role senderRole) {
		this.senderRole = senderRole;
	}

	public Map<String, Role> getDeclaredRecipients() {
		return declaredRecipients;
	}

	public void setDeclaredRecipients(Map<String, Role> declaredRecipients) {
		this.declaredRecipients = declaredRecipients;
	}

	public Map<String, Role> getValidRecipients() {
		return validRecipients;
	}

	public void setValidRecipients(Map<String, Role> validRecipients) {
		this.validRecipients = validRecipients;
	}

	public MessageTypes getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageTypes messageType) {
		this.messageType = messageType;
	}	
	
	
}
