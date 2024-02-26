package database;

import java.util.List;
import java.util.Map;

import database.structureClasses.BranchDetails;
import handleError.CustomException;

public interface IBranchData {
	
	public Map<Integer,BranchDetails> getBranchDetails(List<String> fieldList)  throws CustomException;
}
