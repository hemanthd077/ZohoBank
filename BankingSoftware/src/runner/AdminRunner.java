package runner;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import database.structure.BankAccount;
import database.structure.BankBranch;
import database.structure.BankCustomer;
import database.structure.BankEmployee;
import database.structure.BankTransaction;
import globalUtilities.CustomException;
import helper.CustomerHelper;
import helper.EmployeeHelper;
import helper.UserHelper;
import helper.enumFiles.ExceptionStatus;
import helper.enumFiles.StatusType;

public class AdminRunner extends BankRunner {

	EmployeeHelper employeeHelper = null;
	UserHelper userHelper = null;
	CustomerHelper customerHelper = null;

	public AdminRunner() throws CustomException {
		try {
			employeeHelper = new EmployeeHelper();
			customerHelper = new CustomerHelper();
			userHelper = new UserHelper();
		} catch (CustomException e) {
			e.printStackTrace();
			throw new CustomException("Failed to fetch Employee Data", e);
		}
	}

	public void AdminRunnerTask() throws CustomException {
		int userId = userHelper.getMyUserId();
		logger.log(Level.FINEST, "result : Admin");
		BankEmployee bankEmployee = employeeHelper.getMyData();
		logger.log(Level.FINE, "Welcome " + bankEmployee.getName() + " to BankOfZoho");
		int adminChoice = 0;
		boolean flag = true;

		while (flag) {
			try {
				do {
					AdminChoicePage();
					adminChoice = scanner.nextInt();
					scanner.nextLine();

					switch (adminChoice) {
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
								validPassword(password);
								bankCustomerDetails.setPassword(password);

								logger.log(Level.INFO, "Enter the Phone Number : ");
								String PhoneNo = scanner.nextLine();
								validPhoneNumber(PhoneNo);
								bankCustomerDetails.setPhonenumber(PhoneNo);

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

								if (employeeHelper.adminCreateCustomer(bankCustomerDetails)) {
									logger.log(Level.FINEST, "Created successfully");
								} else {
									logger.log(Level.FINEST, "User already exist or creation failed");
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
							int pageCount = 1;
							try {
								logger.log(Level.FINE, "\n*** Creating an Bank Account ***");
								logger.log(Level.INFO, "Select the Account type" + "\n1. Savings Account"
										+ "\n2. Salary Account" + "\n3. Current Account");
								int accountType = scanner.nextInt();
								if (accountType < 0 || accountType > 3) {
									logger.log(Level.WARNING, ExceptionStatus.INVALIDINPUT.getStatus());
									continue;
								}
								logger.log(Level.INFO, "Select the Branch to add");
								Map<Integer, BankBranch> allBranchDetails = userHelper.getBranchData();
								avaliableBranch(allBranchDetails);
								int branchChoice = scanner.nextInt();
								scanner.nextLine();

								BankBranch branchDetails = allBranchDetails.get(branchChoice);
								if (branchDetails == null) {
									logger.log(Level.WARNING, "Invalid Input");
									continue;
								}
								int rowLimit = 5;
								int userChoice;
								do {
									logger.log(Level.INFO, "\nSelect the user to Create Bank Account");
									Map<Integer, BankCustomer> allUserDetails = employeeHelper
											.getActiveCustomerDetails(rowLimit, pageCount);
									avaliableUser(allUserDetails, pageCount);
									logger.log(Level.INFO, "0. Next\n-1. Prev\n");
									userChoice = scanner.nextInt();
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
											continue;
										}

										if (employeeHelper.adminCreateCustomerAccount(bankCustomerDetails,
												branchDetails, accountType)) {
											logger.log(Level.FINEST, "Successfully Account Created");
										} else {
											logger.log(Level.FINEST, "Failed to Create the Account");
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
						logger.log(Level.FINE, "The Existing User of the Bank");
						int rowLimit = 2;
						int pageCount = 1;
						int choice;
						do {
							Map<Integer, BankCustomer> allcustomerDetails = employeeHelper
									.getActiveCustomerDetails(rowLimit, pageCount);
							avaliableUser(allcustomerDetails, pageCount);
							logger.log(Level.INFO, "0. Next\n-1. Prev\n Other to Exit");
							choice = scanner.nextInt();
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
						flag = true;
						while (flag) {
							try {
								int rowLimit = 5;
								int pageCount = 1;
								int userChoice;
								do {
									logger.log(Level.INFO, "Select the User to Block");
									Map<Integer, BankCustomer> allUserDetails = employeeHelper
											.getActiveCustomerDetails(rowLimit, pageCount);
									avaliableUser(allUserDetails, pageCount);
									logger.log(Level.INFO, "0. Next\n-1. Prev\n");
									userChoice = scanner.nextInt();
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

										if (employeeHelper.deleteUser(bankCustomerDetails)) {
											logger.log(Level.FINEST, "Blocking of user Successfull");
										} else {
											logger.log(Level.FINEST, "Blocking of user failed");
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
					case 5: {
						flag = true;
						while (flag) {
							try {
								logger.log(Level.INFO, "Select the User to get Account");
								int rowLimit = 5;
								int pageCount = 1;
								int userChoice;
								do {
									Map<Integer, BankCustomer> allcustomerDetails = employeeHelper
											.getActiveCustomerDetails(rowLimit, pageCount);
									avaliableUser(allcustomerDetails, pageCount);
									logger.log(Level.INFO, "0. Next\n-1. Prev\n");
									userChoice = scanner.nextInt();
									scanner.nextLine();
									if (userChoice == 0) {
										pageCount++;
									} else if (userChoice == -1 && userChoice == 1) {
										logger.log(Level.WARNING, "Invalid Input");
										continue;
									} else if (userChoice == -1) {
										pageCount--;
									} else {
										if (allcustomerDetails.size() > userChoice && userChoice > 0) {
											BankCustomer bankCustomerDetails = allcustomerDetails.get(userChoice);
											if (bankCustomerDetails == null) {
												logger.log(Level.WARNING, "Invalid Input");
												break;
											}

											logger.log(Level.INFO, "List of " + bankCustomerDetails.getName()
													+ " Accounts Avaliable : ");
											Map<Long, BankAccount> allAccountDetails = employeeHelper
													.getAccountAllBranch(bankCustomerDetails.getUserId(),
															StatusType.ACTIVE.getCode());
											availableAccount(allAccountDetails);
											if (allAccountDetails.size() == 0) {
												logger.log(Level.FINEST, "No Account Found for this User");
											}
										} else {
											logger.log(Level.FINEST, "No user found");
										}
										flag = false;
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
					case 6: {
						flag = true;
						while (flag) {
							try {
								int rowLimit = 5;
								int pageCount = 1;
								int userChoice;
								do {
									logger.log(Level.INFO, "Select the User to get Account");
									Map<Integer, BankCustomer> allcustomerDetails = employeeHelper
											.getActiveCustomerDetails(rowLimit, pageCount);
									avaliableUser(allcustomerDetails, pageCount);
									logger.log(Level.INFO, "0. Next\n-1. Prev\n");
									userChoice = scanner.nextInt();
									scanner.nextLine();
									if (userChoice == 0) {
										pageCount++;
									} else if (userChoice == -1 && userChoice == 1) {
										logger.log(Level.WARNING, "Invalid Input");
										continue;
									} else if (userChoice == -1) {
										pageCount--;
									} else {
										if (allcustomerDetails.size() > userChoice && userChoice > 0) {
											BankCustomer bankCustomerDetails = allcustomerDetails.get(userChoice);
											if (bankCustomerDetails == null) {
												logger.log(Level.WARNING, "Invalid Input");
												break;
											}

											logger.log(Level.INFO, "List of Accounts Avaliable : ");
											Map<Long, BankAccount> allAccountDetails = employeeHelper
													.getAccountAllBranch(bankCustomerDetails.getUserId(),
															StatusType.ACTIVE.getCode());
											availableAccount(allAccountDetails);
											if (allAccountDetails.size() == 0) {
												logger.log(Level.FINEST, "No Account Found for this User");
											} else {
												long accountChoice = scanner.nextLong();
												BankAccount allAccount = allAccountDetails.get(accountChoice);
												scanner.nextLine();
												if (allAccount == null) {
													logger.log(Level.WARNING, "Invalid Input");
													continue;
												}

												logger.log(Level.INFO, "Enter the Last N Days to fetch Bank Statement");
												int days = scanner.nextInt();
												scanner.nextLine();

												List<BankTransaction> bankTransactionDetails = customerHelper
														.getNDayTransactionDetails(allAccount.getAccountNo(), days);

												if (bankTransactionDetails.size() > 0) {
													logger.log(Level.FINE,
															"---- Last " + days + " Days Transaction History ----");
													logTransactionHistory(bankTransactionDetails);
												} else {
													logger.log(Level.FINEST, "No Transaction to Show");
												}
											}
										} else {
											logger.log(Level.FINEST, "No user found");
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
					case 7: {
						flag = true;
						while (flag) {
							try {
								logger.log(Level.INFO, "Select the User to Block.");
								int rowLimit = 5;
								int pageCount = 1;
								int userChoice;
								do {
									logger.log(Level.INFO, "Select the User to get Account");
									Map<Integer, BankCustomer> allcustomerDetails = employeeHelper
											.getActiveCustomerDetails(rowLimit, pageCount);
									avaliableUser(allcustomerDetails, pageCount);
									logger.log(Level.INFO, "0. Next\n-1. Prev\n");
									userChoice = scanner.nextInt();
									scanner.nextLine();
									if (userChoice == 0) {
										pageCount++;
									} else if (userChoice == -1 && userChoice == 1) {
										logger.log(Level.WARNING, "Invalid Input");
										continue;
									} else if (userChoice == -1) {
										pageCount--;
									} else {
										if (allcustomerDetails.size() > userChoice && userChoice > 0) {
											BankCustomer bankCustomerDetails = allcustomerDetails.get(userChoice);

											logger.log(Level.INFO, "Select the Account to Block.");
											Map<Long, BankAccount> allAccountDetails = employeeHelper
													.getAccountAllBranch(bankCustomerDetails.getUserId(),
															StatusType.ACTIVE.getCode());
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
					}
					case 8: {
						logger.log(Level.INFO, "\nList of Inactive User.");
						int rowLimit = 5;
						int pageCount = 1;
						Map<Integer, BankCustomer> allInactiveUser = employeeHelper.getInActiveUserDetails(rowLimit,
								pageCount);
						avaliableInactiveUser(allInactiveUser);
						break;
					}
					case 9: {
						flag = true;
						while (flag) {
							try {
								int rowLimit = 5;
								int pageCount = 1;
								logger.log(Level.INFO, "\nList of Inactive User.");
								Map<Integer, BankCustomer> allInactiveUser = employeeHelper
										.getInActiveUserDetails(rowLimit, pageCount);
								avaliableInactiveUser(allInactiveUser);
								int userChoice = scanner.nextInt();
								scanner.nextLine();

								BankCustomer bankInvalidUser = allInactiveUser.get(userChoice);
								if (bankInvalidUser == null) {
									logger.log(Level.WARNING, "Invalid Input");
									break;
								}

								if (employeeHelper.activateUser(bankInvalidUser)) {
									logger.log(Level.FINEST, "UnBlocking of user Successfull");
								} else {
									logger.log(Level.FINEST, "UnBlocking of user failed");
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
					case 10: {
						flag = true;
						while (flag) {
							try {
								int rowLimit = 5;
								int pageCount = 1;
								int userChoice;
								do {
									logger.log(Level.INFO, "\nSelect the User Bank Account.");
									Map<Integer, BankCustomer> allcustomerDetails = employeeHelper
											.getActiveCustomerDetails(rowLimit, pageCount);
									avaliableUser(allcustomerDetails, pageCount);
									logger.log(Level.INFO, "0. Next\n-1. Prev\n");
									userChoice = scanner.nextInt();
									scanner.nextLine();
									if (userChoice == 0) {
										pageCount++;
									} else if (userChoice == -1 && userChoice == 1) {
										logger.log(Level.WARNING, "Invalid Input");
										continue;
									} else if (userChoice == -1) {
										pageCount--;
									} else {
										if (allcustomerDetails.size() > userChoice && userChoice > 0) {
											BankCustomer bankvalidCustomer = allcustomerDetails.get(userChoice);
											if (bankvalidCustomer == null) {
												logger.log(Level.WARNING, "Invalid Input");
												break;
											}

											logger.log(Level.INFO, "\nList of Inactive User Bank Account.");
											Map<Long, BankAccount> allAccountDetails = employeeHelper
													.getAccountAllBranch(bankvalidCustomer.getUserId(),
															StatusType.INACTIVE.getCode());
											availableAccount(allAccountDetails);
											if (allAccountDetails.size() == 0) {
												logger.log(Level.FINEST, "No Blocked Account Found for this User");
											}
										} else {
											logger.log(Level.FINEST, "No user found");
										}
										flag = false;
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
					case 11: {
						flag = true;
						while (flag) {
							try {
								int rowLimit = 5;
								int pageCount = 1;
								logger.log(Level.INFO, "\nList of User Bank Account.");
								Map<Integer, BankCustomer> allcustomerDetails = employeeHelper
										.getAllUserDetails(rowLimit, pageCount);
								;
								avaliableUser(allcustomerDetails, pageCount);

								if (allcustomerDetails.size() > 0) {
									int userChoice = scanner.nextInt();

									BankCustomer bankvalidCustomer = allcustomerDetails.get(userChoice);
									if (bankvalidCustomer == null) {
										logger.log(Level.WARNING, "Invalid Input");
										break;
									}

									logger.log(Level.INFO, "\nList of Inactive User Bank Account.");
									Map<Long, BankAccount> allAccountDetails = employeeHelper.getAccountAllBranch(
											bankvalidCustomer.getUserId(), StatusType.INACTIVE.getCode());
									availableAccount(allAccountDetails);

									if (allAccountDetails.size() > 0) {
										long accountChoice = scanner.nextLong();
										scanner.nextLine();
										BankAccount bankAccount = allAccountDetails.get(accountChoice);
										if (employeeHelper.activateAccount(bankAccount.getAccountNo(),
												bankAccount.getUserId())) {
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
								logger.log(Level.SEVERE, ExceptionStatus.WRONGINPUTTYPE.getStatus());
								scanner.nextLine();
							} catch (CustomException e) {
								logger.log(Level.SEVERE, e.getMessage());
							}
						}
						break;
					}
					case 12: {
						logger.log(Level.INFO, "\nShow My Profile.");
						BankEmployee bankEmployee1 = employeeHelper.getMyData();
						employeeDetails(Map.of(userId, bankEmployee1));
						break;
					}
					case 13: {
						flag = true;
						while (flag) {
							try {
								BankEmployee employeeDetails = new BankEmployee();

								logger.log(Level.INFO, "Enter the Emailid :");
								String email = scanner.nextLine();
								validEmail(email);
								employeeDetails.setEmail(email);

								logger.log(Level.INFO, "Enter the Password :");
								String password = scanner.nextLine();
								validPassword(password);
								employeeDetails.setPassword(password);

								logger.log(Level.INFO, "Enter the Phone Number : ");
								String phoneNo = scanner.nextLine();
								validPhoneNumber(phoneNo);
								employeeDetails.setPhonenumber(phoneNo);

								logger.log(Level.INFO, "Enter the Name of the User : ");
								employeeDetails.setName(scanner.nextLine());

								logger.log(Level.INFO, "Select the Gender :" + "\n1. Male" + "\n2. Female");
								int genderChoice = scanner.nextInt();
								scanner.nextLine();
								String gender = genderChoice == 1 ? "Male" : "Female";
								employeeDetails.setGender(gender);

								logger.log(Level.INFO, "Enter the Address : ");
								employeeDetails.setAddress(scanner.nextLine());

								logger.log(Level.INFO, "Select the Branch to add");
								Map<Integer, BankBranch> allBranchDetails = userHelper.getBranchData();
								avaliableBranch(allBranchDetails);
								if (allBranchDetails.size() == 0) {
									break;
								}
								int branchChoice = scanner.nextInt();
								scanner.nextLine();

								BankBranch bankBranch = allBranchDetails.get(branchChoice);
								if (bankBranch == null) {
									logger.log(Level.WARNING, "Invalid Input");
									break;
								}
								employeeDetails.setBankBranch(bankBranch);

								if (employeeHelper.createEmployee(List.of(employeeDetails))) {
									logger.log(Level.FINEST, "Created successfully");
								} else {
									logger.log(Level.FINEST, "User already exist or creation failed");
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
					case 14: {
						logger.log(Level.FINE, "The all Employee of the Bank");
						int rowLimit = 5;
						int pageCount = 1;
						int choice;
						do {
							Map<Integer, BankEmployee> allEmployeeDetails = employeeHelper
									.getActiveEmployeeDetails(rowLimit, pageCount);
							logEmployeeDetails(allEmployeeDetails, pageCount);
							logger.log(Level.INFO, "0. Next\n-1. Prev\n Other to Exit");
							choice = scanner.nextInt();
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
					case 15: {
						flag = true;
						while (flag) {
							try {
								int rowLimit = 5;
								int pageCount = 1;
								logger.log(Level.INFO, "Select the User to Block");
								Map<Integer, BankEmployee> allEmployeeDetails = employeeHelper
										.getActiveEmployeeDetails(rowLimit, pageCount);
								logEmployeeDetails(allEmployeeDetails, pageCount);
								if (allEmployeeDetails.size() == 0) {
									break;
								}
								int userChoice = scanner.nextInt();
								scanner.nextLine();

								BankEmployee bankValidEmployee = allEmployeeDetails.get(userChoice);
								if (bankValidEmployee == null) {
									logger.log(Level.WARNING, "Invalid Input");
									break;
								}

								if (employeeHelper.deleteUser(bankValidEmployee)) {
									logger.log(Level.FINEST, "Blocking of user Successfull");
								} else {
									logger.log(Level.FINEST, "Blocking of user failed");
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
					case 16: {
						logger.log(Level.FINE, "The all Employee of the Bank");
						int rowLimit = 2;
						int pageCount = 1;
						int choice;
						do {
							Map<Integer, BankEmployee> allEmployeeDetails = employeeHelper
									.getInActiveEmployeeDetails(rowLimit, pageCount);
							logEmployeeDetails(allEmployeeDetails, pageCount);
							logger.log(Level.INFO, "0. Next\n-1. Prev\n Other to Exit");
							choice = scanner.nextInt();
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
					case 17: {
						flag = true;
						while (flag) {
							try {
								int rowLimit = 5;
								int pageCount = 1;
								logger.log(Level.INFO, "Select the User to UnBlock");
								Map<Integer, BankEmployee> allEmployeeDetails = employeeHelper
										.getInActiveEmployeeDetails(rowLimit, pageCount);
								;
								logEmployeeDetails(allEmployeeDetails, pageCount);
								if (allEmployeeDetails.size() == 0) {
									break;
								}
								int userChoice = scanner.nextInt();
								scanner.nextLine();

								BankEmployee bankInvalidEmployee = allEmployeeDetails.get(userChoice);
								if (bankInvalidEmployee == null) {
									logger.log(Level.WARNING, "Invalid Input");
									break;
								}

								if (employeeHelper.activateUser(bankInvalidEmployee)) {
									logger.log(Level.FINEST, "Blocking of user Successfull");
								} else {
									logger.log(Level.FINEST, "Blocking of user failed");
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
					}
				} while (adminChoice > 0 && adminChoice < 17);
				flag = false;
			} catch (InputMismatchException e) {
				logger.log(Level.SEVERE, "Input Miss Match Error");
				scanner.nextLine();
			} catch (CustomException e) {
				logger.log(Level.SEVERE, e.getMessage());
			}
		}
	}

	static void AdminChoicePage() {
		logger.log(Level.INFO,
				"\n--- Admin Choice ---" + "\n1. Add Customer" + "\n2. Add Account to Customer"
						+ "\n3. show all Customer" + "\n4. Delete the Customer"
						+ "\n5. Show the Account of the Customer" + "\n6. Show Custom Account Transaction."
						+ "\n7. Delete the Bank Account" + "\n8. Show all Inactive Customer"
						+ "\n9. Activate the Blocked Customer" + "\n10. Show all Inactive Customer Bank Account"
						+ "\n11. Activate Blocked Customer Bank Account" + "\n12. Show My Profile"
						+ "\n12. Create Employee" + "\n14. Show All Active Employee" + "\n15. Delete Employee"
						+ "\n16. Show All InActive Employee" + "\n17. Activate Blocked Employee" + "\nOther to Back");
	}

	static void avaliableInactiveUser(Map<Integer, BankCustomer> mapUserDetails) throws CustomException {

		logger.log(Level.FINE, "Total no of Users :" + mapUserDetails.size());

		for (Map.Entry<Integer, BankCustomer> entry : mapUserDetails.entrySet()) {
			Integer userId = entry.getKey();
			BankCustomer branchDetails = entry.getValue();
			logger.log(Level.FINEST,
					" " + userId + " : " + branchDetails.getName() + " , Address,  : " + branchDetails.getAddress());
		}
	}
}
