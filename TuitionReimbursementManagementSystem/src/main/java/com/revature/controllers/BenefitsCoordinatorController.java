package com.revature.controllers;

import java.util.HashMap;
import java.util.Map;

import com.revature.beans.Employee;
import com.revature.beans.ReimbursementAmountChangeNotification;
import com.revature.beans.TrmsMessage;
import com.revature.beans.TuitionReimbursementRequest;
import com.revature.beans.TuitionReimbursementRequest.FinalAssesmentTypes;
import com.revature.beans.User;
import com.revature.beans.User.Role;
import com.revature.services.BenefitsCoordinatorServiceImpl;
import com.revature.services.EmployeeServiceImpl;
import com.revature.services.MessageService;
import com.revature.services.TrmsMessageServiceImpl;
import com.revature.services.TuitionReimbursementRequestService;
import com.revature.services.TuitionReimbursementRequestServiceImpl;
import com.revature.services.UserService;

import io.javalin.http.Context;

public class BenefitsCoordinatorController {
	private static UserService userSvc;
	private static TuitionReimbursementRequestService trrSvc;
	private static MessageService msgSvc;
	
	//service method for user authorization
	private static boolean authorization(Context ctx) {
		User currentUser = ctx.sessionAttribute("User");	//VERIFY
		if(currentUser == null || !currentUser.getUserRole().equals(Role.BENCO)) {	//VERIFY
			//ERROR STATUS
			//either NO LOGGED in User
			//or not an autorized Role
			return false;
		}
		String targetUsername = ctx.pathParam("username");	//VERIFY
		userSvc = new EmployeeServiceImpl();	//VERIFY
		Employee targetUser = (Employee) userSvc.getUser(targetUsername);
		if(!targetUser.getBenCo().equals(currentUser.getUsername())) {
			//ERROR STATUS
			//employee not assigned to you
			return false;
		}
		return true;
	}
	
	//service method for trr existence verification
	private static boolean trrExistence(Context ctx) {
		int id = Integer.parseInt(ctx.pathParam("id")); //VERIFY
		String targetUsername = ctx.pathParam("username");	//VERIFY
		trrSvc = new TuitionReimbursementRequestServiceImpl();
		if(trrSvc.getTrr(targetUsername, id) == null) {
			//ERROR STATUS
			//this trr doesn't exist
			return false;
		}
		return true;
	}
	
	//View Approval Email from a DirSup
	public static void viewApprovalEmail(Context ctx) {
		if(!authorization(ctx)) {
			//ERROR STATUS
			////employee not assigned to you
			return;
		}
		if(!trrExistence(ctx)) {
			//ERROR STATUS
			//this trr doesn't exist
			return;
		}
		
		trrSvc = new TuitionReimbursementRequestServiceImpl();
		int id = Integer.parseInt(ctx.pathParam("id")); //VERIFY
		String targetUsername = ctx.pathParam("username");	//VERIFY
		TuitionReimbursementRequest trr = trrSvc.getTrr(targetUsername, id);
		
		//Some methods for viewing approval email
		//null checks
		//SUCCESS
	}
	
	//Validate Approval Email
	public static void validateApprovalEmail(Context ctx) {
		if(!authorization(ctx)) {
			//ERROR STATUS
			////employee not assigned to you
			return;
		}
		if(!trrExistence(ctx)) {
			//ERROR STATUS
			//this trr doesn't exist
			return;
		}
		
		trrSvc = new TuitionReimbursementRequestServiceImpl();
		int id = Integer.parseInt(ctx.pathParam("id")); //VERIFY
		String targetUsername = ctx.pathParam("username");	//VERIFY
		TuitionReimbursementRequest trr = trrSvc.getTrr(targetUsername, id);
		
		//VERIFY BLOCK
		switch(trr.getEmailedApprovalType()) {
		case DEPTHEAD:
			trr.setDeptHeadApproval(new Boolean(true));
			trrSvc.updateDeptHeadApproval(trr);
		case DIRSUP:
			trr.setDirSupApproval(new Boolean(true));
			trrSvc.updateDirSupApproval(trr);
			break;
		default:
			//ERROR STATUS
			//no valid approval email present
			break;
		}
		
		//SUCCESS
	}
	
