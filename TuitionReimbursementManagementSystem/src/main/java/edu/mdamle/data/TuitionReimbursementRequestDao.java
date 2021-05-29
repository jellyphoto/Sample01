package edu.mdamle.data;

import edu.mdamle.beans.TuitionReimbursementRequest;

public interface TuitionReimbursementRequestDao {

	public TuitionReimbursementRequest getTrr(String targetUsername, int id);

	public void updateTrr(TuitionReimbursementRequest trr);

	public boolean updateDeptHeadApproval(TuitionReimbursementRequest trr);

	public boolean updateDirSupApproval(TuitionReimbursementRequest trr);

	public boolean updateFinalAssesmentReviewed(TuitionReimbursementRequest trr);

	public boolean updateCostCoverage(TuitionReimbursementRequest trr);

	public boolean updateExceedsAvailableFunds(TuitionReimbursementRequest trr);

	public boolean updateAvailableFundsExcessJustification(TuitionReimbursementRequest trr);

}
