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
import javafx.scene.text.Font;
import javafx.stage.Stage;
import logic.Logic;

public class MainApp extends Application {

    private static Logger logger = Logger.getLogger("MainApp");
    private Logic logic;
    private BorderPane root;
    private DisplayController display;
    private InputController input;
    
    private static final int WINDOW_WIDTH_MIN = 400;
    private static final int WINDOW_HEIGHT_MIN = 300;
    private static final int WINDOW_WIDTH_DEFAULT = 450;
    private static final int WINDOW_HEIGHT_DEFAULT = 350;
    
    private static final String RESOURCES_ICON_PROGRAM = "/icons/logo-smaller.png";
    private static final String RESOURCES_FONT_ROBOTO = "/fonts/Roboto-Regular.ttf";
    private static final String RESOURCES_FONT_ROBOTO_SLAB = "/fonts/RobotoSlab-Regular.ttf";
    private static final String RESOURCES_FONT_ROBOTO_CONDENSED = "/fonts/RobotoCondensed-Regular.ttf";
    
    public static void main(String[] args) {
        launch(args);
    }
    
    public void start(Stage primaryStage) {
        logger.log(Level.INFO, "initalizing components");
        initializeLogic();
        initializeWindow(primaryStage);
        initializeUI(primaryStage);
    }
    
    private void initializeLogic() {
        logic = new Logic();
    }

    private void initializeWindow(Stage primaryStage) {
        root = new BorderPane();
        primaryStage.setMinWidth(WINDOW_WIDTH_MIN);
        primaryStage.setMinHeight(WINDOW_HEIGHT_MIN);
        Scene scene = new Scene(root, WINDOW_WIDTH_DEFAULT, WINDOW_HEIGHT_DEFAULT);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.getIcons().add(new Image(RESOURCES_ICON_PROGRAM));
    }
    
    private void initializeUI(Stage primaryStage) {
        Font.loadFont(getClass().getResource(RESOURCES_FONT_ROBOTO).toExternalForm(), 20);
        Font.loadFont(getClass().getResource(RESOURCES_FONT_ROBOTO_SLAB).toExternalForm(), 20);
        Font.loadFont(getClass().getResource(RESOURCES_FONT_ROBOTO_CONDENSED).toExternalForm(), 20);
        display = new DisplayController(this, primaryStage);
        input = new InputController(this);
        root.setCenter(display);
        root.setBottom(input);
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
