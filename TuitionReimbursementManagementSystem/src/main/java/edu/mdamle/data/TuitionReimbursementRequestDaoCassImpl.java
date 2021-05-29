package edu.mdamle.data;

import java.time.LocalDateTime;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.DefaultConsistencyLevel;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatementBuilder;

import edu.mdamle.beans.TuitionReimbursementRequest;
import edu.mdamle.beans.TuitionReimbursementRequestWithGrade;
import edu.mdamle.beans.TuitionReimbursementRequestWithPresentation;
import edu.mdamle.beans.TuitionReimbursementRequest.EventTypes;
import edu.mdamle.beans.TuitionReimbursementRequest.FinalAssesmentTypes;
import edu.mdamle.beans.User.Role;
import edu.mdamle.utils.CassandraUtil;

public class TuitionReimbursementRequestDaoCassImpl implements TuitionReimbursementRequestDao {
	private CqlSession session = CassandraUtil.getInstance().getSession();

	@Override
	public TuitionReimbursementRequest getTrr(String targetUsername, int id) {
		TuitionReimbursementRequest request = null;
		String query = "select username, " 
				+ "id, "
				+ "eventType, eventStartDate, eventEndDate, location, description, cost, "
				+ "costCoverage, justification, dirSupApproval, deptHeadApproval, "
				+ "benCoApproval, confirmation, gradingFormat, finalAssesmentReviewed, "
				+ "workHoursMissed, passingGradePercentage, emailedApprovalType, "
				+ "exceedsAvailableFunds, availableFundsExcessJustification, "
				+ "submissionDate, isUrgent, isExtra, reimbursementAdjustedByBenCo "
				+ "from trrs where username = ? and id = ?";
		BoundStatement bound = session.prepare(query).bind(targetUsername, id);
		ResultSet rs = session.execute(bound);
		Row data = rs.one();
		if(data != null) {
			if(FinalAssesmentTypes.valueOf(data.getString("gradingFormat")).equals(FinalAssesmentTypes.GRADE)) {
				request = new TuitionReimbursementRequestWithGrade();
			} else if(FinalAssesmentTypes.valueOf(data.getString("gradingFormat")).equals(FinalAssesmentTypes.PRESENTATION)) {
				request = new TuitionReimbursementRequestWithPresentation();
			}
			request.setUsername(data.getString("username"));
			request.setId(data.getInt("id"));
			request.setEventType(EventTypes.valueOf(data.getString("eventType")));
			request.setEventStartDate(LocalDateTime.parse(data.getString("eventStartDate")));
			request.setEventEndDate(LocalDateTime.parse(data.getString("eventEndDate")));
			request.setLocation(data.getString("location"));
			request.setDescription(data.getString("description"));
			request.setCost(data.getDouble("cost"));
			request.setCostCoverage(data.getDouble("costCoverage"));
			request.setJustification(data.getString("justification"));
			request.setDirSupApproval(data.getBoolean("dirSupApproval"));
			request.setDeptHeadApproval(data.getBoolean("deptHeadApproval"));
			request.setBenCoApproval(data.getBoolean("benCoApproval"));
			request.setConfirmation(data.getBoolean("confirmation"));
			request.setFinalAssesmentReviewed(data.getBoolean("finalAssesmentReviewed"));
			request.setWorkHoursMissed(data.getDouble("workHoursMissed"));
			request.setPassingGradePercentage(data.getDouble("passingGradePercentage"));
			request.setEmailedApprovalType(Role.valueOf(data.getString("emailedApprovalType")));
			request.setExceedsAvailableFunds(data.getBoolean("exceedsAvailableFunds"));
			request.setAvailableFundsExcessJustification(data.getString("availableFundsExcessJustification"));
			request.setSubmissionDate(LocalDateTime.parse(data.getString("submissionDate")));
			request.setUrgent(data.getBoolean("isUrgent"));
			request.setExtra(data.getBoolean("isExtra"));
			request.setReimbursementAdjustedByBenCo(data.getBoolean("reimbursementAdjustedByBenCo"));
		}
		return request;
	}

	@Override
	public void updateTrr(TuitionReimbursementRequest trr) {
		/*
		String query = "update trrs set role = ?, score = ? where username = ? and id = ?";
		SimpleStatement s = new SimpleStatementBuilder(query)
				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM).build();
		BoundStatement bound = session.prepare(s).bind(p.getRole().toString(), p.getScore(), p.getName());
		session.execute(bound);
		*/		
	}

	@Override
	public boolean updateDeptHeadApproval(TuitionReimbursementRequest trr) {
		String query = "update trrs set deptHeadApproval = ? where username = ? and id = ?";
		SimpleStatement s = new SimpleStatementBuilder(query)
				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM).build();
		BoundStatement bound = session.prepare(s).bind(trr.getDeptHeadApproval(), trr.getUsername(), trr.getId());
		session.execute(bound);
		return true;
		//use exception check for return false
	}

	@Override
	public boolean updateDirSupApproval(TuitionReimbursementRequest trr) {
		String query = "update trrs set dirSupApproval = ? where username = ? and id = ?";
		SimpleStatement s = new SimpleStatementBuilder(query)
				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM).build();
		BoundStatement bound = session.prepare(s).bind(trr.getDirSupApproval(), trr.getUsername(), trr.getId());
		session.execute(bound);
		return true;
		//use exception check for return false
	}

	@Override
	public boolean updateFinalAssesmentReviewed(TuitionReimbursementRequest trr) {
		String query = "update trrs set finalAssesmentReviewed = ? where username = ? and id = ?";
		SimpleStatement s = new SimpleStatementBuilder(query)
				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM).build();
		BoundStatement bound = session.prepare(s).bind(trr.isFinalAssesmentReviewed(), trr.getUsername(), trr.getId());
		session.execute(bound);
		return true;
		//use exception check for return false
	}

	@Override
	public boolean updateCostCoverage(TuitionReimbursementRequest trr) {
		String query = "update trrs set costCoverage = ? where username = ? and id = ?";
		SimpleStatement s = new SimpleStatementBuilder(query)
				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM).build();
		BoundStatement bound = session.prepare(s).bind(trr.getCostCoverage(), trr.getUsername(), trr.getId());
		session.execute(bound);
		return true;
		//use exception check for return false
	}

	@Override
	public boolean updateExceedsAvailableFunds(TuitionReimbursementRequest trr) {
		String query = "update trrs set exceedsAvailableFunds = ? where username = ? and id = ?";
		SimpleStatement s = new SimpleStatementBuilder(query)
				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM).build();
		BoundStatement bound = session.prepare(s).bind(trr.isExceedsAvailableFunds(), trr.getUsername(), trr.getId());
		session.execute(bound);
		return true;
		//use exception check for return false
	}

	@Override
	public boolean updateAvailableFundsExcessJustification(TuitionReimbursementRequest trr) {
		String query = "update trrs set availableFundsExcessJustification = ? where username = ? and id = ?";
		SimpleStatement s = new SimpleStatementBuilder(query)
				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM).build();
		BoundStatement bound = session.prepare(s).bind(trr.getAvailableFundsExcessJustification(), trr.getUsername(), trr.getId());
		session.execute(bound);
		return true;
		//use exception check for return false
	}

}
