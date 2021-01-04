import java.io.Serializable;

class Acknowledgement implements Serializable {
	private static final long serialVersionUID = 6063846073945813494L;
	private int acknowledgementNumber;
	
	Acknowledgement(int acknowledgementNumber) {
		super();
		this.acknowledgementNumber = acknowledgementNumber;
	}

	public int getAcknowledgementNumber() {
		return this.acknowledgementNumber;
	}
}