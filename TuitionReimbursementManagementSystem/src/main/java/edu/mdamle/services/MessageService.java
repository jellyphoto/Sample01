package edu.mdamle.services;

import edu.mdamle.beans.TrmsMessage;

public interface MessageService {
	public void transmitMessage(TrmsMessage message);
	public Object getMessage(String targetUsername, int id);
}
