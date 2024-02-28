package runner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import database.ConnectionCreation;
import database.structureClasses.BankAccount;
import database.structureClasses.BankCustomer;
import database.structureClasses.BankBranch;
import database.structureClasses.BankEmployee;
import database.structureClasses.BankUser;
import globalUtilities.GlobalChecker;
import handleError.CustomException;
import helper.UserHelper;

public class BankRunner {
	static Logger logger = Logger.getGlobal();
	static Scanner scanner = new Scanner(System.in);

	public static void main(String... args) {

		UserHelper userHelper = null;
		try {
			userHelper = new UserHelper();
		} catch (CustomException e) {
			logger.log(Level.FINEST, e.getMessage(), e);
			return;
		}

		try {
			GlobalChecker.loggerHandler();
		} catch (CustomException e) {
			e.printStackTrace();
		}

		logger.setLevel(Level.ALL);

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
					logger.log(Level.INFO, "Enter the Phone number to Login");

						String phoneNo = scanner.nextLine();
						validPhoneNumber(phoneNo);
						userLoginDetails.setPhonenumber(phoneNo);
						logger.log(Level.INFO,"Enter the Password");
						String password = scanner.nextLine();
						validPassword(password);
						userLoginDetails.setPassword(password);	

//						Admin login Credential //Hemanth
//					String phoneNo = "9876543210";
//					validPhoneNumber(phoneNo);
//					userLoginDetails.setPhonenumber(phoneNo);
//					logger.log(Level.INFO, "Enter the Password");
//					String password = "Hem@12345";
//					validPassword(password);
//					userLoginDetails.setPassword(password);
//						
//						customer Login 1 //Madhavan
//						String phoneNo = "8901234567";
//						validPhoneNumber(phoneNo);
//						userLoginDetails.setPhonenumber(phoneNo);
//						logger.log(Level.INFO,"Enter the Password");
//						String password = "Madhavan@12345";
//						validPassword(password);
//						userLoginDetails.setPassword(password);

//						customer Login 2 //Joshi
//						String phoneNo = "9123456789";
//						validPhoneNumber(phoneNo);
//						userLoginDetails.setPhonenumber(phoneNo);
//						logger.log(Level.INFO,"Enter the Password");
//						String password = "Joshi@12345";
//						validPassword(password);
//						userLoginDetails.setPassword(password);

//						Employee Login 1 //Surya
//						String phoneNo = "9087654321";
//						validPhoneNumber(phoneNo);
//						userLoginDetails.setPhonenumber(phoneNo);
//						logger.log(Level.INFO,"Enter the Password");
//						String password = "Surya@12345";
//						validPassword(password);
//						userLoginDetails.setPassword(password);

//						Customer Login 2 //Bharath
//						String phoneNo = "7654321098";
//						validPhoneNumber(phoneNo);
//						userLoginDetails.setPhonenumber(phoneNo);
//						logger.log(Level.INFO,"Enter the Password");
//						String password = "Bharath@12345";
//						validPassword(password);
//						userLoginDetails.setPassword(password);

					int result = userHelper.userLogin(userLoginDetails);
					switch (result) {
					case 1: {
						CustomerRunner customerRunner = new CustomerRunner();
						customerRunner.CustomerRunnerTask();
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
			} catch (InputMismatchException e) {
				logger.log(Level.SEVERE, "Error in input Type");
				scanner.nextLine();
			} catch (CustomException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		} while (choice != 0);
		logger.log(Level.FINE, "!!! Application Closed Successfully !!!");
		scanner.close();
		ConnectionCreation.closeConnection();
	}

	static void avaliableBranch(Map<Integer, BankBranch> mapBranchDetails) throws CustomException {

		for (Map.Entry<Integer, BankBranch> entry : mapBranchDetails.entrySet()) {
			Integer branchId = entry.getKey();
			BankBranch branchDetails = entry.getValue();
			logger.log(Level.FINEST,
					" " + branchId + " : " + branchDetails.getCity() + " , Address : " + branchDetails.getAddress());
		}
	}

	static void avaliableUser(Map<Integer, BankCustomer> mapUserDetails) throws CustomException {

		logger.log(Level.FINEST, "Total no of Users :" + mapUserDetails.size());
		for (Map.Entry<Integer, BankCustomer> entry : mapUserDetails.entrySet()) {
			Integer userId = entry.getKey();
			BankCustomer branchDetails = entry.getValue();
			logger.log(Level.FINEST,
					" " + userId + " : " + branchDetails.getName() + " , Address,  : " + branchDetails.getAddress());
		}
	}

	static void availableAccount(Map<Integer, BankAccount> mapAccountDetails) throws CustomException {

		logger.log(Level.FINEST, "Total no of Accounts : " + mapAccountDetails.size());
		for (Map.Entry<Integer, BankAccount> entry : mapAccountDetails.entrySet()) {
			int id = entry.getKey();
			BankAccount branchAccountDetails = entry.getValue();
			logger.log(Level.FINEST,
					"Id : " + id + ",	Account Id : " + branchAccountDetails.getAccountNo() + ", Balance : "
							+ branchAccountDetails.getBalance() + ", User Id : "
							+ branchAccountDetails.getUserDetails().getUserId() + ", IFSC : "
							+ branchAccountDetails.getBranchDetails().getIfsc() + ", City : "
							+ branchAccountDetails.getBranchDetails().getCity() + ", State : "
							+ branchAccountDetails.getBranchDetails().getState() + ", Address : "
							+ branchAccountDetails.getBranchDetails().getAddress() + "\n");
		}
	}

	static void mainPage() {
		logger.log(Level.FINER, "\nWelcome to Zoho Bank ");
		logger.log(Level.INFO, "1. Login");
		logger.log(Level.INFO, "0. Close the Application");
	}

	static String convertMillsToDateTime(Long currentTimeMillis) {
		Date date = new Date(currentTimeMillis);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}

	protected void employeeDetails(BankEmployee employeeDetails) {
		logger.log(Level.FINEST,
				"Email : " + employeeDetails.getEmail() + "\nPhone Number : " + employeeDetails.getPhonenumber()
						+ "\nName : " + employeeDetails.getName() + "\nDate of Birth : "
						+ employeeDetails.getDateOfBirth() + "\nGender : " + employeeDetails.getGender()
						+ "\nAddress : " + employeeDetails.getAddress() + "\nBranch Details:" + "\nCity : "
						+ employeeDetails.getBranchDetails().getCity() + "\nState : "
						+ employeeDetails.getBranchDetails().getState() + "\nISFC : "
						+ employeeDetails.getBranchDetails().getIfsc() + "\nBranch Address : "
						+ employeeDetails.getBranchDetails().getAddress());
	}

	protected static boolean isValidPassword(String password) {
		if (password == null) {
			return false;
		}
		String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{" + 8 + ",}$";
		Pattern pattern = Pattern.compile(regex);
		return pattern.matcher(password).matches();
	}

	public static boolean isValidEmail(String email) {
		if (email == null) {
			return false;
		}

		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
		Pattern pattern = Pattern.compile(emailRegex);
		Matcher matcher = pattern.matcher(email);

		return matcher.matches();
	}

	public static boolean isValidIndianPhoneNumber(String phoneNumber) {
		if (phoneNumber == null) {
			return false;
		}
		String indianPhoneNumberRegex = "^(\\+91[\\-\\s]?)?[0]?[6789]\\d{9}$";

		Pattern pattern = Pattern.compile(indianPhoneNumberRegex);
		Matcher matcher = pattern.matcher(phoneNumber);

		return matcher.matches();
	}

	public static void validPassword(String password) throws CustomException {
		try {
			if (!isValidPassword(password)) {
				throw new CustomException("\nEnter the Valid Password");
			}
		} catch (CustomException e) {
			logger.log(Level.WARNING,
					"\nEnter the Valid Password\n");
			throw new CustomException(e.getMessage());
		}
	}

	public static void validEmail(String email) throws CustomException {
		try {
			if (!isValidEmail(email)) {
				throw new CustomException("\nEnter the Valid Email");
			}
		} catch (CustomException e) {
			throw new CustomException(e.getMessage());
		}
	}

	public static void validPhoneNumber(String phoneNumber) throws CustomException {
		try {
			if (!isValidIndianPhoneNumber(phoneNumber)) {
				throw new CustomException("Enter the Valid Phone number");
			}
		} catch (CustomException e) {
			throw new CustomException(e.getMessage(), e);
		}
	}

	public static void logEmployeeDetails(Map<Integer, BankEmployee> mapAccountDetails) {

		logger.log(Level.FINEST, "Total no of Employee : " + mapAccountDetails.size());
		for (Map.Entry<Integer, BankEmployee> entry : mapAccountDetails.entrySet()) {
			int id = entry.getKey();
			BankEmployee employeeDetails = entry.getValue();
			logger.log(Level.FINEST,
					"\nEmployee no: " + id + "\nEmail: " + employeeDetails.getEmail() + "\nPhone Number: "
							+ employeeDetails.getPhonenumber() + "\nName: " + employeeDetails.getName() + "\nGender: "
							+ employeeDetails.getGender() + "\nAddress: " + employeeDetails.getAddress()
							+ "\nBranch ID: " + employeeDetails.getBranchDetails().getBranch_id());
			int access = employeeDetails.getEmployeeAccess();
			if (access == 1) {
				logger.log(Level.FINEST, "Role: Admin");
			} else {
				logger.log(Level.FINEST, "Role: Employee");
			}

		}
	}

}
