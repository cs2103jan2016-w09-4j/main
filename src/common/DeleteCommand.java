package common;

public class DeleteCommand extends Command{

    private int id;

	public DeleteCommand(int id) {
	    this.id = id;
	}

	public int getId(){
	    return id;
	}
}
