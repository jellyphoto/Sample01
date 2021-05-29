package com.revature.controllers;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.Admin;
import com.revature.beans.DepartmentHead;
import com.revature.beans.DirectSupervisor;
import com.revature.beans.Employee;
import com.revature.beans.User;
import com.revature.beans.User.Role;
import com.revature.services.AdminServiceImpl;
import com.revature.services.BenefitsCoordinatorServiceImpl;
import com.revature.services.DirectSupervisorServiceImpl;
import com.revature.services.EmployeeServiceImpl;
import com.revature.services.UserService;
import com.revature.services.UserServiceImpl;

import io.javalin.http.Context;

public class AdminController {
	private static UserService userSvc;
	private static final Logger log = LogManager.getLogger(AdminController.class);
	
	//temp
	private static Map<String, List<String>> tempMap = null;
	
	//LOGGED
	//done enuf
	//'service method'(?)
	private static boolean authorization(Context ctx) {
		log.trace("authorization("+"ctx"+") invoked");	//log flag
		log.trace("session attributes:\n"+ctx.sessionAttributeMap());	//log flag
		
		String userRole = ctx.sessionAttribute("userRole");
		String username = ctx.sessionAttribute("username");
		
		log.trace("currently logged-in user's  userRole: "+userRole);	//log flag
		log.trace("currently logged-in username: "+username);	//log flag
		
		if(username == null) {
			log.error("username is null");	//log flag
			ctx.status(500);//:InternalServerError	//502:BadGateway
			//ctx.result("no logged-in username detected");
			log.error("authorization unsuccesful");	//log flag
			return false;
		}
		
		if(userRole == null) {
			log.error("userRole is null");	//log flag
			ctx.status(500);//:InternalServerError	//502:BadGateway
			//ctx.result("no logged-in userRole detected");
			log.error("authorization unsuccesful");	//log flag
			return false;
		}
		
		if(!userRole.equalsIgnoreCase("ADMIN")) {
			log.error("unauthorized userRole");	//log flag
			ctx.status(403);//:Forbidden
			//ctx.result("unauthorized userRole");
			log.error("authorization unsuccesful");	//log flag
			return false;
		}
		
		userSvc = new AdminServiceImpl();
		log.trace("attempting to verify existence of "+userRole+" "+username);	//log flag
		if(!userSvc.userExistence(username)) {
			log.error("existence of logged-in "+userRole+" "+username+" could not be verified");	//log flag
			ctx.status(500);//:InternalServerError	//502:BadGateway
			//ctx.result("existence of logged-in \"+userRole+\" \"+username+\" could not be verified");
			log.error("authorization unsuccesful");	//log flag
			return false;
		}
		log.trace("existence of logged-in "+userRole+" "+username+" verified");	//log flag
		//ctx.result("existence of logged-in "+userRole+" "+username+" verified");
		return true;
	}
	
