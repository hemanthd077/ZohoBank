package database;

import java.util.List;

import database.structureClasses.BankUser;
import handleError.CustomException;

public interface IUserData {
	
	public int userLogin(BankUser userDetails) throws CustomException;
	
	public boolean validatePassword(BankUser userDetails) throws CustomException;
	
	public boolean createBankUserOrEmployee(List<? extends BankUser> userDetails, boolean isEmployee) throws CustomException;
}
