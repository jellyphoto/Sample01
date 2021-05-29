package com.revature.beans;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TuitionReimbursementRequestWithPresentation extends TuitionReimbursementRequest {
	private static final Logger log = LogManager.getLogger(TuitionReimbursementRequestWithPresentation.class);
	
	public TuitionReimbursementRequestWithPresentation() {
		super();
		setGradingFormat(FinalAssesmentTypes.PRESENTATION);
		log.trace("new "+this.getClass()+" instantiated");	//log flag
	}
}
