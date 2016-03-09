package model;

public class Decision {

	private byte result;
	public static final byte ON = 1;
	public static final byte OFF = 0;
	public static final byte INTERVALL = 10;

	public Decision(byte result) {
		this.result = result;
	}

	public byte getResult() {
		return result;
	}

	public void setResult(byte result) {
		this.result = result;
	}

	@Override
	public String toString() {
		if (getResult() == ON) {
			return "on";
		}
		if (getResult() == OFF) {
			return "off";
		}
		if (getResult() == INTERVALL) {
			return "intervall";
		}

		return "UNKNOWN";
	}

}
