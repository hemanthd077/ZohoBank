package helper;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.IAccountData;
import database.ICustomerData;
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
import helper.enumFiles.StatusType;

public class CustomerHelper {

	private static ICustomerData customerDatabase;
	private static IAccountData accountDatabase;
	private static IUserData userDatabase;
	private static UserHelper userHelper;

	public CustomerHelper() throws CustomException {
		try {
			Class<?> bankCustomerDao = Class.forName("database.CustomerDatabase");
			customerDatabase = (ICustomerData) bankCustomerDao.getDeclaredConstructor().newInstance();

			Class<?> bankAccountDao = Class.forName("database.AccountDatabase");
			accountDatabase = (IAccountData) bankAccountDao.getDeclaredConstructor().newInstance();

			Class<?> bankUserDao = Class.forName("database.UserDatabase");
			userDatabase = (IUserData) bankUserDao.getDeclaredConstructor().newInstance();

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
				StatusType.ACTIVE.getCode());

		return accStatus.getBalance();
	}

	public int withdrawTransaction(BankTransaction transactionDetails, BankAccount senderBankAccountDetails,
			String password) throws CustomException {

		GlobalCommonChecker.checkNull(transactionDetails);

		transactionDetails.setPaymentType(PaymentType.WITHDRAWAL.getCode());
		return moneyTransaction(transactionDetails, senderBankAccountDetails, password);
	}

	public int depositTransaction(BankTransaction transactionDetails, BankAccount senderBankAccountDetails,
			String password) throws CustomException {
		GlobalCommonChecker.checkNull(transactionDetails);

		transactionDetails.setPaymentType(PaymentType.DEPOSIT.getCode());

		return moneyTransaction(transactionDetails, senderBankAccountDetails, password);
	}

	public int moneyTransaction(BankTransaction transactionDetails, BankAccount refAccount, String password)
			throws CustomException {
		transactInputNullCheck(transactionDetails, refAccount, password);

		userHelper.validatePassword(password);
		double amount = transactionDetails.getAmount();

		if (amount < 0) {
			throw new CustomException(ExceptionStatus.INVALIDINPUT.getStatus());
		}

		String transactionId = GlobalCommonChecker.generateTransactionId();
		int paymentType = transactionDetails.getPaymentType();

		BankAccount senderAccData = accountDatabase.getAccountData(refAccount.getAccountNo(),
				StatusType.ACTIVE.getCode());

		long senderAccNo = senderAccData.getAccountNo();
		double balance = senderAccData.getBalance();

		Map<String, Object> updateFieldMap = new HashMap<>();
		senderExceptionValidate(senderAccData, amount, balance, paymentType, transactionId);
		if (paymentType == PaymentType.DEPOSIT.getCode() || paymentType == PaymentType.WITHDRAWAL.getCode()) {
			updateFieldMap.put("BALANCE",
					paymentType == PaymentType.DEPOSIT.getCode() ? balance + amount : balance - amount);

			boolean senderUpdateResult = accountDatabase.updateAccount(senderAccNo, senderAccData.getUserId(),
					updateFieldMap);
			if (senderUpdateResult) {
				boolean senderTransactionDetails = transactSetterAndStore(transactionId, senderAccData,
						transactionDetails, StatusType.ACTIVE.getCode());
				if (senderTransactionDetails) {
					return 1;
				}
				transactSetterAndStore(transactionId, senderAccData, transactionDetails, StatusType.INACTIVE.getCode());
				return 0;
			} else {
				updateFieldMap.put("BALANCE",
						paymentType == PaymentType.DEPOSIT.getCode() ? balance - amount : balance + amount);
				accountDatabase.updateAccount(senderAccNo, senderAccData.getUserId(), updateFieldMap);
				return 0;
			}

		} else {
			BankAccount receiverAccData = accountDatabase
					.getAccountData(transactionDetails.getTransactorAccountNumber(), StatusType.ACTIVE.getCode());

			try {
				long receiverAccNo = receiverAccData.getAccountNo();
				double rBalance = receiverAccData.getBalance();
				if (senderAccNo == receiverAccNo) {
					throw new CustomException(ExceptionStatus.INVALIDINPUT.getStatus());
				}

				updateFieldMap.put("BALANCE", balance - amount);
				boolean senderUpdateResult = accountDatabase.updateAccount(senderAccNo, senderAccData.getUserId(),
						updateFieldMap);
				if (senderUpdateResult) {
					transactionDetails.setPaymentType(PaymentType.DEBIT.getCode());
					boolean senderTransactionDetails = transactSetterAndStore(transactionId, senderAccData,
							transactionDetails, StatusType.ACTIVE.getCode());
					if (senderTransactionDetails) {
						updateFieldMap.put("BALANCE", rBalance + amount);
						accountDatabase.updateAccount(receiverAccNo, receiverAccData.getUserId(), updateFieldMap);

						transactionDetails.setPaymentType(PaymentType.CREDIT.getCode());
						transactSetterAndStore(transactionId, receiverAccData, transactionDetails,
								StatusType.ACTIVE.getCode());
						return 1;
					} else {
						transactSetterAndStore(transactionId, senderAccData, transactionDetails,
								StatusType.INACTIVE.getCode());
						return 0;
					}
				} else {
					updateFieldMap.put("BALANCE",
							paymentType == PaymentType.DEPOSIT.getCode() ? balance - amount : balance + amount);
					accountDatabase.updateAccount(senderAccNo, senderAccData.getUserId(), updateFieldMap);
					return 0;
				}
			} catch (NullPointerException e) {
				throw new CustomException(ExceptionStatus.INVALIDACCOUNT.getStatus());
			}
		}
	}

