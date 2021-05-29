package com.revature.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.Driver;
import com.revature.beans.Admin;
import com.revature.utils.CassandraUtil;

public class DriverServiceImpl implements DriverService {
	private static final Logger log = LogManager.getLogger(DriverServiceImpl.class);
	private static UserService userSvc;

	@Override
	public boolean preinitialize() {
		//db check
		if(!CassandraUtil.getInstance().assertKeyspaceExistence()) {	//verify
			log.error("keyspace access initialization may have failed");	//verify
			return false;
		}
		log.trace("keyspace access initialization succeeded");
		//admin db
		//employee db
		//dirsup db
		//benco db
		userSvc = new UserServiceImpl();
		if(!userSvc.dataAccessCheck()) {	//verify
			return false;
		}
		log.trace("user tables access initialization succeeded");
		//trr db
		//s3 db?
		
		userSvc = new AdminServiceImpl();
		log.trace("new Admin svc instantiated", userSvc);	//VERIFY
		
		if(userSvc.isEmpty()) {
			log.trace("No admins found");	//VERIFY
			userSvc = new UserServiceImpl();
			log.trace("new User svc instantiated", userSvc);	//VERIFY
			
			if(userSvc.userExistence("default")) {
				//ERROR
				log.trace("A non-admin user with username \"default\" exists");	//VERIFY
				//there is a non-admin-user named "default"
				return false;
			}	
			
			log.trace("Begin registration of \"default\" Admin account");	//VERIFY
			
			userSvc = new AdminServiceImpl();
			log.trace("new Admin svc instantiated", userSvc);	//VERIFY
			String defaultName = "default";	//What is the best way to deal with a String pool?
			if(!userSvc.addUser(defaultName, defaultName)) {
				//ERROR
				//unsuccess full admin account creation
				log.trace("Addition of \"default\" Admin to DB failed");	//VERIFY
				return false;
			}
			log.trace("Addition of \"default\" Admin to DB succeeded");		//VERIFY
		}
		log.trace("Admin exists");	//VERIFY
		//CassandraUtil.getInstance().getSession().close();	//move
		return true;
	}
	//^tests
	//must return only true or false
	//assert true
	//assert false
}
