package edu.mdamle.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.mdamle.beans.Admin;
import edu.mdamle.beans.BenefitsCoordinator;
import edu.mdamle.beans.DepartmentHead;
import edu.mdamle.beans.DirectSupervisor;
import edu.mdamle.beans.Employee;
import edu.mdamle.beans.User;
import edu.mdamle.beans.User.Role;
import edu.mdamle.data.AdminDaoCassImpl;
import edu.mdamle.data.BenefitsCoordinatorDaoCassImpl;
import edu.mdamle.data.DirectSupervisorDaoCassImpl;
import edu.mdamle.data.EmployeeDaoCassImpl;
import edu.mdamle.data.UserDao;

public class UserServiceImpl implements UserService {
	private static final Logger log = LogManager.getLogger(UserServiceImpl.class);
	private UserDao userDao;
	
	//constructor---------------------------------------------------------------------------------------------
	public UserServiceImpl() {
		super();
		log.trace("User svc instantiated");		//VERIFY
		log.trace("new "+this.getClass()+" instantiated");	//log flag
	}
	
	//create--------------------------------------------------------------------------------------------------
	
	@Override
	public boolean addUser(String username, String password) {
		log.trace("Adding User: "+username);	//VERIFY
		log.warn("this method always returns false through a general implementation of UserService");
		//METHOD LOG?
		//Exception log?
		return false;
	}
	//verify logging
	
	//LOGGED
	@Override
	public boolean addUser(String username, String password, Role userRole) {
		log.trace("addUser("+username+","+password+","+userRole.toString()+") invoked");	//log flag
		
		log.trace("creating new account for " + userRole.toString() + ": "+username);	//VERIFY
		
		User newUser;
		log.trace("instantiating appropriate UserDao and appropriate User");	//log flag
		switch(userRole) {
		case ADMIN:
			newUser = new Admin();
			userDao = new AdminDaoCassImpl();
			break;
		case EMP:
			newUser = new Employee();
			userDao = new EmployeeDaoCassImpl();
		case BENCO:
			newUser = new BenefitsCoordinator();
			userDao = new BenefitsCoordinatorDaoCassImpl();
			break;
		case DIRSUP:
			newUser = new DirectSupervisor();
			userDao = new DirectSupervisorDaoCassImpl();
			break;
		case DEPTHEAD:
			newUser = new DepartmentHead();
			userDao = new DirectSupervisorDaoCassImpl();
			break;
		default:
			log.error("the entered userRole is "+userRole.toString());	//log flag
			log.error("Error in specifying role. User addition failed.");	//log flag
			return false;
		}
		
		newUser.setUsername(username);
		newUser.setPassword(password);
		log.trace("new "+userRole.toString()+" instantiated:\n"+newUser.toString());	//log flag
		
		log.trace("attempting to add new User to DB");	//log flag
		if(!userDao.add(newUser)) {
			log.error("User addition failed at User Dao: "+ userDao.getClass().toString());
			return false;
		}
		log.trace("User addition should have succeeded");	//log flag
		return true;
	}
	
	
	//read--------------------------------------------------------------------------------------------------
	
	@Override
	public User getUser(String username) {
		User target;
		
		userDao = new AdminDaoCassImpl();
		log.trace("Admin dao instantiated", userDao);	//VERIFY
		//exception log?
		target = userDao.getUser(username);
		if(target != null) {
			log.trace(username + " found as Admin: ", target);	//VERIFY
			return target;
		}
		log.trace(username + " not found as Admin:", target);	//VERIFY
		
		userDao = new EmployeeDaoCassImpl();
		log.trace("Employee dao instantiated", userDao);	//VERIFY
		//exception log?
		target = userDao.getUser(username);
		if(target != null) {
			log.trace(username + " found as Employee: ", target);	//VERIFY
			return target;
		}
		log.trace(username + " not found as Employee:", target);	//VERIFY
		
		userDao = new BenefitsCoordinatorDaoCassImpl();
		log.trace("BenCo dao instantiated", userDao);	//VERIFY
		//exception log?
		target = userDao.getUser(username);
		if(target != null) {
			log.trace(username + " found as BenCo: ", target);	//VERIFY
			return target;
		}
		log.trace(username + " not found as BenCo:", target);	//VERIFY
		
		userDao = new DirectSupervisorDaoCassImpl();
		target = userDao.getUser(username);
		log.trace("DirSup dao instantiated", userDao);	//VERIFY
		//exception log?
		if(target != null) {
			log.trace(username + " found as DirSup: ", target);	//VERIFY
			return target;
		}
		log.trace(username + " not found as DirSup:", target);	//VERIFY
		
		//method log?
		return target;
	}
	//verify logging
	
	@Override
	public boolean isEmpty() {
		log.trace("Verifying existence of Users in DB");	//VERIFY
		//METHOD LOG?
		//Exception log?
		return false;
	}
	//verify logging

	@Override
	public boolean dataAccessCheck() {
		boolean success = true;
		
		userDao = new AdminDaoCassImpl();
		success &= userDao.dataContainerExistenceAssertion();
		if(!success) {
			log.error("Admin data access initialization may have failed");	//verify
			return success;
		}
		
		userDao = new EmployeeDaoCassImpl();
		success &= userDao.dataContainerExistenceAssertion();
		if(!success) {
			log.error("Employee data access initialization may have failed");	//verify
			return success;
		}
		
		userDao = new DirectSupervisorDaoCassImpl();
		success &= userDao.dataContainerExistenceAssertion();
		if(!success) {
			log.error("Direct Supervisor data access initialization may have failed");	//verify
			return success;
		}
		
		userDao = new BenefitsCoordinatorDaoCassImpl();
		success &= userDao.dataContainerExistenceAssertion();
		if(!success) {
			log.error("Benefits Coordinator data access initialization may have failed");	//verify
			return success;
		}
		
		return success;
	}
	
