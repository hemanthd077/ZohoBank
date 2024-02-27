package runner;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import database.structureClasses.BankAccount;
import database.structureClasses.BankCustomer;
import handleError.CustomException;
import helper.EmployeeHelper;
import helper.enumFiles.StatusType;

public class EmployeeRunner extends BankRunner {

	static EmployeeHelper employeeHelper = new EmployeeHelper();

	public EmployeeRunner() throws CustomException {
		logger.log(Level.INFO, "result : Employee");
		logger.log(Level.FINE, "Welcome " + employeeHelper.getMyData().getName() + " to BankOfZoho");
		int employeeChoice = 1;
		boolean flag = true;
		while (flag) {
			try {
				do {
					EmployeeChoicePage();
					employeeChoice = scanner.nextInt();
					scanner.nextLine();
					switch (employeeChoice) {
					case 1: {
						flag = true;
						while (flag) {
							try {
								logger.log(Level.FINE, "\n*** Enter the Account Data to add user ***");
								BankCustomer bankCustomerDetails = new BankCustomer();

								logger.log(Level.INFO, "Enter the Emailid :");
								String email = scanner.nextLine();
								validEmail(email);
								bankCustomerDetails.setEmail(email);

								logger.log(Level.INFO, "Enter the Password :");
								String password = scanner.nextLine();
								validEmail(password);
								bankCustomerDetails.setPassword(password);

								logger.log(Level.INFO, "Enter the Phone Number : ");
								String phoneNo = scanner.nextLine();
								validPhoneNumber(phoneNo);
								bankCustomerDetails.setPhonenumber(phoneNo);

								logger.log(Level.INFO, "Enter the Name of the User : ");
								bankCustomerDetails.setName(scanner.nextLine());

								logger.log(Level.INFO, "Select the Gender :" + "\n1. Male" + "\n2. Female");
								int genderChoice = scanner.nextInt();
								scanner.nextLine();
								String gender = genderChoice == 1 ? "Male" : "Female";
								bankCustomerDetails.setGender(gender);

								logger.log(Level.INFO, "Enter the Address : ");
								bankCustomerDetails.setAddress(scanner.nextLine());

								logger.log(Level.INFO, "Enter the Pan no : ");
								bankCustomerDetails.setPanNumber(scanner.nextLine());

								logger.log(Level.INFO, "Enter the Aadhar no : ");
								bankCustomerDetails.setAadharNumber(scanner.nextLine());

								if (employeeHelper.adminCreateCustomer(List.of(bankCustomerDetails))) {
									logger.log(Level.FINEST, "Created successfully");
								} else {
									logger.log(Level.FINEST, "User already exist or creation failed");
								}
								flag = false;
							} catch (InputMismatchException e) {
								logger.log(Level.SEVERE, "Input Miss Match Error");
								scanner.nextLine();
							} catch (CustomException e) {
								logger.log(Level.SEVERE, e.getMessage());
							}
						}
						break;
					}
					case 2: {
						flag = true;
						while (flag) {
							try {
								logger.log(Level.FINE, "\n*** Creating an Bank Account ***");
								logger.log(Level.INFO, "Select the user to Create Bank Account");
								Map<Integer, BankCustomer> allUserDetails = employeeHelper.getActiveUserDetails();
								avaliableUser(allUserDetails);
								int userChoice = scanner.nextInt();
								scanner.nextLine();
								BankCustomer bankCustomerDetails = allUserDetails.get(userChoice);

								if (employeeHelper.employeeCreateCustomerAccount(bankCustomerDetails)) {
									logger.log(Level.FINEST, "Successfully Account Created");
								} else {
									logger.log(Level.FINEST, "Failed to Create the Account");
								}
								flag = false;
							} catch (InputMismatchException e) {
								logger.log(Level.SEVERE, "Input Miss Match Error");
								scanner.nextLine();
							} catch (CustomException e) {
								logger.log(Level.SEVERE, e.getMessage());
							}
						}
						break;
					}
					case 3: {
						logger.log(Level.FINE, "The Existing User of the Bank");
						Map<Integer, BankCustomer> allcustomerDetails = employeeHelper.getActiveUserDetails();
						;
						avaliableUser(allcustomerDetails);
						break;
					}
					case 4: {
						flag = true;
						while (flag) {
							try {
								logger.log(Level.INFO, "Select the User to get Account");
								Map<Integer, BankCustomer> allcustomerDetails = employeeHelper.getActiveUserDetails();
								avaliableUser(allcustomerDetails);
								if (allcustomerDetails.size() > 0) {
									int userChoice = scanner.nextInt();
									logger.log(Level.INFO, "List of Accounts Avaliable : ");
									Map<Integer, BankAccount> allAccountDetails = employeeHelper.getBranchAccounts(
											List.of(allcustomerDetails.get(userChoice)), StatusType.ACTIVE.getCode());
									availableAccount(allAccountDetails);
									if (allAccountDetails.size() == 0) {
										logger.log(Level.FINEST, "No Account Found for this User");
									}
								} else {
									logger.log(Level.FINEST, "No user found");
								}
								flag = false;
							} catch (InputMismatchException e) {
								logger.log(Level.SEVERE, "Input Miss Match Error");
								scanner.nextLine();
							} catch (CustomException e) {
								logger.log(Level.SEVERE, e.getMessage());
							}
						}
						break;
					}
					case 5: {
						flag = true;
						while (flag) {
							try {
								logger.log(Level.INFO, "Select the User to Block.");
								Map<Integer, BankCustomer> allcustomerDetails = employeeHelper.getActiveUserDetails();
								avaliableUser(allcustomerDetails);
								if (allcustomerDetails.size() > 0) {
									int userChoice = scanner.nextInt();
									logger.log(Level.INFO, "Select the Account to Block.");
									Map<Integer, BankAccount> allAccountDetails = employeeHelper.getBranchAccounts(
											List.of(allcustomerDetails.get(userChoice)), StatusType.ACTIVE.getCode());
									availableAccount(allAccountDetails);
									if (allAccountDetails.size() > 0) {
										int accountChoice = scanner.nextInt();
										scanner.nextLine();
										if (employeeHelper.deleteAccount(allAccountDetails.get(accountChoice))) {
											logger.log(Level.FINEST, "Blocking of user Account Successfull");
										} else {
											logger.log(Level.FINEST, "Blocking of user Account failed");
										}
									} else {
										logger.log(Level.FINEST, "No Account Found for this User");
									}
								} else {
									logger.log(Level.INFO, "No user found");
								}
								flag = false;
							} catch (InputMismatchException e) {
								logger.log(Level.SEVERE, "Input Miss Match Error");
								scanner.nextLine();
							} catch (CustomException e) {
								logger.log(Level.SEVERE, e.getMessage());
							}
						}
						break;
					}
					case 6: {
						flag = true;
						while (flag) {
							try {
								logger.log(Level.INFO, "Select the User to get InActive Account");
								Map<Integer, BankCustomer> allcustomerDetails = employeeHelper.getActiveUserDetails();
								avaliableUser(allcustomerDetails);
								if (allcustomerDetails.size() > 0) {
									int userChoice = scanner.nextInt();
									logger.log(Level.INFO, "List of InactiveAccounts Avaliable : ");
									Map<Integer, BankAccount> allAccountDetails = employeeHelper.getBranchAccounts(
											List.of(allcustomerDetails.get(userChoice)), StatusType.INACTIVE.getCode());
									availableAccount(allAccountDetails);
									if (allAccountDetails.size() == 0) {
										logger.log(Level.FINEST, "No Account Found for this User");
									}
								} else {
									logger.log(Level.FINEST, "No user found");
								}
								flag = false;
							} catch (InputMismatchException e) {
								logger.log(Level.SEVERE, "Input Miss Match Error");
								scanner.nextLine();
							} catch (CustomException e) {
								logger.log(Level.SEVERE, e.getMessage());
							}
						}
						break;
					}
					case 7: {
						flag = true;
						while (flag) {
							try {
								logger.log(Level.INFO, "\nList of User Bank Account.");
								Map<Integer, BankCustomer> allcustomerDetails = employeeHelper.getAllUserDetails();
								avaliableUser(allcustomerDetails);

								if (allcustomerDetails.size() > 0) {
									int userChoice = scanner.nextInt();
									logger.log(Level.INFO, "\nList of Inactive User Bank Account.");
									Map<Integer, BankAccount> allAccountDetails = employeeHelper.getBranchAccounts(
											List.of(allcustomerDetails.get(userChoice)), StatusType.INACTIVE.getCode());
									availableAccount(allAccountDetails);

									if (allAccountDetails.size() > 0) {
										int accountchoice = scanner.nextInt();
										scanner.nextLine();
										if (employeeHelper.activateAccount(allAccountDetails.get(accountchoice))) {
											logger.log(Level.FINEST, "User Account Activation Successfull");
										} else {
											logger.log(Level.FINEST, "User Account Activation failed");
										}
									} else {
										logger.log(Level.FINEST, "No Blocked Account Found for this User");
									}
								} else {
									logger.log(Level.FINEST, "No user found");
								}
								flag = false;
							} catch (InputMismatchException e) {
								logger.log(Level.SEVERE, "Input Miss Match Error");
								scanner.nextLine();
							} catch (CustomException e) {
								logger.log(Level.SEVERE, e.getMessage());
							}
						}
						break;
					}
					case 8: {
						logger.log(Level.FINE, "---- My Profile ----");
						employeeDetails(employeeHelper.getMyData());
						break;
					}
					}
				} while (employeeChoice > 0 && employeeChoice < 10);
				flag = false;
			} catch (InputMismatchException e) {
				logger.log(Level.SEVERE, "Input Miss Match Error");
				scanner.nextLine();
			} catch (CustomException e) {
				throw new CustomException(e.getMessage());
			}
		}
	}

	static void EmployeeChoicePage() {
		logger.log(Level.INFO,
				"\n--- Employee Choice ---" + "\n1. Add User" + "\n2. Add Account to User" + "\n3. show all Users"
						+ "\n4. Show the Account of the User" + "\n5. Delete the Bank Account"
						+ "\n6. Show all Inactive User Bank Account" + "\n7. Activate Bloked User Bank Account"
						+ "\n8. Show My Profile" + "\nOther to Back");
	}
}
