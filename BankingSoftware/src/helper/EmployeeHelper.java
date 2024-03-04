package helper;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.IAccountData;
import database.IEmployeeData;
import database.IUserData;
import database.UserDatabase;
import database.structureClasses.BankAccount;
import database.structureClasses.BankCustomer;
import database.structureClasses.BankBranch;
import database.structureClasses.BankEmployee;
import globalUtilities.CustomException;
import globalUtilities.GlobalChecker;
import helper.enumFiles.ExceptionStatus;
import helper.enumFiles.StatusType;

public class EmployeeHelper {

	private static IUserData userDatabase;
	private static IAccountData bankAccountDatabase;
	private static IEmployeeData employeeDatabase;

	static BankEmployee empDetails;

	public EmployeeHelper() throws CustomException {
		try {
			Class<?> bankUserDao = Class.forName("database.UserDatabase");
			userDatabase = (IUserData) bankUserDao.getDeclaredConstructor().newInstance();

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
		int userId = UserDatabase.getUserId();
		empDetails = employeeDatabase.getEmployeeData(StatusType.ACTIVE.getCode(), userId, -1).get(userId);
		return empDetails;
	}

	public Map<Integer, BankCustomer> getInActiveUserDetails() throws CustomException {
		return userDatabase.getUserDetails(StatusType.INACTIVE.getCode());
	}

	public Map<Integer, BankCustomer> getActiveCustomerDetails() throws CustomException {
		Map<Integer, BankCustomer> activeUsers = userDatabase.getUserDetails(StatusType.ACTIVE.getCode());
		if (activeUsers.size() == 0) {
			throw new CustomException(ExceptionStatus.USERNOTFOUND.getStatus());
		}
		return activeUsers;
	}

	public Map<Integer, BankEmployee> getActiveEmployeeDetails() throws CustomException {
		Map<Integer, BankEmployee> activeEmployee = employeeDatabase.getEmployeeData(StatusType.ACTIVE.getCode(), -1,
				StatusType.EMPLOYEEACCESS.getCode());
		if (activeEmployee.size() == 0) {
			throw new CustomException(ExceptionStatus.USERNOTFOUND.getStatus());
		}
		return activeEmployee;
	}

	public Map<Integer, BankEmployee> getInActiveEmployeeDetails() throws CustomException {
		Map<Integer, BankEmployee> inActiveEmployee = employeeDatabase.getEmployeeData(StatusType.ACTIVE.getCode(), -1,
				StatusType.EMPLOYEEACCESS.getCode());
		if (inActiveEmployee.size() == 0) {
			throw new CustomException(ExceptionStatus.USERNOTFOUND.getStatus());
		}
		return inActiveEmployee;
	}

	public Map<Integer, BankCustomer> getAllUserDetails() throws CustomException {
		Map<Integer, BankCustomer> allUserDetail = userDatabase.getUserDetails(StatusType.ACTIVE.getCode());
		if (allUserDetail.size() == 0) {
			throw new CustomException(ExceptionStatus.USERNOTFOUND.getStatus());
		}
		return allUserDetail;
	}

	public boolean adminCreateCustomer(List<BankCustomer> userDetails) throws CustomException {
		GlobalChecker.checkNull(userDetails);
		int userSize = userDetails.size();
		for (int i = 0; i < userSize; i++) {
			userDetails.get(i).setPassword(GlobalChecker.hashPassword(userDetails.get(i).getPassword()));
		}
		return userDatabase.createUser(userDetails, false);
	}

	public <T> boolean deleteUser(T bankUserDetails) throws CustomException {
		GlobalChecker.checkNull(bankUserDetails);
		Map<Object, Object> updateMap = new HashMap<>();
		updateMap.put("STATUS", StatusType.INACTIVE.getCode());
		return userDatabase.updateUser(bankUserDetails, updateMap);
	}

	public <T> boolean activateUser(T bankUserDetails) throws CustomException {
		GlobalChecker.checkNull(bankUserDetails);
		Map<Object, Object> updateMap = new HashMap<>();
		updateMap.put("STATUS", StatusType.ACTIVE.getCode());
		return userDatabase.updateUser(bankUserDetails, updateMap);
	}

	public boolean adminCreateCustomerAccount(BankCustomer bankCustomerDetails, BankBranch branchDetails)
			throws CustomException {
		return bankAccountDatabase.createBankAccount(bankCustomerDetails, branchDetails);
	}

	public boolean employeeCreateCustomerAccount(BankCustomer bankCustomerDetails) throws CustomException {
		GlobalChecker.checkNull(bankCustomerDetails);
		BankBranch branchDetails = getMyData().getBankBranch();
		return bankAccountDatabase.createBankAccount(bankCustomerDetails, branchDetails);
	}

	public Map<Integer, BankAccount> getAccountAllBranch(List<BankCustomer> bankCustomerDetails, int status)
			throws CustomException {
		return bankAccountDatabase.getAccountWithBranch(bankCustomerDetails, status, -1);
	}

	public Map<Integer, BankAccount> getBranchAccounts(List<BankCustomer> bankCustomerDetails, int status)
			throws CustomException {
		Map<Integer, BankAccount> allBranchAccounts = bankAccountDatabase.getAccountWithBranch(bankCustomerDetails,
				status, empDetails.getBankBranch().getBranchId());
		return allBranchAccounts;
	}

	public boolean deleteAccount(BankAccount bankAccountDetails) throws CustomException {
		GlobalChecker.checkNull(bankAccountDetails);
		Map<Object, Object> updateMap = new HashMap<>();
		updateMap.put("STATUS", StatusType.INACTIVE.getCode());
		return bankAccountDatabase.updateAccount(bankAccountDetails, updateMap);
	}

	public boolean activateAccount(BankAccount bankAccountDetails) throws CustomException {
		GlobalChecker.checkNull(bankAccountDetails);
		Map<Object, Object> updateMap = new HashMap<>();
		updateMap.put("STATUS", StatusType.ACTIVE.getCode());
		return bankAccountDatabase.updateAccount(bankAccountDetails, updateMap);
	}

	public boolean createEmployee(List<BankEmployee> employeeDetails) throws CustomException {
		GlobalChecker.checkNull(employeeDetails);
		int totalEmployeeSize = employeeDetails.size();
		for (int i = 0; i < totalEmployeeSize; i++) {
			BankEmployee bankEmployee = employeeDetails.get(i);
			bankEmployee.setEmployeeAccess(StatusType.EMPLOYEEACCESS.getCode());
			bankEmployee.setPassword(GlobalChecker.hashPassword(bankEmployee.getPassword()));
		}
		return userDatabase.createUser(employeeDetails, true);
	}
}
