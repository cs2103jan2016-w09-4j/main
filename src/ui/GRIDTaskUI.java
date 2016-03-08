package ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import common.Category;
import common.Command.CommandType;
import common.Result;
import common.Task;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import logic.Logic;

public class GRIDTaskUI extends Application {
    
    private Stage primaryStage;
    private Scene mainView;
    private GridPane root;
    private VBox categoryPanel;
    private VBox taskPanel;
    private TextField userInput;
    
    private Logic logic;
    private ArrayList<Task> todayTasks;
    private ArrayList<Task> othersTasks;
    private HashMap<String, Integer> categories;
    
    private static final String RESOURCES_ICON_SUCCESS = "File:main/resources/icons/success-smaller.png";
    private static final String RESOURCES_ICONS_FAIL = "File:main/resources/icons/fail-smaller.png";

    private static final int WINDOW_HEIGHT = 350;
    private static final int WINDOW_WIDTH = 450;    
    private static final String PROGRAM_NAME = "GRIDTask";
    private static final String USER_INPUT_PROMPT = "Enter a task here!";
    private static final String HEADER_CATEGORY = "Categories";
    private static final String HEADER_TODAY = "Today";
    private static final String HEADER_OTHERS = "Others";
    private static final String HEADER_SEARCH = "Search results";
    
    private static final String CSS_MAIN_VIEW = "mainView.css";
    private static final String CSS_CLASS_ROOT = "grid";
    private static final String CSS_CLASS_HEADER = "header";
    private static final String CSS_CLASS_PANEL_CAT = "panel-cat";
    private static final String CSS_CLASS_PANEL_TASK = "panel-task";
    private static final String CSS_CLASS_PANEL_SEARCH = "panel-search";
    private static final String CSS_CLASS_ENTRY_CAT = "entry-cat";
    private static final String CSS_CLASS_ENTRY_TASK = "entry-task";
    private static final String CSS_CLASS_ENTRY_TASK_DESCRIPTION = "desc";
    
    public static void main(String[] args) {
        launch(args);
    }
    
    public void start(Stage primaryStage) {
        initStage(primaryStage);
        initLogic();
        initMainView();
        initUserInput();
        handleUserInteractions();
    }

