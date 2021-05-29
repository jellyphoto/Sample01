package com.revature.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.revature.beans.DenialReason;
import com.revature.beans.DirectSupervisor;
import com.revature.beans.Employee;
import com.revature.beans.InfoRequest;
import com.revature.beans.TrmsMessage;
import com.revature.beans.TuitionReimbursementRequest;
import com.revature.beans.TuitionReimbursementRequest.FinalAssesmentTypes;
import com.revature.beans.User;
import com.revature.beans.User.Role;
import com.revature.services.DirectSupervisorServiceImpl;
import com.revature.services.EmployeeServiceImpl;
import com.revature.services.MessageService;
import com.revature.services.TrmsMessageServiceImpl;
import com.revature.services.TuitionReimbursementRequestService;
import com.revature.services.TuitionReimbursementRequestServiceImpl;
import com.revature.services.UserService;
import com.revature.util.TrrDateComparator;

import io.javalin.http.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;	//VERIFY

public class ManagerController {
	private static UserService userSvc; // = new UserServiceImpl();	//BEANFACTORY?
	private static TuitionReimbursementRequestService trrSvc;	//BEANFACTORY?
	private static MessageService msgSvc;
	
	//service method for user authorization
	private static boolean authorization(Context ctx) {
		User currentUser = ctx.sessionAttribute("User");	//VERIFY
		if(currentUser == null) {	//VERIFY
			//ERROR STATUS
			//NO LOGGED in User
			return false;
		}
		if(currentUser.getUserRole().equals(Role.ADMIN) || currentUser.getUserRole().equals(Role.EMP)) {
			//ERROR STATUS
			//wrong user type
			return false;
		}
		String currentUsername = currentUser.getUsername();
		String targetUsername = ctx.pathParam("username");	//VERIFY
		userSvc = new EmployeeServiceImpl();	//VERIFY		
		Employee targetUser = (Employee) userSvc.getUser(targetUsername);
		userSvc = new DirectSupervisorServiceImpl();
		DirectSupervisor dirSupOfTarget = (DirectSupervisor) userSvc.getUser(targetUser.getDirSup());
		
		if(!targetUser.getBenCo().equals(currentUsername) && !targetUser.getDirSup().equals(currentUsername) && !dirSupOfTarget.getSupervisorUsername().equals(currentUsername)) {	//VERIFY
			//ERROR STATUS
			//employee not assigned to you or your underlings
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
	
	//Approve or Deny a TRR
	public static void approveTrr(Context ctx) {
		User currentUser = ctx.sessionAttribute("User");	//VERIFY
		if(currentUser == null || currentUser.getUserRole().equals(Role.ADMIN) || currentUser.getUserRole().equals(Role.EMP)) {
			//ERROR STATUS
			//either NO LOGGED in User
			//or ADMIN does not have this privelege
			//or EMP does not have this privelege
			//Maybe break this down into 3 validations
			return;
		}
		
		Boolean approval = new Boolean(ctx.formParam("approval"));	//VERIFY
		String targetUsername = ctx.pathParam("username");	//VERIFY
		userSvc = new EmployeeServiceImpl();
		Employee targetUser = (Employee) userSvc.getUser(targetUsername);
		if(targetUser == null) {
			//ERROR STATUS
			//Employee DNE
			return;
		}
		
		Role currentUserRole = currentUser.getUserRole();
		if(currentUserRole.equals(Role.DIRSUP)) {
			if(!currentUser.getUsername().equals(targetUser.getDirSup())) {	//VERIFY
				//ERROR STATUS
				//employee not assigned to you
				return;
			}
			//Some message of successful authorization?
		} else if(currentUserRole.equals(Role.BENCO)) {
			if(!currentUser.getUsername().equals(targetUser.getBenCo())) {	//VERIFY
				//ERROR STATUS
				//employee not assigned to you
				return;
			}
			//Some message of successful authorization?
		} else if(currentUserRole.equals(Role.DEPTHEAD)) {	//VERIFY
			userSvc = new DirectSupervisorServiceImpl();
			DirectSupervisor dirSupOfTarget = (DirectSupervisor) userSvc.getUser(targetUser.getDirSup());	//VERIFY
			if(!currentUser.getUsername().equals(targetUser.getDirSup()) && !currentUser.getUsername().equals(dirSupOfTarget.getSupervisorUsername())) {	//VERIFY
				//ERROR STATUS
				//employee not assigned to you or your underling
				return;
			}
		}
		
		trrSvc = new TuitionReimbursementRequestServiceImpl();	//VERIFY
		TuitionReimbursementRequest trr = trrSvc.getTrr(targetUsername, Integer.parseInt(ctx.pathParam("id"))); 	//VERIFY
		
		if(trr == null) {
			//ERROR STATUS
			//TRR DNE
			return;
		}
		
		if(trr.getConfirmation() != null && !trr.getConfirmation()) {
			//ERROR STATUS
			//Confirmation DENIED
			return;
		}
		
		switch(currentUserRole) {
			case DIRSUP:
				trr.setDirSupApproval(approval);
				break;
			case DEPTHEAD:
				DirectSupervisor curUser = (DirectSupervisor) currentUser;
				if(curUser.getSupervisorUsername().equals(curUser.getUsername()))
				{
					trr.setDirSupApproval(approval);
				} else if(trr.getDirSupApproval() == null) {
					//ERROR STATUS
					//DirSup approval pending
					return;
				}
				trr.setDeptHeadApproval(approval);
				break;
			case BENCO:
				if(trr.getDeptHeadApproval() == null) {
					//ERROR STATUS
					//DeptHead approval pending
					return;
				}
				trr.setBenCoApproval(approval);
				break;
			default:
				//STATUS ERROR
				//CODE ERROR
				;
		}
		trrSvc.updateTrr(trr); 		//VERIFY
		
		//VERIFY BLOCK
		if(!approval) {		//VERIFY
			trr.setConfirmation(false);
			msgSvc = new TrmsMessageServiceImpl();	//VERIFY
			TrmsMessage denialMessage = new DenialReason();	//VERIFY
			denialMessage.setBody(ctx.formParam("body"));
			denialMessage.setSenderUsername(currentUser.getUsername());
			denialMessage.setSenderRole(currentUser.getUserRole());
			Map<String, Role> validRecipients = new HashMap<String, Role>();
			validRecipients.put(targetUsername, Role.EMP);
			denialMessage.setValidRecipients(validRecipients);
			msgSvc.transmitMessage(denialMessage);
		}
		//Some Message of success
		//Response
	}
	
	//Request Info apropo a TRR
	public static void requestInfo(Context ctx) throws JsonMappingException, JsonProcessingException {
		User currentUser = ctx.sessionAttribute("User");	//VERIFY
		if(currentUser == null || currentUser.getUserRole().equals(Role.ADMIN) || currentUser.getUserRole().equals(Role.EMP)) {
			//ERROR STATUS
			//either NO LOGGED in User
			//or ADMIN does not have this privelege
			//or EMP does not have this privelege
			//Maybe break this down into 3 validations
			return;
		}
		
		TrmsMessage request = new InfoRequest();	//VERIFY
		//request.setHead(ctx.formParam("head"));		//VERIFY
		request.setBody(ctx.formParam("body"));		//VERIFY
		request.setSenderUsername(currentUser.getUsername()); 	//VERIFY
		request.setSenderRole(currentUser.getUserRole()); 	//VERIFY
		Map<String, Role> declaredRecipients = (HashMap<String, Role>) new ObjectMapper().readValue(ctx.formParam("declaredRecipients"), HashMap.class);
		//^VERIFY
		//Exceptions thrown in method declaration
		request.setDeclaredRecipients(declaredRecipients);
		Map<String, Role> validRecipients = new HashMap<String, Role>();
		
		String targetUsername = ctx.pathParam("username");	//VERIFY
		userSvc = new EmployeeServiceImpl();
		Employee targetUser = (Employee) userSvc.getUser(targetUsername);	//VERIFY
		
		userSvc = new DirectSupervisorServiceImpl();	//VERIFY
		String dirSupOfTarget = targetUser.getDirSup();		//VERIFY
		String supervisorOfDirSupOfTarget = ((DirectSupervisor) userSvc.getUser(dirSupOfTarget)).getSupervisorUsername();	//VERIFY
		
		boolean recipientValidity = false;
		switch(currentUser.getUserRole()) {		//VERIFY
			case DIRSUP:
				if(!currentUser.getUsername().equals(targetUser.getDirSup())) {
					//ERROR STATUS
					//employee not assigned to you
					return;
				}
				for(String key : declaredRecipients.keySet()) {
					if(declaredRecipients.get(key).equals(Role.EMP) && key.equals(targetUsername)) {		//VERIFY
						validRecipients.put(key, declaredRecipients.get(key));	//VERIFY
					}
				}
				break;
			case DEPTHEAD:
				if(!currentUser.getUsername().equals(dirSupOfTarget) && !currentUser.getUsername().equals(supervisorOfDirSupOfTarget)) {	//VERIFY
					//ERROR STATUS
					//employee not assigned to you
					return;
				}
				for(String key : declaredRecipients.keySet()) {		//VERIFY
					if(declaredRecipients.get(key).equals(Role.EMP) && key.equals(targetUsername)) {	//VERIFY
						recipientValidity = true;
					} else if(declaredRecipients.get(key).equals(Role.DIRSUP) && key.equals(dirSupOfTarget)) {	//VERIFY
						recipientValidity = true;
					}
					if(recipientValidity) {
						validRecipients.put(key, declaredRecipients.get(key));	//VERIFY
					}
					recipientValidity = false;
				}
				break;
			case BENCO:
				if(!currentUser.getUsername().equals(targetUser.getBenCo())) {
					//ERROR STATUS
					//employee not assigned to you
					return;
				}
				//VERIFY BLOCK
				for(String key : declaredRecipients.keySet()) {		//VERIFY
					if(declaredRecipients.get(key).equals(Role.EMP) && key.equals(targetUsername)) {
						recipientValidity = true;
					} else if(declaredRecipients.get(key).equals(Role.DIRSUP) && key.equals(dirSupOfTarget)){
						recipientValidity = true;
					} else if(declaredRecipients.get(key).equals(Role.DEPTHEAD) && key.equals(supervisorOfDirSupOfTarget)) {
						recipientValidity = true;
					}
					if(recipientValidity) {		//VERIFY
						validRecipients.put(key, declaredRecipients.get(key));	//VERIFY
					}
					recipientValidity = false;
				}
				break;
			default:
				//ERROR STATUS
				//no authorization?
				return;
		}
		if(validRecipients.size() == 0) {
			//ERROR STATUS
			//No Valid Recipients
			return;
		}
		request.setValidRecipients(validRecipients);
		//RETURN list of invalid/valid recipients?
		
		msgSvc = new TrmsMessageServiceImpl();	//BEANFACTORY?
		msgSvc.transmitMessage(request);
		//STATUS
		//success message
	}
	
	//Confirm whether or not Employee has passed, and funds can be released
	public static void confirmPassing(Context ctx) {
		User currentUser = ctx.sessionAttribute("User");	//VERIFY
		if(currentUser == null || currentUser.getUserRole().equals(Role.ADMIN) || currentUser.getUserRole().equals(Role.EMP)) {
			//ERROR STATUS
			//either NO LOGGED in User
			//or ADMIN does not have this privelege
			//or EMP does not have this privelege
			//Maybe break this down into 3 validations
			return;
		}
		
		String targetUsername = ctx.pathParam("username");
		int id = Integer.parseInt(ctx.pathParam("id"));
		userSvc = new EmployeeServiceImpl();
		Employee targetUser = (Employee) userSvc.getUser(targetUsername);
		trrSvc = new TuitionReimbursementRequestServiceImpl();
		TuitionReimbursementRequest trr = trrSvc.getTrr(targetUsername, id);
		
		if(!trr.isFinalAssesmentReviewed()) {
			//STATUS ERROR
			//Final Assesment Review is Pending
			return;
		}
		
		//VERIFY BLOCK
		switch(currentUser.getUserRole()) {		//VERIFY
			case DIRSUP:
			case DEPTHEAD:
				if(!currentUser.getUsername().equals(targetUser.getDirSup())) {
					//ERROR STATUS
					//Not authorized, employee not assigned to you
					return;
				}
				if(!trr.getGradingFormat().equals(FinalAssesmentTypes.PRESENTATION)) {
					//ERROR STATUS
					//Not authorized, no presentation
					return;
				}
				break;
			case BENCO:
				if(!currentUser.getUsername().equals(targetUser.getBenCo())) {
					//ERROR STATUS
					//Not authorized, employee not assigned to you
					return;
				}
				if(!trr.getGradingFormat().equals(FinalAssesmentTypes.GRADE)) {
					//ERROR STATUS
					//Not authorized, no grade
					return;
				}
				break;
			default:
				break;
		}
		
		trr.setConfirmation(new Boolean(ctx.formParam("confirmation")));	//VERIFY
		trrSvc.updateTrr(trr);		//VERIFY
		
		/*
		//VERIFY BLOCK
		List<TuitionReimbursementRequest> trrs = trrSvc.getTrrs(targetUsername);	//VERIFY
		trrs.sort(new TrrDateComparator());
		for(TuitionReimbursementRequest request : trrs) {
			if(!request.getConfirmation().equals(false)) {
				double requestedFunds = request.getCostCoverage();
				double available = targetUser.getAvailableReimbursement();
				if(requestedFunds > available) {
					request.setCostCoverage(available);
					targetUser.setAvailableReimbursement(0);
				} else {
					targetUser.setAvailableReimbursement(available - requestedFunds);
				}
			}
		}
		trrSvc.updateTrrs(targetUsername, trrs);	//VERIFY
		*/
		userSvc.updateUser(targetUser);		//VERIFY
		//SUCESS STATUS
		//RETURN something?
	}
	
	//View Additional Material
	public static void viewAdditionalMaterial(Context ctx) {
		if(!authorization(ctx)) {
			//ERROR STATUS
			////employee not assigned to you or your underling
			return;
		}
		if(!trrExistence(ctx)) {
			//ERROR STATUS
			//this trr doesn't exist
			return;
		}
		
		//return Additional material
		//SUCCESS
	}
}
