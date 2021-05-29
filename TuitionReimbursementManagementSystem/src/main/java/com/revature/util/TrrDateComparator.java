package com.revature.util;

import java.util.Comparator;
import java.time.LocalDateTime;

import com.revature.beans.TuitionReimbursementRequest;

public class TrrDateComparator implements Comparator<TuitionReimbursementRequest> {
	
	//no explicit constructor, because functional interface?

	//VERIFY BLOCK
	@Override
	public int compare(TuitionReimbursementRequest arg0, TuitionReimbursementRequest arg1) {
		LocalDateTime date0 = arg0.getSubmissionDate();
		LocalDateTime date1 = arg1.getSubmissionDate();
		return date0.compareTo(date1);
	}

}
