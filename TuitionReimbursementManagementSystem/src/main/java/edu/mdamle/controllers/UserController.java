package edu.mdamle.controllers;

import io.javalin.http.Context;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.mdamle.beans.DirectSupervisor;
import edu.mdamle.beans.Employee;
import edu.mdamle.beans.TuitionReimbursementRequest;
import edu.mdamle.beans.User;
import edu.mdamle.beans.User.Role;
import edu.mdamle.services.AdminServiceImpl;
import edu.mdamle.services.BenefitsCoordinatorServiceImpl;
import edu.mdamle.services.DirectSupervisorServiceImpl;
import edu.mdamle.services.EmployeeServiceImpl;
import edu.mdamle.services.MessageService;
import edu.mdamle.services.TrmsMessageServiceImpl;
import edu.mdamle.services.TuitionReimbursementRequestService;
import edu.mdamle.services.TuitionReimbursementRequestServiceImpl;
import edu.mdamle.services.UserService;
import edu.mdamle.services.UserServiceImpl;

public class UserController {
	private static final Logger log = LogManager.getLogger(UserController.class);	
	private static UserService userSvc; // = new UserServiceImpl();	//BEANFACTORY?
	private static TuitionReimbursementRequestService trrSvc;	//BEANFACTORY?
	private static MessageService msgSvc;
	
	//temp
	private static String temp = null;
	
