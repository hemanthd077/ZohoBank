package helper.enumFiles;

public enum StatusType {
	INACTIVE(0),
    ACTIVE(1);
    
    private final int code;

    StatusType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
