package com.revature.beans;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReimbursementAmountChangeNotification extends TrmsMessage {
	private static final Logger log = LogManager.getLogger(InfoRequest.class);
	
	public ReimbursementAmountChangeNotification() {
		super();
		body=null;
		senderUsername=null;
		senderRole=null;
		declaredRecipients=null;
		validRecipients=null;
		messageType = MessageTypes.TRRCHANGE;
		id=0;
		log.trace("new "+this.getClass()+" instantiated");	//log flag
	}
}
