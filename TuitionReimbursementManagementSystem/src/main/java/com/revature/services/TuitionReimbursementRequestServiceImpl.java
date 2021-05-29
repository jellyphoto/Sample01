package com.revature.services;

import java.util.List;

import com.revature.beans.Employee;
import com.revature.beans.TuitionReimbursementRequest;
import com.revature.data.TuitionReimbursementRequestDao;
import com.revature.data.TuitionReimbursementRequestDaoCassImpl;
import com.revature.util.TrrDateComparator;

public class TuitionReimbursementRequestServiceImpl implements TuitionReimbursementRequestService {
	private TuitionReimbursementRequestDao trrDao;
	
	public TuitionReimbursementRequestServiceImpl() {
		super();
	}

	@Override
	public List<TuitionReimbursementRequest> getTrrs(String targetUsername) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	@Override
	public List<TuitionReimbursementRequest> getTrrs() {
	
	}
	*/
	
	@Override
	public TuitionReimbursementRequest getTrr(String targetUsername, int id) {
		trrDao = new TuitionReimbursementRequestDaoCassImpl();
		return trrDao.getTrr(targetUsername, id);
	}

	@Override
	public void updateTrr(TuitionReimbursementRequest trr) {
		trrDao = new TuitionReimbursementRequestDaoCassImpl();
		trrDao.updateTrr(trr);
	}

	@Override
	public void updateTrrs(String targetUsername, List<TuitionReimbursementRequest> trrs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean addTrr(TuitionReimbursementRequest trr) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteTrr(String targetUsername, int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateDeptHeadApproval(TuitionReimbursementRequest trr) {
		trrDao = new TuitionReimbursementRequestDaoCassImpl();
		return trrDao.updateDeptHeadApproval(trr);		
	}

	@Override
	public boolean updateDirSupApproval(TuitionReimbursementRequest trr) {
		trrDao = new TuitionReimbursementRequestDaoCassImpl();
		return trrDao.updateDirSupApproval(trr);	
	}

	@Override
	public boolean updateFinalAssesmentReviewed(TuitionReimbursementRequest trr) {
		trrDao = new TuitionReimbursementRequestDaoCassImpl();
		return trrDao.updateFinalAssesmentReviewed(trr);
	}

	@Override
	public boolean updateCostCoverage(TuitionReimbursementRequest trr) {
		trrDao = new TuitionReimbursementRequestDaoCassImpl();
		return trrDao.updateCostCoverage(trr);
	}

	@Override
	public boolean updateExceedsAvailableFunds(TuitionReimbursementRequest trr) {
		trrDao = new TuitionReimbursementRequestDaoCassImpl();
		return trrDao.updateExceedsAvailableFunds(trr);
	}

	@Override
	public boolean updateAvailableFundsExcessJustification(TuitionReimbursementRequest trr) {
		trrDao = new TuitionReimbursementRequestDaoCassImpl();
		return trrDao.updateAvailableFundsExcessJustification(trr);
	}

	/*
	@Override
	public void updateTrrs(Employee currentUser) {
		List<TuitionReimbursementRequest> trrs = getTrrs(currentUser.getUsername());
		trrs.sort(new TrrDateComparator());		//VERIFY
		double availableReimbursement = 
		
	}
	*/
	
	
}
