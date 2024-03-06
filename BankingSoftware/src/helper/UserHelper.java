package helper;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import database.IBranchData;
import database.IUserData;
import database.structure.BankBranch;
import database.structure.BankEmployee;
import database.structure.CurrentUser;
import globalUtilities.CustomException;
import globalUtilities.GlobalCommonChecker;
import helper.enumFiles.ExceptionStatus;

public class UserHelper {

	private IUserData userDatabase;
	private IBranchData branchDatabase;

	static BankEmployee empDetails;

	public UserHelper() throws CustomException {
		try {
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

	public long getMyUserId() {
		return CurrentUser.getUserId();
	}
}
