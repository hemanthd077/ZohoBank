package helper;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import database.IBranchData;
import database.IUserData;
import database.structure.BankAccount;
import database.structure.BankBranch;
import database.structure.BankCustomer;
import database.structure.BankEmployee;
import database.structure.CurrentUser;
import globalUtilities.CustomException;
import globalUtilities.GlobalCommonChecker;
import helper.cache.LRUCache;
import helper.enumfiles.CacheSize;
import helper.enumfiles.ExceptionStatus;

public class UserHelper {

	private IUserData userDatabase;
	private IBranchData branchDatabase;

	static BankEmployee empDetails;

	public static LRUCache<Long, BankCustomer> customerCache;
	public static LRUCache<Long, BankEmployee> employeeCache;
	public static LRUCache<Long, Map<Long, BankAccount>> accountCache;

	public UserHelper() throws CustomException {
		try {
			customerCache = new LRUCache<>(CacheSize.CUSTOMER_CACHE.getSize());
			employeeCache = new LRUCache<>(CacheSize.EMPLOYEE_CACHE.getSize());
			accountCache = new LRUCache<>(CacheSize.ACCOUNT_CACHE.getSize());

			Class<?> bankUserDao = Class.forName("database.UserDatabase");
			userDatabase = (IUserData) bankUserDao.getDeclaredConstructor().newInstance();

			Class<?> bankBranchDao = Class.forName("database.BranchDatabase");
			branchDatabase = (IBranchData) bankBranchDao.getDeclaredConstructor().newInstance();

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new CustomException(ExceptionStatus.FILENOTFOUNT.getStatus(), e);
		}

	}

	// return values -1 ->usernotFount 1 -> Customer 2->Employee 3->Admin
	public int userLogin(long checkUserId, String password) throws CustomException {

		try {
			GlobalCommonChecker.checkNull(checkUserId);
			GlobalCommonChecker.checkNull(password);

			password = GlobalCommonChecker.hashPassword(password);
			int userType = userDatabase.userValidation(checkUserId, password);
			if (userType == 0) {
				throw new CustomException(ExceptionStatus.WRONGPASSWORD.getStatus());
			} else if (userType == 1) {
				return 1;
			} else {
				boolean employeeResult = userDatabase.isAdmin(checkUserId);
				if (employeeResult) {
					return 3;
				}
				return 2;
			}
		} catch (CustomException e) {
			throw new CustomException(e.getMessage(), e);
		}
	}

	public Map<Integer, BankBranch> getBranchData() throws CustomException {
		return branchDatabase.getBranchDetails();
	}

	public boolean validatePassword(String password) throws CustomException {
		password = GlobalCommonChecker.hashPassword(password);
		if (userDatabase.userValidation(CurrentUser.getUserId(), password) != 0) {
			return true;
		}
		throw new CustomException(ExceptionStatus.INVALIDPASSWORD.getStatus());
	}
}
