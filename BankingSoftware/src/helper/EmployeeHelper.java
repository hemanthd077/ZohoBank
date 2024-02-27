package helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.BankAccountDatabase;
import database.BankCustomerDatabase;
import database.BankEmployeeDatabase;
import database.BankUserDatabase;
import database.structureClasses.BankAccount;
import database.structureClasses.BankCustomer;
import database.structureClasses.BankBranch;
import database.structureClasses.BankEmployee;
import globalUtilities.GlobalChecker;
import handleError.CustomException;
import helper.enumFiles.StatusType;

public class EmployeeHelper {
	
	static BankEmployeeDatabase employeeDatabase = new BankEmployeeDatabase();
	static BankUserDatabase userDatabase = new BankUserDatabase();
	static BankEmployee empDetails;
	static BankCustomerDatabase customerDatabase = new BankCustomerDatabase();
	static BankAccountDatabase bankAccountDatabase = new BankAccountDatabase();
	
	public BankEmployee getMyData() throws CustomException{
		empDetails = employeeDatabase.getEmployeeData();
		return empDetails;
	}
	
	public Map<Integer,BankCustomer> getInActiveUserDetails() throws CustomException{
		return customerDatabase.getUserDetails(StatusType.INACTIVE.getCode());
	}
	
	public Map<Integer,BankCustomer> getActiveUserDetails() throws CustomException{
		return customerDatabase.getUserDetails(StatusType.ACTIVE.getCode());
	}
	
	public Map<Integer,BankEmployee> getActiveEmployeeDetails() throws CustomException{
		return employeeDatabase.getEmployeeDetails(StatusType.ACTIVE.getCode());
	}
	
	public Map<Integer,BankEmployee> getInActiveEmployeeDetails() throws CustomException{
		return employeeDatabase.getEmployeeDetails(StatusType.INACTIVE.getCode());
	}
	
	public Map<Integer,BankCustomer> getAllUserDetails() throws CustomException{
		return customerDatabase.getUserDetails(StatusType.ACTIVE.getCode());
	}
	
	public boolean adminCreateCustomer(List<BankCustomer> userDetails) throws CustomException{
		GlobalChecker.checkNull(userDetails);
		int size = userDetails.size();
		for(int i=0;i<size;i++) {
			userDetails.get(i).setPassword(GlobalChecker.hashPassword(userDetails.get(i).getPassword()));
		}
		return userDatabase.createBankUserOrEmployee(userDetails,false);
	}
	
	public <T> boolean deleteUser(T bankUserDetails) throws CustomException{
		GlobalChecker.checkNull(bankUserDetails);
		Map<Object,Object> updateMap = new HashMap<>();
		updateMap.put("STATUS", 0);
		return customerDatabase.updateUser(bankUserDetails,updateMap);
	}
	
	public <T> boolean activateUser(T bankUserDetails) throws CustomException{
		GlobalChecker.checkNull(bankUserDetails);
		Map<Object,Object> updateMap = new HashMap<>();
		updateMap.put("STATUS", 1);
		return customerDatabase.updateUser(bankUserDetails,updateMap);
	}

	public boolean adminCreateCustomerAccount(BankCustomer bankCustomerDetails,BankBranch branchDetails) throws CustomException{
		return bankAccountDatabase.createBankAccount(bankCustomerDetails,branchDetails);
	}
	
	public boolean employeeCreateCustomerAccount(BankCustomer bankCustomerDetails) throws CustomException{
		GlobalChecker.checkNull(bankCustomerDetails);
		BankBranch branchDetails = getMyData().getBranchDetails();
		return bankAccountDatabase.createBankAccount(bankCustomerDetails,branchDetails);
	}
	
	public Map<Integer,BankAccount> getAccountAllBranch(List<BankCustomer> bankCustomerDetails,int status) throws CustomException{
		GlobalChecker.checkNull(bankCustomerDetails);
		return bankAccountDatabase.getAccountAllBranch(bankCustomerDetails,status);
	}
	
	public Map<Integer,BankAccount> getBranchAccounts(List<BankCustomer> bankCustomerDetails,int status) throws CustomException{
		GlobalChecker.checkNull(bankCustomerDetails);
		return bankAccountDatabase.getAccountSingleBranch(bankCustomerDetails,status,empDetails.getBranchDetails().getBranch_id());
	}
	
	public boolean deleteAccount(BankAccount bankAccountDetails) throws CustomException{
		Map<Object,Object> updateMap = new HashMap<>();
		updateMap.put("STATUS", 0);
		return bankAccountDatabase.updateAccount(bankAccountDetails,updateMap);
	}
	
	public boolean activateAccount(BankAccount bankAccountDetails) throws CustomException{
		Map<Object,Object> updateMap = new HashMap<>();
		updateMap.put("STATUS", 1);
		return bankAccountDatabase.updateAccount(bankAccountDetails,updateMap);
	}
	
	public boolean createEmployee(List<BankEmployee> userDetails) throws CustomException{
		GlobalChecker.checkNull(userDetails);
		int size = userDetails.size();
		for(int i=0;i<size;i++) {
			userDetails.get(i).setEmployeeAccess(0);
			userDetails.get(i).setPassword(GlobalChecker.hashPassword(userDetails.get(i).getPassword()));
		}
		return userDatabase.createBankUserOrEmployee(userDetails,true);
	}
}
