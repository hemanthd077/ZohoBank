package runner;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import database.structure.BankAccount;
import database.structure.BankBranch;
import database.structure.BankCustomer;
import database.structure.BankEmployee;
import database.structure.BankTransaction;
import database.structure.BankUser;
import database.structure.CurrentUser;
import globalUtilities.CustomException;
import globalUtilities.DateTimeUtils;
import globalUtilities.GlobalCommonChecker;
import globalUtilities.InputValidationUtil;
import helper.UserHelper;

public class BankRunner {
	static Logger logger = Logger.getGlobal();
	static Scanner scanner = new Scanner(System.in);

	public static void main(String... args) {
		UserHelper userHelper = null;
		logger.setLevel(Level.FINEST);
		try {
			userHelper = new UserHelper();
		} catch (CustomException e) {
			logger.log(Level.FINEST, e.getMessage(), e);
			return;
		}

		try {
			GlobalCommonChecker.loggerHandler();
		} catch (CustomException e) {
			e.printStackTrace();
		}

		int choice = -1;
		do {
			try {
				mainPage();
				choice = scanner.nextInt();
				scanner.nextLine();
				switch (choice) {
				case 1: {
					logger.log(Level.INFO, "----- Login Here -----");
					BankUser userLoginDetails = new BankUser();
					logger.log(Level.INFO, "Enter the User ID  to Login");

//					long userId = scanner.nextLong();
//					scanner.nextLine();
//					userLoginDetails.setUserId(userId);
//					logger.log(Level.INFO, "Enter the Password");
//					String password = scanner.nextLine();
//					userLoginDetails.setPassword(password);

//					Admin login Credential //Hemanth
//					int userId = 1;
//					userLoginDetails.setUserId(userId);
//					logger.log(Level.INFO, "Enter the Password");
//					String password = "Hem@12345";
//					userLoginDetails.setPassword(password);
//						
//					customer Login 1 //Madhavan
					long userId = 2;
					userLoginDetails.setUserId(userId);
					logger.log(Level.INFO,"Enter the Password");
					String password = "Madhavan@12345";
					userLoginDetails.setPassword(password);

//					customer Login 2 //Joshi
//					long userId = 3;
//					userLoginDetails.setUserId(userId);
//					logger.log(Level.INFO, "Enter the Password");
//					String password = "Joshi@12345";
//					userLoginDetails.setPassword(password);

//					Employee Login 1 //Surya
//					long userId = 4;
//					userLoginDetails.setUserId(userId);
//					logger.log(Level.INFO, "Enter the Password");
//					String password = "Surya@12345";
//					userLoginDetails.setPassword(password);

//					Customer Login 2 //Bharath
//					long userId = 5;
//					userLoginDetails.setUserId(userId);
//					logger.log(Level.INFO,"Enter the Password");
//					String password = "Bharath@12345";
//					userLoginDetails.setPassword(password);

					int result = userHelper.userLogin(userLoginDetails.getUserId(), userLoginDetails.getPassword());
					switch (result) {
					case 1: {
						new CustomerRunner().CustomerRunnerTask();
						break;
					}
					case 2: {
						new EmployeeRunner().EmployeeRunnerTask();
						break;
					}
					case 3: {
						new AdminRunner().AdminRunnerTask();
						break;
					}
					default:
						logger.log(Level.WARNING, "Login Failed");
					}
					break;
				}
				}

				CurrentUser.clearUserData();
			} catch (InputMismatchException e) {
				logger.log(Level.SEVERE, "Error in input Type");
				scanner.nextLine();
			} catch (CustomException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		} while (choice != 0);
		logger.log(Level.FINE, "!!! Application Closed Successfully !!!");
		scanner.close();
	}

	static void avaliableBranch(Map<Integer, BankBranch> mapBranchDetails) throws CustomException {

		for (Map.Entry<Integer, BankBranch> entry : mapBranchDetails.entrySet()) {
			Integer branchId = entry.getKey();
			BankBranch branchDetails = entry.getValue();
			logger.log(Level.FINEST,
					" " + branchId + " : " + branchDetails.getCity() + " , Address : " + branchDetails.getAddress());
		}
	}

	static void avaliableUser(Map<Long, BankCustomer> mapUserDetails, int pageCount) throws CustomException {

		logger.log(Level.FINEST, "Page :" + pageCount);
		for (Map.Entry<Long, BankCustomer> entry : mapUserDetails.entrySet()) {
			long userId = entry.getKey();
			BankCustomer branchDetails = entry.getValue();
			logger.log(Level.FINEST,
					" " + userId + " : " + branchDetails.getName() + " , Address,  : " + branchDetails.getAddress());
		}
	}

	static void availableAccount(Map<Long, BankAccount> mapAccountDetails) throws CustomException {

		logger.log(Level.FINEST, "Total no of Accounts : " + mapAccountDetails.size());
		for (Map.Entry<Long, BankAccount> allBranchentry : mapAccountDetails.entrySet()) {
			long id = allBranchentry.getKey();
			BankAccount bankAccount = allBranchentry.getValue();
			BankBranch bankBranch = bankAccount.getBankBranch();
			int accountType = bankAccount.getAccountType().getCode();
			String accType = null;
			if (accountType == 1) {
				accType = "Saving";
			} else if (accountType == 2) {
				accType = "Salary";
			} else {
				accType = "Current";
			}

			logger.log(Level.FINEST,
					"Id : " + id + ",  Account No : " + bankAccount.getAccountNo() + ", IFSC : " + bankBranch.getIfsc()
							+ ", City : " + bankBranch.getCity() + ", State : " + bankBranch.getState() + ", Address : "
							+ bankBranch.getAddress() + ",	AccountType : " + accType + "\n");
		}
	}

	static void mainPage() {
		logger.log(Level.FINER, "\n Welcome to Zoho Of Bank ZOB ");
		logger.log(Level.INFO, "1. Login");
		logger.log(Level.INFO, "0. Close the Application");
	}

	protected void employeeDetails(Map<Long, BankEmployee> employeeDetails) {

		for (Map.Entry<Long, BankEmployee> allEmployeeEntry : employeeDetails.entrySet()) {
			BankEmployee bankEmployee = allEmployeeEntry.getValue();
			long date = bankEmployee.getDateOfBirth();

			logger.log(Level.FINEST, "Email : " + bankEmployee.getEmail() + "\nPhone Number : "
					+ bankEmployee.getPhoneNumber() + "\nName : " + bankEmployee.getName() + "\nDate of Birth : "
					+ (date != 0 ? DateTimeUtils.convertMillsToDate(bankEmployee.getDateOfBirth()) : "") + "\nGender : "
					+ bankEmployee.getGender() + "\nAddress : " + bankEmployee.getAddress() + "\nBranch Details:"
					+ "\nCity : " + bankEmployee.getBankBranch().getCity() + "\nState : "
					+ bankEmployee.getBankBranch().getState() + "\nISFC : " + bankEmployee.getBankBranch().getIfsc()
					+ "\nBranch Address : " + bankEmployee.getBankBranch().getAddress());
		}
	}

	public static void validPassword(String password) throws CustomException {
		try {
			if (!InputValidationUtil.isValidPassword(password)) {
				throw new CustomException("\nEnter the Valid Password");
			}
		} catch (CustomException e) {
			logger.log(Level.WARNING, "\nEnter the Valid Password\n");
			throw new CustomException(e.getMessage());
		}
	}

	public static void validEmail(String email) throws CustomException {
		try {
			if (!InputValidationUtil.isValidEmail(email)) {
				throw new CustomException("\nEnter the Valid Email");
			}
		} catch (CustomException e) {
			throw new CustomException(e.getMessage());
		}
	}

	public static void validPhoneNumber(String phoneNumber) throws CustomException {
		try {
			if (!InputValidationUtil.isValidIndianPhoneNumber(phoneNumber)) {
				throw new CustomException("Enter the Valid Phone number");
			}
		} catch (CustomException e) {
			throw new CustomException(e.getMessage(), e);
		}
	}

	public static <K, V> void logEmployeeDetails(Map<Long, BankEmployee> mapAccountDetails, int pageCount) {

		logger.log(Level.FINEST, "Page : " + pageCount);
		for (Map.Entry<Long, BankEmployee> entry : mapAccountDetails.entrySet()) {
			long id = entry.getKey();
			BankEmployee employeeDetails = entry.getValue();
			logger.log(Level.FINEST,
					"\nEmployee no: " + id + "	Email: " + employeeDetails.getEmail() + "	Phone Number: "
							+ employeeDetails.getPhoneNumber() + "  Name: " + employeeDetails.getName() + "	Gender: "
							+ employeeDetails.getGender() + "	Address: " + employeeDetails.getAddress()
							+ "	Branch ID: " + employeeDetails.getBankBranch().getBranchId());
			int access = employeeDetails.getEmployeeAccess().getCode();
			if (access == 1) {
				logger.log(Level.FINEST, "Role: Admin");
			} else {
				logger.log(Level.FINEST, "Role: Employee");
			}

		}
	}

	protected static void logTransactionHistory(List<BankTransaction> transactionHistory) {

		int size = transactionHistory.size();

		for (int i = 0; i < size; i++) {
			logger.log(Level.FINEST,
					"\nTransaction ID: " + transactionHistory.get(i).getTransactionId() + "\nTransaction Timestamp: "
							+ DateTimeUtils.convertMillsToDateTime(transactionHistory.get(i).getTransactionTimestamp())
							+ "\nAccount Number: " + transactionHistory.get(i).getAccountNumber() + "\nAmount: "
							+ transactionHistory.get(i).getAmount());

			int type = transactionHistory.get(i).getPaymentType();

			if (type == 0) {
				logger.log(Level.FINEST, "Type: Debit" + "\nTransactor Account Number: "
						+ transactionHistory.get(i).getTransactorAccountNumber());
			} else if (type == 1) {
				logger.log(Level.FINEST, "Type: Credit" + "\nTransactor Account Number: "
						+ transactionHistory.get(i).getTransactorAccountNumber());
			} else if (type == 2) {
				logger.log(Level.FINEST, "Type: Withdraw");
			} else {
				logger.log(Level.FINEST, "Type: Deposit");
			}

			logger.log(Level.FINEST, "Current Balance: " + transactionHistory.get(i).getCurrentBalance());

			if (transactionHistory.get(i).getStatus().getCode() == 1) {
				logger.log(Level.FINEST, "Status: Success");
			} else {
				logger.log(Level.FINEST, "Status: Failed");
			}
			if (transactionHistory.get(i).getDecription() != null) {
				logger.log(Level.FINEST, "Description : " + transactionHistory.get(i).getDecription());
			}
		}
	}

}
