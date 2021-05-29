package edu.mdamle.data;

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

import edu.mdamle.beans.Admin;
import edu.mdamle.beans.DirectSupervisor;
import edu.mdamle.beans.Employee;
import edu.mdamle.beans.TuitionReimbursementRequest;
import edu.mdamle.beans.User;
import edu.mdamle.beans.User.Role;
import edu.mdamle.utils.CassandraUtil;

public class EmployeeDaoCassImpl implements UserDao {
	private static final Logger log = LogManager.getLogger(EmployeeDaoCassImpl.class);
	private CqlSession session;
	
	//constructors
	
	public EmployeeDaoCassImpl() {
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
	
	
	//create--------------------------------------------------------------------------------------
	
	//LOGGED
	@Override
	public boolean add(User candidate) {
		log.trace("addUser("+"\"candidate\""+") invoked");	//log flag
		log.trace("candidate:\n"+candidate.toString());	//log flag
		
		String query = "insert into trms_ks.employees"
				+ " (username, password, userrole, nextmessageid, dirsup, benco, nexttrrid, totalreimbursement, availablereimbursement)"
				+ " values (?,?,?,?,?,?,?,?,?);";
		
		SimpleStatement s = new SimpleStatementBuilder(query)
				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM)
				.build();
		log.trace("simple statement with consistency level LOCAL_QUORUM built:\n"+s.toString());	//log flag
		
		Employee emp = (Employee) candidate;
		
		BoundStatement bound = session.prepare(s)
				.bind(emp.getUsername(),
						emp.getPassword(),
						emp.getUserRole().toString(),
						emp.getNextMessageId(),
						emp.getDirSup(),
						emp.getBenCo(),
						emp.getNextTrrId(),
						emp.getTOTALREIMBURSEMENT(),
						emp.getAvailableReimbursement());
		log.trace("insert Employee " + candidate.getUsername() + " query prepared:\n", query);	//log flag
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
		log.trace("addition of "+emp.getUsername()+" should have succeeded");	//log flag
		return true;
	}
	//verify logging
	
	
	//read----------------------------------------------------------------------------------------
		
	@Override
	public List<? extends User> getUsers() {
		//method log?
		
		List<Employee> employees = new ArrayList<Employee>();
		log.trace("Empty list of Employees instantiated:\n", employees);	//VERIFY
		
		String query = "Select username, password, nextMessageId, dirSup, benCo, nextTrrId, availableReimbursement from employees";
		log.trace("Select Employee list query assigned:\n", query);		//VERIFY
		
		ResultSet rs = session.execute(query);
		log.trace("ResultSet executed:\n", rs);		//VERIFY
		
		rs.forEach(data -> {
			Employee target = new Employee();
			target.setUsername(data.getString("username"));
			target.setPassword(data.getString("password"));
			target.setNextMessageId(data.getInt("nextMessageId"));
			target.setDirSup(data.getString("dirSup"));
			target.setBenCo(data.getString("benCo"));
			target.setNextTrrId(data.getInt("nextTrrId"));
			target.setAvailableReimbursement(data.getDouble("availableReimbursement"));
			employees.add(target);
			log.trace("Employee " + target.getUsername() + "added to Employee list:\n", target, employees.get(employees.size()-1));	//VERIFY
		});
		
		return employees;
	}
	//verify logging
	
	@Override
	public Role getRole(String username) {
		Role userRole = null;
		String query = "select userrole from trms_ks.employees where username = ?;";
		BoundStatement bound = session.prepare(query).bind(username);
		log.trace("query to get role of "+username+" initialized");
		//try-catch
		ResultSet rs = session.execute(bound);
		Row data = rs.one();
		if(data == null) {
			log.error("role of "+username+" could not be obtained");
		} else {
			userRole = Role.valueOf(data.getString("userrole").toUpperCase());
			log.trace("User role of "+username+" determined as being "+userRole.toString());
		}
		return userRole;
	}
	
