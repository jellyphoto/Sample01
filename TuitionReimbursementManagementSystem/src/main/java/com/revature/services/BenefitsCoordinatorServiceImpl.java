package com.revature.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.BenefitsCoordinator;
import com.revature.beans.Employee;
import com.revature.beans.User;
import com.revature.beans.User.Role;
import com.revature.data.AdminDaoCassImpl;
import com.revature.data.BenefitsCoordinatorDaoCassImpl;
import com.revature.data.DirectSupervisorDaoCassImpl;
import com.revature.data.EmployeeDaoCassImpl;
import com.revature.data.UserDao;

public class BenefitsCoordinatorServiceImpl implements UserService {
	private static final Logger log = LogManager.getLogger(BenefitsCoordinatorServiceImpl.class);
	private UserDao userDao = new BenefitsCoordinatorDaoCassImpl();
	
	//constructors-------------------------------------------------------------
	public BenefitsCoordinatorServiceImpl() {
		super();
		log.trace("new "+this.getClass()+" instantiated");	//log flag
	}
	
	
	//create-------------------------------------------------------------------
	
	//LOGGED
	@Override
	public boolean addUser(String username, String password) {
		log.trace("addUser("+username+","+password+") invoked");	//log flag
		log.trace("Adding BenefitsCoordinator: "+username);	//VERIFY
		User newUser = new BenefitsCoordinator();
		newUser.setUsername(username);
		newUser.setPassword(password);
		if(!userDao.add(newUser)) {
			log.error("User Addition failed at User Dao: "+ userDao.getClass().toString());
			return false;
		}
		log.trace("addition of User "+username+" should have succeeded");	//log flag
		return true;
	}
	//logging skipped
	
	@Override
	public boolean addUser(String username, String password, Role userRole) {
		return addUser(username, password);
	}
	
	
	//read---------------------------------------------------------------------
	
	//LOGGED
	@Override
	public boolean passwordMatch(String password, String username) {
		log.trace("passwordMatch("+password+","+username+") invoked"); //log flag
		log.trace("username"+" argument is: "+username);	//log flag
		log.trace("password"+" argument is: "+password);	//log flag
		log.trace(username+" is: "+"BENCO");	//log flag
		return userDao.passwordMatch(password, username);
	}
	
	
	//update-------------------------------------------------------------------
		
		
	//delete-------------------------------------------------------------------
	
	@Override
	public boolean deleteUser(String username) {
		log.trace("Deleting BenCo: " + username);	//VERIFY
		//METHOD LOG?
		//Exception log?
		return userDao.delete(username);
	}
	//logging skipped
	
	@Override
	public boolean deleteAll() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public User getUser(String username) {
		// TODO Auto-generated method stub
		return userDao.getUser(username);
	}
	
	@Override
	public boolean userExistence(String username) {
		boolean success = true;
				
		userDao = new BenefitsCoordinatorDaoCassImpl();
		success &= userDao.userExistence(username);
		if(success) {
			log.trace(username+" found as a Benefits Coordinator");
			return success;
		}
		
		return success;
	}

	@Override
	public void updateUser(User targetUser) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean dataAccessCheck() {
		// TODO Auto-generated method stub
		return false;
	}

}
