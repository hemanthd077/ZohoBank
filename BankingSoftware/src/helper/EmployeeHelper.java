package helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.BankAccountDatabase;
import database.CustomerDatabase;
import database.EmployeeDatabase;
import database.UserDatabase;
import database.structureClasses.BankAccountDetails;
import database.structureClasses.BankCustomerDetails;
import database.structureClasses.BranchDetails;
import database.structureClasses.EmployeeDetails;
import handleError.CustomException;

public class EmployeeHelper {
	
	static EmployeeDatabase employeeDatabase = new EmployeeDatabase();
	static UserDatabase userDatabase = new UserDatabase();
	
	static EmployeeDetails empDetails;
	
	public EmployeeDetails getMyData() throws CustomException{
		empDetails = employeeDatabase.getEmployeeData();
		return empDetails;
	}
	
	static CustomerDatabase customerDatabase = new CustomerDatabase();
	static BankAccountDatabase bankAccountDatabase = new BankAccountDatabase();
	
	public Map<Integer,BankCustomerDetails> getInActiveUserDetails() throws CustomException{
		return customerDatabase.getUserDetails(0);
	}
	
	public Map<Integer,BankCustomerDetails> getActiveUserDetails() throws CustomException{
		return customerDatabase.getUserDetails(1);
	}
	
	public Map<Integer,BankCustomerDetails> getAllUserDetails() throws CustomException{
		return customerDatabase.getUserDetails(0);
	}
	
	public boolean adminCreateCustomer(List<BankCustomerDetails> userDetails) throws CustomException{
		return userDatabase.createBankUserOrEmployee(userDetails,false);
	}
	
	public <K,V> boolean deleteUser(BankCustomerDetails bankCustomerDetails) throws CustomException{
		Map<Object,Object> updateMap = new HashMap<>();
		updateMap.put("STATUS", 0);
		return customerDatabase.updateUser(bankCustomerDetails,updateMap);
	}
	public boolean adminCreateCustomerAccount(BankCustomerDetails bankCustomerDetails,BranchDetails branchDetails) throws CustomException{
		return bankAccountDatabase.createBankAccount(bankCustomerDetails,branchDetails);
	}
	
	public boolean employeeCreateCustomerAccount(BankCustomerDetails bankCustomerDetails) throws CustomException{
		BranchDetails branchDetails = getMyData().getBranchDetails();
		return bankAccountDatabase.createBankAccount(bankCustomerDetails,branchDetails);
	}
	
	public Map<Integer,BankAccountDetails> getAccountAllBranch(List<BankCustomerDetails> bankCustomerDetails,int status) throws CustomException{
		return bankAccountDatabase.getAccountAllBranch(bankCustomerDetails,status);
	}
	
	public Map<Integer,BankAccountDetails> getBranchAccounts(List<BankCustomerDetails> bankCustomerDetails,int status) throws CustomException{
		
		return bankAccountDatabase.getAccountSingleBranch(bankCustomerDetails,status,empDetails.getBranchDetails().getBranch_id());
	}
	
	public boolean deleteAccount(BankAccountDetails bankAccountDetails) throws CustomException{
		Map<Object,Object> updateMap = new HashMap<>();
		updateMap.put("STATUS", 0);
		return bankAccountDatabase.updateAccount(bankAccountDetails,updateMap);
	}
	
	public boolean activateAccount(BankAccountDetails bankAccountDetails) throws CustomException{
		Map<Object,Object> updateMap = new HashMap<>();
		updateMap.put("STATUS", 1);
		return bankAccountDatabase.updateAccount(bankAccountDetails,updateMap);
	}
	
	public boolean createEmployee(List<EmployeeDetails> userDetails) throws CustomException{
		int size = userDetails.size();
		for(int i=0;i<size;i++) {
			userDetails.get(i).setEmployeeAccess(0);
		}
		
		return userDatabase.createBankUserOrEmployee(userDetails,true);
	}
	
}
