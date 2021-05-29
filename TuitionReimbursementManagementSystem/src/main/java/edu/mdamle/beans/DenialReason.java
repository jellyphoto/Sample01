package edu.mdamle.beans;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DenialReason extends TrmsMessage {
	private static final Logger log = LogManager.getLogger(DenialReason.class);
	
	public DenialReason() {
		super();
		body=null;
		senderUsername=null;
		senderRole=null;
		declaredRecipients=null;
		validRecipients=null;
		messageType = MessageTypes.DENIALRES;
		id=0;
		log.trace("new "+this.getClass()+" instantiated");	//log flag
	}
}