	//LOGGED
	//done enuf
	//register a new account
	public static void register(Context ctx) {
		log.trace("register("+"ctx"+") invoked");	//log flag
		log.trace(new StringBuilder("PUT")
				.append(" request to:\n")
				.append(ctx.fullUrl())
				.append("\nmethod: ")
				.append(" register")
				.toString());
		log.trace("session attributes:\n"+ctx.sessionAttributeMap());	//log flag
		log.trace("form parameters:\n"+ctx.formParamMap());	//log flag
		log.trace("path parameters:\n"+ctx.pathParamMap());	//log flag
		log.trace("query parameters:\n"+ctx.queryParamMap());	//log flag
		
		//temporarily disable
		if(!authorization(ctx)) {
			log.error("authorization failed"); 	//log flag
			ctx.status(401);//:Unauthorized
			ctx.result("authorization failed");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		
		log.trace("validating form parameters");	//log flag
		Map<String, List<String>> formParamMap = ctx.formParamMap();
		log.trace("form parameters:"+formParamMap.toString());	//log flag
		log.trace("form parameter map keyset:\n"+formParamMap.keySet().toString());	//log flag
		//form parameter validation
		if(!formParamMap.containsKey("username")) {
			log.error("new user registration failed"); 	//log flag
			log.error("\"username\" not found in form parameters");		//log flag
			ctx.status(400);//:BadRequest
			ctx.result("new user registration failed");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		log.trace("form parameter map contains "+"username"+" key\n");	//log flag
		if(!formParamMap.containsKey("password")) {
			log.error("new user registration failed"); 	//log flag
			log.error("\"password\" not found in form parameters");		//log flag
			ctx.status(400);//:BadRequest
			ctx.result("new user registration failed");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		log.trace("form parameter map contains "+"password"+" key\n");	//log flag
		if(!formParamMap.containsKey("userRole")) {
			log.error("new user registration failed"); 	//log flag
			log.error("\"userRole\" not found in form parameters");		//log flag
			ctx.status(400);//:BadRequest
			ctx.result("new user registration failed");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		log.trace("form parameter map contains "+"userRole"+" key\n");	//log flag
		
		String username = ctx.formParam("username");
		log.trace(username+": read from form param: "+"username");	//log flag
		String password = ctx.formParam("password");
		log.trace(password+": read from form param: "+"password");	//log flag
		String userRole = ctx.formParam("userRole");
		log.trace(userRole+": read from form param: "+"userRole");	//log flag
		userRole = userRole.toUpperCase();
		log.trace("userRole"+" is now: "+userRole);	//log flag
		
		
		log.trace("making sure that user with same username does not exist");	//log flag
		//make sure that user with same username does not exist
		userSvc = new UserServiceImpl();
		if(userSvc.userExistence(username)) {
			log.error("new user registration failed"); 	//log flag
			log.error("user "+username+" already exists");		//log flag
			ctx.status(400);//:BadRequest
			ctx.result("new user registration failed");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}		
		
		log.trace("Initializing appropriate UserService for creating a new "+userRole+" account");	//log flag
		switch(Role.valueOf(userRole)) {
		case ADMIN:
			userSvc = new AdminServiceImpl();
			//candidate = new Admin();
			break;
		case EMP:
			userSvc = new EmployeeServiceImpl();
			//candidate = new Employee();
			break;
		case DIRSUP:
			userSvc = new DirectSupervisorServiceImpl();
			//candidate = new DirectSupervisor();
			break;
		case DEPTHEAD:
			userSvc = new DirectSupervisorServiceImpl();
			//candidate = new DepartmentHead();
			break;
		case BENCO:
			userSvc = new BenefitsCoordinatorServiceImpl();
			break;
		default:
			//ERROR STATUS
			log.error("invalid role entered");
			//new user must have a valid role
			ctx.status(500);//:InternalServerError
			ctx.result("addition of " + userRole + " " + username + " to DB failed");
			return;	//VERIFY
		}
		
		log.trace("attempting to create new "+userRole+" account for "+" "+username+" with password "+password);	//log flag
		if(!userSvc.addUser(username, password, Role.valueOf(userRole))) {
			//error message
			log.error("addition of " + userRole + " " + username + " to DB failed");	//log flag
			ctx.status(500);//:InternalServerError
			ctx.result("addition of " + userRole + " " + username + " to DB failed");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}		
		//^returns boolean for success?
		//success check
		log.trace("addition of " + userRole + " " + username + " to DB succeeded");
		ctx.status(201);//:Created
		ctx.result("addition of " + userRole + " " + username + " to DB succeeded");
		log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
		return;
		//SUCCESS
	}
	
	//done enuf
	//delete/unregister an existing user account
	public static void unregister(Context ctx) {
		log.trace("unregister("+"ctx"+") invoked");	//log flag
		log.trace(new StringBuilder("DELETE")
				.append(" request to:\n")
				.append(ctx.fullUrl())
				.append("\nmethod: ")
				.append(" unregister")
				.toString());
		log.trace("session attributes:\n"+ctx.sessionAttributeMap());	//log flag
		log.trace("form parameters:\n"+ctx.formParamMap());	//log flag
		log.trace("path parameters:\n"+ctx.pathParamMap());	//log flag
		log.trace("query parameters:\n"+ctx.queryParamMap());	//log flag
		
		//temporarily disable
		/*
		if(!authorization(ctx)) {
			log.error("authorization failed"); 	//log flag
			ctx.status(401);//:Unauthorized
			ctx.result("authorization failed");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		*/
		
		log.trace("assigning to "+"username"+", value of "+"path parameter: "+ ctx.pathParam("username"));	//log flag
		String username = ctx.pathParam("username");
		if(username == null) {
			log.error("Error: path param :username read as NULL");	//log flag
			ctx.status(400);//:BadRequest
			ctx.result("Error: path param :username read as NULL");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		log.trace("path param :username is not null");	//log flag
		if(username.equals(ctx.sessionAttribute("username"))) {
			log.error("unregistration failed: you cannot delete yourself");
			ctx.status(400);//:BadRequest
			ctx.result("unregistration failed: you cannot delete yourself");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		log.trace("user to be unregistered is different than the currently logged-in user");	//log flag
		
		log.trace("attempting to unregister account of "+username);	//log flag
		userSvc = new UserServiceImpl();
		if(!userSvc.deleteUser(username)) {
			log.error("unregistration of "+username+" failed");	//log flag
			ctx.status(500);//:InternalServerError
			ctx.result("unregistration of "+username+" failed");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		log.trace("unregistration of "+username+" succeeded");	//log flag
		log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
		ctx.status(200);
		ctx.result("unregistration of "+username+" succeeded");
		return;
	}
	
	//LOGGED
	public static void assignDeptHead(Context ctx) {
		log.trace("assignDeptHead("+"ctx"+") invoked");	//log flag
		log.trace(new StringBuilder("PUT")
				.append(" request to:\n")
				.append(ctx.fullUrl())
				.append("\nmethod: ")
				.append(" assignDeptHead")
				.toString());
		log.trace("session attributes:\n"+ctx.sessionAttributeMap());	//log flag
		log.trace("form parameters:\n"+ctx.formParamMap());	//log flag
		log.trace("path parameters:\n"+ctx.pathParamMap());	//log flag
		log.trace("query parameters:\n"+ctx.queryParamMap());	//log flag
		
		//temporarily disable
		/*
		if(!authorization(ctx)) {
			log.error("authorization failed"); 	//log flag
			ctx.status(401);//:Unauthorized
			ctx.result("authorization failed");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		*/
		
		String deptHead = ctx.formParam("username");	//maybe this formParm should be changed to deptHead?
		String dirSup = ctx.pathParam("username");
		log.trace("Assign DeptHead "+deptHead+" to DirSup "+dirSup);	//log flag
		
		userSvc = new DirectSupervisorServiceImpl();
		//target
		if(!userSvc.userExistence(dirSup)) {
			log.error("No DirSup "+dirSup+" found");	//log flag
			ctx.status(400);//:BadRequest
			ctx.result("No DirSup "+dirSup+" found");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		log.trace("DirSup "+dirSup+" found");	//log flag
		//payload
		if(!userSvc.userExistence(deptHead)) {
			log.error("No DeptHead "+deptHead+" found");	//log flag
			ctx.status(400);//:BadRequest
			ctx.result("No DeptHead "+deptHead+" found");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		log.trace("DeptHead "+deptHead+" found");	//log flag
		
		if(!((DirectSupervisorServiceImpl) userSvc).assignDeptHead(deptHead, dirSup)) {
			log.error("Assignemnt of DeptHead "+deptHead+" to DirSup "+dirSup+" failed");	//log flag
			ctx.status(500);//:InternalServerError
			ctx.result("Assignemnt of DeptHead "+deptHead+" to DirSup "+dirSup+" failed");
			return;
		}
		log.error("Assignemnt of DeptHead "+deptHead+" to DirSup "+dirSup+" succeeded");
		ctx.status(201);//:Created
		ctx.result("Assignemnt of DeptHead "+deptHead+" to DirSup "+dirSup+" succeeded");
		log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
		return;
	}
	
	//LOGGED enuf
	public static void assignDirSup(Context ctx) {
		log.trace("assignDirSup("+"ctx"+") invoked");	//log flag
		log.trace(new StringBuilder("PUT")
				.append(" request to:\n")
				.append(ctx.fullUrl())
				.append("\nmethod: ")
				.append(" assignDirSup")
				.toString());
		log.trace("session attributes:\n"+ctx.sessionAttributeMap());	//log flag
		log.trace("form parameters:\n"+ctx.formParamMap());	//log flag
		log.trace("path parameters:\n"+ctx.pathParamMap());	//log flag
		log.trace("query parameters:\n"+ctx.queryParamMap());	//log flag
		
		//temporarily disable
		/*
		if(!authorization(ctx)) {
			log.error("authorization failed"); 	//log flag
			ctx.status(401);//:Unauthorized
			ctx.result("authorization failed");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		*/
		
		String dirSup = ctx.formParam("username");	//maybe this formParm should be changed to dirSup?
		String employee = ctx.pathParam("username");
		log.trace("Assign DirSup "+dirSup+" to Employee "+employee);	//log flag
		
		//payload
		userSvc = new DirectSupervisorServiceImpl();
		if(!userSvc.userExistence(dirSup)) {
			log.error("No DirSup "+dirSup+" found");	//log flag
			ctx.status(400);//:BadRequest
			ctx.result("No DirSup "+dirSup+" found");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		log.trace("DirSup "+dirSup+" found");	//log flag
		//target
		userSvc = new EmployeeServiceImpl();
		if(!userSvc.userExistence(employee)) {
			log.error("No Employee "+employee+" found");	//log flag
			ctx.status(400);//:BadRequest
			ctx.result("No Employee "+employee+" found");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		log.trace("Employee "+employee+" found");	//log flag
		
		if(!((EmployeeServiceImpl) userSvc).assignDirSup(dirSup, employee)) {
			log.error("Assignemnt of DirSup "+dirSup+" to Employee "+employee+" failed");	//log flag
			ctx.status(500);//:InternalServerError
			ctx.result("Assignemnt of DirSup "+dirSup+" to Employee "+employee+" failed");	//log flag
			return;
		}
		log.error("Assignemnt of DirSup "+dirSup+" to Employee "+employee+" succeeded");
		ctx.status(201);//:Created
		ctx.result("Assignemnt of DirSup "+dirSup+" to Employee "+employee+" succeeded");
		log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
		return;
	}
	
	//LOGGED enuf
	public static void assignBenCo(Context ctx) {
		log.trace("assignBenCo("+"ctx"+") invoked");	//log flag
		log.trace(new StringBuilder("PUT")
				.append(" request to:\n")
				.append(ctx.fullUrl())
				.append("\nmethod: ")
				.append(" assignBenCo")
				.toString());
		log.trace("session attributes:\n"+ctx.sessionAttributeMap());	//log flag
		log.trace("form parameters:\n"+ctx.formParamMap());	//log flag
		log.trace("path parameters:\n"+ctx.pathParamMap());	//log flag
		log.trace("query parameters:\n"+ctx.queryParamMap());	//log flag
		
		//temporarily disable
		/*
		if(!authorization(ctx)) {
			log.error("authorization failed"); 	//log flag
			ctx.status(401);//:Unauthorized
			ctx.result("authorization failed");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		*/
		
		String benCo = ctx.formParam("username");	//maybe this formParm should be changed to benCo?
		String employee = ctx.pathParam("username");
		log.trace("Assign BenCo "+benCo+" to Employee "+employee);	//log flag
		
		//payload
		userSvc = new BenefitsCoordinatorServiceImpl();
		if(!userSvc.userExistence(benCo)) {
			log.error("No BenCo "+benCo+" found");	//log flag
			ctx.status(400);//:BadRequest
			ctx.result("No BenCo "+benCo+" found");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		log.trace("BenCo "+benCo+" found");	//log flag
		//target
		userSvc = new EmployeeServiceImpl();
		if(!userSvc.userExistence(employee)) {
			log.error("No Employee "+employee+" found");	//log flag
			ctx.status(400);//:BadRequest
			ctx.result("No Employee "+employee+" found");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		log.trace("Employee "+employee+" found");	//log flag
		
		if(!((EmployeeServiceImpl) userSvc).assignBenCo(benCo, employee)) {
			log.error("Assignemnt of BenCo "+benCo+" to Employee "+employee+" failed");	//log flag
			ctx.status(500);//:InternalServerError
			ctx.result("Assignemnt of BenCo "+benCo+" to Employee "+employee+" failed");	//log flag
			return;
		}
		log.error("Assignemnt of BenCo "+benCo+" to Employee "+employee+" succeeded");
		ctx.status(201);//:Created
		ctx.result("Assignemnt of BenCo "+benCo+" to Employee "+employee+" succeeded");
		log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
		return;
	}
	
	//delete all users except for admin users
	/*
	public static void resetAllSansAdmins(Context ctx) {
		if(!authorization(ctx)) {
			//ERROR STATUS
			//not logged in
			//or unauthorized account type
			return;
		}
		
		userSvc = new EmployeeServiceImpl();
		userSvc.deleteAll();
		//success check
		
		userSvc = new BenefitsCoordinatorServiceImpl();
		userSvc.deleteAll();
		//success check
		
		userSvc = new DirectSupervisorServiceImpl();
		userSvc.deleteAll();
		//success check
		
		//success check
		
		//SUCCESS
	}
	*/
}