	public boolean userExistence(String username) {	
		log.trace("userExistence("+username+") invoked");	//log flag
		
		userDao = new AdminDaoCassImpl();
		if(userDao.userExistence(username)) {
			log.trace(username+" found as an Admin");	//log flag
			return true;
		}
		log.trace(username+" not found as an Admin");	//log flag
		
		userDao = new EmployeeDaoCassImpl();
		if(userDao.userExistence(username)) {
			log.trace(username+" found as an Employee");	//log flag
			return true;
		}
		log.trace(username+" not found as an Employee");	//log flag
		
		userDao = new DirectSupervisorDaoCassImpl();
		if(userDao.userExistence(username)) {
			log.trace(username+" found as an Direct Supervisor");	//log flag
			return true;
		}
		log.trace(username+" not found as an Direct Supervisor");	//log flag
		
		userDao = new BenefitsCoordinatorDaoCassImpl();
		if(userDao.userExistence(username)) {
			log.trace(username+" found as an Benefits Coordinator");	//log flag
			return true;
		}
		log.trace(username+" not found as an Benefits Coordinator");	//log flag
		
		log.trace("User "+username+" not found in DB");	//log flag
		return false;
	}

	//LOGGED
	@Override
	public boolean passwordMatch(String password, String username) {
		log.trace("passwordMatch("+password+","+username+") invoked"); //log flag
		log.trace("username"+" argument is: ", username);	//log flag
		log.trace("password"+" argument is: ", password);	//log flag
		
		if(new AdminDaoCassImpl().passwordMatch(password, username)) {
			log.trace(username+" is: ", "ADMIN");	//log flag
			log.trace("password entered: ", password);	//log flag
			log.trace("password is correct");
			return true;
		}
		if(new EmployeeDaoCassImpl().passwordMatch(password, username)) {
			log.trace(username+" is: ", "EMP");	//log flag
			log.trace("password entered: ", password);	//log flag
			log.trace("password is correct");
			return true;
		}
		if(new BenefitsCoordinatorDaoCassImpl().passwordMatch(password, username)) {
			log.trace(username+" is: ", "BENCO");	//log flag
			log.trace("password entered: ", password);	//log flag
			log.trace("password is correct");
			return true;
		}
		if(new DirectSupervisorDaoCassImpl().passwordMatch(password, username)) {
			log.trace(username+" is: ", "DIRSUP");	//log flag
			log.trace("password entered: ", password);	//log flag
			log.trace("password is correct");
			return true;
		}
		log.trace("password is incorrect or no such user exists");	//log flag
		return false;
	}
	
	//LOGGED
	//child-specific method
	//may have to change the inheritance structure of UserService
	public Role getUserRole(String username) {
		log.trace("getUserRole("+username+") invoked");	//log flag
		
		userDao = new AdminDaoCassImpl();
		if(userDao.userExistence(username)) {
			log.trace(username+" is an ADMIN");	//log flag
			return Role.ADMIN;
		}
		log.trace(username+" does not exist as an "+"ADMIN");	//log flag
		userDao = new EmployeeDaoCassImpl();
		if(userDao.userExistence(username)) {
			log.trace(username+" is an EMPloyee");	//log flag
			return Role.EMP;
		}
		log.trace(username+" does not exist as an "+"EMP");	//log flag
		userDao = new BenefitsCoordinatorDaoCassImpl();
		if(userDao.userExistence(username)) {
			log.trace(username+" is a BENefits COordinator");	//log flag
			return Role.BENCO;
		}
		log.trace(username+" does not exist as an "+"BENCO");	//log flag
		userDao = new DirectSupervisorDaoCassImpl();
		if(userDao.userExistence(username)) {
			Role userRole = userDao.getRole(username);
			log.trace(username+" is a "+userRole.toString());	//log flag
			return userRole;
		}
		log.trace(username+" does not exist as an "+"DIRSUP");	//log flag
		log.error("unable to determine the user role of "+username);	//log flag
		log.error("effectively, User "+username+" doesn't exist in the DB");	//log flag
		return null;
	}
	
	//update-----------------------------------------------------------------------------------------
	
	@Override
	public void updateUser(User targetUser) {
		log.trace("Updating User: ", targetUser);	//VERIFY
		//METHOD LOG?
		//Exception log?
	}
	//verify logging

	
	//delete-----------------------------------------------------------------------------------------
	
	//LOGGED
	@Override
	public boolean deleteUser(String username) {
		log.trace("deleteUser("+username+") invoked");	//log flag
		log.trace("Deleting User: " + username);	//log flag
		//METHOD LOG?
		//Exception log?
		//private get user role
		log.trace("determining userRole of "+username);	//log flag
		switch(getUserRole(username)) {
		case ADMIN:
			userDao = new AdminDaoCassImpl();
			break;
		case EMP:
			userDao = new EmployeeDaoCassImpl();
			break;
		case BENCO:
			userDao = new BenefitsCoordinatorDaoCassImpl();
			break;
		case DIRSUP:
		case DEPTHEAD:
			userDao = new DirectSupervisorDaoCassImpl();
			break;
		default:
			log.error("unable to delete "+username+" because their role could not be determined");
			return false;
		}
		log.trace("deletetion/unregistration of User "+username+" should be succesfull");	//log flag
		return userDao.delete(username);
	}
	//verify logging

	@Override
	public boolean deleteAll() {
		log.trace("Deleting all Users");	//VERIFY
		//METHOD LOG?
		//Exception log?
		return false;
	}
	//verify logging

	
}
