package gridtask.common;

/**
 * Represents a category entry.
 * Contains information on the category name and the
 * number of ongoing tasks that belong to this category.
 */
public class Category {

    // Name of the category
    private String name;
    // Number of ongoing tasks left in this category
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
