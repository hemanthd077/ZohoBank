package runner;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import database.structureClasses.BankAccountDetails;
import database.structureClasses.BankCustomerDetails;
import database.structureClasses.BranchDetails;
import database.structureClasses.EmployeeDetails;
import handleError.CustomException;

public class AdminRunner extends BankRunner {
	
	public AdminRunner() throws CustomException {
		try {
			logger.log(Level.FINEST,"result : Admin");
			logger.log(Level.FINE,"Welcome "+employeeHelper.getMyData().getName()+" to BankOfZoho");
			int adminChoice ;
			do {
				EmployeeChoicePage();
				adminChoice = scanner.nextInt();
				scanner.nextLine();
				switch(adminChoice) {
					case 1:{
						logger.log(Level.FINE,"\n*** Enter the Account Data to add user***");
						BankCustomerDetails bankCustomerDetails = new BankCustomerDetails();
						
						logger.log(Level.INFO,"Enter the Emailid :");
						bankCustomerDetails.setEmail("madhavan@gmail.com");
						
						logger.log(Level.INFO,"Enter the Password :");
						bankCustomerDetails.setPassword("54321");
						
						logger.log(Level.INFO,"Enter the Phone Number : ");
						bankCustomerDetails.setPhonenumber("8901234567");
						
						logger.log(Level.INFO,"Enter the Name of the User : ");
						bankCustomerDetails.setName("Madhavan");
						
						logger.log(Level.INFO,"Enter the Gender : ");
						bankCustomerDetails.setGender("Male");
						
						logger.log(Level.INFO,"Enter the Address : ");
						bankCustomerDetails.setAddress("Rasipuram,Namakkal 637408");
						
						logger.log(Level.INFO,"Enter the Pan no : ");
						bankCustomerDetails.setPanNumber("ASDFH1234K");
						
						logger.log(Level.INFO,"Enter the Aadhar no : ");
						bankCustomerDetails.setAadharNumber("567834569876");
						
						BankCustomerDetails bankCustomerDetails1 = new BankCustomerDetails();
						
						logger.log(Level.INFO,"Enter the Emailid :");
						bankCustomerDetails1.setEmail("joshi@gmail.com");
						
						logger.log(Level.INFO,"Enter the Password :");
						bankCustomerDetails1.setPassword("54321");
						
						logger.log(Level.INFO,"Enter the Phone Number : ");
						bankCustomerDetails1.setPhonenumber("9123456789");
						
						logger.log(Level.INFO,"Enter the Name of the User : ");
						bankCustomerDetails1.setName("joshi");
						
						logger.log(Level.INFO,"Enter the Gender : ");
						bankCustomerDetails1.setGender("Female");
						
						logger.log(Level.INFO,"Enter the Address : ");
						bankCustomerDetails1.setAddress("salem,Namakkal 636488");
						
						logger.log(Level.INFO,"Enter the Pan no : ");
						bankCustomerDetails1.setPanNumber("KJHGF6543D");
						
						logger.log(Level.INFO,"Enter the Aadhar no : ");
						bankCustomerDetails1.setAadharNumber("345678924567");
						
						if(employeeHelper.adminCreateCustomer(List.of(bankCustomerDetails,bankCustomerDetails1))) {
							logger.log(Level.FINEST,"Created successfully");
						}
						else {
							logger.log(Level.FINEST,"User already exist or creation failed");
						}
						break;
					}
					case 2:{
						
						logger.log(Level.FINE,"\n*** Creating an Bank Account ***");
						
						logger.log(Level.INFO,"Select the Branch to add");
						Map<Integer,BranchDetails> allBranchDetails = userHelper.getBranchData();;
						avaliableBranch(allBranchDetails);
						int branchChoice = scanner.nextInt();
						BranchDetails branchDetails = allBranchDetails.get(branchChoice);
						scanner.nextLine();
						
						logger.log(Level.INFO,"\nSelect the user to Create Bank Account");
						Map<Integer,BankCustomerDetails> allUserDetails = employeeHelper.getActiveUserDetails();;
						avaliableUser(allUserDetails);
						int userChoice = scanner.nextInt();
						scanner.nextLine();
						BankCustomerDetails bankCustomerDetails = allUserDetails.get(userChoice);
						
						if(employeeHelper.adminCreateCustomerAccount(bankCustomerDetails,branchDetails)) {
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
						logger.log(Level.INFO,"Select the User to Block");
						Map<Integer,BankCustomerDetails> allUserDetails = employeeHelper.getActiveUserDetails();;
						avaliableUser(allUserDetails);
						int userChoice = scanner.nextInt();
						scanner.nextLine();
						
						if(employeeHelper.deleteUser(allUserDetails.get(userChoice))) {
							logger.log(Level.FINEST,"Blocking of user Successfull");
						}
						else {
							logger.log(Level.FINEST,"Blocking of user failed");
						}
						break;
					}
					case 5:{
						logger.log(Level.INFO,"Select the User to get Account");
						Map<Integer,BankCustomerDetails> allcustomerDetails = employeeHelper.getActiveUserDetails();;
						avaliableUser(allcustomerDetails);
						if(allcustomerDetails.size()>0) {
							int userChoice = scanner.nextInt();
							logger.log(Level.INFO,"List of Accounts Avaliable : ");
							Map<Integer,BankAccountDetails> allAccountDetails = employeeHelper.getAccountAllBranch(List.of(allcustomerDetails.get(userChoice)),1);
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
					case 6 :{
						logger.log(Level.INFO,"Select the User to Block.");
						Map<Integer,BankCustomerDetails> allcustomerDetails = employeeHelper.getActiveUserDetails();;
						avaliableUser(allcustomerDetails);
						if(allcustomerDetails.size()>0) {
							int userChoice = scanner.nextInt();
							logger.log(Level.INFO,"Select the Account to Block.");
							Map<Integer,BankAccountDetails> allAccountDetails = employeeHelper.getAccountAllBranch(List.of(allcustomerDetails.get(userChoice)),1);
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
					case 7:{
						logger.log(Level.INFO,"\nList of Inactive User.");
						Map<Integer,BankCustomerDetails> allInactiveUser = employeeHelper.getInActiveUserDetails();
						avaliableInactiveUser(allInactiveUser);
						break;
					}
					case 8 :{
						logger.log(Level.INFO,"\nList of User Bank Account.");
						Map<Integer,BankCustomerDetails> allcustomerDetails = employeeHelper.getActiveUserDetails();;
						avaliableUser(allcustomerDetails);
						
						if(allcustomerDetails.size()>0) {
							int userChoice = scanner.nextInt();
							logger.log(Level.INFO,"\nList of Inactive User Bank Account.");
							Map<Integer,BankAccountDetails> allAccountDetails = employeeHelper.getAccountAllBranch(List.of(allcustomerDetails.get(userChoice)),0);
							availableAccount(allAccountDetails);
							if(allAccountDetails.size()==0) {
								logger.log(Level.FINEST,"No Blocked Account Found for this User");
							}
						}
						else {
							logger.log(Level.FINEST,"No user found");
						}
						break;
					}
					case 9:{
						logger.log(Level.INFO,"\nList of User Bank Account.");
						Map<Integer,BankCustomerDetails> allcustomerDetails = employeeHelper.getAllUserDetails();;
						avaliableUser(allcustomerDetails);
						
						if(allcustomerDetails.size()>0) {
							int userChoice = scanner.nextInt();
							logger.log(Level.INFO,"\nList of Inactive User Bank Account.");
							Map<Integer,BankAccountDetails> allAccountDetails = employeeHelper.getAccountAllBranch(List.of(allcustomerDetails.get(userChoice)),0);
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
					case 10:{
						logger.log(Level.INFO,"\nShow My Profile.");
						employeeDetails(employeeHelper.getMyData());
					}
					case 11:{
						EmployeeDetails employeeDetails = new EmployeeDetails();

						logger.log(Level.INFO,"Enter the Emailid :");
						employeeDetails.setEmail("surya@gmail.com");
						
						logger.log(Level.INFO,"Enter the Password :");
						employeeDetails.setPassword("11111");
						
						logger.log(Level.INFO,"Enter the Phone Number : ");
						employeeDetails.setPhonenumber("9087654321");
						
						logger.log(Level.INFO,"Enter the Name of the User : ");
						employeeDetails.setName("surya");
						
						logger.log(Level.INFO,"Enter the Gender : ");
						employeeDetails.setGender("Male");
						
						logger.log(Level.INFO,"Enter the Address : ");
						employeeDetails.setAddress("Bangalore,karnataka");
						
						logger.log(Level.INFO,"Select the Branch to add");
						Map<Integer,BranchDetails> allBranchDetails = userHelper.getBranchData();;
						avaliableBranch(allBranchDetails);
						int branchChoice = scanner.nextInt();
						scanner.nextLine();
						employeeDetails.setBranchDetails(allBranchDetails.get(branchChoice));
						
						if(employeeHelper.createEmployee(List.of(employeeDetails))) {
							logger.log(Level.FINEST,"Created successfully");
						}
						else {
							logger.log(Level.FINEST,"User already exist or creation failed");
						}
						
						break;
					}
					case 12:{
						break;
					}
					case 13:{
						break;
					}
					case 14:{
						break;
					}
				}
			}while(adminChoice>0 && adminChoice<15);
		}
		catch(CustomException e) {
			throw new CustomException("error",e);
		}
	}
	
	static void EmployeeChoicePage() {
		logger.log(Level.INFO,"\n--- Admin Choice ---"
					+"\n1. Add User"
					+"\n2. Add Account to User"
					+"\n3. show all Users"
					+"\n4. Delete the User"
					+"\n5. Show the Account of the User"
					+"\n6. Delete the Bank Account"
					+"\n7. Show all Inactive User"
					+"\n8. Show all Inactive User Bank Account"
					+"\n9. Activate Blocked User Bank Account"
					+"\n10. Show My Profile"
					+"\n11. Create Employee"
					+"\n12. Show All Employee"
					+"\n13. Delete Employee"
					+"\n14. Activate Blocked Employee"
					+"\nOther to Back");
	}
	
	static void avaliableInactiveUser(Map<Integer,BankCustomerDetails> mapUserDetails) throws CustomException {
		
		logger.log(Level.FINE,"Total no of Users :"+mapUserDetails.size());
		
		for (Map.Entry<Integer, BankCustomerDetails> entry : mapUserDetails.entrySet()) {
		    Integer userId = entry.getKey();
		    BankCustomerDetails branchDetails = entry.getValue();
		    logger.log(Level.FINEST," "+userId + " : " + branchDetails.getName()
		    					+" , Address,  : "+branchDetails.getAddress());
		}
	}
}
