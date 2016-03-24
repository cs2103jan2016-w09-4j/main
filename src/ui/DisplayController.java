package ui;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.controlsfx.control.HiddenSidesPane;

import common.Command.CommandType;
import common.Result;
import common.Task;
import ui.MainApp;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class DisplayController extends HiddenSidesPane {

    private static Logger logger = Logger.getLogger("MainApp.DisplayController");
    private MainApp main;
    private Stage primaryStage;
    
    @FXML private VBox mainPanel;
    private VBox taskPanel, searchPanel, completedPanel;
    private SidebarController sidebar;
    private Popup feedback;
    
    private static final String DISPLAY_FXML = "Display.fxml";
    private static final String SEARCH_HEADER_SINGLE = " search result found";
    private static final String SEARCH_HEADER_PLURAL = " search results found";
    private static final String COMPLETED_HEADER_SINGLE = " completed task";
    private static final String COMPLETED_HEADER_PLURAL = " completed tasks";
    private static final String RESOURCES_ICON_SUCCESS = "/icons/success-smaller.png";
    private static final String RESOURCES_ICONS_FAIL = "/icons/fail-smaller.png";

    public DisplayController(MainApp main, Stage primaryStage) {
        this.main = main;
        this.primaryStage = primaryStage;
        initializeLogger();
        loadFXML();
        initializeTaskPanel();
        initializeSearchPanel();
        initializeCompletedPanel();
        initializeSidebarContent();
        initializePopup();
        handleUserInteractions();
    }

    private void initializeLogger() {
        try {
            Handler fh = new FileHandler("log_ui_display");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();  
            fh.setFormatter(formatter);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadFXML() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(DISPLAY_FXML));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "failed to load Display", e);
        }
    }
    
    private void initializeTaskPanel() {
        taskPanel = new VBox();
        taskPanel.getStyleClass().add("panel-task");
        this.setContent(taskPanel);
        
        ArrayList<Task> allTasks = main.getTasks();
        updateTaskPanel(allTasks);
    }

    private void initializeSearchPanel() {
        searchPanel = new VBox();
        searchPanel.getStyleClass().add("panel-search");
    }
    
    private void initializeCompletedPanel() {
        completedPanel = new VBox();
        completedPanel.getStyleClass().add("panel-task");
    }

    private void initializeSidebarContent() {
        sidebar = new SidebarController(main);
        this.setLeft(sidebar);
        this.setTriggerDistance(30);
    }
    
    private void initializePopup() {
        feedback = new Popup();
        feedback.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                feedback.hide();
            }
        });
    }
    
    private void handleUserInteractions() {
        DisplayController instance = this;
        this.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            
            public void handle(KeyEvent event) {
                if (event.getCode()==KeyCode.M && event.isControlDown()) {
                    logger.log(Level.INFO, "user toggled sidebar");
                    if (instance.getPinnedSide() == Side.LEFT) {
                        instance.setPinnedSide(null);
                    } else {
                        instance.setPinnedSide(Side.LEFT);
                    }
                } else if (event.getCode()==KeyCode.HOME) {
                    logger.log(Level.INFO, "user pressed home");
                    instance.setContent(taskPanel);
                }
            }
            
        });
    }
    
    public void displayResult(Result result) {
        CommandType cmd = result.getCommandType();
        if (cmd == CommandType.SEARCH) {
            updateSearchPanel(result.getResults());
            this.setContent(searchPanel);
        } else if (cmd == CommandType.COMPLETE || cmd == CommandType.SEARCHOLD) {
            updateCompletedPanel(result.getResults());
            this.setContent(completedPanel);
        } else {
            ArrayList<Task> allTasks = result.getResults();
            updateTaskPanel(allTasks);
            sidebar.update();
            showFeedback(cmd, result.getMessage(), result.isSuccess());
            this.setContent(taskPanel);
        }
    }
    
    private void updateTaskPanel(ArrayList<Task> allTasks) {
        LocalDateTime todayDate = LocalDateTime.now();
        ArrayList<VBox> todayTasks = new ArrayList<VBox>();
        ArrayList<VBox> otherTasks = new ArrayList<VBox>();
        for (Task task : allTasks) {
            if (task.isSameDate(todayDate)) {
                todayTasks.add(createToday(task, task.getId()));
            } else {
                otherTasks.add(createOther(task, task.getId()));
            }
        }
        
        // build today panel
        Label todayHeader = createHeader("Today");
        VBox todayContent = new VBox();
        if (todayTasks.isEmpty()) {
            Label empty = new Label("No tasks today");
            todayContent.getChildren().add(empty);
        } else {
            ObservableList<VBox> todayList = FXCollections.observableArrayList(todayTasks);
            ListView<VBox> todayListView = new ListView<VBox>(todayList);
            todayListView.prefHeightProperty().bind(Bindings.size(todayList).multiply(65));
            todayContent.getChildren().add(todayListView);
        }
        
        VBox todayPanel = new VBox();
        VBox.setVgrow(todayContent, Priority.ALWAYS);
        todayPanel.getChildren().addAll(todayHeader, todayContent);
        
        // build other panel
        Label otherHeader = createHeader("Others");
        VBox otherContent = new VBox();
        if (otherTasks.isEmpty()) {
            Label empty = new Label("No tasks today");
            otherContent.getChildren().add(empty);
        } else {
            ObservableList<VBox> otherList = FXCollections.observableArrayList(otherTasks);
            ListView<VBox> otherListView = new ListView<VBox>(otherList);
            otherListView.prefHeightProperty().bind(Bindings.size(otherList).multiply(65));
            otherContent.getChildren().add(otherListView);
        }
        
        VBox otherPanel = new VBox();
        VBox.setVgrow(otherContent, Priority.ALWAYS);
        otherPanel.getChildren().addAll(otherHeader, otherContent);
        
        // add to task panel
        taskPanel.getChildren().clear();
        taskPanel.getChildren().addAll(todayPanel, otherPanel);
    }
    
    private void updateSearchPanel(ArrayList<Task> results) {
        searchPanel.getChildren().clear();
        Label searchHeader = createHeader(results.size() + (results.size() == 1 ? SEARCH_HEADER_SINGLE : SEARCH_HEADER_PLURAL));
        ArrayList<VBox> searchTasks = new ArrayList<VBox>();
        for (Task result : results) {
            searchTasks.add(createOther(result, result.getId()));
        }
        ObservableList<VBox> searchList = FXCollections.observableArrayList(searchTasks);
        ListView<VBox> searchListView = new ListView<VBox>(searchList);
        searchListView.prefHeightProperty().bind(Bindings.size(searchList).multiply(58));
        searchPanel.getChildren().addAll(searchHeader, searchListView);
    }
    
    private void updateCompletedPanel(ArrayList<Task> results) {
        completedPanel.getChildren().clear();
        Label completedHeader = createHeader(results.size() + (results.size() == 1 ? COMPLETED_HEADER_SINGLE : COMPLETED_HEADER_PLURAL));
        ArrayList<VBox> completedTasks = new ArrayList<VBox>();
        for (Task result : results) {
            completedTasks.add(createCompleted(result));
        }
        ObservableList<VBox> completedList = FXCollections.observableArrayList(completedTasks);
        ListView<VBox> completedListView = new ListView<VBox>(completedList);
        completedListView.prefHeightProperty().bind(Bindings.size(completedList).multiply(58));
        completedPanel.getChildren().addAll(completedHeader, completedListView);
    }

    private void showFeedback(CommandType cmd, String msg, boolean isSuccess) {
        logger.log(Level.INFO, String.format("showing feedback for %1s, %2s", cmd, isSuccess));

        HBox box = createFeedback(cmd, msg, isSuccess);
        feedback.getContent().clear();
        feedback.getContent().add(box);
        double x = primaryStage.getX() + 10;
        double y = primaryStage.getY() + primaryStage.getHeight();
        feedback.setX(x);
        feedback.setY(y);
        feedback.show(primaryStage);
    }
    
    private HBox createFeedback(CommandType commandType, String msg, boolean isSuccess) {
        HBox box = new HBox();
        box.getStylesheets().add(getClass().getResource("feedback.css").toExternalForm()); 
        Text message = new Text(msg);
        ImageView icon;
        
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
    
    private Label createHeader(String heading) {
        Label header = new Label(heading);
        header.getStyleClass().add("header");
        header.setMinHeight(USE_PREF_SIZE);
        return header;
    }
    
    private VBox createToday(Task task, int index) {
        VBox entry = new VBox();
        Label desc = new Label(index + ". " + task.getDescription());
        
        HBox details = new HBox();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        Label startEndDate = null;
        
        if (task.isDeadline()) {
            String endDate = timeFormatter.format(task.getEndDate());
            startEndDate = new Label("By " + endDate);
        } else if (task.isEvent()) {
            LocalDateTime start = task.getStartDate();
            LocalDateTime end = task.getEndDate();
            LocalDate now = LocalDate.now();
            if (start.toLocalDate().equals(now) && end.toLocalDate().equals(now)) {
                String startDate = timeFormatter.format(start);
                String endDate = timeFormatter.format(end);
                startEndDate = new Label("From " + startDate + " to " + endDate);
            } else if (start.toLocalDate().equals(now)) {
                String startDate = timeFormatter.format(start);
                String endDate = dateFormatter.format(end);
                startEndDate = new Label("From " + startDate + " to " + endDate);
            } else if (end.toLocalDate().equals(now)) {
                String startDate = dateFormatter.format(start);
                String endDate = timeFormatter.format(end);
                startEndDate = new Label("From " + startDate + " to " + endDate);
            }
            
        }

        if (startEndDate != null) {
            startEndDate.getStyleClass().add("details");
            details.getChildren().add(startEndDate);
        }
        
        entry.getChildren().addAll(desc, details);
        entry.getStyleClass().add("entry-task");
        return entry;
    }
    
    private VBox createOther(Task task, int index) {
        VBox entry = new VBox();
        
        Label desc = new Label(index + ". " + task.getDescription());
        
        HBox details = new HBox();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        Label startEndDate = null;
        
        if (task.isDeadline()) {
            String endDate = dateFormatter.format(task.getEndDate());
            startEndDate = new Label("By " + endDate);
        } else if (task.isEvent()) {
            LocalDateTime start = task.getStartDate();
            LocalDateTime end = task.getEndDate();
            LocalDate now = LocalDate.now();
            if (start.toLocalDate().equals(now) && end.toLocalDate().equals(now)) {
                String startDate = dateFormatter.format(start);
                String endDate = dateFormatter.format(end);
                startEndDate = new Label("From " + startDate + " to " + endDate);
            } else if (start.toLocalDate().equals(now)) {
                String startDate = dateFormatter.format(start);
                String endDate = dateFormatter.format(end);
                startEndDate = new Label("From " + startDate + " to " + endDate);
            } else if (end.toLocalDate().equals(now)) {
                String startDate = dateFormatter.format(start);
                String endDate = dateFormatter.format(end);
                startEndDate = new Label("From " + startDate + " to " + endDate);
            }
            
        }

        if (startEndDate != null) {
            startEndDate.getStyleClass().add("details");
            details.getChildren().add(startEndDate);
        }
        
        entry.getChildren().addAll(desc, details);
        entry.getStyleClass().add("entry-task");
        return entry;
    }
    
    private VBox createCompleted(Task task) {
        VBox entry = new VBox();
        
        Label desc = new Label(task.getDescription());
        
        HBox details = new HBox();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        Label startEndDate = null;
        
        if (task.isDeadline()) {
            String endDate = dateFormatter.format(task.getEndDate());
            startEndDate = new Label("By " + endDate);
        } else if (task.isEvent()) {
            LocalDateTime start = task.getStartDate();
            LocalDateTime end = task.getEndDate();
            LocalDate now = LocalDate.now();
            if (start.toLocalDate().equals(now) && end.toLocalDate().equals(now)) {
                String startDate = dateFormatter.format(start);
                String endDate = dateFormatter.format(end);
                startEndDate = new Label("From " + startDate + " to " + endDate);
            } else if (start.toLocalDate().equals(now)) {
                String startDate = dateFormatter.format(start);
                String endDate = dateFormatter.format(end);
                startEndDate = new Label("From " + startDate + " to " + endDate);
            } else if (end.toLocalDate().equals(now)) {
                String startDate = dateFormatter.format(start);
                String endDate = dateFormatter.format(end);
                startEndDate = new Label("From " + startDate + " to " + endDate);
            }
            
        }

        if (startEndDate != null) {
            startEndDate.getStyleClass().add("details");
            details.getChildren().add(startEndDate);
        }
        
        entry.getChildren().addAll(desc, details);
        entry.getStyleClass().add("entry-task");
        return entry;
    }
    
}