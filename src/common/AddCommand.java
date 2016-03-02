package common;

public class AddCommand extends Command{
    private String description;

	public AddCommand(String description) {
	    this.description = description;
	}

	public String getDescription(){
	    return description;
	}
}
