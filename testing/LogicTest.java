import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import common.*;
import logic.Logic;

public class LogicTest{
    
    Logic hey = Logic.getInstance();
    ArrayList<Task> mainList = new ArrayList<Task>();
    ArrayList<Task> compareList = new ArrayList<Task>();
    
    
    @Test
    public void testOne(){  
        
        hey.processCommand("add hello");
        hey.processCommand("add goodbye");
        hey.processCommand("add meeting 2pm");
        hey.processCommand("delete 2");
        hey.processCommand("save logic_testFile1.txt");
        mainList = hey.getMainList();
        compareList = hey.processCommand("load logic_expected1.txt").getResults();
        assertArrayEquals(mainList.toArray(), compareList.toArray());
        mainList.clear();
        compareList.clear();
    }
    
    @Test
    public void testTwo(){
        
        hey.processCommand("add 123456");
        hey.processCommand("add take me to the top");
        hey.processCommand("add hello there");
        hey.processCommand("add myself; yourself");
        hey.processCommand("edit 1 used to be numbers here");
        hey.processCommand("undo");
        hey.processCommand("save logic_testFile2.txt");
        mainList = hey.getMainList();
        compareList = hey.processCommand("load logic_expected2.txt").getResults();
        assertArrayEquals(mainList.toArray(), compareList.toArray());
        mainList.clear();
        compareList.clear();
    }    
}
