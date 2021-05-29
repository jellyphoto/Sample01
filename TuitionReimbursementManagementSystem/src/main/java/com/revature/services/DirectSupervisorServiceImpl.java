package com.revature.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.BenefitsCoordinator;
import com.revature.beans.DepartmentHead;
import com.revature.beans.DirectSupervisor;
import com.revature.beans.User;
import com.revature.beans.User.Role;
import com.revature.data.AdminDaoCassImpl;
import com.revature.data.BenefitsCoordinatorDaoCassImpl;
import com.revature.data.DirectSupervisorDaoCassImpl;
import com.revature.data.EmployeeDaoCassImpl;
import com.revature.data.UserDao;

public class DirectSupervisorServiceImpl implements UserService {
	private static final Logger log = LogManager.getLogger(DirectSupervisorServiceImpl.class);
	private UserDao userDao;
	
	//constructors-------------------------------------------------------------
	public DirectSupervisorServiceImpl() {
		super();
		userDao = new DirectSupervisorDaoCassImpl();
		log.trace("new "+this.getClass()+" instantiated");	//log flag
	}
	
	
	//create-------------------------------------------------------------------
	
	//LOGGED
	@Override
	public boolean addUser(String username, String password) {
		log.trace("addUser("+username+","+password+") invoked");	//log flag
		log.trace("Adding DirectSupervisor: "+username);	//VERIFY
		DirectSupervisor newUser = new DirectSupervisor();
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
		log.trace("addUser("+username+","+password+","+userRole.toString()+") invoked");	//log flag
		log.trace("userRole of "+username+" is: "+userRole.toString());	//log flag
		switch(userRole) {
		case DIRSUP:
			return addUser(username, password);
		case DEPTHEAD:
			log.trace("Adding DepartmentSupervisor: "+username);	//VERIFY
			DirectSupervisor newUser = new DepartmentHead();
			log.trace("String username = "+username);	//log flag
			newUser.setUsername(username);
			newUser.setPassword(password);
			if(!userDao.add(newUser)) {
				log.error("User Addition failed at User Dao: "+ userDao.getClass().toString());
				return false;
			}
			break;
		default:
			log.error("invalid role");
			//error message
			return false;
		}
		return true;		
	}
	
	
	//read---------------------------------------------------------------------
	
	@Override
	public User getUser(String username) {
		// TODO Auto-generated method stub
		return userDao.getUser(username);
	}
	
	//done enuf
	@Override
	public boolean userExistence(String username) {
		boolean success = true;
		
		userDao = new DirectSupervisorDaoCassImpl();
		success &= userDao.userExistence(username);
		if(success) {
			log.trace(username+" found as a Direct Supervisor");
			return success;
		}

		return success;
	}
	
	//LOGGED
	@Override
	public boolean passwordMatch(String password, String username) {
		log.trace("passwordMatch("+password+","+username+") invoked"); //log flag
		log.trace("username"+" argument is: "+username);	//log flag
		log.trace("password"+" argument is: "+password);	//log flag
		log.trace(username+" is: "+"DIRSUP/DEPTHEAD");	//log flag
		return userDao.passwordMatch(password, username);
	}
	
	
	//update-------------------------------------------------------------------
	
	//LOGGED
	//assign deptHead to dirSup
	public boolean assignDeptHead(String deptHead, String dirSup) {
		log.trace("assignDeptHead("+deptHead+","+dirSup+") invoked");	//log flag
		return userDao.updateText("supervisorusername", deptHead, dirSup);
	}
	
			
	//delete-------------------------------------------------------------------
	
	@Override
	public boolean deleteUser(String username) {
		log.trace("Deleting DirSup: " + username);	//VERIFY
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
	
	
	//uncategorized-------------------------------------------------------------	

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
