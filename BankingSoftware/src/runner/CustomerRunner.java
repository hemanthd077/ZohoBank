package runner;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import database.structureClasses.BankAccount;
import database.structureClasses.BankCustomer;
import database.structureClasses.BankTransaction;
import database.structureClasses.BankUser;
import globalUtilities.CustomException;
import helper.CustomerHelper;
import helper.EmployeeHelper;
import helper.enumFiles.ExceptionStatus;
import helper.enumFiles.StatusType;

public class CustomerRunner extends BankRunner {

	CustomerHelper customerHelper = null;
	EmployeeHelper employeeHelper = null;

	public CustomerRunner() throws CustomException {
		try {
			customerHelper = new CustomerHelper();
			employeeHelper = new EmployeeHelper();
		} catch (CustomException e) {
			throw new CustomException("Error Occured");
		}
	}

	public void CustomerRunnerTask() throws CustomException {
		BankCustomer bankCustomerDetails = customerHelper.getCustomerData();
		logger.log(Level.FINEST, "result: Customer");
		logger.log(Level.FINE, "Welcome " + bankCustomerDetails.getName() + " to BankOfZoho");
		boolean flag = true;
		while (flag) {
			try {
				int userChoice;
				do {
					CustomerChoicePage();
					userChoice = scanner.nextInt();
					scanner.nextLine();
					switch (userChoice) {
					case 1: {
						flag = true;
						while (flag) {
							try {
								BankTransaction bankTransactionDetails = new BankTransaction();
								BankUser userDetails = new BankUser();
								bankCustomerDetails = customerHelper.getCustomerData();
								logger.log(Level.INFO, "Enter the Amount to Send");
								double amount = scanner.nextDouble();
								if (amount <= 0) {
									logger.log(Level.WARNING, "Enter the Amount Greater than 0");
									continue;
								}
								bankTransactionDetails.setAmount(amount);

								logger.log(Level.INFO, "Enter the Receiver Account no");
								bankTransactionDetails.setTransactorAccountNumber(scanner.nextLong());

								logger.log(Level.INFO, "Select your Account to send Amount");
								Map<Integer, BankAccount> allAccountDetails = employeeHelper
										.getAccountAllBranch(List.of(bankCustomerDetails), StatusType.ACTIVE.getCode());
								availableAccount(allAccountDetails);
								int accountChoice = scanner.nextInt();
								scanner.nextLine();

								int size = allAccountDetails.size();

								if (size < accountChoice || accountChoice < 1) {
									logger.log(Level.WARNING, "Invalid Input");
									continue;
								}

								BankAccount refAccount = allAccountDetails.get(accountChoice);

								logger.log(Level.INFO, "Enter the Description");
								bankTransactionDetails.setDecription(scanner.nextLine());

								logger.log(Level.INFO, "Enter the Password to send Amount");
								userDetails.setPassword(scanner.nextLine());

								int result = customerHelper.moneyTransaction(bankTransactionDetails, refAccount,
										userDetails);
								paymentResultLog(result);
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
								BankUser userDetails = new BankUser();
								bankCustomerDetails = customerHelper.getCustomerData();

								logger.log(Level.INFO, "Select your Account to check Balance");
								Map<Integer, BankAccount> allAccountDetails = employeeHelper
										.getAccountAllBranch(List.of(bankCustomerDetails), StatusType.ACTIVE.getCode());
								availableAccount(allAccountDetails);
								int accountChoice = scanner.nextInt();
								scanner.nextLine();

								int size = allAccountDetails.size();
								if (size < accountChoice || accountChoice < 1) {
									logger.log(Level.WARNING, "Invalid Input");
									continue;
								}

								BankAccount allAccount = allAccountDetails.get(accountChoice);

								logger.log(Level.INFO, "Enter the Password to check Balance");
								userDetails.setPassword(scanner.nextLine());

								Double balance = customerHelper.checkBalance(allAccount, userDetails);
								logger.log(Level.FINEST, "Account Balance : " + balance);
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
					case 3: {
						flag = true;
						while (flag) {
							try {
								bankCustomerDetails = customerHelper.getCustomerData();
								logger.log(Level.INFO, "Select your Account to check Balance");

								Map<Integer, BankAccount> allAccountDetails = employeeHelper
										.getAccountAllBranch(List.of(bankCustomerDetails), StatusType.ACTIVE.getCode());
								availableAccount(allAccountDetails);
								int accountChoice = scanner.nextInt();

								int size = allAccountDetails.size();

								if (size < accountChoice || accountChoice < 1) {
									logger.log(Level.WARNING, "Invalid Input");
									continue;
								}

								BankAccount allAccount = allAccountDetails.get(accountChoice);
								scanner.nextLine();

								logger.log(Level.INFO, "Enter the Last N Days to fetch Bank Statement");
								int days = scanner.nextInt();
								scanner.nextLine();

								Map<Integer, BankTransaction> bankTransactionDetails = customerHelper
										.getNDayTransactionDetails(allAccount, days);

								if (bankTransactionDetails.size() > 0) {
									logger.log(Level.FINE, "---- Last " + days + " Days Transaction History ----");
									logTransactionHistory(bankTransactionDetails);
								} else {
									logger.log(Level.FINEST, "No Transaction to Show");
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
					case 4: {
						logger.log(Level.FINE, "\n-------- My Profile --------");
						bankCustomerDetails = customerHelper.getCustomerData();
						logBankCustomerDetails(bankCustomerDetails);
						break;
					}
					case 5: {
						flag = true;
						while (flag) {
							try {
								logger.log(Level.FINE, "----  Withdraw  ----");
								bankCustomerDetails = customerHelper.getCustomerData();
								BankTransaction bankTransactionDetails = new BankTransaction();
								BankUser userDetails = new BankUser();

								logger.log(Level.INFO, "Enter the Amount to withdraw");
								double amount = scanner.nextDouble();
								if (amount <= 0) {
									logger.log(Level.WARNING, "Enter the Amount Greater than 0");
									continue;
								}
								bankTransactionDetails.setAmount(amount);

								logger.log(Level.INFO, "Select your Account to withdraw Amount");
								Map<Integer, BankAccount> allAccountDetails = employeeHelper
										.getAccountAllBranch(List.of(bankCustomerDetails), StatusType.ACTIVE.getCode());
								availableAccount(allAccountDetails);
								int accountChoice = scanner.nextInt();
								scanner.nextLine();

								int size = allAccountDetails.size();

								if (size < accountChoice || accountChoice < 1) {
									logger.log(Level.WARNING, "Invalid Input");
									continue;
								}

								BankAccount allAccount = allAccountDetails.get(accountChoice);

								logger.log(Level.INFO, "Enter the Password to withdraw");
								userDetails.setPassword(scanner.nextLine());

								int result = customerHelper.withdrawTransaction(bankTransactionDetails, allAccount,
										userDetails);
								paymentResultLog(result);
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
								bankCustomerDetails = customerHelper.getCustomerData();
								logger.log(Level.FINE, "----  Deposit  ----");
								BankTransaction bankTransactionDetails = new BankTransaction();
								BankUser userDetails = new BankUser();

								logger.log(Level.INFO, "Enter the Amount to Deposit");
								double amount = scanner.nextDouble();
								if (amount <= 0) {
									logger.log(Level.WARNING, "Enter the Amount Greater than 0");
									continue;
								}
								bankTransactionDetails.setAmount(amount);

								logger.log(Level.INFO, "Select your Account to Deposit Amount");
								Map<Integer, BankAccount> allAccountDetails = employeeHelper
										.getAccountAllBranch(List.of(bankCustomerDetails), StatusType.ACTIVE.getCode());
								availableAccount(allAccountDetails);
								int accountChoice = scanner.nextInt();
								scanner.nextLine();

								int size = allAccountDetails.size();

								if (size < accountChoice || accountChoice < 1) {
									logger.log(Level.WARNING, "Invalid Input");
									continue;
								}

								BankAccount allAccount = allAccountDetails.get(accountChoice);

								logger.log(Level.INFO, "Enter the Password to Deposit");
								userDetails.setPassword(scanner.nextLine());

								int result = customerHelper.depositTransaction(bankTransactionDetails, allAccount,
										userDetails);
								paymentResultLog(result);
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
						logger.log(Level.WARNING, "Feature Coming Soon");
						break;
					}
					}
				} while (userChoice > 0 && userChoice < 9);
				logger.log(Level.WARNING, "Logged Out Successfully");
				flag = false;
			} catch (InputMismatchException e) {
				logger.log(Level.SEVERE, "Input Miss Match Error");
				scanner.nextLine();
			} catch (CustomException e) {
				logger.log(Level.SEVERE, e.getMessage());
			}
		}
	}

	static void CustomerChoicePage() {
		logger.log(Level.FINE, "\n--- User Choice ---");
		logger.log(Level.INFO, "1. Send Amount" + "\n2. Check Balance" + "\n3. Get Last N Day Transaction"
				+ "\n4. My Profile" + "\n5. Withdraw" + "\n6. Deposit" + "\n7. Update Password" + "\nOther to LogOut");
	}

	static void logBankCustomerDetails(BankCustomer bankCustomerDetails) {
		logger.log(Level.FINEST, "Customer Details: " + "\nName: " + bankCustomerDetails.getName() + "\nEmail: "
				+ bankCustomerDetails.getEmail() + "\nPhone Number: " + bankCustomerDetails.getPhoneNumber()
				+ "\nDate of Birth: " + bankCustomerDetails.getDateOfBirth() + "\nGender: "
				+ bankCustomerDetails.getGender() + "\nAddress: " + bankCustomerDetails.getAddress() + "\nPAN Number: "
				+ bankCustomerDetails.getPanNumber() + "\nAadhar Number: " + bankCustomerDetails.getAadharNumber());
	}

	public void paymentResultLog(int result) throws CustomException {
		switch (result) {
		case 0: {
			logger.log(Level.FINEST, "Transaction Failed");
			break;
		}
		case 1: {
			logger.log(Level.FINEST, "Transaction Successfully Done");
			break;
		}
		}
	}
}
