package com.revature.beans;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TuitionReimbursementRequestWithGrade extends TuitionReimbursementRequest {
	private static final Logger log = LogManager.getLogger(TuitionReimbursementRequestWithGrade.class);
	
	public TuitionReimbursementRequestWithGrade() {
		super();
		setGradingFormat(FinalAssesmentTypes.GRADE);
		log.trace("new "+this.getClass()+" instantiated");	//log flag
	}
}
