package com.revature.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.DefaultConsistencyLevel;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatementBuilder;
import com.revature.beans.Admin;
import com.revature.beans.BenefitsCoordinator;
import com.revature.beans.DepartmentHead;
import com.revature.beans.DirectSupervisor;
import com.revature.beans.Employee;
import com.revature.beans.User;
import com.revature.beans.User.Role;
import com.revature.utils.CassandraUtil;

public class DirectSupervisorDaoCassImpl implements UserDao {
	private static final Logger log = LogManager.getLogger(DirectSupervisorDaoCassImpl.class);
	private CqlSession session;
	
	//constructor
	
	public DirectSupervisorDaoCassImpl() {
		super();
		log.trace("Obtaining Cassandra session");	//VERIFY
		try {
			session = CassandraUtil.getInstance().getSession();
			log.trace("Cassandra session obtained: \n", session);		//VERIFY
		} catch(Exception e) {
			log.trace("Cassandra session not obtained: ", e);	//VERIFY
		}
		log.trace("new "+this.getClass()+" instantiated");	//log flag
	}
	//verify logging
	
	
	//create---------------------------------------------------------------------------
	
	//LOGGED
	@Override
	public boolean add(User candidate) {
		log.trace("addUser("+"\"candidate\""+") invoked");	//log flag
		log.trace("candidate:\n"+candidate.toString());	//log flag
		
		String query = "insert into trms_ks.dirsups"
				+ " (username, password, userrole, nextmessageid, supervisorusername)"
				+ " values (?,?,?,?,?);";
		
		SimpleStatement s = new SimpleStatementBuilder(query)
				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM)
				.build();
		log.trace("simple statement with consistency level LOCAL_QUORUM built:\n"+s.toString());	//log flag
		
		DirectSupervisor dirsup = (DirectSupervisor) candidate;
		
		BoundStatement bound = session.prepare(s)
				.bind(dirsup.getUsername(),
						dirsup.getPassword(),
						dirsup.getUserRole().toString(),
						dirsup.getNextMessageId(),
						dirsup.getSupervisorUsername());
		log.trace("insert DirSup " + candidate.getUsername() + " query prepared:\n", query);	//VERIFY
		log.trace("attempting to execute the query:\n"+bound.toString());	//log flag
		
