package com.revature.controllers;

import com.revature.beans.Employee;
import com.revature.beans.TuitionReimbursementRequest;
import com.revature.beans.User;
import com.revature.beans.TuitionReimbursementRequest.FinalAssesmentTypes;
import com.revature.beans.User.Role;
import com.revature.services.EmployeeServiceImpl;
import com.revature.services.TuitionReimbursementRequestService;
import com.revature.services.TuitionReimbursementRequestServiceImpl;
import com.revature.services.UserService;

import io.javalin.http.Context;

public class DirectSupervisorController {
	private static UserService userSvc;
	private static TuitionReimbursementRequestService trrSvc;
	
	//service method for user authorization
	private static boolean authorization(Context ctx) {
		User currentUser = ctx.sessionAttribute("User");	//VERIFY
		if(currentUser == null) {	//VERIFY
			//ERROR STATUS
			//NO LOGGED in User
			return false;
		}
		if(!currentUser.getUserRole().equals(Role.DIRSUP) && !currentUser.getUserRole().equals(Role.DEPTHEAD)) {
			//ERROR STATUS
			//wrong user type
			return false;
		}
		String targetUsername = ctx.pathParam("username");	//VERIFY
		userSvc = new EmployeeServiceImpl();	//VERIFY
		Employee targetUser = (Employee) userSvc.getUser(targetUsername);
		if(!targetUser.getDirSup().equals(currentUser.getUsername())) {
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
	
	//View Presentation
	public static void viewPresentation(Context ctx) {
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
		
		if(!trr.getGradingFormat().equals(FinalAssesmentTypes.PRESENTATION)) {
			//ERROR STATUS
			//this trr has a different grading format
			return;
		}
		
		trr.setFinalAssesmentReviewed(true);
		
		//update trr
		//return the grade
		//null check
		//SUCCESS
	}
}
