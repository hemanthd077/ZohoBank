package helper;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import database.IBranchData;
import database.IUserData;
import database.UserDatabase;
import database.structureClasses.BankBranch;
import database.structureClasses.BankEmployee;
import database.structureClasses.BankUser;
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

	public int userLogin(BankUser userDetails) throws CustomException {

		try {
			GlobalChecker.checkNull(userDetails);

			userDetails.setPassword(GlobalChecker.hashPassword(userDetails.getPassword()));
			Map<String, Integer> loginResult = userDatabase.userLogin(userDetails);
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
		return branchDatabase.getBranchDetails(List.of("BRANCH_ID", "IFSC", "CITY", "ADDRESS"));
	}

	public boolean validatePassword(BankUser userDetails) throws CustomException {
		userDetails.setPassword(GlobalChecker.hashPassword(userDetails.getPassword()));
		if (userDatabase.validatePassword(userDetails)) {
			return true;
		}
		throw new CustomException(ExceptionStatus.INVALIDPASSWORD.getStatus());
	}

	public int getMyUserId() {
		return UserDatabase.getUserId();
	}
}
