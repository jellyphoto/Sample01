package edu.mdamle.services;

import edu.mdamle.beans.User;
import edu.mdamle.beans.User.Role;

public interface UserService {
	//create
	public boolean addUser(String username, String password);
	public boolean addUser(String username, String password, Role userRole);
	
	//read
	public User getUser(String username);
	public boolean userExistence(String username);
	public boolean dataAccessCheck();
	public boolean isEmpty();
	public boolean passwordMatch(String password, String username);
	
	//update
	public void updateUser(User targetUser);
	
	//delete
	public boolean deleteUser(String username);
	public boolean deleteAll();
}
