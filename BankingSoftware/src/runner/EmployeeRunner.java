package runner;

import java.util.InputMismatchException;
import java.util.Map;
import java.util.logging.Level;

import database.structure.BankAccount;
import database.structure.BankCustomer;
import database.structure.BankEmployee;
import globalUtilities.CustomException;
import globalUtilities.DateTimeUtils;
import helper.EmployeeHelper;
import helper.UserHelper;
import helper.enumfiles.ExceptionStatus;
import helper.enumfiles.RecordStatus;

public class EmployeeRunner extends BankRunner {

	EmployeeHelper employeeHelper = null;
	UserHelper userHelper = null;

	public EmployeeRunner() {
		try {
			employeeHelper = new EmployeeHelper();
			userHelper = new UserHelper();
		} catch (CustomException e) {
			e.printStackTrace();
		}
	}

	public void EmployeeRunnerTask() throws CustomException {
		logger.log(Level.FINEST, "result : Employee");
		BankEmployee bankEmployee = employeeHelper.getMyData();
		logger.log(Level.FINE, "Welcome " + bankEmployee.getName() + " to BankOfZoho");
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
								logger.log(Level.FINE, "\n*** Enter the Account Data to add Customer ***");
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

								logger.log(Level.INFO, "Enter the Name of the Customer : ");
								bankCustomerDetails.setName(scanner.nextLine());

								logger.log(Level.INFO, "Select the Gender :" + "\n1. Male" + "\n2. Female");
								int genderChoice = scanner.nextInt();
								scanner.nextLine();
								String gender = genderChoice == 1 ? "Male" : "Female";
								bankCustomerDetails.setGender(gender);

								logger.log(Level.INFO, "Enter the Date of Birth YYYY-MM-DD : ");
								String dob = scanner.nextLine();
								bankCustomerDetails.setDateOfBirth(DateTimeUtils.convertDateTimeToMillis(dob));

								logger.log(Level.INFO, "Enter the Address : ");
								bankCustomerDetails.setAddress(scanner.nextLine());

								logger.log(Level.INFO, "Enter the Pan no : ");
								bankCustomerDetails.setPanNumber(scanner.nextLine());

								logger.log(Level.INFO, "Enter the Aadhar no : ");
								bankCustomerDetails.setAadharNumber(scanner.nextLine());

								if (employeeHelper.adminCreateCustomer(bankCustomerDetails)) {
									logger.log(Level.FINEST, "Created successfully");
								} else {
									logger.log(Level.WARNING, "User already exist or creation failed");
								}
								flag = false;
							} catch (InputMismatchException e) {
								logger.log(Level.SEVERE, ExceptionStatus.WRONGINPUTTYPE.getStatus());
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
								int rowLimit = 5;
								int pageCount = 1;
								long userChoice;
								do {
									logger.log(Level.INFO, "\nSelect the user to Create Bank Account");
									Map<Long, BankCustomer> allUserDetails = employeeHelper
											.getActiveCustomerDetails(rowLimit, pageCount);
									avaliableUser(allUserDetails, pageCount);
									logger.log(Level.INFO, "0. Next\n-1. Prev\n");
									userChoice = scanner.nextLong();
									scanner.nextLine();
									if (userChoice == 0) {
										pageCount++;
									} else if (userChoice == -1 && userChoice == 1) {
										logger.log(Level.WARNING, "Invalid Input");
										continue;
									} else if (userChoice == -1) {
										pageCount--;
									} else {
										BankCustomer bankCustomerDetails = allUserDetails.get(userChoice);
										if (bankCustomerDetails == null) {
											logger.log(Level.WARNING, "Invalid Input");
											break;
										}
										logger.log(Level.INFO, "Select the Account type" + "\n1. Savings Account"
												+ "\n2. Salary Account" + "\n3. Current Account");
										int accountType = scanner.nextInt();
										scanner.nextLine();
										if (accountType < 0 || accountType > 3) {
											logger.log(Level.WARNING, ExceptionStatus.INVALIDINPUT.getStatus());
											continue;
										}

										if (employeeHelper.employeeCreateCustomerAccount(bankCustomerDetails,
												accountType)) {
											logger.log(Level.FINEST, "Successfully Account Created");
										} else {
											logger.log(Level.WARNING, "Failed to Create the Account");
										}
										flag = false;
										break;
									}
								} while (userChoice == 0 || userChoice == -1);
							} catch (InputMismatchException e) {
								logger.log(Level.SEVERE, ExceptionStatus.WRONGINPUTTYPE.getStatus());
								scanner.nextLine();
							} catch (CustomException e) {
								logger.log(Level.SEVERE, e.getMessage());
							}
						}
						break;
					}
					case 3: {
						int rowLimit = 5;
						int pageCount = 1;
						long choice;
						logger.log(Level.FINE, "The Existing Customer of the Bank");
						do {
							Map<Long, BankCustomer> allcustomerDetails = employeeHelper
									.getActiveCustomerDetails(rowLimit, pageCount);
							avaliableUser(allcustomerDetails, pageCount);
							logger.log(Level.INFO, "0. Next\n-1. Prev\n Other to Exit");
							choice = scanner.nextLong();
							scanner.nextLine();
							if (choice == 0) {
								pageCount++;
							} else if (choice == -1 && pageCount == 1) {
								logger.log(Level.WARNING, "Invalid Input");
								continue;
							} else {
								pageCount--;
							}
						} while (choice == 0 || choice == -1);
						break;
					}
					case 4: {
						int rowLimit = 5;
						int pageCount = 1;
						flag = true;
						while (flag) {
							try {
								logger.log(Level.INFO, "Select the Customer to get Account");
								Map<Long, BankCustomer> allcustomerDetails = employeeHelper
										.getActiveCustomerDetails(rowLimit, pageCount);
								avaliableUser(allcustomerDetails, pageCount);
								if (allcustomerDetails.size() > 0) {
									long userChoice = scanner.nextLong();

									BankCustomer bankCustomerDetails = allcustomerDetails.get(userChoice);
									if (bankCustomerDetails == null) {
										logger.log(Level.WARNING, "Invalid Input");
										break;
									}

									logger.log(Level.INFO, "List of Accounts Avaliable : ");
									Map<Long, BankAccount> allAccountDetails = employeeHelper.getBranchAccounts(
											bankCustomerDetails.getUserId(), RecordStatus.ACTIVE.getCode());
									availableAccount(allAccountDetails);
									if (allAccountDetails.size() == 0) {
										logger.log(Level.WARNING, "No Account Found for this Customer");
									}
								} else {
									logger.log(Level.WARNING, "No Customer found");
								}
								flag = false;
							} catch (InputMismatchException e) {
								logger.log(Level.SEVERE, ExceptionStatus.WRONGINPUTTYPE.getStatus());
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
								int rowLimit = 5;
								int pageCount = 1;
								logger.log(Level.INFO, "Select the User to Block.");
								Map<Long, BankCustomer> allcustomerDetails = employeeHelper
										.getActiveCustomerDetails(rowLimit, pageCount);
								avaliableUser(allcustomerDetails, pageCount);
								long userChoice = scanner.nextLong();

								BankCustomer bankCustomerDetails = allcustomerDetails.get(userChoice);
								if (bankCustomerDetails == null) {
									logger.log(Level.WARNING, "Invalid Input");
									break;
								}

								logger.log(Level.INFO, "Select the Account to Block.");
								Map<Long, BankAccount> allAccountDetails = employeeHelper.getBranchAccounts(
										bankCustomerDetails.getUserId(), RecordStatus.ACTIVE.getCode());
								availableAccount(allAccountDetails);
								if (allAccountDetails.size() > 0) {
									long accountChoice = scanner.nextLong();
									scanner.nextLine();

									BankAccount bankAccount = allAccountDetails.get(accountChoice);
									if (bankAccount == null) {
										logger.log(Level.WARNING, "Invalid Input");
										break;
									}

									if (employeeHelper.deleteAccount(bankAccount.getAccountNo(),
											bankAccount.getUserId())) {
										logger.log(Level.FINEST, "Blocking of Customer Account Successfull");
									} else {
										logger.log(Level.WARNING, "Blocking of Customer Account failed");
									}
								} else {
									logger.log(Level.WARNING, "No Account Found for this Customer");
								}
								flag = false;
							} catch (InputMismatchException e) {
								logger.log(Level.SEVERE, ExceptionStatus.WRONGINPUTTYPE.getStatus());
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
								int rowLimit = 5;
								int pageCount = 1;
								logger.log(Level.INFO, "Select the User to get InActive Account");
								Map<Long, BankCustomer> allcustomerDetails = employeeHelper
										.getActiveCustomerDetails(rowLimit, pageCount);
								avaliableUser(allcustomerDetails, pageCount);
								if (allcustomerDetails.size() > 0) {
									long userChoice = scanner.nextLong();

									BankCustomer bankCustomerDetails = allcustomerDetails.get(userChoice);
									if (bankCustomerDetails == null) {
										logger.log(Level.WARNING, "Invalid Input");
										break;
									}

									logger.log(Level.INFO, "List of InactiveAccounts Avaliable : ");
									Map<Long, BankAccount> allAccountDetails = employeeHelper.getBranchAccounts(
											bankCustomerDetails.getUserId(), RecordStatus.INACTIVE.getCode());
									availableAccount(allAccountDetails);
									if (allAccountDetails.size() == 0) {
										logger.log(Level.WARNING, "No Account Found for this Customer");
									}
								} else {
									logger.log(Level.WARNING, "No Customer found");
								}
								flag = false;
							} catch (InputMismatchException e) {
								logger.log(Level.SEVERE, ExceptionStatus.WRONGINPUTTYPE.getStatus());
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
								int rowLimit = 5;
								int pageCount = 1;
								logger.log(Level.INFO, "\nList of User Bank Account.");
								Map<Long, BankCustomer> allcustomerDetails = employeeHelper.getAllUserDetails(rowLimit,
										pageCount);
								avaliableUser(allcustomerDetails, pageCount);

								if (allcustomerDetails.size() > 0) {
									long userChoice = scanner.nextLong();

									BankCustomer bankCustomerDetails = allcustomerDetails.get(userChoice);
									if (bankCustomerDetails == null) {
										logger.log(Level.WARNING, "Invalid Input");
										break;
									}

									logger.log(Level.INFO, "\nList of Inactive User Bank Account.");
									Map<Long, BankAccount> allAccountDetails = employeeHelper.getBranchAccounts(
											bankCustomerDetails.getUserId(), RecordStatus.INACTIVE.getCode());
									availableAccount(allAccountDetails);

									if (allAccountDetails.size() > 0) {
										long accountChoice = scanner.nextLong();
										scanner.nextLine();
										BankAccount bankAccount = allAccountDetails.get(accountChoice);
										if (employeeHelper.activateAccount(bankAccount.getAccountNo(),
												bankAccount.getUserId())) {
											logger.log(Level.FINEST, "Customer Account Activation Successfull");
										} else {
											logger.log(Level.WARNING, "Customer Account Activation failed");
										}
									} else {
										logger.log(Level.WARNING, "No Blocked Account Found for this Customer");
									}
								} else {
									logger.log(Level.WARNING, "No Customer found");
								}
								flag = false;
							} catch (InputMismatchException e) {
								logger.log(Level.SEVERE, ExceptionStatus.WRONGINPUTTYPE.getStatus());
								scanner.nextLine();
							} catch (CustomException e) {
								logger.log(Level.SEVERE, e.getMessage());
							}
						}
						break;
					}
					case 8: {
						logger.log(Level.FINE, "---- My Profile ----");
						employeeDetails(Map.of(1L, employeeHelper.getMyData()));
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
		logger.log(Level.FINE, "\n--- Employee Choice ---");
		logger.log(Level.INFO,
				"1. Add Customer" + "\n2. Add Account to Customer" + "\n3. show all Customer"
						+ "\n4. Show the Account of the Customer" + "\n5. Delete the Bank Account"
						+ "\n6. Show all Inactive Customer Bank Account" + "\n7. Activate Bloked Customer Bank Account"
						+ "\n8. Show My Profile" + "\nOther to Back");
	}
}
