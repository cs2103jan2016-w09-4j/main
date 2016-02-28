public class Command {
    public static final int ADD_TYPE = 1;
    public static final int DELETE_TYPE = 2;
    public static final int EDIT_TYPE = 3;
    public static final int SEARCH_TYPE = 4;
    public static final int INVALID_TYPE = -1;
    private int type;
    private int id;
    private String description;

    public Command(int type){
        this.type = type;
    }

    public Command(int type, String description){
        this.type = type;
        this.description = description;
    }

    public Command(int type, int id){
        this.type = type;
        this.id = id;
    }

    public Command(int type, int id, String description){
        this.type = type;
        this.id = id;
        this.description = description;
    }

    public int getType(){
        return type;
    }

    public String getDescription(){
        return description;
    }

    public int getId(){
        return id;
    }
}
