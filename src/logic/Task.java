package logic;


public class Task {
	private String description;
	
	public Task(String description) {
		this.description = description;
	}
	
	public void setDescription(String line) {
		description = line;
	}
	
	public String getDescription() {
		return description;
	}
}	