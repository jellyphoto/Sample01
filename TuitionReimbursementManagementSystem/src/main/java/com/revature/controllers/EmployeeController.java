//maybe next time, validate whether or not the username matches,
//	before validating the role.

package com.revature.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.Employee;
import com.revature.beans.TuitionReimbursementRequest;
import com.revature.beans.TuitionReimbursementRequest.EventType;
import com.revature.beans.TuitionReimbursementRequest.GradingFormat;
import com.revature.beans.User;
import com.revature.beans.User.Role;
import com.revature.services.AdminServiceImpl;
import com.revature.services.EmployeeServiceImpl;
import com.revature.services.UserService;
import com.revature.services.TuitionReimbursementRequestService;
import com.revature.services.TuitionReimbursementRequestServiceImpl;

import io.javalin.http.Context;

public class EmployeeController {
	private static final Logger log = LogManager.getLogger(AdminController.class);
	private static final UserService userSvc = new EmployeeServiceImpl();
	private static final TuitionReimbursementRequestService trrSvc = new TuitionReimbursementRequestServiceImpl();
	
	//LOGGED enuf
	//'service method'(?)
	private static boolean authorization(Context ctx) {
		//authorization of Employee for HTTPS requests specifically to the URI:
		//	"{URLL}/trrs/:username"
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
		
		//check if ctx.sessionAttribute("username").equals(ctx.pathParam("username"))
		String usernameUri = ctx.pathParam("username");
		if(!username.equalsIgnoreCase(usernameUri)) {
			log.error(username+" has now access to "+usernameUri);
			ctx.status(401);//:Unauthorized
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
		
		if(!userRole.equalsIgnoreCase("EMP")) {
			log.error("unauthorized userRole");	//log flag
			ctx.status(403);//:Forbidden
			//ctx.result("unauthorized userRole");
			log.error("authorization unsuccesful");	//log flag
			return false;
		}
		
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
	
	/*
	private static boolean idVerification(Context ctx) {
		return true;
	}
	*/
	
	//Create a Tuition Reimbursement Request
	public static void createTrr(Context ctx) {
		log.trace("createTrr("+"ctx"+") invoked");	//log flag
		log.trace(new StringBuilder("PUT")
				.append(" request to:\n")
				.append(ctx.fullUrl())
				.append("\nmethod: ")
				.append(" createTrr")
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
		
		log.trace("validating form parameters, and initializing new TRR accordingly");	//log flag
		//check for: username, eventType, gradingFormat, eventStartDate, eventEndDate, location, description,
		//	justification, cost, costCoverage, workHoursMissed (optional), passingGradePercentage (optional)
		TuitionReimbursementRequest trr = new TuitionReimbursementRequest();
		String username;
		EventType eventType;
		GradingFormat gradingFormat;
		LocalDate eventStartDate;	//must be yyyy-mm-dd for LocalDate.parse(str)
		LocalDate eventEndDate;		//must be yyyy-mm-dd for LocalDate.parse(str)
		String location;
		String description;
		String justification;
		double cost;
		double costCoverage;	//must be between 0 & 1
		double workHoursMissed;
		double passingGradePercentage;
		
		//username
		log.trace("Checking for: username");	//log flag
		if(ctx.formParam("username") == null) {
			log.error("no username found in request");	//log flag
			ctx.status(400);//:BadRequest
			ctx.result("no username found in request");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		username = ctx.formParam("username");
		log.trace("username: "+username);	//log flag
		
		
		//eventType
		log.trace("Checking for: eventType");	//log flag
		if(ctx.formParam("eventType") == null) {
			log.error("no eventType found in request");	//log flag
			ctx.status(400);//:BadRequest
			ctx.result("no eventType found in request");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		try {
			eventType = EventType.valueOf(ctx.formParam("eventType")) ;
			log.trace("eventType: "+eventType.toString());	//log flag
		} catch(Exception e) {
			log.error("Method thre exception: "+e);
			for(StackTraceElement ste : e.getStackTrace()) {
				log.warn(ste);
			}
			log.error("error in eventType field");
			ctx.status(400);//:BadRequest
			ctx.result("error in eventType field");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		
		//gradingFormat
		log.trace("Checking for: gradingFormat");	//log flag
		if(ctx.formParam("gradingFormat") == null) {
			log.error("no gradingFormat found in request");	//log flag
			ctx.status(400);//:BadRequest
			ctx.result("no gradingFormat found in request");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		try {
			gradingFormat = GradingFormat.valueOf(ctx.formParam("gradingFormat")) ;
			log.trace("gradingFormat: "+gradingFormat.toString());	//log flag
		} catch(Exception e) {
			log.error("Method thre exception: "+e);
			for(StackTraceElement ste : e.getStackTrace()) {
				log.warn(ste);
			}
			log.error("error in gradingFormat field");
			ctx.status(400);//:BadRequest
			ctx.result("error in gradingFormat field");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		
		//eventStartDate
		log.trace("Checking for: eventStartDate");	//log flag
		if(ctx.formParam("eventStartDate") == null) {
			log.error("no eventStartDate found in request");	//log flag
			ctx.status(400);//:BadRequest
			ctx.result("no eventStartDate found in request");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		try {
			eventStartDate = LocalDate.parse(ctx.formParam("eventStartDate")) ;
			log.trace("eventStartDate: "+eventStartDate.toString());	//log flag
		} catch(Exception e) {
			log.error("Method thre exception: "+e);
			for(StackTraceElement ste : e.getStackTrace()) {
				log.warn(ste);
			}
			log.error("error in eventStartDate field");
			ctx.status(400);//:BadRequest
			ctx.result("error in eventStartDate field");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		
		//eventEndDate
		log.trace("Checking for: eventEndDate");	//log flag
		if(ctx.formParam("eventEndDate") == null) {
			log.error("no eventEndDate found in request");	//log flag
			ctx.status(400);//:BadRequest
			ctx.result("no eventEndDate found in request");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		try {
			eventEndDate = LocalDate.parse(ctx.formParam("eventEndDate")) ;
			log.trace("eventEndDate: "+eventEndDate.toString());	//log flag
		} catch(Exception e) {
			log.error("Method thre exception: "+e);
			for(StackTraceElement ste : e.getStackTrace()) {
				log.warn(ste);
			}
			log.error("error in eventEndDate field");
			ctx.status(400);//:BadRequest
			ctx.result("error in eventEndDate field");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		
		//location
		log.trace("Checking for: location");	//log flag
		if(ctx.formParam("location") == null) {
			log.error("no location found in request");	//log flag
			ctx.status(400);//:BadRequest
			ctx.result("no location found in request");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		location = ctx.formParam("location");
		log.trace("location: "+location);	//log flag
		
		//description
		log.trace("Checking for: description");	//log flag
		if(ctx.formParam("description") == null) {
			log.error("no description found in request");	//log flag
			ctx.status(400);//:BadRequest
			ctx.result("no description found in request");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		description = ctx.formParam("description");
		log.trace("description: "+description);	//log flag
		
		//justification
		log.trace("Checking for: justification");	//log flag
		if(ctx.formParam("justification") == null) {
			log.error("no justification found in request");	//log flag
			ctx.status(400);//:BadRequest
			ctx.result("no justification found in request");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		description = ctx.formParam("justification");
		log.trace("justification: "+justification);	//log flag
		
		//cost
		log.trace("Checking for: cost");	//log flag
		if(ctx.formParam("cost") == null) {
			log.error("no cost found in request");	//log flag
			ctx.status(400);//:BadRequest
			ctx.result("no cost found in request");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		try {
			cost = Double.parseDouble(ctx.formParam("cost")) ;
			log.trace("cost: "+cost);	//log flag
		} catch(Exception e) {
			log.error("Method thre exception: "+e);
			for(StackTraceElement ste : e.getStackTrace()) {
				log.warn(ste);
			}
			log.error("error in cost field");
			ctx.status(400);//:BadRequest
			ctx.result("error in cost field");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		
		//costCoverage
		log.trace("Checking for: costCoverage");	//log flag
		if(ctx.formParam("costCoverage") == null) {
			log.error("no costCoverage found in request");	//log flag
			ctx.status(400);//:BadRequest
			ctx.result("no costCoverage found in request");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		try {
			costCoverage = Double.parseDouble(ctx.formParam("costCoverage")) ;
			log.trace("costCoverage: "+costCoverage);	//log flag
		} catch(Exception e) {
			log.error("Method thre exception: "+e);
			for(StackTraceElement ste : e.getStackTrace()) {
				log.warn(ste);
			}
			log.error("error in costCoverage field");
			ctx.status(400);//:BadRequest
			ctx.result("error in costCoverage field");
			log.trace("--------------------------------------------------------------------------------------\n\n");	//log flag
			return;
		}
		
		//workHoursMissed
		
		
		//passingGradePercentage
		
		
		
		
		
		
		//username
		trr.setUsername(ctx.pathParam("username"));
		
		//initialize trr
		userSvc = new EmployeeServiceImpl();
		Employee currentUser = (Employee) userSvc.getUser(ctx.pathParam("username"));
		//NULL CHECK?
		trr.setId(currentUser.getNextTrrId());
		currentUser.increamentNextTrrId();
		
		//event type
		switch(ctx.formParam("eventType")) {
			case "unicourse":
				trr.setEventType(EventTypes.UNICOURSE);
				break;
			case "seminar":
				trr.setEventType(EventTypes.SEMINAR);
				break;
			case "certprep":
				trr.setEventType(EventTypes.CERTPREP);
				break;
			case "cert":
				trr.setEventType(EventTypes.CERT);
				break;
			case "technicaltraining":
				trr.setEventType(EventTypes.TECHNICALTRAINING);
				break;
			case "other":
				trr.setEventType(EventTypes.OTHER);
				break;
		}
		//UNICOURSE, SEMINAR, CERTPREP, CERT, TECHNICALTRAINING, OTHER
		
		//event start date
		String eventStartDate = ctx.formParam("eventStartDate");	//ERROR CHEK
		trr.setEventStartDate(LocalDateTime.of(	Integer.parseInt(eventStartDate.substring(4, 8)),
												Integer.parseInt(eventStartDate.substring(2,4)),
												Integer.parseInt(eventStartDate.substring(0,2)),
												0,
												0
											));
		
		//event end date
		String eventEndDate = ctx.formParam("eventEndDate");	//ERROR CHEK
		trr.setEventEndDate(LocalDateTime.of(	Integer.parseInt(eventEndDate.substring(4, 8)),
												Integer.parseInt(eventEndDate.substring(2,4)),
												Integer.parseInt(eventEndDate.substring(0,2)),
												0,
												0
											));
		
		//event location
		trr.setLocation(ctx.pathParam("location"));
		
		//event description
		trr.setDescription(ctx.formParam("description"));
		
		//event cost
		trr.setCost(Double.parseDouble(ctx.formParam("cost")));
		
		//event cost coverage
		trr.setCostCoverage(Double.parseDouble(ctx.formParam("costCoverage")));
		double maxCoverage = -1;	//VERIFY BLOCK
		switch(trr.getEventType()) {
		case UNICOURSE:
			maxCoverage = 0.8*trr.getCost();
			break;
		case SEMINAR:
			maxCoverage = 0.6*trr.getCost();
			break;
		case CERTPREP:
			maxCoverage = 0.75*trr.getCost();
			break;
		case CERT:
			maxCoverage = trr.getCost();
			break;
		case TECHNICALTRAINING:
			maxCoverage = 0.9*trr.getCost();
			break;
		case OTHER:
			maxCoverage = 0.3*trr.getCost();
			break;
		}
		if(trr.getCostCoverage() > maxCoverage) {
			trr.setCostCoverage(maxCoverage);
		}
		if(trr.getCostCoverage() > currentUser.getAvailableReimbursement()) {
			trr.setCostCoverage(currentUser.getAvailableReimbursement());
		}
		currentUser.setAvailableReimbursement(currentUser.getAvailableReimbursement() - trr.getCostCoverage());	//VERIFY
		
		//work-related justification
		trr.setJustification(ctx.formParam("justification"));	//VERIFY
		
		//submission date
		trr.setSubmissionDate(LocalDateTime.now()); 	//VERIFY
		
		//update current user (EMP) & add new trr in/to DB
		userSvc.updateUser(currentUser);
		trrSvc = new TuitionReimbursementRequestServiceImpl();		//FACTORY?
		trrSvc.addTrr(trr);
	}
	
	//Upload Approval Email from a Direct Supervisor
	public static void uploadApprovalEmail(Context ctx) {
		if(!authorization(ctx)) {
			//Not authorized
			//incorrect login, or incorrect account type
			return;
		}
		
		//VERIFY trr existence
		
		trrSvc = new TuitionReimbursementRequestServiceImpl();
		
		switch(ctx.formParam("approvalType")) {
		case "dirsup":
			//trr.setEmailedApprovalType(Role.DIRSUP);
			//?
			break;
		case "depthead":
			//trr.setEmailedApprovalType(Role.DEPTHEAD);
			break;
		default:
			//ERROR STATUS
			//not a valid approval type
			return;
		}
		
		//update trr
		//write the document to the database
	}
	
	//Upload Additional Material
	public static void uploadAdditionalMaterial(Context ctx) {
		if(!authorization(ctx)) {
			//Not authorized
			//incorrect login, or incorrect account type
			return;
		}
		
		//VERIFY trr existence
		
		trrSvc = new TuitionReimbursementRequestServiceImpl();
		
		//update trr
		//write the document to the database
	}
	
	//Upload Presentation
	public static void uploadPresentation(Context ctx) {
		if(!authorization(ctx)) {
			//Not authorized
			//incorrect login, or incorrect account type
			return;
		}
		
		//VERIFY trr existence
		
		trrSvc = new TuitionReimbursementRequestServiceImpl();
		
		//update trr
		//write the document to the database
	}
	
	//Upload Presentation
	public static void uploadGrade(Context ctx) {
		if(!authorization(ctx)) {
			//Not authorized
			//incorrect login, or incorrect account type
			return;
		}
		
		//VERIFY trr existence
		
		trrSvc = new TuitionReimbursementRequestServiceImpl();
		TuitionReimbursementRequest trr = trrSvc.getTrr(ctx.pathParam("username"), Integer.parseInt(ctx.pathParam("id"))); 	//VERIFY
		if(!trr.getGradingFormat().equals(FinalAssesmentTypes.GRADE)) {
			//ERROR STATUS
			//wrong TRR type
			return;
		}
		if(ctx.formParam("passingGradePercentage").equals(null)) {	//VERIFY
			trr.setPassingGradePercentage(0.75); //VERIFY
			//default passing grade percentage
		} else {
			trr.setPassingGradePercentage(Double.parseDouble(ctx.formParam("passingGradePercentage")));
		}
		
		//update trr
		//write the grade sheet to the database
	}
		
	//Cancel TRR
	public static void cancelTrr(Context ctx) {
		if(!authorization(ctx)) {
			//Not authorized
			//incorrect login, or incorrect account type
			return;
		}
		
		trrSvc = new TuitionReimbursementRequestServiceImpl();
		//VERIFY trr existence
		int id = Integer.parseInt(ctx.pathParam("id"));
		String username = ctx.pathParam("username");
		TuitionReimbursementRequest trr = trrSvc.getTrr(username, id); 	//VERIFY
		
		//VERIFY
		if(!trr.getReimbursementAdjustedByBenCo()) {
			//ERROR STATUS
			//not allowed to cancel trr unless benco adjusts the amount
			return;
		}
		
		trrSvc.deleteTrr(username, id);		//VERIFY
		//SUCCESS
	}
}