	private static void transactInputNullCheck(Object transactionDetails, Object senderBankAccountDetails,
			Object userDetails) throws CustomException {
		GlobalCommonChecker.checkNull(transactionDetails);
		GlobalCommonChecker.checkNull(senderBankAccountDetails);
		GlobalCommonChecker.checkNull(userDetails);
	}

	private static boolean transactSetterAndStore(String transactionId, BankAccount accStatus,
			BankTransaction bankTransaction, int status) throws CustomException {

		GlobalCommonChecker.checkNull(accStatus);
		GlobalCommonChecker.checkNull(transactionId);

		double amount = bankTransaction.getAmount();

		int type = bankTransaction.getPaymentType();
		double currentAmount;
		if (type == PaymentType.DEBIT.getCode() || type == PaymentType.WITHDRAWAL.getCode()) {
			currentAmount = accStatus.getBalance() - amount;
			amount = 0 - amount;
		} else {
			currentAmount = accStatus.getBalance() + amount;
		}
		BankTransaction transactionDetails = new BankTransaction();
		transactionDetails.setTransactionId(transactionId);
		transactionDetails.setTransactionTimestamp(DateTimeUtils.getCurrentTimeMills());
		transactionDetails.setUserId(accStatus.getUserId());
		transactionDetails.setAccountNumber(accStatus.getAccountNo());
		transactionDetails.setAmount(amount);
		transactionDetails.setPaymentType(type);
		transactionDetails.setCurrentBalance(currentAmount);
		transactionDetails.setTransactorAccountNumber(bankTransaction.getTransactorAccountNumber());
		transactionDetails.setDescription(bankTransaction.getDecription());
		transactionDetails.setStatus(status);

		return accountDatabase.storeTransaction(transactionDetails);
	}

	private static void senderExceptionValidate(BankAccount senderAcc, double amount, double balance, int paymentType,
			String transactionId) throws CustomException {
		if (amount <= 0) {
			throw new CustomException(ExceptionStatus.INVALIDINPUT.getStatus());
		}
		if (paymentType != PaymentType.DEPOSIT.getCode() && amount > balance) {
			throw new CustomException(ExceptionStatus.INSUFFICIENTBALENCE.getStatus());
		}
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
			throw new CustomException(ExceptionStatus.INVALIDPASSWORD.getStatus() + " Enter same Password");
		}
		updateMap.put("PASSWORD", GlobalCommonChecker.hashPassword(newPassword));
		return userDatabase.updateUser(tempUserId, updateMap);
	}
}