	//LOGGED
	@Override
	public boolean userExistence(String username) {
		log.trace("userExistence("+username+") invoked");	//log flag
		String query = "select username from trms_ks.employees where username = ?;";
		
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
	public boolean passwordMatch(String password, String username) {
		log.trace("passwordMatch("+password+","+username+") invoked");	//log flag
		
		String query = "select password from trms_ks.employees where username = ?;";
		BoundStatement bound = session.prepare(query).bind(username);
		
		log.trace("attempting to executer the query:\n"+bound.toString());
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
	
	
	//update--------------------------------------------------------------------------------------
	
	@Override
	public boolean updateText(String key, String value, String username) {
		log.trace("updateText("+key+","+value+","+username+") invoked");	//log flag
		StringBuilder sb = new StringBuilder("update trms_ks.employees")
				.append(" set ")
				.append(key)
				.append(" = ?")
				.append(" where")
				.append(" username = ?;");
		String query = sb.toString();
		SimpleStatement s = new SimpleStatementBuilder(query)
				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM)
				.build();
		BoundStatement bound = session.prepare(s)
				.bind(value, username);
		
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
	
	
	//delete--------------------------------------------------------------------------------------
	
	

	

	@Override
	public User getUser(String username) {
		Employee target = null;
		
		String query = "Select username, password, nextMessageId, dirSup, benCo, nextTrrId, availableReimbursement from employees where username = ?;";
		log.trace("Select Employee " + username + " query assigned:\n", query);	//VERIFY
		
		BoundStatement bound = session.prepare(query).bind(username);
		log.trace(username + " bound to query:\n", bound);	//VERIFY
		
		ResultSet rs = session.execute(bound);
		log.trace("ResultSet executed:\n", rs);		//VERIFY
		
		Row data = rs.one();	//?
		if(data != null) {
			target = new Employee();
			target.setUsername(data.getString("username"));
			target.setPassword(data.getString("password"));
			//target.setUserRole(Role.valueOf(data.getString("userRole")));
			target.setNextMessageId(data.getInt("nextMessageId"));
			target.setDirSup(data.getString("dirSup"));
			target.setBenCo(data.getString("benCo"));
			target.setNextTrrId(data.getInt("nextTrrId"));
			target.setAvailableReimbursement(data.getDouble("availableReimbursement"));
		}
		log.trace(username + " obtained as:\n", target);	//VERIFY
		return target;
	}
	//verify logging
	
	

	//LOGGED
	@Override
	public boolean delete(String username) {
		String query = "delete from trms_ks.employees where username = ?";
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

	@Override
	public boolean deleteAll() {
		/*
		//VERIFY
		log.trace("Deleting all Employeess");	//VERIFY
		
		List<Employee> employees = (ArrayList<Employee>) getUsers();	//VERIFY
		log.trace("Employee list obtained:\n", employees);	//VERIFY
		
		//VERIFY
		employees.forEach(current -> {
			deleteUser(current.getUsername());
			log.trace("Employee " + current.getUsername() + " should be deleted now");	//VERIFY
		});
		
		employees = (ArrayList<Employee>) getUsers();	//VERIFY
		log.trace("Resulting Employee list:\n", employees);	//VERIFY
		
		if(employees.size() != 0) {
			log.trace("Deletion of all Employees failed");	//VERIFY
			return false;
		}
		log.trace("Deletion of all Employees succeded");	//VERIFY
		*/
		return false;
	}
	//verify logging

	public boolean updateAvailableReimbursement(Employee targetUser) {
		log.trace("Updating avail. reimburse. of Employee " + targetUser.getUsername() + " to $" + targetUser.getAvailableReimbursement());		//VERIFY
		
		String query = "update employees set availableReimbursement = ? where username = ?";
		log.trace("Update Employee " + targetUser.getUsername() + " query assigned:\n", query);	//VERIFY
		
		SimpleStatement s = new SimpleStatementBuilder(query)
				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM).build();
		log.trace("Consistency level of query set to LOCAL_QUORUM");	//VERIFY
		
		BoundStatement bound = session.prepare(s).bind(targetUser.getAvailableReimbursement(), targetUser.getUsername());
		log.trace("Username: " + targetUser.getUsername() + " bound to query assigned:\n", bound);	//VERIFY
		session.execute(bound);
		log.trace("query executed:\n", session);	//VERIFY
		return true;
		//use exception check for return false
	}
	//verify logging
	

	//other---------------------------------------------------------------------------------------
	
	@Override
	public boolean dataContainerExistenceAssertion() {
		StringBuilder sb = new StringBuilder("create table if not exists")
				.append(" trms_ks.employees (")
				.append("username text,")
				.append(" password text,")
				.append(" userrole text,")
				.append(" nextmessageid int,")
				.append(" dirsup text,")
				.append(" benco text,")
				.append("nexttrrid int,")
				.append("totalreimbursement double,")
				.append("availablereimbursement double,")
				.append(" primary key(username));");	//, dirsup, benco, userrole, availablereimbursement, nexttrrid
			
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
