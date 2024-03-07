package database;

import database.structure.BankAccount;
import database.structure.BankTransaction;
import globalUtilities.CustomException;

public interface ITransactionData {

	int withdrawOrDepositTransaction(BankAccount bankAccount, BankTransaction bankTransaction) throws CustomException;

	int netBankingTransactionSameBank(BankAccount bankAccount1, BankAccount bankAccount2, BankTransaction bankTransaction,
			BankTransaction bankTransaction2) throws CustomException;
}
