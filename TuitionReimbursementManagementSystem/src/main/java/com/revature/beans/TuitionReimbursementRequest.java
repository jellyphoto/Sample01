package com.revature.beans;

import java.time.LocalDate;
import java.time.Period;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TuitionReimbursementRequest {
	private static Logger log = LogManager.getLogger(TuitionReimbursementRequest.class);
	
	public enum GradingFormat {
		NULL, GRADE, PRESENTATION
	}
	public enum EventType {
		NULL(0), UNICOURSE(0.8), SEMINAR(0.6), CERT_PREP(0.75), CERT(1), TECHNICAL_TRAINING(0.9), OTHER(0.3);
		public final double maxCoverage;
		private EventType(double maxCoverage) {
			this.maxCoverage = maxCoverage;
		}
	}
	public enum Status {
		NULL(0), DRAFT(0), CANCELLED(0), FAILED(0), PASSED(0),
		DIRSUP_APPROVED(1), DEPTHEAD_APPROVED(2), BENCO_APPROVED(3);
		public final int level;
		private Status(int level) {
			this.level = level;
		}
	}
	
	private String username;	//default is (unassigned)
	private int id;	//default is -1
	
	private EventType eventType;	//default is NULL
	private GradingFormat gradingFormat;	//default is NULL, but reset by child constructor as GRADE or PRESENTATION
	
	private LocalDate eventStartDate;	//default is 2100-01-01
	private LocalDate eventEndDate;		//default is 2100-01-02
	private Status status;	//default is DRAFT
	private boolean isUrgent;	//default is computed from present date & eventStartDate
	
	private String location;	//default is (unassigned)
	private String description;		//default is (unassigned)
	private String justification;	//default is (unassigned)
	
	private double cost;	//default is 0
	private double costCoverage;	//default is 0
	
	//readonly &/or system
	private final int URGENCY_WINDOW;	//set to 7 days, x >= 0
	private final double PASSING_GRADE_DEFAULT;	//set to 0.75, 0 <= x <= 1
	private LocalDate submissionDate;	//default is present date
	private boolean isReimbursementAdjustedByBenCo;	//default is false
	private boolean isFinalAssesmentReviewed;	//default is false
	
	//optional or separately set
	private double workHoursMissed;	//default is 0
	private double passingGradePercentage;	//default is PASSING_GRADE_DEFAULT
	private boolean exceedsAvailableFunds;	//default is false
	private String availableFundsExcessJustification;	//default is (unassigned)
	
	//VERIFY BLOCK
	public TuitionReimbursementRequest() {
		super();
		log.trace("constuctor "+this.getClass()+"() invoked");	//log flag
		this.PASSING_GRADE_DEFAULT = 0.75;
		this.URGENCY_WINDOW = 7;
		this.setUsername("(unassigned)");
		this.setId(-1);
		this.setEventType(EventType.NULL);
		this.setGradingFormat(GradingFormat.NULL);
		this.setEventStartDate(LocalDate.of(2100,1,1));
		this.setEventEndDate(LocalDate.of(2100,1,2));
		this.setStatus(Status.DRAFT);
		this.setUrgent();
		this.setLocation("(unassigned)");
		this.setDescription("(unassigned)");
		this.setJustification("(unassigned)");
		this.setCost(0);
		this.setCostCoverage(0);
		this.setSubmissionDate();
		this.setReimbursementAdjustedByBenCo(false);
		this.setFinalAssesmentReviewed(false);
		this.setWorkHoursMissed(0);
		this.setPassingGradePercentage(PASSING_GRADE_DEFAULT);
		this.setExceedsAvailableFunds(false);
		this.setAvailableFundsExcessJustification("(unassigned)");
		log.trace("new "+this.getClass()+" instantiated\n");	//log flag
		log.trace(this.toString());	//log flag
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public GradingFormat getGradingFormat() {
		return gradingFormat;
	}

	public void setGradingFormat(GradingFormat gradingFormat) {
		this.gradingFormat = gradingFormat;
	}

	public LocalDate getEventStartDate() {
		return eventStartDate;
	}

	public void setEventStartDate(LocalDate eventStartDate) {
		this.eventStartDate = eventStartDate;
	}

	public LocalDate getEventEndDate() {
		return eventEndDate;
	}

	public void setEventEndDate(LocalDate eventEndDate) {
		this.eventEndDate = eventEndDate;
	}

	public double getPASSING_GRADE_DEFAULT() {
		return PASSING_GRADE_DEFAULT;
	}

	public int getURGENCY_WINDOW() {
		return URGENCY_WINDOW;
	}

	public boolean isUrgent() {
		return isUrgent;
	}

	public void setUrgent() {
		LocalDate start = getEventStartDate();
		Period nowTillStart = LocalDate.now().until(start);
		if(nowTillStart.getDays() < 0) {
			isUrgent = false;
			setStatus(Status.CANCELLED);
		} else {
			isUrgent = nowTillStart.getDays() <= getURGENCY_WINDOW();
		}	
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getJustification() {
		return justification;
	}

	public void setJustification(String justification) {
		this.justification = justification;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public double getCostCoverage() {
		return costCoverage;
	}

	public void setCostCoverage(double costCoverage) {
		this.costCoverage = costCoverage;
	}

	public LocalDate getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate() {
		this.submissionDate = LocalDate.now();
	}

	public boolean isReimbursementAdjustedByBenCo() {
		return isReimbursementAdjustedByBenCo;
	}

	public void setReimbursementAdjustedByBenCo(boolean isReimbursementAdjustedByBenCo) {
		this.isReimbursementAdjustedByBenCo = isReimbursementAdjustedByBenCo;
	}

	public boolean isFinalAssesmentReviewed() {
		return isFinalAssesmentReviewed;
	}

	public void setFinalAssesmentReviewed(boolean isFinalAssesmentReviewed) {
		this.isFinalAssesmentReviewed = isFinalAssesmentReviewed;
	}

	public double getWorkHoursMissed() {
		return workHoursMissed;
	}

	public void setWorkHoursMissed(double workHoursMissed) {
		this.workHoursMissed = workHoursMissed;
	}

	public double getPassingGradePercentage() {
		return passingGradePercentage;
	}

	public void setPassingGradePercentage(double passingGradePercentage) {
		this.passingGradePercentage = passingGradePercentage;
	}
	
	public boolean isExceedsAvailableFunds() {
		return exceedsAvailableFunds;
	}

	public void setExceedsAvailableFunds(boolean exceedsAvailableFunds) {
		this.exceedsAvailableFunds = exceedsAvailableFunds;
	}

	public String getAvailableFundsExcessJustification() {
		return availableFundsExcessJustification;
	}

	public void setAvailableFundsExcessJustification(String availableFundsExcessJustification) {
		this.availableFundsExcessJustification = availableFundsExcessJustification;
	}

	@Override
	public String toString() {
		return "Employee: "+getUsername()
			+"\nTRR ID: "+getId()
			+"\nEvent Type: "+getEventType().toString()
			+"\nGrading Format: "+getGradingFormat().toString()
			+"\nCost: "+getCost()
			+"\nCost Coverage: "+getCostCoverage()
			+"\nStatus: "+getStatus()
			+"\nUrgent?: "+isUrgent();
	}
	
}
