package com.revature.beans;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DirectSupervisor extends User {
	private static final Logger log = LogManager.getLogger(DirectSupervisor.class);
	
	private String supervisorUsername;
	
	//LOGGED
	public DirectSupervisor() {
		super();
		log.trace("constuctor "+this.getClass()+"() invoked");	//log flag
		this.setSupervisorUsername("unassigned");
		super.setUserRole(Role.DIRSUP);
		log.trace("new "+this.getClass()+" instantiated");	//log flag
		log.trace(this.toString());	//log flag
	}

	public String getSupervisorUsername() {
		return supervisorUsername;
	}

	public void setSupervisorUsername(String supervisorUsername) {
		this.supervisorUsername = supervisorUsername;
	}
	
	@Override
	public String toString() {
		return super.toString() + "Supervisor: " + getSupervisorUsername() + "\n";
	}
}
