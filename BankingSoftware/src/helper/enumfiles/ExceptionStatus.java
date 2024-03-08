package helper.enumfiles;

public enum ExceptionStatus {
	INVALIDPASSWORD("Invalid Password"),
	WRONGPASSWORD("Wrong Password Entered"),
	
	INSUFFICIENTBALENCE("Balence is Insufficient"),
	FAILEDTRANSACTION("Transaction is Failed"),
	INVALIDACCOUNT("Invalid Account"),
	ACCOUNTNOTFOUND("Account Not Found"),
	
	INVALIDINPUT("Invalid Input"),
	WRONGINPUTTYPE("Wrong input type is entered"),
	
	USERNOTFOUND("User not Found"),
	FILENOTFOUNT("File is Missing Try Again Later");

	private final String status;

	ExceptionStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
}
