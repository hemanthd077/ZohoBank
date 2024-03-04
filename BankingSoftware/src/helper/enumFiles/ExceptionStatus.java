package helper.enumFiles;

public enum ExceptionStatus {
	INVALIDPASSWORD("Invalid Password"), INSUFFICIENTBALENCE("Balence is Insufficient"),
	FAILEDTRANSACTION("Transaction is Failed"), INVALIDACCOUNT("Invalid Account"), INVALIDINPUT("Invalid Input"),
	WRONGINPUTTYPE("Wrong input type is entered"), ACCOUNTNOTFOUND("Account Not Found"), USERNOTFOUND("User not Found");

	private final String status;

	ExceptionStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
}
