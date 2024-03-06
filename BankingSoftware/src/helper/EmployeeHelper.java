package helper;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.IAccountData;
import database.IEmployeeData;
import database.IUserData;
import database.UserDatabase;
import database.structure.BankAccount;
import database.structure.BankBranch;
import database.structure.BankCustomer;
import database.structure.BankEmployee;
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
		empDetails = employeeDatabase.getEmployeeData(StatusType.ACTIVE.getCode(), userId, -1, 1, 0).get(userId);
		return empDetails;
	}

	public Map<Integer, BankCustomer> getInActiveUserDetails(int rowLimit, int pageCount) throws CustomException {
		pageCount = (pageCount - 1) * rowLimit;
		return userDatabase.getUserDetails(StatusType.INACTIVE.getCode(), rowLimit, pageCount);
	}

	public Map<Integer, BankCustomer> getActiveCustomerDetails(int rowLimit, int pageCount) throws CustomException {
		pageCount = (pageCount - 1) * rowLimit;
		Map<Integer, BankCustomer> activeUsers = userDatabase.getUserDetails(StatusType.ACTIVE.getCode(), rowLimit,
				pageCount);
		if (activeUsers.size() == 0) {
			throw new CustomException(ExceptionStatus.USERNOTFOUND.getStatus());
		}
		return activeUsers;
	}

	public Map<Integer, BankEmployee> getActiveEmployeeDetails(int rowLimit, int pageCount) throws CustomException {
		pageCount = (pageCount - 1) * rowLimit;
		Map<Integer, BankEmployee> activeEmployee = employeeDatabase.getEmployeeData(StatusType.ACTIVE.getCode(), -1,
				StatusType.EMPLOYEEACCESS.getCode(), rowLimit, pageCount);
		if (activeEmployee.size() == 0) {
			throw new CustomException(ExceptionStatus.USERNOTFOUND.getStatus());
		}
		return activeEmployee;
	}

	public Map<Integer, BankEmployee> getInActiveEmployeeDetails(int rowLimit, int pageCount) throws CustomException {
		pageCount = (pageCount - 1) * rowLimit;
		Map<Integer, BankEmployee> inActiveEmployee = employeeDatabase.getEmployeeData(StatusType.INACTIVE.getCode(),
				-1, StatusType.EMPLOYEEACCESS.getCode(), rowLimit, pageCount);
		if (inActiveEmployee.size() == 0) {
			throw new CustomException(ExceptionStatus.USERNOTFOUND.getStatus());
		}
		return inActiveEmployee;
	}

	public Map<Integer, BankCustomer> getAllUserDetails(int rowLimit, int pageCount) throws CustomException {
		pageCount = (pageCount - 1) * rowLimit;
		Map<Integer, BankCustomer> allUserDetail = userDatabase.getUserDetails(StatusType.ACTIVE.getCode(), rowLimit,
				pageCount);
		if (allUserDetail.size() == 0) {
			throw new CustomException(ExceptionStatus.USERNOTFOUND.getStatus());
		}
		return allUserDetail;
	}

	public boolean adminCreateCustomer(BankCustomer userDetails) throws CustomException {
		GlobalChecker.checkNull(userDetails);
		userDetails.setPassword(GlobalChecker.hashPassword(userDetails.getPassword()));
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

	public boolean adminCreateCustomerAccount(BankCustomer bankCustomerDetails, BankBranch branchDetails,int accountType)
			throws CustomException {
		return bankAccountDatabase.createBankAccount(bankCustomerDetails, branchDetails,accountType);
	}

	public boolean employeeCreateCustomerAccount(BankCustomer bankCustomerDetails,int accountType) throws CustomException {
		GlobalChecker.checkNull(bankCustomerDetails);
		BankBranch branchDetails = getMyData().getBankBranch();
		return bankAccountDatabase.createBankAccount(bankCustomerDetails, branchDetails,accountType);
	}

	public Map<Long, BankAccount> getAccountAllBranch(int userId, int status) throws CustomException {
		return bankAccountDatabase.getAccountWithBranch(userId, status, -1);
	}

	public Map<Long, BankAccount> getBranchAccounts(int userId, int status) throws CustomException {
		return bankAccountDatabase.getAccountWithBranch(userId, status, empDetails.getBankBranch().getBranchId());
	}

	public boolean deleteAccount(long accountNo, int userId) throws CustomException {
		Map<Object, Object> updateMap = new HashMap<>();
		updateMap.put("STATUS", StatusType.INACTIVE.getCode());
		return bankAccountDatabase.updateAccount(accountNo, userId, updateMap);
	}

	public boolean activateAccount(long accountNo, int userId) throws CustomException {
		Map<Object, Object> updateMap = new HashMap<>();
		updateMap.put("STATUS", StatusType.ACTIVE.getCode());
		return bankAccountDatabase.updateAccount(accountNo, userId, updateMap);
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
