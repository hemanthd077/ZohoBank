package helper;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import database.IBranchData;
import database.IUserData;
import database.structureClasses.BankBranch;
import database.structureClasses.BankEmployee;
import database.structureClasses.BankUser;
import globalUtilities.GlobalChecker;
import handleError.CustomException;
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
			return userDatabase.userLogin(userDetails);
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

}
