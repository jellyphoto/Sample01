package com.revature.beans;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InfoRequest extends TrmsMessage {
	private static final Logger log = LogManager.getLogger(InfoRequest.class);
	
	public InfoRequest() {
		super();	//VERIFY
		//head=null;
		body=null;
		senderUsername=null;
		senderRole=null;
		declaredRecipients=null;
		validRecipients=null;
		messageType = MessageTypes.INFOREQ;
		id=0;
		log.trace("new "+this.getClass()+" instantiated");	//log flag
	}
}