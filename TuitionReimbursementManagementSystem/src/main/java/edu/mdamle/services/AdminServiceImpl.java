package edu.mdamle.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.mdamle.beans.Admin;
import edu.mdamle.beans.User;
import edu.mdamle.beans.User.Role;
import edu.mdamle.data.AdminDaoCassImpl;
import edu.mdamle.data.BenefitsCoordinatorDaoCassImpl;
import edu.mdamle.data.DirectSupervisorDaoCassImpl;
import edu.mdamle.data.EmployeeDaoCassImpl;
import edu.mdamle.data.UserDao;

public class AdminServiceImpl implements UserService {
	private static final Logger log = LogManager.getLogger(AdminServiceImpl.class);
	private UserDao userDao;	//may have to focus this into an ABSTRACT UserService class
	
	//constructors-------------------------------------------------------------
	
	public AdminServiceImpl() {
		super();
		userDao = new AdminDaoCassImpl();
		log.trace("Admin Dao instantiated"+userDao);	//VERIFY
		log.trace("Admin svc instantiated");		//VERIFY
		log.trace("new "+this.getClass()+" instantiated");	//log flag
	}

	@Override
	public User getUser(String username) {
		log.trace("Searching for Admin: " + username);	//VERIFY
		//METHOD LOG?
		//Exception log?
		return userDao.getUser(username);
	}
	//logging skipped
	
	@Override
	public boolean userExistence(String username) {
		boolean success = true;
		
		userDao = new AdminDaoCassImpl();
		success &= userDao.userExistence(username);
		if(success) {
			log.trace(username+" found as an Admin");
			return success;
		}		
		return success;
	}

	@Override
	public void updateUser(User targetUser) {
		log.trace("Updating Admin: ", targetUser);	//VERIFY
		//METHOD LOG?
		//Exception log?
	}
	//logging skipped

	//create-------------------------------------------------------------------
	
	//LOGGED
	@Override
	public boolean addUser(String username, String password) {
		log.trace("addUser("+username+","+password+") invoked");	//log flag
		log.trace("Adding Admin: "+username);	//VERIFY
		Admin newUser = new Admin();
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
		log.trace("addUser("+username+","+password+","+"Role "+userRole.toString()+") invoked");	//log flag
		return addUser(username, password);
	}
	
	
	//read---------------------------------------------------------------------
	
	@Override
	public boolean isEmpty() {
		log.trace("Verifying existence of Admins in DB");	//VERIFY
		//METHOD LOG?
		//Exception log?
		if(userDao.size() == 0) {
			log.trace("There exist no Admins");	//VERIFY
			return true;
		}
		log.trace("There exists atleast one Admin");	//VERIFY
		return false;
	}
	
	//LOGGED
	@Override
	public boolean passwordMatch(String password, String username) {
		log.trace("passwordMatch("+password+","+username+") invoked"); //log flag
		log.trace("username"+" argument is: "+username);	//log flag
		log.trace("password"+" argument is: "+password);	//log flag
		log.trace(username+" is: "+"ADMIN");	//log flag
		return userDao.passwordMatch(password, username);
	}
	
	
	//update-------------------------------------------------------------------
	
	
	//delete-------------------------------------------------------------------
	
	@Override
	public boolean deleteUser(String username) {
		log.trace("Deleting Admin: " + username);	//VERIFY
		//METHOD LOG?
		//Exception log?
		return userDao.delete(username);
	}
	//logging skipped

	@Override
	public boolean deleteAll() {
		log.trace("Deleting all Admins");	//VERIFY
		//METHOD LOG?
		//Exception log?
		return false;
	}
	//logging skipped
	
	
	//integrity

	@Override
	public boolean dataAccessCheck() {
		return userDao.dataContainerExistenceAssertion();
	}

	

}
