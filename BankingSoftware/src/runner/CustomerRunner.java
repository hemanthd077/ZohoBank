package runner;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import database.structureClasses.BankAccountDetails;
import database.structureClasses.BankCustomerDetails;
import database.structureClasses.BankTransactionDetails;
import database.structureClasses.UserDetails;
import handleError.CustomException;

public class CustomerRunner extends BankRunner {
	
	
	
	public CustomerRunner() throws CustomException {
		try {
			BankCustomerDetails bankCustomerDetails = customerHelper.getCustomerData();
			logger.log(Level.FINEST,"result: User");
			logger.log(Level.FINE,"Welcome "+bankCustomerDetails.getName() + " to BankOfZoho");
			int userChoice;
			do {
				CustomerChoicePage();
				userChoice = scanner.nextInt();
				scanner.nextLine();
				switch(userChoice) {
					case 1:{
						BankTransactionDetails bankTransactionDetails = new BankTransactionDetails();
						UserDetails userDetails = new UserDetails();
						bankCustomerDetails = customerHelper.getCustomerData();
						
						logger.log(Level.INFO,"Enter the Amount to Send");
						bankTransactionDetails.setAmount(scanner.nextDouble());
						
						logger.log(Level.INFO,"Enter the Receiver Account no");
						bankTransactionDetails.setTransactorAccountNumber(17084938169898680L);
						
						logger.log(Level.INFO,"Select your Account to send Amount");
						Map<Integer,BankAccountDetails> allAccountDetails = employeeHelper.getAccountAllBranch(List.of(bankCustomerDetails),1);
						availableAccount(allAccountDetails);
						int accountChoice = scanner.nextInt();
						scanner.nextLine();
						
						logger.log(Level.INFO,"Enter the Password to send Amount");
						userDetails.setPassword("54321");
						
						int result = customerHelper.moneyTransaction(bankTransactionDetails,allAccountDetails.get(accountChoice),userDetails);
						if(result == 3) {
							logger.log(Level.FINEST,"Transaction Successfully Done");
						}
						else if(result == 2) {
							logger.log(Level.FINEST,"Transaction Failed");
						}
						else if(result == 1){
							logger.log(Level.FINEST,"Insufficient Balence");
						}
						else if(result ==0){
							logger.log(Level.FINEST,"Account Not Found or Invalid Account");
						}
						else {
							logger.log(Level.FINEST,"Invalid Password");
						}
						break;
					}
					case 2:{
						UserDetails userDetails = new UserDetails();
						bankCustomerDetails = customerHelper.getCustomerData();
						
						logger.log(Level.INFO,"Select your Account to check Balance");
						Map<Integer,BankAccountDetails> allAccountDetails = employeeHelper.getAccountAllBranch(List.of(bankCustomerDetails),1);
						availableAccount(allAccountDetails);
						int accountChoice = scanner.nextInt();
						scanner.nextLine();

						logger.log(Level.INFO,"Enter the Password to check Balance");
						userDetails.setPassword("54321");
						
						try {
							Double balance = customerHelper.checkBalance(allAccountDetails.get(accountChoice),userDetails);
							logger.log(Level.FINEST,"Account Balance : "+balance);
						}
						catch(CustomException e) {
							logger.log(Level.FINEST,e.getMessage());
						}
						break;
					}
					case 3:{
						bankCustomerDetails = customerHelper.getCustomerData();
						logger.log(Level.INFO,"Select your Account to check Balance");
						
						Map<Integer,BankAccountDetails> allAccountDetails = employeeHelper.getAccountAllBranch(List.of(bankCustomerDetails),1);
						availableAccount(allAccountDetails);
						int accountChoice = scanner.nextInt();
						scanner.nextLine();
						Map<Integer,BankTransactionDetails> bankTransactionDetails = customerHelper.getTransactionDetails(allAccountDetails.get(accountChoice));
						
						if(bankTransactionDetails.size()>0) {
							logger.log(Level.FINE,"---- Transaction History ----");
							logTransactionHistory(bankTransactionDetails);
						}
						else {
							logger.log(Level.FINEST,"No Transaction to Show");
						}
						break;
					}
					case 4:{
						logger.log(Level.FINE,"\n-------- My Profile --------");
						bankCustomerDetails = customerHelper.getCustomerData();
						logBankCustomerDetails(bankCustomerDetails);
						break;
					}
					case 5:{
						logger.log(Level.FINE,"----  Withdraw  ----");
						bankCustomerDetails = customerHelper.getCustomerData();
						BankTransactionDetails bankTransactionDetails = new BankTransactionDetails();
						UserDetails userDetails = new UserDetails();

						logger.log(Level.INFO,"Enter the Amount to withdraw");
						bankTransactionDetails.setAmount(scanner.nextDouble());
						
						logger.log(Level.INFO,"Select your Account to withdraw Amount");
						Map<Integer,BankAccountDetails> allAccountDetails = employeeHelper.getAccountAllBranch(List.of(bankCustomerDetails),1);
						availableAccount(allAccountDetails);
						int accountChoice = scanner.nextInt();
						scanner.nextLine();
						
						logger.log(Level.INFO,"Enter the Password to withdraw");
						userDetails.setPassword("54321");
						
						int result = customerHelper.withdrawTransaction(bankTransactionDetails,allAccountDetails.get(accountChoice),userDetails);
						if(result == 3) {
							logger.log(Level.FINEST,"Transaction Successfully Done");
						}
						else if(result == 2) {
							logger.log(Level.FINEST,"Transaction Failed");
						}
						else if(result == 1){
							logger.log(Level.FINEST,"Insufficient Balence");
						}
						else if(result ==0){
							logger.log(Level.FINEST,"Account Not Found or Invalid Account");
						}
						else {
							logger.log(Level.FINEST,"Invalid Password");
						}
						break;
					}
					case 6:{
						bankCustomerDetails = customerHelper.getCustomerData();
						logger.log(Level.FINE,"----  Deposit  ----");
						BankTransactionDetails bankTransactionDetails = new BankTransactionDetails();
						UserDetails userDetails = new UserDetails();

						logger.log(Level.INFO,"Enter the Amount to Deposit");
						bankTransactionDetails.setAmount(scanner.nextDouble());
						
						logger.log(Level.INFO,"Select your Account to Deposit Amount");
						Map<Integer,BankAccountDetails> allAccountDetails = employeeHelper.getAccountAllBranch(List.of(bankCustomerDetails),1);
						availableAccount(allAccountDetails);
						int accountChoice = scanner.nextInt();
						scanner.nextLine();
						
						logger.log(Level.INFO,"Enter the Password to Deposit");
						userDetails.setPassword("54321");
						
						int result = customerHelper.depositTransaction(bankTransactionDetails,allAccountDetails.get(accountChoice),userDetails);
						if(result == 3) {
							logger.log(Level.FINEST,"Transaction Successfully Done");
						}
						else if(result == 2) {
							logger.log(Level.FINEST,"Transaction Failed");
						}
						else if(result == 1){
							logger.log(Level.FINEST,"Insufficient Balence");
						}
						else if(result ==0){
							logger.log(Level.FINEST,"Account Not Found or Invalid Account");
						}
						else {
							logger.log(Level.FINEST,"Invalid Password");
						}
						break;
					}
				}
			}while(userChoice>0 && userChoice<7);
			logger.log(Level.WARNING,"Logged Out Successfully");
		}
		catch(CustomException e) {
			throw new CustomException("error",e);
		}
	}
	
