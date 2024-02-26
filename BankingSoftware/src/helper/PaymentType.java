package helper;

public enum PaymentType {
    DEPOSIT(3),
    WITHDRAWAL(2),
    CREDIT(1),
    DEBIT(0);
    
    private final int code;

    PaymentType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

