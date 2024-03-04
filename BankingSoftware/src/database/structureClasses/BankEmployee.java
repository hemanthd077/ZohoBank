package database.structureClasses;

public class BankEmployee extends BankUser {
	private int employeeId;
	private int employeeAccess;
	private BankBranch bankBranch;

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

	public BankBranch getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(BankBranch bankBranch) {
		this.bankBranch = bankBranch;
	}
}
