package helper;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.IAccountData;
import database.ICustomerData;
import database.ITransactionData;
import database.IUserData;
import database.structure.BankAccount;
import database.structure.BankCustomer;
import database.structure.BankTransaction;
import database.structure.CurrentUser;
import globalUtilities.CustomException;
import globalUtilities.DateTimeUtils;
import globalUtilities.GlobalCommonChecker;
import helper.enumFiles.ExceptionStatus;
import helper.enumFiles.PaymentType;
import helper.enumFiles.RecordStatus;

public class CustomerHelper {

	private static ICustomerData customerDatabase;
	private static IAccountData accountDatabase;
	private static IUserData userDatabase;
	private static ITransactionData transactionDatabase;
	private static UserHelper userHelper;

	public CustomerHelper() throws CustomException {
		try {
			Class<?> bankCustomerDao = Class.forName("database.CustomerDatabase");
			customerDatabase = (ICustomerData) bankCustomerDao.getDeclaredConstructor().newInstance();

			Class<?> bankAccountDao = Class.forName("database.AccountDatabase");
			accountDatabase = (IAccountData) bankAccountDao.getDeclaredConstructor().newInstance();

			Class<?> bankUserDao = Class.forName("database.UserDatabase");
			userDatabase = (IUserData) bankUserDao.getDeclaredConstructor().newInstance();

			Class<?> bankTransactionDao = Class.forName("database.TransactionDatabase");
			transactionDatabase = (ITransactionData) bankTransactionDao.getDeclaredConstructor().newInstance();

			userHelper = new UserHelper();

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new CustomException("Error Occured : Some Files Not Found ", e);
		}
	}

	public BankCustomer getCustomerData() throws CustomException {
		return customerDatabase.getCustomerData();
	}

	public double checkBalance(BankAccount bankAccountDetails, String password) throws CustomException {
		GlobalCommonChecker.checkNull(bankAccountDetails);
		GlobalCommonChecker.checkNull(password);

		userHelper.validatePassword(password);

		BankAccount accStatus = accountDatabase.getAccountData(bankAccountDetails.getAccountNo(),
				RecordStatus.ACTIVE.getCode());

		return accStatus.getBalance();
	}

	public int performTransaction(BankTransaction transactionDetails, long accountNo, String password,
			PaymentType paymentType) throws CustomException {
		GlobalCommonChecker.checkNull(transactionDetails);
		GlobalCommonChecker.checkNull(password);

		userHelper.validatePassword(password);

		double amount = transactionDetails.getAmount();
		if (amount < 0) {
			throw new CustomException(ExceptionStatus.INVALIDINPUT.getStatus());
		}

		BankAccount accountData = accountDatabase.getAccountData(accountNo, RecordStatus.ACTIVE.getCode());
		double balance = accountData.getBalance();

		if ((paymentType == PaymentType.WITHDRAWAL && balance - amount < 0)
				|| (paymentType == PaymentType.DEPOSIT && balance < 0)) {
			throw new CustomException(ExceptionStatus.INSUFFICIENTBALENCE.getStatus());
		}

		double updatedBalance = (paymentType == PaymentType.WITHDRAWAL) ? balance - amount : balance + amount;

		BankAccount bankAccount = new BankAccount();
		bankAccount.setAccountNo(accountNo);
		bankAccount.setBalance(updatedBalance);

		String transactionId = GlobalCommonChecker.generateTransactionId();
		transactionDetails.setTransactionId(transactionId);
		transactionDetails.setTransactionTimestamp(DateTimeUtils.getCurrentTimeMills());
		transactionDetails.setUserId(CurrentUser.getUserId());
		transactionDetails.setAccountNumber(accountNo);
		transactionDetails.setTransactorAccountNumber(0L);
		transactionDetails.setCurrentBalance(updatedBalance);
		transactionDetails.setAmount((paymentType == PaymentType.WITHDRAWAL) ? 0 - amount : amount);
		transactionDetails.setPaymentType(paymentType.getCode());

		return transactionDatabase.withdrawOrDepositTransaction(bankAccount, transactionDetails);
	}

	public int withdrawTransaction(BankTransaction transactionDetails, long accountNo, String password)
			throws CustomException {
		return performTransaction(transactionDetails, accountNo, password, PaymentType.WITHDRAWAL);
	}

	public int depositTransaction(BankTransaction transactionDetails, long accountNo, String password)
			throws CustomException {
		return performTransaction(transactionDetails, accountNo, password, PaymentType.DEPOSIT);
	}

