package database.structureClasses;

public class BankEmployee extends BankUser {
	private int employeeId;
	private int employeeAccess;
	private BankBranch branchDetails;

	public BankBranch getBranchDetails() {
		return branchDetails;
	}

	public void setBranchDetails(BankBranch branchDetails) {
		this.branchDetails = branchDetails;
	}

	public int getEmployeeAccess() {
		return employeeAccess;
	}

	public void setEmployeeAccess(int employeeAccess) {
		this.employeeAccess = employeeAccess;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}
}
