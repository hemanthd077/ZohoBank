package helper;

import java.util.List;
import java.util.Map;

import database.BranchDatabase;
import database.UserDatabase;
import database.structureClasses.BranchDetails;
import database.structureClasses.UserDetails;
import handleError.CustomException;

public class UserHelper {
	
	static UserDatabase userDatabase = new UserDatabase();
	static BranchDatabase branchDatabase = new BranchDatabase();
	
	public int userLogin(UserDetails userDetails) throws CustomException {
		
		try {
			int loginResult = userDatabase.userLogin(userDetails);
			return loginResult;
		} catch (CustomException e) {
			throw new CustomException(e.getMessage(),e);
		}
	}
	
	public Map<Integer,BranchDetails> getBranchData()  throws CustomException{
		return branchDatabase.getBranchDetails(List.of("BRANCH_ID","IFSC","CITY","ADDRESS"));
	}
	
	
}
