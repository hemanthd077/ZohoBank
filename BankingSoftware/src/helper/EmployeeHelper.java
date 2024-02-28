package helper;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.IAccountData;
import database.ICustomerData;
import database.IEmployeeData;
import database.IUserData;
import database.structureClasses.BankAccount;
import database.structureClasses.BankCustomer;
import database.structureClasses.BankBranch;
import database.structureClasses.BankEmployee;
import globalUtilities.GlobalChecker;
import handleError.CustomException;
import helper.enumFiles.ExceptionStatus;
import helper.enumFiles.StatusType;

public class EmployeeHelper {

	private static IUserData userDatabase;
	private static ICustomerData customerDatabase;
	private static IAccountData bankAccountDatabase;
	private static IEmployeeData employeeDatabase;

	static BankEmployee empDetails;

	public EmployeeHelper() throws CustomException {
		try {
			Class<?> bankUserDao = Class.forName("database.UserDatabase");
			userDatabase = (IUserData) bankUserDao.getDeclaredConstructor().newInstance();

			Class<?> bankCustomerDao = Class.forName("database.CustomerDatabase");
			customerDatabase = (ICustomerData) bankCustomerDao.getDeclaredConstructor().newInstance();

			Class<?> bankAccountDao = Class.forName("database.AccountDatabase");
			bankAccountDatabase = (IAccountData) bankAccountDao.getDeclaredConstructor().newInstance();

			Class<?> bankEmployeeDao = Class.forName("database.EmployeeDatabase");
			employeeDatabase = (IEmployeeData) bankEmployeeDao.getDeclaredConstructor().newInstance();

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new CustomException("Error Occured : Some Files Not Found ", e);
		}

	}

	public BankEmployee getMyData() throws CustomException {
		empDetails = employeeDatabase.getEmployeeData();
		return empDetails;
	}

	public Map<Integer, BankCustomer> getInActiveUserDetails() throws CustomException {
		return customerDatabase.getUserDetails(StatusType.INACTIVE.getCode());
	}

	public Map<Integer, BankCustomer> getActiveCustomerDetails() throws CustomException {
		Map<Integer, BankCustomer> activeUsers = customerDatabase.getUserDetails(StatusType.ACTIVE.getCode());
		if (activeUsers.size() == 0) {
			throw new CustomException(ExceptionStatus.USERNOTFOUND.getStatus());
		}
		return activeUsers;
	}

	public Map<Integer, BankEmployee> getActiveEmployeeDetails() throws CustomException {
		Map<Integer, BankEmployee> activeEmployee = employeeDatabase.getEmployeeDetails(StatusType.ACTIVE.getCode());
		if (activeEmployee.size() == 0) {
			throw new CustomException(ExceptionStatus.USERNOTFOUND.getStatus());
		}
		return activeEmployee;
	}

	public Map<Integer, BankEmployee> getInActiveEmployeeDetails() throws CustomException {
		return employeeDatabase.getEmployeeDetails(StatusType.INACTIVE.getCode());
	}

	public Map<Integer, BankCustomer> getAllUserDetails() throws CustomException {
		Map<Integer, BankCustomer> allUserDetail = customerDatabase.getUserDetails(StatusType.ACTIVE.getCode());
		if (allUserDetail.size() == 0) {
			throw new CustomException(ExceptionStatus.USERNOTFOUND.getStatus());
		}
		return allUserDetail;
	}

	public boolean adminCreateCustomer(List<BankCustomer> userDetails) throws CustomException {
		GlobalChecker.checkNull(userDetails);
		int size = userDetails.size();
		for (int i = 0; i < size; i++) {
			userDetails.get(i).setPassword(GlobalChecker.hashPassword(userDetails.get(i).getPassword()));
		}
		return userDatabase.createBankUserOrEmployee(userDetails, false);
	}

	public <T> boolean deleteUser(T bankUserDetails) throws CustomException {
		GlobalChecker.checkNull(bankUserDetails);
		Map<Object, Object> updateMap = new HashMap<>();
		updateMap.put("STATUS", 0);
		return customerDatabase.updateUser(bankUserDetails, updateMap);
	}

	public <T> boolean activateUser(T bankUserDetails) throws CustomException {
		GlobalChecker.checkNull(bankUserDetails);
		Map<Object, Object> updateMap = new HashMap<>();
		updateMap.put("STATUS", 1);
		return customerDatabase.updateUser(bankUserDetails, updateMap);
	}

	public boolean adminCreateCustomerAccount(BankCustomer bankCustomerDetails, BankBranch branchDetails)
			throws CustomException {
		return bankAccountDatabase.createBankAccount(bankCustomerDetails, branchDetails);
	}

	public boolean employeeCreateCustomerAccount(BankCustomer bankCustomerDetails) throws CustomException {
		GlobalChecker.checkNull(bankCustomerDetails);
		BankBranch branchDetails = getMyData().getBranchDetails();
		return bankAccountDatabase.createBankAccount(bankCustomerDetails, branchDetails);
	}

	public Map<Integer, BankAccount> getAccountAllBranch(List<BankCustomer> bankCustomerDetails, int status)
			throws CustomException {
		return bankAccountDatabase.getAccountAllBranch(bankCustomerDetails, status);
	}

	public Map<Integer, BankAccount> getBranchAccounts(List<BankCustomer> bankCustomerDetails, int status)
			throws CustomException {
		Map<Integer, BankAccount> allBranchAccounts = bankAccountDatabase.getAccountSingleBranch(bankCustomerDetails,
				status, empDetails.getBranchDetails().getBranch_id());
		return allBranchAccounts;
	}

	public boolean deleteAccount(BankAccount bankAccountDetails) throws CustomException {
		GlobalChecker.checkNull(bankAccountDetails);
		Map<Object, Object> updateMap = new HashMap<>();
		updateMap.put("STATUS", 0);
		return bankAccountDatabase.updateAccount(bankAccountDetails, updateMap);
	}

	public boolean activateAccount(BankAccount bankAccountDetails) throws CustomException {
		GlobalChecker.checkNull(bankAccountDetails);
		Map<Object, Object> updateMap = new HashMap<>();
		updateMap.put("STATUS", 1);
		return bankAccountDatabase.updateAccount(bankAccountDetails, updateMap);
	}

	public boolean createEmployee(List<BankEmployee> userDetails) throws CustomException {
		GlobalChecker.checkNull(userDetails);
		int size = userDetails.size();
		for (int i = 0; i < size; i++) {
			userDetails.get(i).setEmployeeAccess(0);
			userDetails.get(i).setPassword(GlobalChecker.hashPassword(userDetails.get(i).getPassword()));
		}
		return userDatabase.createBankUserOrEmployee(userDetails, true);
	}
}
