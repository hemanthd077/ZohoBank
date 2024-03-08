package database;

import java.util.List;

import database.structure.BankAccount;
import database.structure.BankTransaction;
import globalutil.CustomException;

public interface ITransactionData {

	boolean withdrawOrDepositTransaction(BankAccount bankAccount, BankTransaction bankTransaction)
			throws CustomException;

	boolean netBankingTransactionSameBank(BankAccount bankAccount1, BankAccount bankAccount2,
			BankTransaction bankTransaction, BankTransaction bankTransaction2) throws CustomException;

	List<BankTransaction> getTransactDetailsWithinPeriod(long accountNo, long startDate, long endDate)
			throws CustomException;
}
