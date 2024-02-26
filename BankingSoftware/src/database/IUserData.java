package database;

import java.util.List;

import database.structureClasses.UserDetails;
import handleError.CustomException;

public interface IUserData {
	
	public int userLogin(UserDetails userDetails) throws CustomException;
	
	public boolean validatePassword(UserDetails userDetails) throws CustomException;
	
	public boolean createBankUserOrEmployee(List<? extends UserDetails> userDetails, boolean isEmployee) throws CustomException;
}