    private void initStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(PROGRAM_NAME);
        this.primaryStage.show();
    }
    
    private void initMainView() {
        root = new GridPane();
        root.getStyleClass().add(CSS_CLASS_ROOT);
        
        setColumnWidths();
        setRowWidths();
        initCategoryPanel();
        initTaskPanel();
        
        mainView = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        mainView.getStylesheets().add(getClass().getResource(CSS_MAIN_VIEW).toExternalForm()); 
        primaryStage.setScene(mainView);
    }

    // Initialise the left panel that displays categories
    private void initCategoryPanel() {
        categoryPanel = createCategoryPanel();
        root.add(categoryPanel, 0, 0);
    }

    // Initialise the right panel that displays tasks
    private void initTaskPanel() {
        taskPanel = createTaskPanel();
        root.add(taskPanel, 1, 0);
    }
    
    private void initLogic() {
        logic = new Logic();
        Result result = logic.processCommand("home");
        processTasks(result.getResults());
    }
    
    private void processTasks(ArrayList<Task> tasks) {
        todayTasks = new ArrayList<Task>();
        othersTasks = new ArrayList<Task>();
        categories = new HashMap<String, Integer>();
        
        for (Task task : tasks) {
            if (task.isToday()) {
                todayTasks.add(task);
            } else {
                othersTasks.add(task);
            }
            ArrayList<String> cats = task.getCategories();
            for (String cat : cats) {
                Integer i = categories.get(cat);
                if (i != null) {
                    categories.remove(cat);
                    categories.put(cat, ++i);
                } else {
                    categories.put(cat, 1);
                }
            }
        }
    }

    // Initialise text field for user input
    private void initUserInput() {
        userInput = new TextField();
        userInput.setPromptText(USER_INPUT_PROMPT);
        root.add(userInput, 0, 1, 2, 1);
        handleUserInteractions();
    }

    private void setColumnWidths() {
        ColumnConstraints left = new ColumnConstraints();
        left.setPercentWidth(30);
        ColumnConstraints right = new ColumnConstraints();
        right.setPercentWidth(70);
        root.getColumnConstraints().addAll(left, right);
    }
    
    private void setRowWidths() {
        RowConstraints row0 = new RowConstraints();
        row0.setVgrow(Priority.ALWAYS);
        RowConstraints row1 = new RowConstraints();
        row1.setVgrow(Priority.NEVER);
        root.getRowConstraints().addAll(row0, row1);
    }
    
    /* ||||||||||||||||||||||||||||||||||||||||||||||||
     * ||                                            ||
     * ||   METHODS FOR CATEGORY PANEL CREATION      ||
     * ||                                            ||
     * ||||||||||||||||||||||||||||||||||||||||||||||||
     */
       
    // Create panel containing category label and specified entries
    private VBox createCategoryPanel() {
        VBox panel = new VBox();
        
        Label header = new Label(HEADER_CATEGORY);
        header.getStyleClass().add(CSS_CLASS_HEADER);
        
        VBox entries = createCategoryEntries();
        ScrollPane scrollCat = new ScrollPane();
        scrollCat.setContent(entries);
        
        VBox.setVgrow(scrollCat, Priority.ALWAYS);
        VBox.setVgrow(panel, Priority.ALWAYS);
        
        panel.getChildren().addAll(header, scrollCat);
        return panel;
    }
    
    // Create category entries
    private VBox createCategoryEntries() {
        ArrayList<HBox> entries = new ArrayList<HBox>();
        Set<Map.Entry<String, Integer>> set = categories.entrySet();
        for (Map.Entry<String, Integer> cat : set) {
            HBox entry = createCategoryEntry(cat.getKey(), cat.getValue());
            entries.add(entry);
        }
        VBox panel = new VBox();
        panel.getChildren().addAll(entries);
        panel.getStyleClass().add(CSS_CLASS_PANEL_CAT);
        return panel;
    }
    
    // Create usable UI element from category entry 
    private HBox createCategoryEntry(String cat, int num) {
        HBox entry = new HBox();
        Label name = new Label(cat + " [" + num + "]");
        name.setAlignment(Pos.CENTER_LEFT);
        name.setMaxWidth(Double.MAX_VALUE);
        entry.getChildren().add(name);
        entry.getStyleClass().add(CSS_CLASS_ENTRY_CAT);
        return entry;
    }

    /* ||||||||||||||||||||||||||||||||||||||||||||||||
     * ||                                            ||
     * ||   METHODS RELATED TO TASK PANEL CREATION   ||
     * ||                                            ||
     * ||||||||||||||||||||||||||||||||||||||||||||||||
     */
    
    // Create panel containing task labels and entries
    private VBox createTaskPanel() {
        VBox panel = new VBox();
        
        Label todayHeader = new Label(HEADER_TODAY);
        todayHeader.getStyleClass().add(CSS_CLASS_HEADER);
        
        VBox todayEntries = createTodayEntries();
        ScrollPane scrollToday = new ScrollPane();
        scrollToday.setContent(todayEntries);
        VBox.setVgrow(scrollToday, Priority.ALWAYS);

        Label othersHeader = new Label(HEADER_OTHERS);
        othersHeader.getStyleClass().add(CSS_CLASS_HEADER);
        
        VBox othersEntries = createOthersEntries();
        ScrollPane scrollOthers = new ScrollPane();
        scrollOthers.setContent(othersEntries);        
        VBox.setVgrow(scrollOthers, Priority.ALWAYS);
        
        panel.getChildren().addAll(todayHeader, scrollToday, othersHeader, scrollOthers);
        return panel;
    }

    // Create today task entries
    private VBox createTodayEntries() {
        ArrayList<VBox> entries = new ArrayList<VBox>();
        for (Task task : todayTasks) {
            if (task.isToday()) {
                VBox entry = createTodayTaskEntry(task);
                entries.add(entry);
            }
        }
        
        VBox panel = new VBox();
        panel.getChildren().addAll(entries);
        panel.getStyleClass().add(CSS_CLASS_PANEL_TASK);
        return panel;
    }
    
    // Create usable UI element from Task
    private VBox createTodayTaskEntry(Task task) {
        VBox entry = new VBox();
        entry.getStyleClass().add(CSS_CLASS_ENTRY_TASK);
        
        Label desc = new Label(task.getID() + ". " + task.getDescription());
        desc.getStyleClass().add(CSS_CLASS_ENTRY_TASK_DESCRIPTION);
        
        HBox details = new HBox();
        Date date;
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mma");
        if ((date = task.getStart()) != null) {
            String time = dateFormat.format(date);
            Label startDate = new Label("From " + time);
            details.getChildren().add(startDate);
            startDate.getStyleClass().add("details");
        }
        if ((date = task.getEnd()) != null) {
            String time = dateFormat.format(date);
            Label endDate = new Label("To " + time);
            details.getChildren().add(endDate);
            endDate.getStyleClass().add("details");
        }
        
        entry.getChildren().addAll(desc, details);
        return entry;
    }

    // Create other task entries
    private VBox createOthersEntries() {
        ArrayList<VBox> entries = new ArrayList<VBox>();
        for (Task task : othersTasks) {
            if (!task.isToday()) {
                VBox entry = createOthersTaskEntry(task);
                entries.add(entry);
            }
        }
        
        VBox panel = new VBox();
        panel.getChildren().addAll(entries);
        panel.getStyleClass().add(CSS_CLASS_PANEL_TASK);
        return panel;
    }

    // Create usable UI element from Task
    private VBox createOthersTaskEntry(Task task) {
        VBox entry = new VBox();
        entry.getStyleClass().add(CSS_CLASS_ENTRY_TASK);
        
        Label desc = new Label(task.getID() + ". " + task.getDescription());
        desc.getStyleClass().add(CSS_CLASS_ENTRY_TASK_DESCRIPTION);
        
        HBox details = new HBox();
        Date date;
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy h:mma");
        if ((date = task.getStart()) != null) {
            String time = dateFormat.format(date); 
            Label startDate = new Label("From " + time);
            details.getChildren().add(startDate);
            startDate.getStyleClass().add("details");
        }
        if ((date = task.getEnd()) != null) {
            String time = dateFormat.format(date); 
            Label endDate = new Label("To " + time);
            details.getChildren().add(endDate);
            endDate.getStyleClass().add("details");
        }
        
        entry.getChildren().addAll(desc, details);
        return entry;
    }

    // Create panel containing search label and entries
    private VBox createSearchPanel(Result result) {
        VBox panel = new VBox();
        
        Label searchHeader = new Label(HEADER_SEARCH);
        searchHeader.getStyleClass().add(CSS_CLASS_HEADER);
        
        VBox searchEntries = createSearchEntries(result);
        panel.getStyleClass().add(CSS_CLASS_PANEL_SEARCH);
        ScrollPane scrollSearch = new ScrollPane();
        scrollSearch.setContent(searchEntries);
        
        panel.getChildren().addAll(searchHeader, scrollSearch);
        VBox.setVgrow(scrollSearch, Priority.ALWAYS);
        return panel;
    }
    
    // Create search task entries
    private VBox createSearchEntries(Result result) {
        ArrayList<VBox> entries = getSearchEntries(result);
        VBox panel = new VBox();
        panel.getChildren().addAll(entries);
        panel.getStyleClass().add(CSS_CLASS_PANEL_TASK);
        return panel;
    }
    
    private ArrayList<VBox> getSearchEntries(Result result) {
        ArrayList<Task> tasks = result.getResults();
        ArrayList<VBox> entries = new ArrayList<VBox>();
        for (Task task : tasks) {
            VBox entry = createOthersTaskEntry(task);
            entries.add(entry);
        }
        return entries;
    }

    /* ||||||||||||||||||||||||||||||||||||||||||||||||
     * ||                                            ||
     * ||     METHODS TO HANDLE USER INTERACTION     ||
     * ||                                            ||
     * ||||||||||||||||||||||||||||||||||||||||||||||||
     */
    
    private void handleUserInteractions() {
        userInput.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String input = userInput.getText();
                System.out.println(input);
                userInput.setText("");
                // TODO: get result from Logic
                Result result = logic.processCommand(input);
                if (result.getCommandType() == CommandType.SEARCH) {
                    System.out.println("SEARCH"); //debug
                    showSearchResults(result);
                } else {
                    System.out.println("NOT SEARCH"); //debug
                    showFeedback(result);
                    showUpdatedTasks(result);
                }
            }
        });
    }
    
    private void showSearchResults(Result result) {
        VBox newPanel = createSearchPanel(result);
        taskPanel.getChildren().clear();
        taskPanel.getChildren().addAll(newPanel.getChildren());
    }
    
    private void showFeedback(Result result) {
        // TODO: refactor this mess!!!!
        final Popup window = new Popup();
        window.setAutoHide(true);
        window.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                window.hide();
            }
        });
        
        CommandType commandType = result.getCommandType();
        HBox box = createFeedback(commandType, result.isSuccess());
        window.getContent().add(box);
        double x = primaryStage.getX() + 10;
        double y = primaryStage.getY() + primaryStage.getHeight();
        window.setX(x);
        window.setY(y);
        window.show(primaryStage);
    }
    
    private HBox createFeedback(CommandType commandType, boolean  isSuccess) {
        HBox box = new HBox();
        Text message;
        ImageView icon;
        
        switch (commandType) {
            case ADD :
                message = new Text("Added task!");
                break;
                
            case EDIT :
                message = new Text("Edited task!");
                break;
                
            case DELETE :
                message = new Text("Deleted task!");
                break;
                
            default :
                message = new Text("Invalid!");
        }
        
        if (isSuccess) {
            box.setId("popup-success");
            message.setId("popup-success-text");
            icon = new ImageView(new Image(RESOURCES_ICON_SUCCESS));
            icon.setId("popup-success-icon");
        } else {
            box.setId("popup-fail");
            message.setId("popup-fail-text");
            icon = new ImageView(new Image(RESOURCES_ICONS_FAIL));
            icon.setId("popup-fail-icon");
        }
        box.getChildren().addAll(icon, message);
        return box;
    }
    
    private void showUpdatedTasks(Result result) {
        processTasks(result.getResults());
        VBox newPanel = createTaskPanel();
        taskPanel.getChildren().clear();
        taskPanel.getChildren().addAll(newPanel.getChildren());
        VBox newCat = createCategoryPanel();
        categoryPanel.getChildren().clear();
        categoryPanel.getChildren().add(newCat);
    }
    
}
