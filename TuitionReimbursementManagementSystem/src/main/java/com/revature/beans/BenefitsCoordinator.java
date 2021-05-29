package com.revature.beans;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.User.Role;

public class BenefitsCoordinator extends User {
	private static final Logger log = LogManager.getLogger(BenefitsCoordinator.class);
	
	//LOGGED
	public BenefitsCoordinator() {
		super();
		log.trace("constuctor "+this.getClass()+"() invoked");	//log flag
		super.setUserRole(Role.BENCO);
		log.trace("new "+this.getClass()+" instantiated");	//log flag
		log.trace(this.toString());	//log flag
	}
}
