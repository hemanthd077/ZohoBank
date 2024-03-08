package database.structure;

import helper.enumfiles.EmployeeAccess;

public class BankEmployee extends BankUser {
	private int employeeId;
	private EmployeeAccess employeeAccess;
	private BankBranch bankBranch;

	public EmployeeAccess getEmployeeAccess() {
		return employeeAccess;
	}

	public void setEmployeeAccess(int employeeAccess) {
		this.employeeAccess = EmployeeAccess.getByCode(employeeAccess);
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

	@Override
	public String toString() {
		return "BankEmployee [employeeId=" + employeeId + ", employeeAccess=" + employeeAccess + ", bankBranch="
				+ bankBranch + ", getUserId()=" + getUserId() + ", getEmail()=" + getEmail() + ", getPassword()="
				+ getPassword() + ", getPhoneNumber()=" + getPhoneNumber() + ", getName()=" + getName()
				+ ", getDateOfBirth()=" + getDateOfBirth() + ", getGender()=" + getGender() + ", getAddress()="
				+ getAddress() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}

}
