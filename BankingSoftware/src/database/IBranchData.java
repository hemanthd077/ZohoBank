package database;

import java.util.Map;

import database.structure.BankBranch;
import globalutil.CustomException;

public interface IBranchData {

	Map<Integer, BankBranch> getBranchDetails() throws CustomException;

}
