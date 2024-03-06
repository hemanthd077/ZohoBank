package helper;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import database.IBranchData;
import database.IUserData;
import database.UserDatabase;
import database.structure.BankBranch;
import database.structure.BankEmployee;
import globalUtilities.CustomException;
import globalUtilities.GlobalChecker;
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
			throw new CustomException("Error Occured : Some Files Not Found ", e);
		}

	}

	public int userLogin(String phoneNo, String password) throws CustomException {

		try {
			GlobalChecker.checkNull(phoneNo);
			GlobalChecker.checkNull(password);

			password = GlobalChecker.hashPassword(password);
			Map<String, Integer> loginResult = userDatabase.userLogin(phoneNo, password);
			if (loginResult.size() == 0) {
				throw new CustomException("No Account Found");
			}

			if (loginResult.get("STATUS") == 0) {
				return 0;
			}
			int userType = loginResult.get("USER_TYPE");
			int access = loginResult.get("ACCESS");
			return (userType == 1) ? 1 : ((userType == 2 && access == 1) ? 3 : 2);
		} catch (CustomException e) {
			throw new CustomException(e.getMessage(), e);
		}
	}

	public Map<Integer, BankBranch> getBranchData() throws CustomException {
		return branchDatabase.getBranchDetails();
	}

	public boolean validatePassword(String password) throws CustomException {
		password = GlobalChecker.hashPassword(password);
		if (userDatabase.validatePassword(password)) {
			return true;
		}
		throw new CustomException(ExceptionStatus.INVALIDPASSWORD.getStatus());
	}

	public int getMyUserId() {
		return UserDatabase.getUserId();
	}
}
