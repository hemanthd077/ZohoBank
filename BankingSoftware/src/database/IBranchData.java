package database;

import java.util.Map;

import database.structure.BankBranch;
import globalUtilities.CustomException;

public interface IBranchData {

	public Map<Integer, BankBranch> getBranchDetails() throws CustomException;

}
