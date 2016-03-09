package model;

public class ActorModel {

	private String id;

	private String description;

	private int GPIOPin;

	public int getGPIOPin() {
		return GPIOPin;
	}

	public void setGPIOPin(int gPIOPin) {
		GPIOPin = gPIOPin;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
