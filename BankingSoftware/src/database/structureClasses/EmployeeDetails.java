package database.structureClasses;

public class EmployeeDetails extends UserDetails {
	private int employeeId;
	private int employeeAccess;
	private UserDetails userDetails;
	private BranchDetails branchDetails;

	public BranchDetails getBranchDetails() {
		return branchDetails;
	}

	public void setBranchDetails(BranchDetails branchDetails) {
		this.branchDetails = branchDetails;
	}

	public UserDetails getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(UserDetails userDetails) {
		this.userDetails = userDetails;
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
