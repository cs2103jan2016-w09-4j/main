//@@author A0131507R
package gridtask.ui;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import gridtask.common.Category;
import gridtask.common.Result;
import gridtask.common.Task;
import gridtask.logic.Logic;
import gridtask.ui.DisplayController;
import gridtask.ui.InputController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Logger logger = Logger.getLogger("MainApp");
    private Logic logic;
    private BorderPane root;
    private DisplayController display;
    private InputController input;
    private Feedback feedback;
 
    private static final int WINDOW_WIDTH_MIN = 400;
    private static final int WINDOW_HEIGHT_MIN = 380;
    private static final int WINDOW_WIDTH_DEFAULT = 650;
    private static final int WINDOW_HEIGHT_DEFAULT = 500;
    
    private static final String RESOURCES_ICON_PROGRAM = "/icons/logo-smaller.png";
    private static final String RESOURCES_FONT_ROBOTO = "/fonts/Roboto-Regular.ttf";
    private static final String RESOURCES_FONT_ROBOTO_SLAB = "/fonts/RobotoSlab-Regular.ttf";
    private static final String RESOURCES_FONT_ROBOTO_CONDENSED = "/fonts/RobotoCondensed-Regular.ttf";
    private static final String RESOURCES_FONT_ROBOTO_BOLD = "/fonts/Roboto-Bold.ttf";
    
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
        primaryStage.getIcons().add(new Image(RESOURCES_ICON_PROGRAM));
        primaryStage.setMinWidth(WINDOW_WIDTH_MIN);
        primaryStage.setMinHeight(WINDOW_HEIGHT_MIN);
        root = new BorderPane();
        Scene scene = new Scene(root, WINDOW_WIDTH_DEFAULT, WINDOW_HEIGHT_DEFAULT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void initializeUI(Stage primaryStage) {
        Font.loadFont(getClass().getResource(RESOURCES_FONT_ROBOTO).toExternalForm(), 20);
        Font.loadFont(getClass().getResource(RESOURCES_FONT_ROBOTO_SLAB).toExternalForm(), 20);
        Font.loadFont(getClass().getResource(RESOURCES_FONT_ROBOTO_CONDENSED).toExternalForm(), 20);
        Font.loadFont(getClass().getResource(RESOURCES_FONT_ROBOTO_BOLD).toExternalForm(), 20);
        display = new DisplayController(this);
        input = new InputController(this);
        root.setCenter(display);
        root.setBottom(input);
        root.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (event.getCode()==KeyCode.M && event.isControlDown()) {
                    logger.log(Level.INFO, "user toggled sidebar");
                    display.toggleSidebar();
                }
            }
        });
        feedback = new Feedback(primaryStage);
    }

    /* ||||||||||||||||||||||||||||||||||||||||||
     * ||                                      ||
     * ||   METHODS THAT INTERACT WITH LOGIC   ||
     * ||                                      ||
     * ||||||||||||||||||||||||||||||||||||||||||
     */

    public ArrayList<Task> getTasks() {
        Result result = logic.processCommand("home");
        assert (result != null);
        ArrayList<Task> tasks = result.getResults();
        return tasks;
    }

    public ArrayList<Category> getCategories() {
        ArrayList<Category> categories = logic.getCategories();
        return categories;
    }
    
    public void handleCommand(String input) {
        Result result = logic.processCommand(input);
        display.displayResult(result);
        feedback.displayFeedback(result);
    }

    public ArrayList<String> getPredictions(String input) {
        return logic.getPredictions(input);
    }

}
