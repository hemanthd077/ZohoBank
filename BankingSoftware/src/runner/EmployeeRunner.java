package runner;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import database.structureClasses.BankAccountDetails;
import database.structureClasses.BankCustomerDetails;
import handleError.CustomException;
import helper.EmployeeHelper;

public class EmployeeRunner extends BankRunner {
	
	static EmployeeHelper employeeHelper = new EmployeeHelper();
	
	public EmployeeRunner() throws CustomException {
		try {
			logger.log(Level.INFO,"result : Employee");
			logger.log(Level.FINE,"Welcome "+employeeHelper.getMyData().getName()+" to BankOfZoho");
			int employeeChoice;
			do {
				EmployeeChoicePage();
				employeeChoice = scanner.nextInt();
				scanner.nextLine();
				switch(employeeChoice) {
					case 1:{
						logger.log(Level.FINE,"\n*** Enter the Account Data to add user***");
						BankCustomerDetails bankCustomerDetails = new BankCustomerDetails();
						
						logger.log(Level.INFO,"Enter the Emailid :");
						bankCustomerDetails.setEmail("bharath@gmail.com");
						
						logger.log(Level.INFO,"Enter the Password :");
						bankCustomerDetails.setPassword("54321");
						
						logger.log(Level.INFO,"Enter the Phone Number : ");
						bankCustomerDetails.setPhonenumber("7654321098");
						
						logger.log(Level.INFO,"Enter the Name of the User : ");
						bankCustomerDetails.setName("Bharath");
						
						logger.log(Level.INFO,"Enter the Gender : ");
						bankCustomerDetails.setGender("Male");
						
						logger.log(Level.INFO,"Enter the Address : ");
						bankCustomerDetails.setAddress("near salem bus stand/salem");
						
						logger.log(Level.INFO,"Enter the Pan no : ");
						bankCustomerDetails.setPanNumber("ZSRGH9876L");
						
						logger.log(Level.INFO,"Enter the Aadhar no : ");
						bankCustomerDetails.setAadharNumber("762812345678");
					
						if(employeeHelper.adminCreateCustomer(List.of(bankCustomerDetails))) {
							logger.log(Level.FINEST,"Created successfully");
						}
						else {
							logger.log(Level.FINEST,"User already exist or creation failed");
						}
						break;
					}
					case 2:{
						logger.log(Level.FINE,"\n*** Creating an Bank Account ***");
						logger.log(Level.INFO,"Select the user to Create Bank Account");
						Map<Integer,BankCustomerDetails> allUserDetails = employeeHelper.getActiveUserDetails();;
						avaliableUser(allUserDetails);
						int userChoice = scanner.nextInt();
						scanner.nextLine();
						BankCustomerDetails bankCustomerDetails = allUserDetails.get(userChoice);
						
						if(employeeHelper.employeeCreateCustomerAccount(bankCustomerDetails)) {
							logger.log(Level.FINEST,"Successfully Account Created");
						}
						else {
							logger.log(Level.FINEST,"Failed to Create the Account");
						}
						break;
					}
					case 3:{
						logger.log(Level.FINE,"The Existing User of the Bank");
						Map<Integer,BankCustomerDetails> allcustomerDetails = employeeHelper.getActiveUserDetails();;
						avaliableUser(allcustomerDetails);
						break;
					}
					case 4:{
						logger.log(Level.INFO,"Select the User to get Account");
						Map<Integer,BankCustomerDetails> allcustomerDetails = employeeHelper.getActiveUserDetails();;
						avaliableUser(allcustomerDetails);
						if(allcustomerDetails.size()>0) {
							int userChoice = scanner.nextInt();
							logger.log(Level.INFO,"List of Accounts Avaliable : ");
							Map<Integer,BankAccountDetails> allAccountDetails = employeeHelper.getBranchAccounts(List.of(allcustomerDetails.get(userChoice)),1);
							availableAccount(allAccountDetails);
							if(allAccountDetails.size()==0) {
								logger.log(Level.FINEST,"No Account Found for this User");
							}
						}
						else {
							logger.log(Level.FINEST,"No user found");
						}
						break;
					}
					case 5:{
						logger.log(Level.INFO,"Select the User to Block.");
						Map<Integer,BankCustomerDetails> allcustomerDetails = employeeHelper.getActiveUserDetails();;
						avaliableUser(allcustomerDetails);
						if(allcustomerDetails.size()>0) {
							int userChoice = scanner.nextInt();
							logger.log(Level.INFO,"Select the Account to Block.");
							Map<Integer,BankAccountDetails> allAccountDetails = employeeHelper.getBranchAccounts(List.of(allcustomerDetails.get(userChoice)),1);
							availableAccount(allAccountDetails);
							if(allAccountDetails.size()>0) {
								int accountChoice = scanner.nextInt();
								scanner.nextLine();
								if(employeeHelper.deleteAccount(allAccountDetails.get(accountChoice))) {
									logger.log(Level.FINEST,"Blocking of user Account Successfull");
								}
								else {
									logger.log(Level.FINEST,"Blocking of user Account failed");
								}
							}
							else {
								logger.log(Level.FINEST,"No Account Found for this User");
							}
						}
						else {
							logger.log(Level.INFO,"No user found");
						}
						break;
					}
					case 6:{
						logger.log(Level.INFO,"Select the User to get InActive Account");
						Map<Integer,BankCustomerDetails> allcustomerDetails = employeeHelper.getActiveUserDetails();;
						avaliableUser(allcustomerDetails);
						if(allcustomerDetails.size()>0) {
							int userChoice = scanner.nextInt();
							logger.log(Level.INFO,"List of InactiveAccounts Avaliable : ");
							Map<Integer,BankAccountDetails> allAccountDetails = employeeHelper.getBranchAccounts(List.of(allcustomerDetails.get(userChoice)),0);
							availableAccount(allAccountDetails);
							if(allAccountDetails.size()==0) {
								logger.log(Level.FINEST,"No Account Found for this User");
							}
						}
						else {
							logger.log(Level.FINEST,"No user found");
						}
						break;
					}
					case 7:{
						logger.log(Level.INFO,"\nList of User Bank Account.");
						Map<Integer,BankCustomerDetails> allcustomerDetails = employeeHelper.getAllUserDetails();;
						avaliableUser(allcustomerDetails);
						
						if(allcustomerDetails.size()>0) {
							int userChoice = scanner.nextInt();
							logger.log(Level.INFO,"\nList of Inactive User Bank Account.");
							Map<Integer,BankAccountDetails> allAccountDetails = employeeHelper.getBranchAccounts(List.of(allcustomerDetails.get(userChoice)),0);
							availableAccount(allAccountDetails);
							
							if(allAccountDetails.size()>0) {
								int accountchoice = scanner.nextInt();
								scanner.nextLine();
								if(employeeHelper.activateAccount(allAccountDetails.get(accountchoice))) {
									logger.log(Level.FINEST,"User Account Activation Successfull");
								}
								else {
									logger.log(Level.FINEST,"User Account Activation failed");
								}
							}
							else {
								logger.log(Level.FINEST,"No Blocked Account Found for this User");
							}
						}
						else {
							logger.log(Level.FINEST,"No user found");
						}
						break;
					}
					case 8:{
						logger.log(Level.FINE,"---- My Profile ----");
						employeeDetails(employeeHelper.getMyData());
						break;
					}
				}
			}while(employeeChoice>0 && employeeChoice<10);
		}
		catch(CustomException e) {
			throw new CustomException("Error Occurred in EmployeeRunner",e);
		}
	}
	
	static void EmployeeChoicePage() {
		logger.log(Level.INFO,"\n--- Employee Choice ---"
								+ "\n1. Add User"
								+ "\n2. Add Account to User"
								+ "\n3. show all Users"
								+ "\n4. Show the Account of the User"
								+ "\n5. Delete the Bank Account"
								+ "\n6. Show all Inactive User Bank Account"
								+ "\n7. Activate Bloked User Bank Account"
								+ "\n8. Show My Profile"
								+ "\nOther to Back");
	}
}
