package runner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import database.ConnectionCreation;
import database.structureClasses.BankAccountDetails;
import database.structureClasses.BankCustomerDetails;
import database.structureClasses.BranchDetails;
import database.structureClasses.EmployeeDetails;
import database.structureClasses.UserDetails;
import globalUtilities.GlobalChecker;
import handleError.CustomException;
import helper.BankCommonHelper;
import helper.CustomerHelper;
import helper.EmployeeHelper;
import helper.UserHelper;

public class BankRunner {
	static Logger logger = Logger.getGlobal();
	
	static BankCommonHelper bankHelper = new BankCommonHelper();
	static UserHelper userHelper = new UserHelper();
	static CustomerHelper customerHelper = new CustomerHelper();
	static EmployeeHelper employeeHelper = new EmployeeHelper();
	static Scanner scanner = new Scanner(System.in);
	
	public static void main(String... args) {
		
		try {
			GlobalChecker.loggerHandler();
		} catch (CustomException e) {
			e.printStackTrace();
		}
		
		logger.setLevel(Level.ALL);
		
		int choice = 0;
		do {
			try {
				mainPage();
				choice = scanner.nextInt();
				scanner.nextLine();
				switch(choice) {
					case 1:{
						logger.log(Level.INFO,"----- Login Here -----");
						UserDetails userLoginDetails = new UserDetails();
						logger.log(Level.INFO,"Enter the Phone number to Login");
						
//						Admin login Credential
//						userLoginDetails.setPhonenumber("9876543210");
//						logger.log(Level.INFO,"Enter the Password");
//						userLoginDetails.setPassword("12345");
						
//						customer Login 1
						userLoginDetails.setPhonenumber("8901234567");
						logger.log(Level.INFO,"Enter the Password");
						userLoginDetails.setPassword("54321");
						
//						customer Login 2
//						userLoginDetails.setPhonenumber("9123456789");
//						logger.log(Level.INFO,"Enter the Password");
//						userLoginDetails.setPassword("54321");
						
//						Employee Login 1
//						userLoginDetails.setPhonenumber("9087654321");
//						logger.log(Level.INFO,"Enter the Password");
//						userLoginDetails.setPassword("11111");
						
						int result =userHelper.userLogin(userLoginDetails); 
						if(result == 3) {
							new AdminRunner();
						}
						else if(result == 2) {
							new EmployeeRunner();
						}
						else if(result ==1) {
							new CustomerRunner();
						}
						else {
							logger.log(Level.WARNING,"Login Failed");
						}
						break;
					}
				}
			}
			catch(InputMismatchException e) {
				logger.log(Level.SEVERE,"Error in input Type",e);
				scanner.nextLine();
			}
			catch(CustomException e) {
				e.printStackTrace();
				logger.log(Level.SEVERE,"Error occured : ",e.getMessage());
			}
		}
		while(choice!=0);
		logger.log(Level.FINE,"!!! Application Closed Successfully !!!");
		scanner.close();
		ConnectionCreation.closeConnection();
	}
	
	static void avaliableBranch(Map<Integer,BranchDetails> mapBranchDetails) throws CustomException {
		
		for (Map.Entry<Integer, BranchDetails> entry : mapBranchDetails.entrySet()) {
		    Integer branchId = entry.getKey();
		    BranchDetails branchDetails = entry.getValue();
		    logger.log(Level.FINEST," "+branchId + " : " + branchDetails.getCity()
		    					+" , Address : "+branchDetails.getAddress());
		}
	}
	
	static void avaliableUser(Map<Integer,BankCustomerDetails> mapUserDetails) throws CustomException {
		
		logger.log(Level.FINEST,"Total no of Users :"+mapUserDetails.size());
		for (Map.Entry<Integer, BankCustomerDetails> entry : mapUserDetails.entrySet()) {
		    Integer userId = entry.getKey();
		    BankCustomerDetails branchDetails = entry.getValue();
		    logger.log(Level.FINEST," "+userId + " : " + branchDetails.getName()
		    					+" , Address,  : "+branchDetails.getAddress());
		}
	}
	
	static void availableAccount(Map<Integer,BankAccountDetails> mapAccountDetails) throws CustomException{
		
		logger.log(Level.FINEST,"Total no of Accounts : "+mapAccountDetails.size());
		for (Map.Entry<Integer, BankAccountDetails> entry : mapAccountDetails.entrySet()) {
			int id = entry.getKey();
		    BankAccountDetails branchAccountDetails = entry.getValue();
		    logger.log(Level.FINEST,"Id : "+id
    					+",	Account Id : "+branchAccountDetails.getAccountNo() 
    					+", Balance : " + branchAccountDetails.getBalance()
    					+", User Id : "+branchAccountDetails.getUserDetails().getUserId()
    					+", IFSC : "+branchAccountDetails.getBranchDetails().getIfsc()
    					+", City : "+branchAccountDetails.getBranchDetails().getCity()
    					+", State : "+branchAccountDetails.getBranchDetails().getState()
    					+", Address : "+branchAccountDetails.getBranchDetails().getAddress()+"\n");
		}
	}
	
	static void mainPage() {
		logger.log(Level.INFO,"\nWelcome to Zoho Bank ");
		logger.log(Level.INFO,"1. Login");
		logger.log(Level.INFO,"0. Close the Application");
	}
	
	static String convertMillsToDateTime(Long currentTimeMillis) {
		 Date date = new Date(currentTimeMillis);
		 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	     return dateFormat.format(date);
	}
	
	protected void employeeDetails(EmployeeDetails employeeDetails) {
        logger.log(Level.FINEST, "**************************************************"
        		+ "\nEmployee Details :"
        		+ "\nEmail : "+employeeDetails.getEmail()
        		+ "\nPhone Number : "+ employeeDetails.getPhonenumber()
        		+ "\nName : "+ employeeDetails.getName()
        		+ "\nDate of Birth : "+employeeDetails.getDateOfBirth()
        		+ "\nGender : "+employeeDetails.getGender()
        		+ "\nAddress : "+employeeDetails.getAddress()
        		+ "\nBranch Details:"
        		+ "\nCity : "+ employeeDetails.getBranchDetails().getCity()
        		+ "\nState : "+employeeDetails.getBranchDetails().getState()
        		+ "\nISFC : "+employeeDetails.getBranchDetails().getIfsc()
        		+ "\nBranch Address : "+employeeDetails.getBranchDetails().getAddress()
        		+ "\n****************************************************");
    }
	
	protected static boolean isValidPassword(String password) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{" + 8 + ",}$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(password).matches();
    }
}
