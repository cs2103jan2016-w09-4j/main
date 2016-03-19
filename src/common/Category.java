package common;

public class Category {

    private String name;
    private int count;
    private int tasksLeft;
    private int tasksTotal;

    public Category() {
        this("New Category");
    }
    
    public Category(String name) {
        this.name = name;
        this.count = 0;
        this.tasksLeft = 0;
        this.tasksTotal = 0;
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
    
    public int getTasksLeft() {
        return tasksLeft;
    }
    
    public int getTasksTotal() {
        return tasksTotal;
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

    public void setTasksLeft(int tasksLeft) {
        this.tasksLeft = tasksLeft;
    }

    public void setTasksTotal(int tasksTotal) {
        this.tasksTotal = tasksTotal;
    }
    
}
