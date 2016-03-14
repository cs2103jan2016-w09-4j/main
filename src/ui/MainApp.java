package ui;

import java.util.ArrayList;
import java.util.HashMap;

import common.Result;
import common.Task;
import ui.DisplayController;
import ui.InputController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import logic.Logic;

public class MainApp extends Application {
    
    private Logic logic;
    private DisplayController display;
    private InputController input;
    
    private static final int WINDOW_WIDTH_MIN = 400;
    private static final int WINDOW_HEIGHT_MIN = 330;
    private static final int WINDOW_HEIGHT_DEFAULT = 350;
    private static final int WINDOW_WIDTH_DEFAULT = 450;
    
    private static final String RESOURCES_ICON_PROGRAM = "file:main/src/resources/icons/logo-smaller.png";
    
    public static void main(String[] args) {
        launch(args);
    }
    
    public void start(Stage primaryStage) {
        initializeLogic();
        initializeUI(primaryStage);
        initializeWindow(primaryStage);
    }
    
    private void initializeLogic() {
        logic = new Logic();
    }

    private void initializeUI(Stage primaryStage) {
        display = new DisplayController(this, primaryStage);
        input = new InputController(this);
        display.add(input, 0, 1);
    }

    private void initializeWindow(Stage primaryStage) {
        primaryStage.setMinHeight(WINDOW_HEIGHT_MIN);
        primaryStage.setMinWidth(WINDOW_WIDTH_MIN);
        Scene scene = new Scene(display, WINDOW_WIDTH_DEFAULT, WINDOW_HEIGHT_DEFAULT);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.getIcons().add(new Image(RESOURCES_ICON_PROGRAM));
    }
    
    /* ||||||||||||||||||||||||||||||||||||||||||
     * ||                                      ||
     * ||   METHODS THAT INTERACT WITH LOGIC   ||
     * ||                                      ||
     * ||||||||||||||||||||||||||||||||||||||||||
     */

    public ArrayList<Task> getTasks() {
        return logic.processCommand("home").getResults();
    }

    public HashMap<String, Integer> getCategories() {
        return logic.getCategories();
    }
    
    public void handleCommand(String input) {
        Result result = logic.processCommand(input);
        display.displayResult(result);
    }

}
