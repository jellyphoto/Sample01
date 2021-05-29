package edu.mdamle.beans;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DepartmentHead extends DirectSupervisor {
	private static final Logger log = LogManager.getLogger(DepartmentHead.class);
	
	//LOGGED
	public DepartmentHead() {
		super();
		log.trace("constuctor "+this.getClass()+"() invoked");	//log flag
		super.setUserRole(Role.DEPTHEAD);
		log.trace("new "+this.getClass()+" instantiated");	//log flag
		log.trace(this.toString());	//log flag
	}
	
	//VERIFY BLOCK
	@Override
	public void setUsername(String username) {
		super.setUsername(username);
		super.setSupervisorUsername(super.getUsername());
	}
	
	/*
	@Override
	public String toString() {
		return ((User) this).toString(); 	//VERIFY
	}
	*/
}
