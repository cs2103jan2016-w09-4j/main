package ui;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import common.Category;
import common.Result;
import common.Task;
import ui.DisplayController;
import ui.InputController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import logic.Logic;

public class MainApp extends Application {
    
    private static Logger logger = Logger.getLogger("MainApp");
    private Logic logic;
    private BorderPane root;
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
        logger.log(Level.INFO, "initalizing components");
        initializeLogic();
        initializeUI(primaryStage);
        initializeWindow(primaryStage);
    }
    
    private void initializeLogic() {
        logic = new Logic();
    }

    private void initializeUI(Stage primaryStage) {
        root = new BorderPane();
        display = new DisplayController(this, primaryStage);
        input = new InputController(this);
        root.setCenter(display);
        root.setBottom(input);
    }

    private void initializeWindow(Stage primaryStage) {
        primaryStage.setMinHeight(WINDOW_HEIGHT_MIN);
        primaryStage.setMinWidth(WINDOW_WIDTH_MIN);
        Scene scene = new Scene(root, WINDOW_WIDTH_DEFAULT, WINDOW_HEIGHT_DEFAULT);
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
        assert (logic != null);
        Result result = logic.processCommand("home");
        assert (result != null);
        ArrayList<Task> tasks = result.getResults();
        return tasks;
    }

    public ArrayList<Category> getCategories() {
        assert (logic != null);
        ArrayList<Category> categories = logic.getCategories();
        return categories;
    }
    
    public void handleCommand(String input) {
        assert (logic != null);
        Result result = logic.processCommand(input);
        display.displayResult(result);
    }

    public ArrayList<String> getPredictions(String input) {
        assert (logic != null);
        return logic.getPredictions(input);
    }

}
