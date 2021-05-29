package com.revature.beans;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Admin extends User {
	private static final Logger log = LogManager.getLogger(Admin.class);
	
	//LOGGED
	public Admin() {
		super();
		log.trace("constuctor "+this.getClass()+"() invoked");	//log flag
		super.setUserRole(Role.ADMIN);
		log.trace("new "+this.getClass()+" instantiated\n");	//log flag
		log.trace(this.toString());	//log flag
	}
}
