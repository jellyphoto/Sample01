package edu.mdamle.services;

import java.util.List;

import edu.mdamle.beans.Employee;
import edu.mdamle.beans.TuitionReimbursementRequest;
import edu.mdamle.beans.User;

public interface TuitionReimbursementRequestService {

	public List<TuitionReimbursementRequest> getTrrs(String targetUsername);
	//public List<TuitionReimbursementRequest> getTrrs(); 
	public TuitionReimbursementRequest getTrr(String targetUsername, int id);
	public void updateTrr(TuitionReimbursementRequest trr);
	public void updateTrrs(String targetUsername, List<TuitionReimbursementRequest> trrs);
	//public void updateTrrs(Employee currentUser);
	public boolean addTrr(TuitionReimbursementRequest trr);
	public boolean deleteTrr(String targetUsername, int id);
	public boolean updateDeptHeadApproval(TuitionReimbursementRequest trr);
	public boolean updateDirSupApproval(TuitionReimbursementRequest trr);
	public boolean updateFinalAssesmentReviewed(TuitionReimbursementRequest trr);
	public boolean updateCostCoverage(TuitionReimbursementRequest trr);
	public boolean updateExceedsAvailableFunds(TuitionReimbursementRequest trr);
	public boolean updateAvailableFundsExcessJustification(TuitionReimbursementRequest trr);
}