	static void CustomerChoicePage() {
		logger.log(Level.INFO,"\n--- User Choice ---"
				+"\n1. Send Amount"
				+"\n2. Check Balance"
				+"\n3. Show Transaction History"
				+"\n4. My Profile"
				+"\n5. Withdraw"
				+"\n6. Deposit"
				+"\nOther to LogOut");
	}
	
	 static void logBankCustomerDetails(BankCustomerDetails bankCustomerDetails) {
		 logger.log(Level.FINEST, "Customer Details: "
                + "\nName: " + bankCustomerDetails.getName()
                + "\nEmail: " + bankCustomerDetails.getEmail()
                + "\nPhone Number: " + bankCustomerDetails.getPhonenumber()
                + "\nDate of Birth: " + bankCustomerDetails.getDateOfBirth()
                + "\nGender: " + bankCustomerDetails.getGender()
                + "\nAddress: " + bankCustomerDetails.getAddress()
                + "\nPAN Number: " + bankCustomerDetails.getPanNumber()
                + "\nAadhar Number: " + bankCustomerDetails.getAadharNumber());
	 }
	 
	 public static void logTransactionHistory(Map<Integer,BankTransactionDetails> transactionHistory) {
		 
		 int size = transactionHistory.size();
		
		 for(int i=0;i<size;i++) {
        	logger.log(Level.FINEST, "\nTransaction ID: "+ transactionHistory.get(i).getTransactionId()
			        + "\nTransaction Timestamp: " + convertMillsToDateTime(transactionHistory.get(i).getTransactionTimestamp())
			        + "\nAccount Number: " + transactionHistory.get(i).getAccountNumber()
			        + "\nAmount: " + transactionHistory.get(i).getAmount());
        	
        	int type = transactionHistory.get(i).getPaymentType();
	        
        	if(type==0) {
		        logger.log(Level.FINEST,"Type: Debit"
		        		+"\nTransactor Account Number: " + transactionHistory.get(i).getTransactorAccountNumber());
	        }
	        else if(type == 1) {
	        	logger.log(Level.FINEST,"Type: Credit"
	        		+"\nTransactor Account Number: " + transactionHistory.get(i).getTransactorAccountNumber());
	        }
	        else if(type == 2) {
	        	logger.log(Level.FINEST,"Type: Withdraw");
	        }
	        else {
	        	logger.log(Level.FINEST,"Type: Deposit");
	        }
        	
	        logger.log(Level.FINEST,"Current Balance: " + transactionHistory.get(i).getCurrentBalance());
	        
	        if(transactionHistory.get(i).getStatus()==1) {
		        logger.log(Level.FINEST,"Status: Success");
	        }
	        else {
	        	logger.log(Level.FINEST,"Status: Failed");
	        }
        }
	 }
}
