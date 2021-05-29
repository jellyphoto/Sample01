//fix:
//	ctx.contextPath().toString() in login, logout in UserController
//		see ctx.fullUrl() in register in AdminController
//  try query, bound, rs, data, s -> toString() in DAO layer
//		try -> .getQuery()
//	uncomment authorization validation in controllers
package edu.mdamle;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.mdamle.controllers.AdminController;
import edu.mdamle.controllers.BenefitsCoordinatorController;
import edu.mdamle.controllers.DirectSupervisorController;
import edu.mdamle.controllers.EmployeeController;
import edu.mdamle.controllers.ManagerController;
import edu.mdamle.controllers.UserController;
import edu.mdamle.services.DriverService;
import edu.mdamle.services.DriverServiceImpl;
import edu.mdamle.utils.CassandraUtil;
import io.javalin.Javalin;

public class Driver {
	private static final Logger log = LogManager.getLogger(Driver.class);
	public static Javalin app = null;
	
	public static void main(String[] args) {	
		preinitialize();		
		javalin();
	}
	
	public static boolean preinitialize() {
		DriverService driverSvc = new DriverServiceImpl();
		return driverSvc.preinitialize();
	}
	
	public static void javalin() {
		if(app == null) {
			app = Javalin.create().start(8080);
		}

		//Admin Controller:
		app.put("/accounts", AdminController :: register);	//WORKS	//LOGGED
		app.delete("/accounts/:username", AdminController :: unregister);	//WORKS	//LOGGED
		app.put("/dirsups/:username/supervisor", AdminController :: assignDeptHead);	//WORKS	//LOGGED
		app.put("/employees/:username/dirsup", AdminController :: assignDirSup);	//WORKS //LOGGED
		app.put("/employees/:username/benco", AdminController :: assignBenCo);	//WORKS //LOGGED
		
		//User controller:
		app.post("/accounts", UserController :: login);	//WORKS	//LOGGED
		app.delete("/accounts", UserController :: logout);	//WORKS	//LOGGED
		app.get("/trrs/:username", UserController :: viewTrrs);	//incompl
		app.get("/trrs/:username/:id", UserController :: viewTrr);	//incompl
		app.get("/inbox/", UserController :: viewMessages);	//incompl
		app.get("/inbox/:id", UserController :: viewMessage);	//incompl
		
		//Manager controller:
		app.put("/trrs/:username/:id/approve", ManagerController :: approveTrr);	//incompl
		app.put("/trrs/:username/:id/requestinfo", ManagerController :: requestInfo);	//incompl
		app.put("/trrs/:username/:id/confirm", ManagerController :: confirmPassing);	//incompl
		app.get("/trrs/:username/:id/additionalmaterial", ManagerController :: viewAdditionalMaterial);	//incompl
		
		//Employee Controller:
		app.put("/trrs/:username", EmployeeController :: createTrr);	//--------------------------***************
		app.put("/trrs/:username/:id/approvalemail", EmployeeController :: uploadApprovalEmail);	//incompl
		app.put("/trrs/:username/:id/additionalmaterial", EmployeeController :: uploadAdditionalMaterial);	//incompl
		app.put("/trrs/:username/:id/presentation", EmployeeController :: uploadPresentation);		//incompl
		app.put("/trrs/:username/:id/grade", EmployeeController :: uploadGrade);		//incompl
		app.delete("/trrs/:username/:id", EmployeeController :: cancelTrr);	//incoml
		
		//Benefits Coordinator Controller:
		app.get("/trrs/:username/:id/approvalemail", BenefitsCoordinatorController :: viewApprovalEmail);	//incompl
		app.put("/trrs/:username/:id/validateemail", BenefitsCoordinatorController :: validateApprovalEmail);	//incompl
		app.get("/trrs/:username/:id/grade", BenefitsCoordinatorController :: viewGrade);	//incompl
		app.put("/trrs/:username/:id", BenefitsCoordinatorController :: changeReimbursementAmount);	//incompl
		
		//Direct Supervisor Controller:
		app.get("/trrs/:username/:id/presentation", DirectSupervisorController :: viewPresentation);	//incompl
	}
}
