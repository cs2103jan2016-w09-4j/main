package common;

public class SearchCommand extends Command{
	private String keyword;

	public SearchCommand(String keyword) {
	    this.keyword = keyword;

	}

	public String getKeyword(){
	    return keyword;
	}
}
