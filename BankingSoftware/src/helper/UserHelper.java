package helper;

import java.util.List;
import java.util.Map;

import database.BankBranchDatabase;
import database.BankUserDatabase;
import database.structureClasses.BankBranch;
import database.structureClasses.BankUser;
import globalUtilities.GlobalChecker;
import handleError.CustomException;

public class UserHelper {
	
	static BankUserDatabase userDatabase = new BankUserDatabase();
	static BankBranchDatabase branchDatabase = new BankBranchDatabase();
	
	public int userLogin(BankUser userDetails) throws CustomException {
		
		try {
			GlobalChecker.checkNull(userDetails);
			userDetails.setPassword(GlobalChecker.hashPassword(userDetails.getPassword()));
			return userDatabase.userLogin(userDetails);
		} catch (CustomException e) {
			throw new CustomException(e.getMessage(),e);
		}
	}
	
	public Map<Integer,BankBranch> getBranchData()  throws CustomException{
		return branchDatabase.getBranchDetails(List.of("BRANCH_ID","IFSC","CITY","ADDRESS"));
	}
	
	public boolean validatePassword(BankUser userDetails) throws CustomException {
		userDetails.setPassword(GlobalChecker.hashPassword(userDetails.getPassword()));
		if(userDatabase.validatePassword(userDetails)) {
			return true;
		}
		return false;
	}
	
	
}