		try {
			log.trace("Executing insert query");
			session.execute(bound);
			log.trace("execution should have succeeded");
		} catch(Exception e) {
			log.error("Method threw exception: "+e);
			for(StackTraceElement ste : e.getStackTrace()) {
				log.warn(ste);
			}
			//throw e;
			return false;
		}
		log.trace("addition of "+dirsup.getUsername()+" should have succeeded");	//log flag
		return true;
	}
	//verify logging
	
	
	//read-----------------------------------------------------------------------------
	
	@Override
	public List<? extends User> getUsers() {
		//method log?
		
		List<DirectSupervisor> dirSups = new ArrayList<DirectSupervisor>();
		log.trace("Empty list of DirSups instantiated:\n", dirSups);	//VERIFY
		
		String query = "Select username, password, userRole, nextMessageId, supervisorUsername from dirsups";
		log.trace("Select DirSup list query assigned:\n", query);		//VERIFY
		
		ResultSet rs = session.execute(query);
		log.trace("ResultSet executed:\n", rs);		//VERIFY
		
		rs.forEach(data -> {
			DirectSupervisor target = null;
			if(Role.valueOf(data.getString("userRole")).equals(Role.DIRSUP)) {
				log.trace("Role of "
						+ data.getString("username") +" is "
						+ Role.valueOf(data.getString("userRole")));	//VERIFY
				target = new DirectSupervisor();
				target.setSupervisorUsername(data.getString("supervisorUsername"));
			} else {
				target = new DepartmentHead();
			}
			target.setUsername(data.getString("username"));
			log.trace("Role of " + target.getUsername() + " is set to "
					+ target.getUserRole().name());		//VERIFY
			target.setPassword(data.getString("password"));
			target.setNextMessageId(data.getInt("nextMessageId"));
			//target.setSupervisorUsername(data.getString("supervisorUsername"));
			dirSups.add(target);
			log.trace(target.getUserRole().name() + " " + target.getUsername() + "added to DirSup list:\n", target, dirSups.get(dirSups.size()-1));	//VERIFY
		});
		
		return dirSups;
	}
	//verify logging

	@Override
	public User getUser(String username) {
		DirectSupervisor target = null;
		
		String query = "select username, password, userRole, nextMessageId, supervisorUsername from dirsups where username = ?;";
		log.trace("Select DirSup " + username + " query assigned:\n", query);	//VERIFY
		
		BoundStatement bound = session.prepare(query).bind(username);
		log.trace(username + " bound to query:\n", bound);	//VERIFY
		
		ResultSet rs = session.execute(bound);
		log.trace("ResultSet executed:\n", rs);		//VERIFY
		
		Row data = rs.one();	//?
		if(data != null) {
			if(Role.valueOf(data.getString("userRole")).equals(Role.DIRSUP)) {
				log.trace("Role of "
						+ data.getString("username") +" is "
						+ Role.valueOf(data.getString("userRole")));	//VERIFY
				target = new DirectSupervisor();
				target.setSupervisorUsername(data.getString("supervisorUsername"));
			} else {
				target = new DepartmentHead();
			}
			target.setUsername(data.getString("username"));
			log.trace("Role of " + target.getUsername() + " is set to "
					+ target.getUserRole().name());		//VERIFY
			target.setPassword(data.getString("password"));
			//target.setUserRole(Role.valueOf(data.getString("userRole")));
			target.setNextMessageId(data.getInt("nextMessageId"));
		}
		log.trace(username + " obtained as:\n", target);	//VERIFY
		return target;
	}
	//verify logging
	
	//LOGGED
	@Override
	public boolean userExistence(String username) {
		log.trace("userExistence("+username+") invoked");	//log flag
		String query = "select username from trms_ks.dirsups where username = ?;";
		
		BoundStatement bound;
		try {
			log.trace("Binding given username to query");
			 bound = session.prepare(query).bind(username);
			log.trace("Binding succeeded");
		} catch(Exception e) {
			log.error("Method returned exception"+e);
			for(StackTraceElement s : e.getStackTrace()) {
				log.warn(s);
			}
			throw e;
		}
		log.trace("query with bindings:\n"+bound.toString());	//log flag
		
		ResultSet rs;
		try {
			log.trace("Executing username search query");
			rs = session.execute(bound);
			log.trace("Execution of username search query succeeded");
		} catch(Exception e) {
			log.error("Method returned exception"+e);
			for(StackTraceElement s : e.getStackTrace()) {
				log.warn(s);
			}
			throw e;
		}
		
		Row data = rs.one();
		if(data == null) {
			log.trace(username+" not found");
			return false;
		}
		log.trace("result of the query:\n"+data.toString());	//log flag		
		log.trace("data.getString(\"username\") returns: "+data.getString("username"));	//log flag
		if(!username.equals(data.getString("username"))) {
			log.error("error occured when trying to verify that the entered username matches the one in the DB");	//log flag
			return false;
		}
		log.trace(username+" exists");	//log flag
		return true;
	}

	//LOGGED
	@Override
	public Role getRole(String username) {
		log.trace("getRole("+username+") invoked");	//log flag
		
		Role userRole = null;
		String query = "select userrole from trms_ks.dirsups where username = ?;";
		BoundStatement bound = session.prepare(query).bind(username);
		log.trace("query to get role of "+username+" initialized");	//log flag
		log.trace("query with bindings:\n"+bound.toString());	//log flag
		
		//try-catch
		ResultSet rs = session.execute(bound);
		log.trace("query executed");	//log flag
		Row data = rs.one();
		if(data == null) {
			log.error("role of "+username+" could not be obtained");	//log flag
			log.error("result of query is NULL");	//log flag
		} else {
			log.trace("result of the query:\n"+data.toString());	//log flag
			log.trace("data.getString(\"userrole\") returns: "+data.getString("userrole"));	//log flag
			userRole = Role.valueOf(data.getString("userrole").toUpperCase());
			log.trace("User role of "+username+" determined as being "+userRole.toString());	//log flag
		}
		return userRole;
	}
	
	//LOGGED
	@Override
	public boolean passwordMatch(String password, String username) {
		log.trace("passwordMatch("+password+","+username+") invoked");	//log flag
		
		String query = "select password from trms_ks.dirsups where username = ?;";
		BoundStatement bound = session.prepare(query).bind(username);
		
		log.trace("attempting to executer the query:\n"+bound.toString());	//log flag
		ResultSet rs;
		try {
			rs = session.execute(bound);
			log.error("query execution succeeded");
		} catch(Exception e) {
			log.error("Method execution failed: "+e);
			for(StackTraceElement ste : e.getStackTrace()) {
				log.warn(ste);
			}
			return false;
		}
		
		Row data = rs.one();
		if(data == null) {
			log.error("real password of "+username+" could not be obtained");
			return false;
		}
		log.trace("result of the query:\n", data.toString());	//log flag
		log.trace("data.getString(\"password\") returns: ", data.getString("password"));	//log flag
		return password.equals(data.getString("password"));
	}

	
	//update---------------------------------------------------------------------------
	
	//LOGGED enuf
	@Override
	public boolean updateText(String key, String value, String username) {
		log.trace("updateText("+key+","+value+","+username+") invoked");	//log flag
		StringBuilder sb = new StringBuilder("update trms_ks.dirsups")
				.append(" set ")
				.append(key)	//.toLowerCase()
				.append(" = ?")
				.append(" where")
				.append(" username = ?;");
		String query = sb.toString();
		log.trace("unprocessed query:\n"+query);	//log flag
		
		SimpleStatement s = new SimpleStatementBuilder(query)
				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM)
				.build();
		log.trace("SimpleStatement built:\n"+s.getQuery());	//log flag
		
		BoundStatement bound;
		try {
			bound = session.prepare(s)
					.bind(value, username);
			log.trace("arguments bound to query:\n"+bound.toString());	//log flag
		} catch(Exception e) {
			log.error("Method threw exception: "+e);
			for(StackTraceElement ste : e.getStackTrace()) {
				log.warn(ste);
			}
			log.error("binding failed");	//log flag
			return false;
		}
		
		log.trace("Attempting to execute query:\n"+bound.toString());
		try {
			session.execute(bound);
			log.trace("Query executed");
			return true;
		} catch(Exception e) {
			log.error("Method returned exception"+e);
			for(StackTraceElement ste : e.getStackTrace()) {
				log.warn(ste);
			}
			//throw e;
			log.error("Query execution failed");
			return false;
		}
	}
	
	
	//delete---------------------------------------------------------------------------
	
	@Override
	public boolean delete(String username) {
		String query = "delete from trms_ks.dirsups where username = ?";
		SimpleStatement s = new SimpleStatementBuilder(query)
			.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM)
			.build();
		BoundStatement bound = session.prepare(s)
			.bind(username);
		try {
			log.trace("attempting to execute query to delete user "+username);
			session.execute(bound);
			log.trace("deletion of "+username+"should have succeeded");
			return true;
		} catch(Exception e) {
			log.error("Method threw exception"+e);
			for(StackTraceElement ste : e.getStackTrace()) {
				log.warn(ste);
			}
			return false;
		}
	}

	//LOGGED
	@Override
	public boolean deleteAll() {
		/*
		//VERIFY
		log.trace("Deleting all DirSups");	//VERIFY
		
		List<DirectSupervisor> dirSups = (ArrayList<DirectSupervisor>) getUsers();	//VERIFY
		log.trace("DirSup list obtained:\n", dirSups);	//VERIFY
		
		dirSups.forEach(current -> {
			deleteUser(current.getUsername());
			log.trace("DirSup " + current.getUsername() + " should be deleted now");	//VERIFY
		});
			
		dirSups = (ArrayList<DirectSupervisor>) getUsers();	//VERIFY
		log.trace("Resulting DirSup list:\n", dirSups);	//VERIFY
		
		if(dirSups.size() != 0) {
			log.trace("Deletion of all DirSups failed");	//VERIFY
			return false;
		}
		log.trace("Deletion of all DirSups succeded");	//VERIFY
		*/
		return false;
	}
	//verify logging
	
	
	//other----------------------------------------------------------------------------
	
	@Override
	public boolean dataContainerExistenceAssertion() {
		StringBuilder sb = new StringBuilder("create table if not exists")
				.append(" trms_ks.dirsups (")
				.append("username text,")
				.append(" password text,")
				.append(" userrole text,")
				.append(" nextmessageid int,")
				.append(" supervisorusername text,")
				.append(" primary key(username));");	//, supervisorusername, userrole
			
			try {
				this.session.execute(sb.toString());
			} catch(Exception e) {
				log.error("Method returned exception"+e);
				for(StackTraceElement s : e.getStackTrace()) {
					log.warn(e);
				}
				//throw e;
				return false;
			}
			return true;
	}


	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}
}
