package com.revature.data;

import java.util.List;

import com.revature.beans.User;
import com.revature.beans.User.Role;

public interface UserDao {
	//create
	public boolean add(User candidate);
	
	//read
	public List<? extends User> getUsers();	//conditional create
	public User getUser(String username);
	public int size();	//returns total number of data points
	public boolean userExistence(String username);
	public Role getRole(String username);
	public boolean passwordMatch(String password, String username);
	
	//update
	
	//update 'key' with new 'value', of 'row'
	public boolean updateText(String key, String value, String username);
	
	
	//delete
	public boolean delete(String username);
	public boolean deleteAll();
	
	//other
	public boolean dataContainerExistenceAssertion();
	
	

}