	//View Grade
	public static void viewGrade(Context ctx) {
		if(!authorization(ctx)) {
			//ERROR STATUS
			////employee not assigned to you
			return;
		}
		if(!trrExistence(ctx)) {
			//ERROR STATUS
			//this trr doesn't exist
			return;
		}
		
		trrSvc = new TuitionReimbursementRequestServiceImpl();
		int id = Integer.parseInt(ctx.pathParam("id")); //VERIFY
		String targetUsername = ctx.pathParam("username");	//VERIFY
		TuitionReimbursementRequest trr = trrSvc.getTrr(targetUsername, id);
		
		if(!trr.getGradingFormat().equals(FinalAssesmentTypes.GRADE)) {
			//ERROR STATUS
			//this trr has a different grading format
			return;
		}
		
		trr.setFinalAssesmentReviewed(true);
		
		trrSvc.updateFinalAssesmentReviewed(trr);
		//return the grade
		//null check
		//SUCCESS
	}
	
	//Change Reimbursement Amount
	public static void changeReimbursementAmount(Context ctx) {
		if(!authorization(ctx)) {
			//ERROR STATUS
			////employee not assigned to you
			return;
		}
		if(!trrExistence(ctx)) {
			//ERROR STATUS
			//this trr doesn't exist
			return;
		}
		
		trrSvc = new TuitionReimbursementRequestServiceImpl();
		int id = Integer.parseInt(ctx.pathParam("id")); //VERIFY
		String targetUsername = ctx.pathParam("username");	//VERIFY
		TuitionReimbursementRequest trr = trrSvc.getTrr(targetUsername, id);
		
		double newAmount = Double.parseDouble(ctx.formParam("amount"));	//VERIFY
		double amountDifference = newAmount - trr.getCostCoverage();	//VERIFY
		trr.setCostCoverage(newAmount); 	//VERIFY
		trrSvc.updateCostCoverage(trr);
		
		userSvc = new EmployeeServiceImpl();
		Employee targetUser = (Employee) userSvc.getUser(targetUsername);	//VERIFY
		
		targetUser.setAvailableReimbursement(targetUser.getAvailableReimbursement() - amountDifference);	//VERIFY
		((EmployeeServiceImpl) userSvc).updateAvailableReimbursement(targetUser);
		
		if(targetUser.getAvailableReimbursement() < 0) {
			trr.setExceedsAvailableFunds(true);
			trrSvc.updateExceedsAvailableFunds(trr);
			trr.setAvailableFundsExcessJustification(ctx.formParam("justification"));
			trrSvc.updateAvailableFundsExcessJustification(trr);
		}
		
		//initialize notification
		msgSvc = new TrmsMessageServiceImpl();
		TrmsMessage notification = new ReimbursementAmountChangeNotification();
		
		//set notification body
		notification.setBody("The cost coverage for the Tuition Reimbursement Request #"
								+ id
								+ " has been adjusted by "
								+ amountDifference
								+ ".\n The cost coverage is now "
								+ trr.getCostCoverage()
							);
		
		//set notification sender username
		//userSvc = new BenefitsCoordinatorServiceImpl();
		User currentUser = ctx.sessionAttribute("User");	//VERIFY
		String currentUsername = currentUser.getUsername();
		notification.setSenderUsername(currentUsername);
		
		//set notification sender role
		notification.setSenderRole(Role.BENCO);
		
		//set notification valid recipient
		Map<String, Role> validRecipients = new HashMap<String, Role>();
		validRecipients.put(targetUsername, Role.EMP);	//VERIFY
		notification.setValidRecipients(validRecipients); 	//VERIFY
		
		//send notification
		msgSvc.transmitMessage(notification);
		
		//update targetUser (employee)
		//update trr
		
		//SUCCESS
	}
}