	//LOGGED
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
			ctx.result("no logged-in username detected");
			log.error("authorization unsuccesful");	//log flag
			return false;
		}
		
		if(userRole == null) {
			log.error("userRole is null");	//log flag
			ctx.status(500);//:InternalServerError	//502:BadGateway
			ctx.result("no logged-in userRole detected");
			log.error("authorization unsuccesful");	//log flag
			return false;
		}
		
		//This may be enough for general UserController authorization
		
		//maybe later you could implement code for more strict (username,userRole) verification
		//nah, let's implement it now
		//it may have to be removed, or significantly altered/optimized:
		userRole = userRole.toUpperCase();
		switch(userRole) {
		case "ADMIN":
			userSvc = new AdminServiceImpl();
			break;
		case "EMP":
			userSvc = new EmployeeServiceImpl();
			break;
		case "BENCO":
			userSvc = new BenefitsCoordinatorServiceImpl();
			break;
		case "DIRSUP":
		case "DEPTHEAD":
			userSvc = new DirectSupervisorServiceImpl();
			break;
		default:
			log.error("failure to understand logged-in user's userRole: "+userRole);	//log flag
			ctx.status(500);//:InternalServerError	//502:BadGateway
			ctx.result("logged-in userRole incomprehensible");
			log.error("authorization unsuccesful");	//log flag
			return false;
		}
		log.trace("attempting to verify existence of "+userRole+" "+username);	//log flag
		if(!userSvc.userExistence(username)) {
			log.error("existence of logged-in "+userRole+" "+username+" could not be verified");	//log flag
			ctx.status(500);//:InternalServerError	//502:BadGateway
			ctx.result("existence of logged-in \"+userRole+\" \"+username+\" could not be verified");
			log.error("authorization unsuccesful");	//log flag
			return false;
		}
		log.trace("existence of logged-in "+userRole+" "+username+" verified");	//log flag
		ctx.result("existence of logged-in "+userRole+" "+username+" verified");
		return true;
	}
	
	//DELETE
	//service method for user authorization
	private static boolean authorizationSansAdmin(Context ctx) {
		User currentUser = ctx.sessionAttribute("User");	//VERIFY
		if(currentUser == null) {	//VERIFY
			//ERROR STATUS
			//NO LOGGED in User
			return false;
		}
		if(currentUser.getUserRole().equals(Role.ADMIN)) {
			//ERROR STATUS
			//wrong user type
			return false;
		}
		return true;
	}
	
	//service method for message existence verification
	private static boolean messageExistence(Context ctx) {
		int id = Integer.parseInt(ctx.pathParam("id")); //VERIFY
		String targetUsername = ctx.pathParam("username");	//VERIFY
		msgSvc = new TrmsMessageServiceImpl();	//VERIFY
		if(msgSvc.getMessage(targetUsername, id) == null) {
			//ERROR STATUS
			//this message doesn't exist
			return false;
		}
		return true;
	}
	
	//LOGGED
	//User Login
	//Try coding in a response indicating who logged in	& success
	public static void login(Context ctx) {
		//log flag
		log.trace("login("+"ctx"+") invoked");	//log flag
		log.trace(new StringBuilder("POST")
				.append(" request to:\n")
				.append(ctx.contextPath().toString())
				.append("\nmethod: ")
				.append(" login")
				.toString());
		log.trace("session attributes:\n"+ctx.sessionAttributeMap());	//log flag
		log.trace("form parameters:\n"+ctx.formParamMap());	//log flag
		log.trace("path parameters:\n"+ctx.pathParamMap());	//log flag
		log.trace("query parameters:\n"+ctx.queryParamMap());	//log flag
		
		log.trace("Attempting to login");//log flag
		
		if(ctx.sessionAttribute("username") != null) {
			log.error("Cannot log in.\n"+ctx.sessionAttribute("username")+" is already logged in");
			ctx.status(406);	//VERIFY
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		
		//log flag
		if(ctx.sessionAttribute("username") == null) {
			temp = "NULL";
		} else {
			temp = (ctx.sessionAttribute("username"));
		}
		log.trace("sessionAttribute should be null: "+temp);
		log.trace("session attribute map:\n"+ctx.sessionAttributeMap());
		
		String username = ctx.formParam("username").toLowerCase();	//try-catch, VERIFY formParam vs queryParam
		String password = ctx.formParam("password");	//try-catch, VERIFY formParam vs queryParam
		
		//log flag
		log.trace("ctx form param "+"username"+" is ", ctx.formParam("username"));
		log.trace("stored as "+username);
		log.trace("ctx form param "+"password"+" is ", ctx.formParam("password"));
		log.trace("stored as "+password);
		
		userSvc = new UserServiceImpl();
		log.trace("attempting to determine the user role of "+username);//log flag
		Role userRole = ((UserServiceImpl) userSvc).getUserRole(username);
		if(userRole == null) {
			log.error("User "+username+" could not be found, or their role could not be verified");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		log.trace("user role of "+username+" found to be: "+userRole.toString());//log flag
		
		switch(userRole) {
		case ADMIN:
			userSvc = new AdminServiceImpl();
			break;
		case EMP:
			userSvc = new EmployeeServiceImpl();
			break;
		case BENCO:
			userSvc = new BenefitsCoordinatorServiceImpl();
			break;
		case DIRSUP:
		case DEPTHEAD:
			userSvc = new DirectSupervisorServiceImpl();
			break;
		default:
			log.error("unable to verify user role: "+userRole.toString());
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		log.trace("userSvc instantiated as:\n"+userSvc.getClass().toString());//log flag
		
		log.trace("invoking User Service's passwordMatch("+password+","+username+")"); //log flag
		if(!userSvc.passwordMatch(password, username)) {
			//error status
			log.error("incorrect password");	//log flag
			log.error("method returned "+false);	//log flag
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		log.trace("setting session attrib "+"username"+" to: "+username);//log flag
		ctx.sessionAttribute("username", username);
		log.trace("setting session attrib "+"userRole"+" to: "+userRole.toString().toUpperCase());//log flag
		ctx.sessionAttribute("userRole", userRole.toString().toUpperCase());
		log.trace("session attribute map:\n"+ctx.sessionAttributeMap());//log flag
		//ctx.json(target)	//???
		//success STATUS
		//MESSAGES
		log.trace(userRole.toString()+" "+username+" is now logged in");
		ctx.status(200);//:OK, or 201:Created
		ctx.result(userRole.toString()+" "+username+" is now logged in");
		log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
		return;
	}
	
	//LOGGED
	//User Logout
	//Try coding in a response that indicates who logged out & success
	public static void logout(Context ctx) {
		//log flag
		log.trace("logout("+"ctx"+") invoked");	//log flag
		log.trace(new StringBuilder("DELETE")
			.append(" request to:\n")
				.append(ctx.contextPath().toString())
				.append("\nmethod: ")
				.append(" logout")
				.toString());
		log.trace("session attributes:\n"+ctx.sessionAttributeMap());	//log flag
		log.trace("form parameters:\n"+ctx.formParamMap());	//log flag
		log.trace("path parameters:\n"+ctx.pathParamMap());	//log flag
		log.trace("query parameters:\n"+ctx.queryParamMap());	//log flag
		
		log.trace("Attempting to logout");//log flag
		
		if(!authorization(ctx)) {
			log.error("authorization failed"); 	//log flag
			ctx.status(401);
			ctx.result("authorization failed");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		
		log.trace("session attributes:\n"+ctx.sessionAttributeMap());	//log flag
		log.trace("attempting to invalidate javalin session");	//log flag
		ctx.req.getSession().invalidate();
		log.trace("session attributes:\n"+ctx.sessionAttributeMap());	//log flag
		//what if(ctx.sessionAttributeMap() == null)?
		if(!ctx.sessionAttributeMap().isEmpty()) {
			ctx.status(500);//:InternalServerError	//502:BadGateway
			log.trace("logout unsuccessful due to some server error");//log flag
			ctx.result("logout unsuccessful due to some server error");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		ctx.status(200);//:OK
		log.trace("logout successful");//log flag
		ctx.result("logout successful");
		log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
		return;
	}
	
	//Get a list of TRRs
	public static void viewTrrs(Context ctx) {
		User currentUser = ctx.sessionAttribute("User");	//VERIFY
		if(currentUser == null || currentUser.getUserRole().equals(Role.ADMIN)) {
			//ERROR STATUS
			//either NO LOGGED in User
			//or ADMIN does not have this privelege
			//Maybe break this down into 2 validations
			return;
		}
		
		String targetUsername = ctx.pathParam("username");	//VERIFY
		userSvc = new EmployeeServiceImpl();
		Employee targetUser = (Employee) userSvc.getUser(targetUsername);	//VERIFY
		
		//VERIFY BLOCK
		if(currentUser.getUserRole().equals(Role.EMP)){
			if(!currentUser.getUsername().equals(targetUsername)) {
				//ERROR STATUS
				//Unauthorized, emp cannot view another emp's trrs
				return;
			}
			trrSvc = new TuitionReimbursementRequestServiceImpl();
			//RETURN trrs somehow
			//return trrSvc.getTrrs(targetUsername)
			//SUCCESS
			return;
		}
		
		//VERIFY BLOCK
		switch(currentUser.getUserRole()) {
			case DIRSUP:
				if(!currentUser.getUsername().equals(targetUser.getDirSup())) {
					//ERROR STATUS
					//employee not assgned to you
					return;
				}
				break;
			case DEPTHEAD:
				userSvc = new DirectSupervisorServiceImpl();
				DirectSupervisor dirSupOfTarget = (DirectSupervisor) userSvc.getUser(targetUser.getDirSup());	//VERIFY
				if(!currentUser.getUsername().equals(dirSupOfTarget.getUsername()) && !currentUser.getUsername().equals(dirSupOfTarget.getSupervisorUsername())) {	//VERIFY
					//ERROR STATUS
					//employee not assgned to you nor to your underling
					return;
				}
				break;
			case BENCO:
				if(!currentUser.getUsername().equals(targetUser.getBenCo())) {
					//ERROR STATUS
					//employee not assgned to you
					return;
				}
				break;
			default:
				break;
		}
		 
		trrSvc = new TuitionReimbursementRequestServiceImpl();
		//RETURN trrs somehow
		//return trrSvc.getTrrs(targetUsername)
		//SUCCESS
	}
	
	//View a particular Tuition Reimbursement Request
	public static void viewTrr(Context ctx) {
		User currentUser = ctx.sessionAttribute("User");	//VERIFY
		if(currentUser == null || currentUser.getUserRole().equals(Role.ADMIN)) {
			//ERROR STATUS
			//either NO LOGGED in User
			//or ADMIN does not have this privelege
			//Maybe break this down into 2 validations
			return;
		}
		
		String targetUsername = ctx.pathParam("username");	//VERIFY
		int id = Integer.parseInt(ctx.pathParam("id"));
		userSvc = new EmployeeServiceImpl();
		Employee targetUser = (Employee) userSvc.getUser(targetUsername);
		if(targetUser == null) {
			//ERROR STATUS
			//Employee DNE
			return;
		}
		
		if(currentUser.getUserRole().equals(Role.DIRSUP)) {
			if(!currentUser.getUsername().equals(targetUser.getDirSup())) {	//VERIFY
				//ERROR STATUS
				//employee not assigned to you
				return;
			}
			//Some message of successful authorization?
		} else if(currentUser.getUserRole().equals(Role.BENCO)) {
			if(!currentUser.getUsername().equals(targetUser.getBenCo())) {	//VERIFY
				//ERROR STATUS
				//employee not assigned to you
				return;
			}
			//Some message of successful authorization?
		} else if(currentUser.getUserRole().equals(Role.DEPTHEAD)) {
			userSvc = new DirectSupervisorServiceImpl();
			DirectSupervisor dirSupOfTarget = (DirectSupervisor) userSvc.getUser(targetUser.getDirSup());	//VERIFY
			if(!currentUser.getUsername().equals(targetUser.getDirSup()) && !currentUser.getUsername().equals(dirSupOfTarget.getSupervisorUsername())) {	//VERIFY
				//ERROR STATUS
				//employee not assigned to you, nor to your underlings
				return;
			}
		} else if(currentUser.getUsername() != targetUsername) {
			//ERROR STATUS
			//an EMP cannot view another EMP's trr
			return;
		}
		
		trrSvc = new TuitionReimbursementRequestServiceImpl();
		TuitionReimbursementRequest trr = trrSvc.getTrr(targetUsername, id);	//VERIFY
		if(trr == null) {
			//ERROR STATUS
			//TRR DNE
			return;
		}
		
		//RETURN trr in some fashion
	}
	
	//View Messages
	public static void viewMessages(Context ctx) {
		if(!authorizationSansAdmin(ctx)) {
			//ERROR STATUS
			//not logged in
			//or wrong user type
			return;
		}
		
		msgSvc = new TrmsMessageServiceImpl();
		
		//msgSvc.getMessages(currentUsername);
		//return list/map of user's messages
		//null check
		//SUCCESS
	}
	
	//View Message
	public static void viewMessage(Context ctx) {
		if(!messageExistence(ctx)) {
			//ERROR STATUS
			//This message DNE
			return;
		}
		
		msgSvc = new TrmsMessageServiceImpl();
		
		//return msgSvc.getMessage(currentUsername, id);
		//SUCCESS
	}
}
