package ui;

import java.util.ArrayList;

import common.Category;
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
    
    private static Stage stage;
    private static Scene mainView;
    private static VBox panelCat;
    private static VBox panelToday;
    private static VBox panelOthers;
    private static VBox panelSearch;
    private static Scene searchView;
    private static TextField userInput;
    private static TextField searchInput;
    
    private static Logic logic;

    private static final int WINDOW_HEIGHT = 350;
    private static final int WINDOW_WIDTH = 450;    
    private static final String PROGRAM_NAME = "GRIDTask";
    private static final String USER_INPUT_PROMPT = "Enter a task here!";
    private static final String HEADER_OTHERS = "Others";
    private static final String HEADER_CATEGORY = "Categories";
    private static final String HEADER_TODAY = "Today";
    private static final String HEADER_SEARCH = "Search results";
    
    private static final String CSS_MAIN_VIEW = "mainView.css";
    private static final String CSS_SEARCH_VIEW = "searchView.css";
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
        initialiseStage(primaryStage);
        initialiseUserInput();
        createMainView();
        createSearchView();
        stage.setScene(mainView);
        handleUserInteractions();
    }

    private void initialiseStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle(PROGRAM_NAME);
        stage.show();
    }

    private void initialiseUserInput() {
        // create text field for user input
        userInput = new TextField();
        userInput.setPromptText(USER_INPUT_PROMPT);
        searchInput = new TextField();
        searchInput.setPromptText(USER_INPUT_PROMPT);
    }
    
    private void createMainView() {
        GridPane root = new GridPane();
        root.getStyleClass().add(CSS_CLASS_ROOT);
        
        mainView = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        mainView.getStylesheets().add(GRIDTaskUI.class.getResource(CSS_MAIN_VIEW).toExternalForm());
        
        addCategoryPanel(root);
        addTodayPanel(root);
        addOthersPanel(root);
        root.add(userInput, 0, 4, 2, 1);
        
        setColumnWidths(root);
    }

    private void addCategoryPanel(GridPane parent) {
        Label header = new Label(HEADER_CATEGORY);
        header.getStyleClass().add(CSS_CLASS_HEADER);
        parent.add(header, 0, 0);
        
        panelCat = createPanelCategory();
        ScrollPane scrollPanelCat = new ScrollPane();
        scrollPanelCat.setContent(panelCat);
        parent.add(scrollPanelCat, 0, 1, 1, 3);
        
        GridPane.setVgrow(scrollPanelCat, Priority.ALWAYS);
    }

    private void addTodayPanel(GridPane parent) {
        Label header = new Label(HEADER_TODAY);
        header.getStyleClass().add(CSS_CLASS_HEADER);
        parent.add(header, 1, 0);
        
        panelToday = createPanelToday();
        ScrollPane scrollPanelToday = new ScrollPane();
        scrollPanelToday.setContent(panelToday);
        parent.add(scrollPanelToday, 1, 1);
        
        GridPane.setVgrow(scrollPanelToday, Priority.SOMETIMES);
    }

    private void addOthersPanel(GridPane parent) {
        Label header = new Label(HEADER_OTHERS);
        header.getStyleClass().add(CSS_CLASS_HEADER);
        parent.add(header, 1, 2);
        
        panelOthers = createPanelOthers();
        ScrollPane scrollPanelOthers = new ScrollPane();
        scrollPanelOthers.setContent(panelOthers);
        parent.add(scrollPanelOthers, 1, 3);
        
        GridPane.setVgrow(scrollPanelOthers, Priority.ALWAYS);
    }

    private void setColumnWidths(GridPane parent) {
        // set width of left column
        ColumnConstraints left = new ColumnConstraints();
        left.setPercentWidth(30);
        // set width of right column
        ColumnConstraints right = new ColumnConstraints();
        right.setPercentWidth(70);
        parent.getColumnConstraints().addAll(left, right);
    }
    
    private void setRowWidths(GridPane parent) {
        RowConstraints row2 = new RowConstraints();
        row2.setVgrow(Priority.ALWAYS);
        RowConstraints row4 = new RowConstraints();
        row4.setVgrow(Priority.ALWAYS);
        parent.getRowConstraints().addAll(new RowConstraints(), row2, new RowConstraints(), row4);
    }

    private VBox createPanelCategory() {
        ArrayList<HBox> categories = retrieveCategories();
        VBox pane = new VBox();
        pane.getChildren().addAll(categories);
        pane.getStyleClass().add(CSS_CLASS_PANEL_CAT);
        return pane;
    }
    
    private ArrayList<HBox> retrieveCategories() {
        ArrayList<Category> cats = GRIDTaskLogic.getCategories();
        ArrayList<HBox> entries = new ArrayList<HBox>();
        for (Category cat : cats) {
            HBox entry = createCategoryEntry(cat);
            entries.add(entry);
        }
        return entries;
    }

    private HBox createCategoryEntry(Category cat) {
        HBox entry = new HBox();
        Label name = new Label(cat.getName() + " [" + cat.getNum() + "]");
        name.setAlignment(Pos.CENTER_LEFT);
        name.setMaxWidth(Double.MAX_VALUE);
        entry.getChildren().add(name);
        entry.getStyleClass().add(CSS_CLASS_ENTRY_CAT);
        return entry;
    }
    
    private VBox createPanelToday() {
        ArrayList<VBox> tasks = initialiseTodayTasks();
        VBox pane = new VBox();
        pane.getChildren().addAll(tasks);
        pane.getStyleClass().add(CSS_CLASS_PANEL_TASK);
        return pane;
    }
 
    private ArrayList<VBox> initialiseTodayTasks() {
        ArrayList<Task> tasks = GRIDTaskLogic.getTodayTasks();
        ArrayList<VBox> entries = new ArrayList<VBox>();
        for (Task task : tasks) {
            VBox entry = createTaskEntry(task);
            entries.add(entry);
        }
        return entries;
    }
       
    private VBox createPanelOthers() {
        ArrayList<VBox> tasks = initialiseOtherTasks();
        VBox pane = new VBox();
        pane.getChildren().addAll(tasks);
        pane.getStyleClass().add(CSS_CLASS_PANEL_TASK);
        return pane;
    }

    private ArrayList<VBox> initialiseOtherTasks() {
        ArrayList<Task> tasks = GRIDTaskLogic.getOtherTasks();
        ArrayList<VBox> entries = new ArrayList<VBox>();
        for (Task task : tasks) {
            VBox entry = createTaskEntry(task);
            entries.add(entry);
        }
        return entries;
    }
    
    private VBox createTaskEntry(Task task) {
        VBox entry = new VBox();
        entry.getStyleClass().add(CSS_CLASS_ENTRY_TASK);
        
        Label desc = new Label(task.getID() + ". " + task.getDescription());
        desc.getStyleClass().add(CSS_CLASS_ENTRY_TASK_DESCRIPTION);
        
        HBox details = new HBox();
        String field;
        if ((field = task.getStartDate()) != null) {
            Label startDate = new Label("From " + field);
            details.getChildren().add(startDate);
            startDate.getStyleClass().add("details");
        }
        if ((field = task.getEndDate()) != null) {
            Label endDate = new Label("To " + field);
            details.getChildren().add(endDate);
            endDate.getStyleClass().add("details");
        }
        
        entry.getChildren().addAll(desc, details);
        return entry;
    }
    
    private void createSearchView() {
        GridPane root = new GridPane();
        root.getStyleClass().add(CSS_CLASS_ROOT);
        
        searchView = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        searchView.getStylesheets().add(GRIDTaskUI.class.getResource(CSS_SEARCH_VIEW).toExternalForm());
        
        addCategoryPanel(root);
        addSearchPanel(root);
        root.add(searchInput, 0, 4, 2, 1);
        setColumnWidths(root);
        setRowWidths(root);
    }

    private void addSearchPanel(GridPane parent) {
        Label header = new Label(HEADER_SEARCH);
        header.getStyleClass().add(CSS_CLASS_HEADER);
        parent.add(header, 1, 0);
        
        panelSearch = createPanelSearch();
        ScrollPane scrollPanel = new ScrollPane();
        scrollPanel.setContent(panelSearch);
        parent.add(scrollPanel, 1, 1, 1, 3);
        
        GridPane.setVgrow(scrollPanel, Priority.ALWAYS);
    }
    
    private VBox createPanelSearch() {
        VBox pane = new VBox();
        pane.getStyleClass().add(CSS_CLASS_PANEL_SEARCH);
        return pane;
    }
    
    private void handleUserInteractions() {
        logic = new Logic();
        userInput.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String input = userInput.getText();
                System.out.println(input);
                userInput.setText("");
                Result result = logic.processCommand(input);
                if (result.isSearchCommand()) {
                    updateSearchView(result);
                    stage.setScene(searchView);
                } else {
                    showFeedback(result);
                    updateMainView(result);
                    stage.setScene(mainView);
                }
            }
        });
        searchInput.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String input = searchInput.getText();
                System.out.println(input);
                searchInput.setText("");
                Result feedback = logic.processCommand(input);
                if (feedback.isSearchCommand()) {
                    updateSearchView(feedback);
                    stage.setScene(searchView);
                } else {
                    showFeedback(feedback);
                    updateMainView(feedback);
                    stage.setScene(mainView);
                }
            }
        });
    }
 
    private void showFeedback(Result feedback) {
        final Popup window = new Popup();
        window.setAutoHide(true);
        window.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                window.hide();
            }
        });
        HBox box = new HBox();
        Text message = new Text(feedback.getMessage());
        ImageView icon;
        if (feedback.isSuccess()) {
            box.setId("popup-success");
            message.setId("popup-success-text");
            icon = new ImageView(new Image("File:resources/icons/success-smaller.png"));
            icon.setId("popup-success-icon");
        } else {
            box.setId("popup-fail");
            message.setId("popup-fail-text");
            icon = new ImageView(new Image("File:resources/icons/fail-smaller.png"));
            icon.setId("popup-fail-icon");
        }
        box.getChildren().addAll(icon, message);
        window.getContent().add(box);
        double x = stage.getX() + 10;
        double y = stage.getY() + stage.getHeight();
        window.setX(x);
        window.setY(y);
        window.show(stage);
    }

    private void updateSearchView(Result feedback) {
        ArrayList<Task> results = feedback.getResults();
        ArrayList<VBox> tasks = retrieveSearchTasks(results);
        panelSearch.getChildren().clear();
        panelSearch.getChildren().addAll(tasks);
        panelSearch.getStyleClass().add(CSS_CLASS_PANEL_TASK);
    }
    
    private ArrayList<VBox> retrieveSearchTasks(ArrayList<Task> results) {
        ArrayList<VBox> entries = new ArrayList<VBox>();
        for (Task result : results) {
            VBox entry = createTaskEntry(result);
            entries.add(entry);
        }
        return entries;
    }
            
    private void updateMainView(Result feedback) {
        ArrayList<HBox> categories = retrieveCategories();
        panelCat.getChildren().clear();
        panelCat.getChildren().addAll(categories);
        
        ArrayList<Task> results = feedback.getResults();
        
        ArrayList<VBox> todayTasks = retrieveTodayTasks(results);
        panelToday.getChildren().clear();
        panelToday.getChildren().addAll(todayTasks);
        
        ArrayList<VBox> otherTasks = retrieveOtherTasks(results);
        panelOthers.getChildren().clear();
        panelOthers.getChildren().addAll(otherTasks);
    }

    private ArrayList<VBox> retrieveTodayTasks(ArrayList<Task> results) {
        ArrayList<VBox> entries = new ArrayList<VBox>();
        for (Task result : results) {
            //if (isToday(result)) {
                VBox entry = createTaskEntry(result);
                entries.add(entry);
            //}
        }
        return entries;
    }

    private ArrayList<VBox> retrieveOtherTasks(ArrayList<Task> results) {
        ArrayList<VBox> entries = new ArrayList<VBox>();
        for (Task result : results) {
            //if (!isToday(result)) {
                VBox entry = createTaskEntry(result);
                entries.add(entry);
            //}
        }
        return entries;
    }
    
    private boolean isToday(Task task) {
        // TODO
        return false;
    }

}
