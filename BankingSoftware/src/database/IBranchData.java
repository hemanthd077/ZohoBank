package database;

import java.util.List;
import java.util.Map;

import database.structureClasses.BankBranch;
import globalUtilities.CustomException;

public interface IBranchData {

	public Map<Integer, BankBranch> getBranchDetails(List<String> fieldList) throws CustomException;
}
