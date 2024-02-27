package runner;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import database.structureClasses.BankAccount;
import database.structureClasses.BankCustomer;
import database.structureClasses.BankBranch;
import database.structureClasses.BankEmployee;
import handleError.CustomException;
import helper.enumFiles.StatusType;

public class AdminRunner extends BankRunner {
	
	public AdminRunner() throws CustomException {
		logger.log(Level.FINEST,"result : Admin");
		logger.log(Level.FINE,"Welcome "+employeeHelper.getMyData().getName()+" to BankOfZoho");
		int adminChoice = 0 ;
		boolean flag = true;
		
		while(flag) {
			try {
				do {
					AdminChoicePage();
					adminChoice = scanner.nextInt();
					scanner.nextLine();
					
					switch(adminChoice) {
						case 1:{
							flag = true;
							while(flag) {
								try {
									logger.log(Level.FINE,"\n*** Enter the Account Data to add user ***");
									BankCustomer bankCustomerDetails = new BankCustomer();
									
									logger.log(Level.INFO,"Enter the Emailid :");
									String email = scanner.nextLine();
									validEmail(email);
									bankCustomerDetails.setEmail(email);
									
									logger.log(Level.INFO,"Enter the Password :");
									String password = scanner.nextLine();
									validPassword(password);
									bankCustomerDetails.setPassword(password);
									
									logger.log(Level.INFO,"Enter the Phone Number : ");
									String PhoneNo = scanner.nextLine();
									validPhoneNumber(PhoneNo);
									bankCustomerDetails.setPhonenumber(PhoneNo);
									
									logger.log(Level.INFO,"Enter the Name of the User : ");
									bankCustomerDetails.setName(scanner.nextLine());
									
									logger.log(Level.INFO,"Select the Gender :"
											+ "\n1. Male"
											+ "\n2. Female");
									int genderChoice = scanner.nextInt();
									scanner.nextLine();
									String gender = genderChoice==1 ? "Male":"Female"; 
									bankCustomerDetails.setGender(gender);
									
									logger.log(Level.INFO,"Enter the Address : ");
									bankCustomerDetails.setAddress(scanner.nextLine());
									
									logger.log(Level.INFO,"Enter the Pan no : ");
									bankCustomerDetails.setPanNumber(scanner.nextLine());
									
									logger.log(Level.INFO,"Enter the Aadhar no : ");
									bankCustomerDetails.setAadharNumber(scanner.nextLine());
									
									if(employeeHelper.adminCreateCustomer(List.of(bankCustomerDetails))) {
										logger.log(Level.FINEST,"Created successfully");
									}
									else {
										logger.log(Level.FINEST,"User already exist or creation failed");
									}
									flag = false;
								}  catch(InputMismatchException e) {
									logger.log(Level.SEVERE,"Input Miss Match Error");
									scanner.nextLine();
								}
								catch(CustomException e) {
									logger.log(Level.SEVERE,e.getMessage());
								}
							}
							break;
						}
						case 2:{
							flag = true;
							while(flag) {
								try {
									logger.log(Level.FINE,"\n*** Creating an Bank Account ***");
									
									logger.log(Level.INFO,"Select the Branch to add");
									Map<Integer,BankBranch> allBranchDetails = userHelper.getBranchData();;
									avaliableBranch(allBranchDetails);
									int branchChoice = scanner.nextInt();
									BankBranch branchDetails = allBranchDetails.get(branchChoice);
									scanner.nextLine();
									
									logger.log(Level.INFO,"\nSelect the user to Create Bank Account");
									Map<Integer,BankCustomer> allUserDetails = employeeHelper.getActiveUserDetails();;
									avaliableUser(allUserDetails);
									int userChoice = scanner.nextInt();
									scanner.nextLine();
									BankCustomer bankCustomerDetails = allUserDetails.get(userChoice);
									
									if(employeeHelper.adminCreateCustomerAccount(bankCustomerDetails,branchDetails)) {
										logger.log(Level.FINEST,"Successfully Account Created");
									}
									else {
										logger.log(Level.FINEST,"Failed to Create the Account");
									}
									flag = false;
								}  catch(InputMismatchException e) {
									logger.log(Level.SEVERE,"Input Miss Match Error");
									scanner.nextLine();
								}
								catch(CustomException e) {
									logger.log(Level.SEVERE,e.getMessage());
								}
							}
							break;
						}
						case 3:{
							logger.log(Level.FINE,"The Existing User of the Bank");
							Map<Integer,BankCustomer> allcustomerDetails = employeeHelper.getActiveUserDetails();;
							avaliableUser(allcustomerDetails);
							break;
						}
						case 4:{
							flag = true;
							while(flag) {
								try {
									logger.log(Level.INFO,"Select the User to Block");
									Map<Integer,BankCustomer> allUserDetails = employeeHelper.getActiveUserDetails();;
									avaliableUser(allUserDetails);
									int userChoice = scanner.nextInt();
									scanner.nextLine();
									
									if(employeeHelper.deleteUser(allUserDetails.get(userChoice))) {
										logger.log(Level.FINEST,"Blocking of user Successfull");
									}
									else {
										logger.log(Level.FINEST,"Blocking of user failed");
									}
									flag = false;
								}  catch(InputMismatchException e) {
									logger.log(Level.SEVERE,"Input Miss Match Error");
									scanner.nextLine();
								}
								catch(CustomException e) {
									logger.log(Level.SEVERE,e.getMessage());
								}
							}
							break;
						}
						case 5:{
							flag = true;
							while(flag) {
								try {
									logger.log(Level.INFO,"Select the User to get Account");
									Map<Integer,BankCustomer> allcustomerDetails = employeeHelper.getActiveUserDetails();;
									avaliableUser(allcustomerDetails);
									if(allcustomerDetails.size()>0) {
										int userChoice = scanner.nextInt();
										logger.log(Level.INFO,"List of Accounts Avaliable : ");
										Map<Integer,BankAccount> allAccountDetails = employeeHelper.getAccountAllBranch(List.of(allcustomerDetails.get(userChoice)),StatusType.ACTIVE.getCode());
										availableAccount(allAccountDetails);
										if(allAccountDetails.size()==0) {
											logger.log(Level.FINEST,"No Account Found for this User");
										}
									}
									else {
										logger.log(Level.FINEST,"No user found");
									}
									flag = false;
								}  catch(InputMismatchException e) {
									logger.log(Level.SEVERE,"Input Miss Match Error");
									scanner.nextLine();
								}
								catch(CustomException e) {
									logger.log(Level.SEVERE,e.getMessage());
								}
							}
							break;
						}
						case 6 :{
							flag = true;
							while(flag) {
								try {
									logger.log(Level.INFO,"Select the User to Block.");
									Map<Integer,BankCustomer> allcustomerDetails = employeeHelper.getActiveUserDetails();;
									avaliableUser(allcustomerDetails);
									if(allcustomerDetails.size()>0) {
										int userChoice = scanner.nextInt();
										logger.log(Level.INFO,"Select the Account to Block.");
										Map<Integer,BankAccount> allAccountDetails = employeeHelper.getAccountAllBranch(List.of(allcustomerDetails.get(userChoice)),StatusType.ACTIVE.getCode());
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
									flag = false;
								}  catch(InputMismatchException e) {
									logger.log(Level.SEVERE,"Input Miss Match Error");
									scanner.nextLine();
								}
								catch(CustomException e) {
									logger.log(Level.SEVERE,e.getMessage());
								}
							}
							break;
						}
						case 7:{
							logger.log(Level.INFO,"\nList of Inactive User.");
							Map<Integer,BankCustomer> allInactiveUser = employeeHelper.getInActiveUserDetails();
							avaliableInactiveUser(allInactiveUser);
							break;
						}
						case 8:{
							flag = true;
							while(flag) {
								try {
									logger.log(Level.INFO,"\nList of Inactive User.");
									Map<Integer,BankCustomer> allInactiveUser = employeeHelper.getInActiveUserDetails();
									avaliableInactiveUser(allInactiveUser);
									int userChoice = scanner.nextInt();
									scanner.nextLine();
									
									if(employeeHelper.activateUser(allInactiveUser.get(userChoice))) {
										logger.log(Level.FINEST,"UnBlocking of user Successfull");
									}
									else {
										logger.log(Level.FINEST,"UnBlocking of user failed");
									}
									flag = false;
								}  catch(InputMismatchException e) {
									logger.log(Level.SEVERE,"Input Miss Match Error");
									scanner.nextLine();
								}
								catch(CustomException e) {
									logger.log(Level.SEVERE,e.getMessage());
								}
							}
							break;
						}
						case 9 :{
							flag = true;
							while(flag) {
								try {
									logger.log(Level.INFO,"\nList of User Bank Account.");
									Map<Integer,BankCustomer> allcustomerDetails = employeeHelper.getActiveUserDetails();;
									avaliableUser(allcustomerDetails);
									
									if(allcustomerDetails.size()>0) {
										int userChoice = scanner.nextInt();
										logger.log(Level.INFO,"\nList of Inactive User Bank Account.");
										Map<Integer,BankAccount> allAccountDetails = employeeHelper.getAccountAllBranch(List.of(allcustomerDetails.get(userChoice)),StatusType.INACTIVE.getCode());
										availableAccount(allAccountDetails);
										if(allAccountDetails.size()==0) {
											logger.log(Level.FINEST,"No Blocked Account Found for this User");
										}
									}
									else {
										logger.log(Level.FINEST,"No user found");
									}
									flag = false;
								}  catch(InputMismatchException e) {
									logger.log(Level.SEVERE,"Input Miss Match Error");
									scanner.nextLine();
								}
								catch(CustomException e) {
									logger.log(Level.SEVERE,e.getMessage());
								}
							}
							break;
						}
						case 10:{
							flag = true;
							while(flag) {
								try {
									logger.log(Level.INFO,"\nList of User Bank Account.");
									Map<Integer,BankCustomer> allcustomerDetails = employeeHelper.getAllUserDetails();;
									avaliableUser(allcustomerDetails);
									
									if(allcustomerDetails.size()>0) {
										int userChoice = scanner.nextInt();
										logger.log(Level.INFO,"\nList of Inactive User Bank Account.");
										Map<Integer,BankAccount> allAccountDetails = employeeHelper.getAccountAllBranch(List.of(allcustomerDetails.get(userChoice)),StatusType.INACTIVE.getCode());
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
									flag = false;
								}  catch(InputMismatchException e) {
									logger.log(Level.SEVERE,"Input Miss Match Error");
									scanner.nextLine();
								}
								catch(CustomException e) {
									logger.log(Level.SEVERE,e.getMessage());
								}
							}
							break;
						}
						case 11:{
							logger.log(Level.INFO,"\nShow My Profile.");
							employeeDetails(employeeHelper.getMyData());
						}
						case 12:{
							flag = true;
							while(flag) {
								try {
									BankEmployee employeeDetails = new BankEmployee();
									
									logger.log(Level.INFO,"Enter the Emailid :");
									String email = scanner.nextLine();
									validEmail(email);
									employeeDetails.setEmail(email);
									
									logger.log(Level.INFO,"Enter the Password :");
									String password = scanner.nextLine();
									validPassword(password);
									employeeDetails.setPassword(password);
									
									logger.log(Level.INFO,"Enter the Phone Number : ");
									String phoneNo = scanner.nextLine();
									validPhoneNumber(phoneNo);
									employeeDetails.setPhonenumber(phoneNo);
									
									logger.log(Level.INFO,"Enter the Name of the User : ");
									employeeDetails.setName(scanner.nextLine());
									
									logger.log(Level.INFO,"Select the Gender :"
											+ "\n1. Male"
											+ "\n2. Female");
									int genderChoice = scanner.nextInt();
									scanner.nextLine();
									String gender = genderChoice==1 ? "Male":"Female"; 
									employeeDetails.setGender(gender);
									
									logger.log(Level.INFO,"Enter the Address : ");
									employeeDetails.setAddress(scanner.nextLine());
									
									logger.log(Level.INFO,"Select the Branch to add");
									Map<Integer,BankBranch> allBranchDetails = userHelper.getBranchData();;
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
									flag = false;
								}  catch(InputMismatchException e) {
									logger.log(Level.SEVERE,"Input Miss Match Error");
									scanner.nextLine();
								}
								catch(CustomException e) {
									logger.log(Level.SEVERE,e.getMessage());
								}
							}
							break;
						}
						case 13:{
							logger.log(Level.FINE,"The all Employee of the Bank");
							Map<Integer,BankEmployee> allEmployeeDetails = employeeHelper.getActiveEmployeeDetails();;
							logEmployeeDetails(allEmployeeDetails);
							break;
						}
						case 14:{
							flag = true;
							while(flag) {
								try {
									logger.log(Level.INFO,"Select the User to Block");
									Map<Integer,BankEmployee> allEmployeeDetails = employeeHelper.getActiveEmployeeDetails();;
									logEmployeeDetails(allEmployeeDetails);
									int userChoice = scanner.nextInt();
									scanner.nextLine();
									
									if(employeeHelper.deleteUser(allEmployeeDetails.get(userChoice))) {
										logger.log(Level.FINEST,"Blocking of user Successfull");
									}
									else {
										logger.log(Level.FINEST,"Blocking of user failed");
									}
									flag = false;
								}  catch(InputMismatchException e) {
									logger.log(Level.SEVERE,"Input Miss Match Error");
									scanner.nextLine();
								}
								catch(CustomException e) {
									logger.log(Level.SEVERE,e.getMessage());
								}
							}
							break;
						}
						case 15:{
							logger.log(Level.FINE,"The all Employee of the Bank");
							Map<Integer,BankEmployee> allEmployeeDetails = employeeHelper.getInActiveEmployeeDetails();;
							logEmployeeDetails(allEmployeeDetails);
							break;
						}
						case 16:{
							flag = true;
							while(flag) {
								try {
									logger.log(Level.INFO,"Select the User to UnBlock");
									Map<Integer,BankEmployee> allEmployeeDetails = employeeHelper.getInActiveEmployeeDetails();;
									logEmployeeDetails(allEmployeeDetails);
									int userChoice = scanner.nextInt();
									scanner.nextLine();
									
									if(employeeHelper.activateUser(allEmployeeDetails.get(userChoice))) {
										logger.log(Level.FINEST,"Blocking of user Successfull");
									}
									else {
										logger.log(Level.FINEST,"Blocking of user failed");
									}
									flag = false;
								}  catch(InputMismatchException e) {
									logger.log(Level.SEVERE,"Input Miss Match Error");
									scanner.nextLine();
								}
								catch(CustomException e) {
									logger.log(Level.SEVERE,e.getMessage());
								}
							}
							break;
						}
					}
				}while(adminChoice>0 && adminChoice<16);
				flag = false;
			}
			catch(InputMismatchException e) {
				logger.log(Level.SEVERE,"Input Miss Match Error");
				scanner.nextLine();
			}
			catch(CustomException e) {
				logger.log(Level.SEVERE,e.getMessage());
			}
		}
	}
	
	static void AdminChoicePage() {
		logger.log(Level.INFO,"\n--- Admin Choice ---"
					+"\n1. Add Customer"
					+"\n2. Add Account to Customer"
					+"\n3. show all Customer"
					+"\n4. Delete the Customer"
					+"\n5. Show the Account of the Customer"
					+"\n6. Delete the Bank Account"
					+"\n7. Show all Inactive Customer"
					+"\n8. Activate the Blocked Customer"
					+"\n9. Show all Inactive Customer Bank Account"
					+"\n10. Activate Blocked Customer Bank Account"
					+"\n11. Show My Profile"
					+"\n12. Create Employee"
					+"\n13. Show All Active Employee"
					+"\n14. Delete Employee"
					+"\n15. Show All InActive Employee"
					+"\n16. Activate Blocked Employee"
					+"\nOther to Back");
	}
	
	static void avaliableInactiveUser(Map<Integer,BankCustomer> mapUserDetails) throws CustomException {
		
		logger.log(Level.FINE,"Total no of Users :"+mapUserDetails.size());
		
		for (Map.Entry<Integer, BankCustomer> entry : mapUserDetails.entrySet()) {
		    Integer userId = entry.getKey();
		    BankCustomer branchDetails = entry.getValue();
		    logger.log(Level.FINEST," "+userId + " : " + branchDetails.getName()
		    					+" , Address,  : "+branchDetails.getAddress());
		}
	} 
}
