package com.revature.services;

import com.revature.beans.TrmsMessage;

public interface MessageService {
	public void transmitMessage(TrmsMessage message);
	public Object getMessage(String targetUsername, int id);
}
