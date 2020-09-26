package github.ppojoji.pmalert;

public class PmException extends RuntimeException {

	private int responseCode;
	private String errorCode;

	public PmException(int responseCode, String errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
		this.responseCode = responseCode;
	}

	public int getResponseCode() {
		return responseCode;
	}
	public String getErrorCode() {
		return errorCode;
	}

	@Override
	public String toString() {
		return "PmException [responseCode=" + responseCode + ", errorCode=" + errorCode + "]";
	}

	
	
}
