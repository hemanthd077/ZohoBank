package helper;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.IAccountData;
import database.ICustomerData;
import database.structure.BankAccount;
import database.structure.BankCustomer;
import database.structure.BankTransaction;
import globalUtilities.CustomException;
import globalUtilities.GlobalChecker;
import helper.enumFiles.ExceptionStatus;
import helper.enumFiles.PaymentType;
import helper.enumFiles.StatusType;

public class CustomerHelper {

	private static ICustomerData customerDatabase;
	private static IAccountData bankAccountDatabase;
	private static UserHelper userHelper;

	public CustomerHelper() throws CustomException {
		try {
			Class<?> bankCustomerDao = Class.forName("database.CustomerDatabase");
			customerDatabase = (ICustomerData) bankCustomerDao.getDeclaredConstructor().newInstance();

			Class<?> bankAccountDao = Class.forName("database.AccountDatabase");
			bankAccountDatabase = (IAccountData) bankAccountDao.getDeclaredConstructor().newInstance();
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
		GlobalChecker.checkNull(bankAccountDetails);
		GlobalChecker.checkNull(password);

		userHelper.validatePassword(password);

		BankAccount accStatus = bankAccountDatabase.getAccountData(bankAccountDetails.getAccountNo(),
				StatusType.ACTIVE.getCode());

		return accStatus.getBalance();
	}

	public int withdrawTransaction(BankTransaction transactionDetails, BankAccount senderBankAccountDetails,
			String password) throws CustomException {

		GlobalChecker.checkNull(transactionDetails);

		transactionDetails.setPaymentType(PaymentType.WITHDRAWAL.getCode());
		return moneyTransaction(transactionDetails, senderBankAccountDetails, password);
	}

	public int depositTransaction(BankTransaction transactionDetails, BankAccount senderBankAccountDetails,
			String password) throws CustomException {
		GlobalChecker.checkNull(transactionDetails);

		transactionDetails.setPaymentType(PaymentType.DEPOSIT.getCode());

		return moneyTransaction(transactionDetails, senderBankAccountDetails, password);
	}

	public int moneyTransaction(BankTransaction transactionDetails, BankAccount refAccount, String password)
			throws CustomException {
		transactInputNullCheck(transactionDetails, refAccount, password);

		userHelper.validatePassword(password);

		String transactionId = GlobalChecker.generateTransactionId();
		int paymentType = transactionDetails.getPaymentType();

		BankAccount senderAccData = bankAccountDatabase.getAccountData(refAccount.getAccountNo(),
				StatusType.ACTIVE.getCode());

		double amount = transactionDetails.getAmount();
		long senderAccNo = senderAccData.getAccountNo();
		double balance = senderAccData.getBalance();

		Map<String, Object> updateFieldMap = new HashMap<>();
		senderExceptionValidate(senderAccData, amount, balance, paymentType, transactionId);
		if (paymentType == PaymentType.DEPOSIT.getCode() || paymentType == PaymentType.WITHDRAWAL.getCode()) {
			updateFieldMap.put("BALANCE",
					paymentType == PaymentType.DEPOSIT.getCode() ? balance + amount : balance - amount);

			boolean senderUpdateResult = bankAccountDatabase.updateAccount(senderAccNo, senderAccData.getUserId(),
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
				bankAccountDatabase.updateAccount(senderAccNo, senderAccData.getUserId(), updateFieldMap);
				return 0;
			}

		} else {
			BankAccount receiverAccData = bankAccountDatabase
					.getAccountData(transactionDetails.getTransactorAccountNumber(), StatusType.ACTIVE.getCode());

			try {
				long receiverAccNo = receiverAccData.getAccountNo();
				double rBalance = receiverAccData.getBalance();
				if (senderAccNo == receiverAccNo) {
					throw new CustomException(ExceptionStatus.INVALIDINPUT.getStatus());
				}

				updateFieldMap.put("BALANCE", balance - amount);
				boolean senderUpdateResult = bankAccountDatabase.updateAccount(senderAccNo, senderAccData.getUserId(),
						updateFieldMap);
				if (senderUpdateResult) {
					transactionDetails.setPaymentType(PaymentType.DEBIT.getCode());
					boolean senderTransactionDetails = transactSetterAndStore(transactionId, senderAccData,
							transactionDetails, StatusType.ACTIVE.getCode());
					if (senderTransactionDetails) {
						updateFieldMap.put("BALANCE", rBalance + amount);
						bankAccountDatabase.updateAccount(receiverAccNo, receiverAccData.getUserId(), updateFieldMap);

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
					bankAccountDatabase.updateAccount(senderAccNo, senderAccData.getUserId(), updateFieldMap);
					return 0;
				}
			} catch (NullPointerException e) {
				throw new CustomException(ExceptionStatus.INVALIDACCOUNT.getStatus());
			}
		}
	}

	private static void transactInputNullCheck(Object transactionDetails, Object senderBankAccountDetails,
			Object userDetails) throws CustomException {
		GlobalChecker.checkNull(transactionDetails);
		GlobalChecker.checkNull(senderBankAccountDetails);
		GlobalChecker.checkNull(userDetails);
	}

	private static boolean transactSetterAndStore(String transactionId, BankAccount accStatus,
			BankTransaction bankTransaction, int status) throws CustomException {

		GlobalChecker.checkNull(accStatus);
		GlobalChecker.checkNull(transactionId);

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
		transactionDetails.setTransactionTimestamp(GlobalChecker.getCurrentTimeMills());
		transactionDetails.setUserId(accStatus.getUserId());
		transactionDetails.setAccountNumber(accStatus.getAccountNo());
		transactionDetails.setAmount(amount);
		transactionDetails.setPaymentType(type);
		transactionDetails.setCurrentBalance(currentAmount);
		transactionDetails.setTransactorAccountNumber(bankTransaction.getTransactorAccountNumber());
		transactionDetails.setDescription(bankTransaction.getDecription());
		transactionDetails.setStatus(status);

		return bankAccountDatabase.storeTransaction(transactionDetails);
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
		long currentTimeMillis = GlobalChecker.getCurrentTimeMills();
		Long timeStamp = currentTimeMillis - GlobalChecker.calculateNDayMills(days);
		return bankAccountDatabase.getTransactDetailsWithinPeriod(accountNo, timeStamp,
				GlobalChecker.getCurrentTimeMills());
	}

	public List<BankTransaction> getTransactionWithInPeriod(long accountNo, String date1, String date2)
			throws CustomException {
		GlobalChecker.checkNull(date1);
		GlobalChecker.checkNull(date2);

		long dateMilles1 = GlobalChecker.convertDateTimeToMillis(date1);
		long dateMilles2 = GlobalChecker.convertDateTimeToMillis(date2) + GlobalChecker.calculateNDayMills(1);
		if (dateMilles1 > dateMilles2) {
			throw new CustomException(ExceptionStatus.INVALIDINPUT.getStatus());
		}
		return bankAccountDatabase.getTransactDetailsWithinPeriod(accountNo, dateMilles1, dateMilles2);

	}
}