	public int moneyTransactionSameBank(BankTransaction transactionDetails, String password) throws CustomException {

		GlobalCommonChecker.checkNull(transactionDetails);
		GlobalCommonChecker.checkNull(password);

		userHelper.validatePassword(password);

		double amount = transactionDetails.getAmount();
		if (amount < 0) {
			throw new CustomException(ExceptionStatus.INVALIDINPUT.getStatus());
		}

		String transactionId = GlobalCommonChecker.generateTransactionId();
		long senderAccNo = transactionDetails.getAccountNumber();
		BankAccount senderAccData = accountDatabase.getAccountData(senderAccNo, RecordStatus.ACTIVE.getCode());

		if (senderAccNo != 0) {
			long transactorAccNo = transactionDetails.getTransactorAccountNumber();
			BankAccount transactorAccData = accountDatabase.getAccountData(transactorAccNo,
					RecordStatus.ACTIVE.getCode());

			if (transactorAccNo != 0) {
				double senderBalance = senderAccData.getBalance();

				if (senderAccNo == transactorAccNo) {
					throw new CustomException(ExceptionStatus.INVALIDINPUT.getStatus());
				}
				if ((senderBalance - amount) < 0) {
					throw new CustomException(ExceptionStatus.INSUFFICIENTBALENCE.getStatus());
				}

				double transactorBalance = transactorAccData.getBalance();

				// Setting sender and transactor balances
				senderAccData.setBalance(senderBalance);
				transactorAccData.setBalance(transactorBalance);

				transactionDetails.setPaymentType(PaymentType.DEBIT.getCode());
				transactionDetails.setStatus(RecordStatus.ACTIVE.getCode());
				transactionDetails.setTransactorAccountNumber(transactorAccNo);
				BankTransaction bankTransaction1 = transactSetter(transactionId, senderAccData, transactionDetails);

				transactionDetails.setPaymentType(PaymentType.CREDIT.getCode());
				transactionDetails.setTransactorAccountNumber(senderAccNo);
				BankTransaction bankTransaction2 = transactSetter(transactionId, transactorAccData, transactionDetails);

				// Perform the net banking transaction
				return transactionDatabase.netBankingTransactionSameBank(senderAccData, transactorAccData,
						bankTransaction1, bankTransaction2);
			}
			throw new CustomException(ExceptionStatus.INVALIDACCOUNT.getStatus());
		}
		throw new CustomException(ExceptionStatus.INVALIDACCOUNT.getStatus());
	}

	private static BankTransaction transactSetter(String transactionId, BankAccount accData,
			BankTransaction bankTransaction) throws CustomException {

		GlobalCommonChecker.checkNull(transactionId);
		GlobalCommonChecker.checkNull(bankTransaction);
		GlobalCommonChecker.checkNull(transactionId);

		double amount = bankTransaction.getAmount();

		int type = bankTransaction.getPaymentType();
		double currentAmount = (type == PaymentType.DEBIT.getCode() || type == PaymentType.WITHDRAWAL.getCode())
				? accData.getBalance() - amount
				: accData.getBalance() + amount;

		BankTransaction transactionDetails = new BankTransaction();
		transactionDetails.setTransactionId(transactionId);
		transactionDetails.setTransactionTimestamp(DateTimeUtils.getCurrentTimeMills());
		transactionDetails.setUserId(accData.getUserId());
		transactionDetails.setAccountNumber(accData.getAccountNo());
		transactionDetails.setAmount(
				(type == PaymentType.DEBIT.getCode() || type == PaymentType.WITHDRAWAL.getCode()) ? 0 - amount
						: amount);
		transactionDetails.setPaymentType(type);
		transactionDetails.setCurrentBalance(currentAmount);
		transactionDetails.setTransactorAccountNumber(bankTransaction.getTransactorAccountNumber());
		transactionDetails.setDescription(bankTransaction.getDecription());
		transactionDetails.setStatus(bankTransaction.getStatus().getCode());

		return transactionDetails;
	}

	public List<BankTransaction> getNDayTransactionDetails(long accountNo, int days) throws CustomException {
		if (days < 1 || days > 365) {
			throw new CustomException("Invalid Input");
		}
		long currentTimeMillis = DateTimeUtils.getCurrentTimeMills();
		Long timeStamp = currentTimeMillis - DateTimeUtils.calculateNDayMills(days);
		return accountDatabase.getTransactDetailsWithinPeriod(accountNo, timeStamp,
				DateTimeUtils.getCurrentTimeMills());
	}

	public List<BankTransaction> getTransactionWithInPeriod(long accountNo, String date1, String date2)
			throws CustomException {
		GlobalCommonChecker.checkNull(date1);
		GlobalCommonChecker.checkNull(date2);

		long dateMilles1 = DateTimeUtils.convertDateTimeToMillis(date1);
		long dateMilles2 = DateTimeUtils.convertDateTimeToMillis(date2) + DateTimeUtils.calculateNDayMills(1);
		if (dateMilles1 > dateMilles2) {
			throw new CustomException(ExceptionStatus.INVALIDINPUT.getStatus());
		}
		return accountDatabase.getTransactDetailsWithinPeriod(accountNo, dateMilles1, dateMilles2);

	}

	public boolean changeUserPassword(String oldPassword, String newPassword) throws CustomException {
		Map<Object, Object> updateMap = new HashMap<>();
		long tempUserId = CurrentUser.getUserId();
		oldPassword = GlobalCommonChecker.hashPassword(oldPassword);
		if (userDatabase.userValidation(tempUserId, oldPassword) == 0) {
			throw new CustomException(ExceptionStatus.WRONGPASSWORD.getStatus());
		}

		if (oldPassword.endsWith(newPassword)) {
			throw new CustomException(ExceptionStatus.INVALIDPASSWORD.getStatus() + " Entered same Password");
		}
		updateMap.put("PASSWORD", GlobalCommonChecker.hashPassword(newPassword));
		return userDatabase.updateUser(tempUserId, updateMap);
	}
}
