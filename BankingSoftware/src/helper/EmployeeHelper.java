package helper;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import database.IAccountData;
import database.IEmployeeData;
import database.IUserData;
import database.structure.BankAccount;
import database.structure.BankBranch;
import database.structure.BankCustomer;
import database.structure.BankEmployee;
import database.structure.CurrentUser;
import globalutil.CustomException;
import globalutil.GlobalCommonChecker;
import helper.enumfiles.EmployeeAccess;
import helper.enumfiles.ExceptionStatus;
import helper.enumfiles.RecordStatus;

public class EmployeeHelper {

	private static IUserData userDatabase;
	private static IAccountData bankAccountDatabase;
	private static IEmployeeData employeeDatabase;
	private static CustomerHelper customerHelper;
	static BankEmployee empDetails;

	public EmployeeHelper() throws CustomException {
		try {
			Class<?> bankUserDao = Class.forName("database.UserDatabase");
			userDatabase = (IUserData) bankUserDao.getDeclaredConstructor().newInstance();

			Class<?> bankAccountDao = Class.forName("database.AccountDatabase");
			bankAccountDatabase = (IAccountData) bankAccountDao.getDeclaredConstructor().newInstance();

			Class<?> bankEmployeeDao = Class.forName("database.EmployeeDatabase");
			employeeDatabase = (IEmployeeData) bankEmployeeDao.getDeclaredConstructor().newInstance();

			customerHelper = new CustomerHelper();

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new CustomException("Error Occured : Some Files Not Found ", e);
		}
	}

	public BankEmployee getMyData() throws CustomException {
		long userId = CurrentUser.getUserId();

		if (UserHelper.employeeCache.containKey(userId)) {
			System.out.println("Existing Memory");
			return UserHelper.employeeCache.get(userId);
		} else {
			System.out.println("New assigning Memory");
			empDetails = employeeDatabase.getEmployeeData(RecordStatus.ACTIVE.getCode(), userId, -1, 1, 0).get(userId); // last
																														// two
																														// fields
																														// limit
																														// and
																														// offset
																														// for
																														// pagination
			UserHelper.employeeCache.set(userId, empDetails);
			return empDetails;
		}
	}

	public Map<Long, BankCustomer> getInActiveUserDetails(int rowLimit, int pageCount) throws CustomException {
		pageCount = (pageCount - 1) * rowLimit;
		return userDatabase.getUserDetails(RecordStatus.INACTIVE.getCode(), rowLimit, pageCount);
	}

	public Map<Long, BankCustomer> getActiveCustomerDetails(int rowLimit, int pageCount) throws CustomException {
		pageCount = (pageCount - 1) * rowLimit;
		Map<Long, BankCustomer> activeUsers = userDatabase.getUserDetails(RecordStatus.ACTIVE.getCode(), rowLimit,
				pageCount);
		if (activeUsers.size() == 0) {
			throw new CustomException(ExceptionStatus.USERNOTFOUND.getStatus());
		}
		return activeUsers;
	}

	public Map<Long, BankEmployee> getActiveEmployeeDetails(int rowLimit, int pageCount) throws CustomException {
		pageCount = (pageCount - 1) * rowLimit;
		Map<Long, BankEmployee> activeEmployee = employeeDatabase.getEmployeeData(RecordStatus.ACTIVE.getCode(), -1,
				EmployeeAccess.EMPLOYEE.getCode(), rowLimit, pageCount);
		if (activeEmployee.size() == 0) {
			throw new CustomException(ExceptionStatus.USERNOTFOUND.getStatus());
		}
		return activeEmployee;
	}

	public Map<Long, BankEmployee> getInActiveEmployeeDetails(int rowLimit, int pageCount) throws CustomException {
		pageCount = (pageCount - 1) * rowLimit;
		Map<Long, BankEmployee> inActiveEmployee = employeeDatabase.getEmployeeData(RecordStatus.INACTIVE.getCode(), -1,
				EmployeeAccess.EMPLOYEE.getCode(), rowLimit, pageCount);
		if (inActiveEmployee.size() == 0) {
			throw new CustomException(ExceptionStatus.USERNOTFOUND.getStatus());
		}
		return inActiveEmployee;
	}

