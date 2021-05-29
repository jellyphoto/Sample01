package edu.mdamle.beans;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Employee extends User {
	private static final Logger log = LogManager.getLogger(Employee.class);
	
	private String dirSup;
	private String benCo;
	private int nextTrrId;
	
	private final double TOTALREIMBURSEMENT = 1000d;
	private double availableReimbursement;
	
	//LOGGED
	public Employee() {
		super();
		log.trace("constuctor "+this.getClass()+"() invoked");	//log flag
		super.setUserRole(Role.EMP);
		this.setNextTrrId(1);
		this.setAvailableReimbursement(TOTALREIMBURSEMENT);
		this.setDirSup("(unassigned)");	//verify pool
		this.setBenCo("(unassigned)");	//verify pool
		log.trace("new "+this.getClass()+" instantiated");	//log flag
		log.trace(this.toString());	//log flag
	}

	public String getDirSup() {
		return dirSup;
	}

	public void setDirSup(String dirSup) {
		this.dirSup = dirSup;
	}

	public String getBenCo() {
		return benCo;
	}

	public void setBenCo(String benCo) {
		this.benCo = benCo;
	}

	public int getNextTrrId() {
		return nextTrrId;
	}

	public void setNextTrrId(int nextTrrId) {
		this.nextTrrId = nextTrrId;
	}
	
	public void increamentNextTrrId() {
		setNextTrrId(getNextTrrId()+1);
	}

	public double getTOTALREIMBURSEMENT() {
		return TOTALREIMBURSEMENT;
	}

	public double getAvailableReimbursement() {
		return availableReimbursement;
	}

	public void setAvailableReimbursement(double availableReimbursement) {
		this.availableReimbursement = availableReimbursement;
	}

	@Override
	public String toString() {
		return super.toString()
				+ "Direct Supervisor:\t" + getDirSup() + "\n"
				+ "Benefits Coordinator:\t" + getBenCo() + "\n"
				+ "Available Reimbursement Amount:\t" + getAvailableReimbursement() +"\n";
	}
}
