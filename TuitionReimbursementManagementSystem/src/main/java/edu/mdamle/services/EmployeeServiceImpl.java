package edu.mdamle.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.mdamle.beans.Admin;
import edu.mdamle.beans.Employee;
import edu.mdamle.beans.TuitionReimbursementRequest;
import edu.mdamle.beans.User;
import edu.mdamle.beans.User.Role;
import edu.mdamle.data.AdminDaoCassImpl;
import edu.mdamle.data.BenefitsCoordinatorDaoCassImpl;
import edu.mdamle.data.DirectSupervisorDaoCassImpl;
import edu.mdamle.data.EmployeeDaoCassImpl;
import edu.mdamle.data.UserDao;

public class EmployeeServiceImpl implements UserService {
	private static final Logger log = LogManager.getLogger(EmployeeServiceImpl.class);
	private UserDao userDao = new EmployeeDaoCassImpl();
	
	//constructors-------------------------------------------------------------
	public EmployeeServiceImpl() {
		super();
		log.trace("new "+this.getClass()+" instantiated");	//log flag
	}
	
	
	//create-------------------------------------------------------------------
	
	//LOGGED
	@Override
	public boolean addUser(String username, String password) {
		log.trace("addUser("+username+","+password+") invoked");	//log flag
		log.trace("Adding Employee: "+username);	//VERIFY
		Employee newUser = new Employee();
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
		log.trace(username+" is: "+"EMP");	//log flag
		return userDao.passwordMatch(password, username);
	}
	
	
	//update-------------------------------------------------------------------
	
	//LOGGED enuf
	//assign dirSup to employee
	public boolean assignDirSup(String dirSup, String employee) {
		log.trace("assignDirSup("+dirSup+","+employee+") invoked");	//log flag
		return userDao.updateText("dirsup", dirSup, employee);
	}
	
	//LOGGED enuf
	//assign benCo to employee
	public boolean assignBenCo(String benCo, String employee) {
		log.trace("assignBenCo("+benCo+","+employee+") invoked");	//log flag
		return userDao.updateText("benco", benCo, employee);
	}
	
	
	//delete-------------------------------------------------------------------
	
	@Override
	public boolean deleteUser(String username) {
		log.trace("Deleting Employee: " + username);	//VERIFY
		//METHOD LOG?
		//Exception log?
		return userDao.delete(username);
	}
	//logging skipped
	
	@Override
	public boolean deleteAll() {
		userDao.deleteAll();
		return false;
	}
	
	
	//uncategorized-------------------------------------------------------------
	
	@Override
	public User getUser(String username) {
		return userDao.getUser(username);
	}
	
	@Override
	public boolean userExistence(String username) {
		boolean success = true;
		
		userDao = new EmployeeDaoCassImpl();
		success &= userDao.userExistence(username);
		if(success) {
			log.trace(username+" found as an Employee");
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

	public boolean updateAvailableReimbursement(Employee targetUser) {
		return ((EmployeeDaoCassImpl) userDao).updateAvailableReimbursement(targetUser);
	}

	@Override
	public boolean dataAccessCheck() {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
