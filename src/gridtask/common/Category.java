package gridtask.common;

/**
 * Represents a category entry.
 */
public class Category {

    // Category name
    private String name;
    // Number of ongoing tasks in this category
    private int count;
    
    public Category(String name) {
        this.name = name;
        this.count = 0;
    }
    
    public Category(String name, int count) {
        this.name = name;
        this.count = count;
    }
    
    /******************
     * GETTER METHODS *
     ******************/
    
    public String getName() {
        return name;
    }
    
    public int getCount() {
        return count;
    }
    
    /******************
     * SETTER METHODS *
     ******************/
    
    public void setName(String name) {
        this.name = name;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