	public Map<Long, BankCustomer> getAllUserDetails(int rowLimit, int pageCount) throws CustomException {
		pageCount = (pageCount - 1) * rowLimit;
		Map<Long, BankCustomer> allUserDetail = userDatabase.getUserDetails(RecordStatus.ACTIVE.getCode(), rowLimit,
				pageCount);
		if (allUserDetail.size() == 0) {
			throw new CustomException(ExceptionStatus.USERNOTFOUND.getStatus());
		}
		return allUserDetail;
	}

	public boolean adminCreateCustomer(BankCustomer userDetails) throws CustomException {
		GlobalCommonChecker.checkNull(userDetails);
		userDetails.setPassword(GlobalCommonChecker.hashPassword(userDetails.getPassword()));
		return userDatabase.createUser(userDetails, false);
	}

	public boolean deleteUser(long userId) throws CustomException {
		Map<Object, Object> updateMap = new HashMap<>();
		updateMap.put("STATUS", RecordStatus.INACTIVE.getCode());

		customerHelper.deleteUserCache(userId);
		return userDatabase.updateUser(userId, updateMap);
	}

	public boolean activateUser(long userId) throws CustomException {
		Map<Object, Object> updateMap = new HashMap<>();
		updateMap.put("STATUS", RecordStatus.ACTIVE.getCode());
		return userDatabase.updateUser(userId, updateMap);
	}

	public boolean adminCreateCustomerAccount(BankCustomer bankCustomerDetails, BankBranch branchDetails,
			int accountType) throws CustomException {
		return bankAccountDatabase.createBankAccount(bankCustomerDetails, branchDetails, accountType);
	}

	public boolean employeeCreateCustomerAccount(BankCustomer bankCustomerDetails, int accountType)
			throws CustomException {
		GlobalCommonChecker.checkNull(bankCustomerDetails);
		BankBranch branchDetails = getMyData().getBankBranch();
		return bankAccountDatabase.createBankAccount(bankCustomerDetails, branchDetails, accountType);
	}

	public Map<Long, BankAccount> getAccountAllBranch(long userId, int status) throws CustomException {
		Map<Long, BankAccount> tempMap = bankAccountDatabase.getAccountWithBranch(userId, status, -1);

		Set<Long> tempSet = new HashSet<>();
		for (Map.Entry<Long, BankAccount> entry : tempMap.entrySet()) {

			BankAccount values = entry.getValue();
			long accNo = values.getAccountNo();
			UserHelper.accountCache.set(accNo, values);
			tempSet.add(accNo);
		}
		UserHelper.customerAccountCache.set(userId, tempSet);

		return tempMap;
	}

	public Map<Long, BankAccount> getBranchAccounts(long userId, int status) throws CustomException {
		Map<Long, BankAccount> mapOfAccounts = bankAccountDatabase.getAccountWithBranch(userId, status,
				empDetails.getBankBranch().getBranchId());
		return mapOfAccounts;
	}

	public boolean deleteAccount(long accountNo, long userId) throws CustomException {
		Map<Object, Object> updateMap = new HashMap<>();
		updateMap.put("STATUS", RecordStatus.INACTIVE.getCode());

		customerHelper.deleteUserCache(userId);
		return bankAccountDatabase.updateAccount(accountNo, userId, updateMap);
	}

	public boolean activateAccount(long accountNo, long userId) throws CustomException {
		Map<Object, Object> updateMap = new HashMap<>();
		updateMap.put("STATUS", RecordStatus.ACTIVE.getCode());
		return bankAccountDatabase.updateAccount(accountNo, userId, updateMap);
	}

	public boolean createEmployee(BankEmployee employeeDetails) throws CustomException {
		GlobalCommonChecker.checkNull(employeeDetails);
		employeeDetails.setEmployeeAccess(EmployeeAccess.EMPLOYEE.getCode());
		employeeDetails.setPassword(GlobalCommonChecker.hashPassword(employeeDetails.getPassword()));
		return userDatabase.createUser(employeeDetails, true);
	}
}
