//@@author Ruoling
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
import common.Category;
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
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
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
    
    @FXML
    private VBox taskPanel, searchPanel, donePanel, helpPanel;
    @FXML
    VBox todayList, otherList;
    @FXML
    GridPane catList, helpList;
    private Popup feedback;

    private static final int UNMODIFIED = -1;
    private static final String HEADER_COMPLETED_SINGLE = " completed task";
    private static final String HEADER_COMPLETED_PLURAL = " completed tasks";
    private static final String HEADER_SEARCH_SINGLE = " search result found";
    private static final String HEADER_SEARCH_PLURAL = " search results found";
    private static final String TASK_DETAILS_DATE_FLOATING = "From %s";
    private static final String TASK_DETAILS_DATE_DEADLINE = "By %s";
    private static final String TASK_DETAILS_DATE_EVENT = "From %s to %s";

    private static final String FXML_DISPLAY = "Display.fxml";
    private static final String RESOURCES_ICON_SUCCESS = "/icons/success-small.png";
    private static final String RESOURCES_ICON_SUCCESS_DELETE = "/icons/delete-success-small.png";
    private static final String RESOURCES_ICON_SUCCESS_SAVE = "/icons/save-success-small.png";
    private static final String RESOURCES_ICON_FAIL = "/icons/fail-small.png";
    private static final String RESOURCES_ICON_FAIL_DELETE = "/icons/delete-fail-small.png";
    private static final String RESOURCES_ICON_FAIL_SAVE = "/icons/save-fail-small.png";
    private static final String RESOURCES_ICON_PRIORITY = "/icons/priority.png";
    private static final String RESOURCES_ICON_SIDEBAR = "/icons/sidebar.png";

    public DisplayController(MainApp main, Stage primaryStage) {
        this.main = main;
        this.primaryStage = primaryStage;
        this.setFocusTraversable(false);
        //initializeLogger();
        loadFXML();
        initializeTaskPanel();
        initializeHelpPanel();
        initializeSidebar();
        initializePopup();
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_DISPLAY));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "failed to load Display", e);
        }
    }
    
    private void initializeTaskPanel() {
        ArrayList<Task> allTasks = main.getTasks();
        updateTaskPanel(allTasks);
        setMainPanelContent(taskPanel);
    }

    private void initializeHelpPanel() {
        String[] commands = { "Add a task",
                "Edit a task",
                "Search tasks",
                "Mark as done",
                "Access done tasks",
                "Delete a task",
                "Undo",
                "Redo",
                "Show help guide",
                "Exit",
                "Save file",
                "Load file",
                "View sidebar" };

        String[] formats = { "add <description>",
                       "edit <task number> <new description> <new timing> #<new category>",
                       "search <keyword / date / #category>",
                       "done <task number>",
                       "searchdone <keyword / date / #category>",
                       "delete <task number>",
                       "undo",
                       "redo",
                       "help",
                       "exit",
                       "save <directory> <filename>",
                       "load <directory> <filename>",
                       "CTRL+M" };
        
        assert (commands.length == formats.length);
        
        for (int i = 0; i < commands.length; i++) {
            Label cmd = new Label(commands[i]);
            helpList.add(cmd, 0, i);
            Label format = new Label(formats[i]);
            helpList.add(format, 1, i);
        }
    }

    private void initializeSidebar() {
        ArrayList<Category> categories = main.getCategories();
        updateSidebar(categories);
        this.setTriggerDistance(50);
    }
    
    private void initializePopup() {
        feedback = new Popup();
        feedback.setAutoHide(true);
        // hide feedback popup on any key press
        feedback.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                feedback.hide();
            }
        });
    }
    
    /***********************************************
     * PUBLIC APIS TO MANAGE DISPLAY OF MAIN PANEL *
     ***********************************************/
    
    public void toggleSidebar() {
        if (getPinnedSide() == Side.LEFT) {
            setPinnedSide(null);
        } else {
            setPinnedSide(Side.LEFT);
        }
    }
    
    /**
     * Displays the result of the command to the user.
     * 
     * @param result    the result to be shown to the user
     */
    public void displayResult(Result result) {
        assert (result != null);
        CommandType cmdType = result.getCommandType();
        switch(cmdType) {
            case INVALID :
                showFeedback(cmdType, result.getMessage(), result.isSuccess());
                break;

            case SEARCH :
                updateSearchPanel(result.getResults());
                setMainPanelContent(searchPanel);
                break;
                
            case SEARCHDONE :
                updateCompletedPanel(result.getResults());
                setMainPanelContent(donePanel);
                break;
            
            case HELP :
                setMainPanelContent(helpPanel);
                break;
            
            default :
                updateSidebar(main.getCategories());
                updateTaskPanel(result.getResults());
                showFeedback(cmdType, result.getMessage(), result.isSuccess());
                setMainPanelContent(taskPanel);
                break;
        }
    }

    /******************
     * HELPER METHODS *
     ******************/

    private void setMainPanelContent(Node node) {
        this.setContent(node);
    }

    private void updateTaskPanel(ArrayList<Task> allTasks) {
        assert (allTasks != null);
        // add sidebar button
        ImageView sidebarIcon = new ImageView(new Image(RESOURCES_ICON_SIDEBAR));
        
        // group tasks in Today or Others section
        LocalDateTime todayDateTime = LocalDateTime.now();
        LocalDate todayDate = todayDateTime.toLocalDate();
        ObservableList<VBox> todayTasks = FXCollections.observableArrayList();
        ObservableList<VBox> otherTasks = FXCollections.observableArrayList();
        // index of tasks to scroll to
        int todayModified = UNMODIFIED;
        int otherModified = UNMODIFIED;
        for (Task task : allTasks) {
            VBox entry = createOngoingEntry(task, todayDateTime);
            if (task.isOccurringOn(todayDate)) {
                todayTasks.add(entry);
                if (task.isModified()) {
                    todayModified = todayTasks.size() - 1;
                }
            } else {
                otherTasks.add(entry);
                if (task.isModified()) {
                    otherModified = otherTasks.size() - 1;
                }
            }
        }
        
        // build today panel
        VBox todayPanel = setTodayList(todayTasks, todayModified);
        // build other panel
        VBox otherPanel = setOtherList(otherTasks, otherModified);
        
        taskPanel.getChildren().clear();
        taskPanel.getChildren().addAll(sidebarIcon, todayPanel, otherPanel);
    }

    private VBox setOtherList(ObservableList<VBox> otherTasks, int otherModified) {
        VBox otherPanel = new VBox();
        if (otherTasks.isEmpty()) {
            Label empty = new Label("No other tasks pending this week! Type 'search' to view all tasks.");
            empty.getStyleClass().add("entry-empty");
            otherPanel.getChildren().add(empty);
        } else {
            ListView<VBox> otherListView = createListView(otherTasks);
            otherListView.setMaxHeight(primaryStage.getHeight()/2.5);
            if (otherModified != UNMODIFIED) {
                otherListView.scrollTo(otherModified);
            }
            otherPanel.getChildren().add(otherListView);
        }
        return otherPanel;
    }

    private void setTodayList(ObservableList<VBox> todayTasks, int todayModified) {
        VBox todayPanel = new VBox();
        if (todayTasks.isEmpty()) {
            Label empty = new Label("No tasks due today!");
            empty.getStyleClass().add("entry-empty");
            todayList.getChildren().add(empty);
        } else {
            ListView<VBox> todayListView = createListView(todayTasks);
            todayList.getChildren().add(todayListView);
            todayListView.setMaxHeight(primaryStage.getHeight()/2.5);
            if (todayModified != UNMODIFIED) {
                todayListView.scrollTo(todayModified);
            }
        }
    }

    private void updateSearchPanel(ArrayList<Task> results) {
        assert (searchPanel != null);
        LocalDateTime todayDate = LocalDateTime.now();
        
        Label searchHeader = createHeader(results.size() + (results.size() == 1 ? HEADER_SEARCH_SINGLE : HEADER_SEARCH_PLURAL));
        ObservableList<VBox> searchTasks = FXCollections.observableArrayList();
        for (Task result : results) {
            searchTasks.add(createOngoingEntry(result, todayDate));
        }
        ListView<VBox> searchListView = createListView(searchTasks);
        
        searchPanel.getChildren().clear();
        searchPanel.getChildren().addAll(searchHeader, searchListView);
    }
    
    private void updateCompletedPanel(ArrayList<Task> results) {
        assert (donePanel != null);

        Label completedHeader = createHeader(results.size() + (results.size() == 1 ? HEADER_COMPLETED_SINGLE : HEADER_COMPLETED_PLURAL));
        ObservableList<VBox> completedTasks = FXCollections.observableArrayList();
        for (Task result : results) {
            completedTasks.add(createCompletedEntry(result));
        }
        ListView<VBox> completedListView = createListView(completedTasks);
        
        donePanel.getChildren().clear();
        donePanel.getChildren().addAll(completedHeader, completedListView);
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
            switch(commandType) {
                case DELETE :
                    icon = new ImageView(new Image(RESOURCES_ICON_SUCCESS_DELETE));
                    break;
                    
                case SAVE :
                    icon = new ImageView(new Image(RESOURCES_ICON_SUCCESS_SAVE));
                    break;
                    
                default :
                    icon = new ImageView(new Image(RESOURCES_ICON_SUCCESS));
                    break;
            }
        } else {
            box.setId("popup-fail");
            message.setId("popup-fail-text");
            switch(commandType) {
                case DELETE :
                icon = new ImageView(new Image(RESOURCES_ICON_FAIL_DELETE));
                break;
                
                case SAVE :
                icon = new ImageView(new Image(RESOURCES_ICON_FAIL_SAVE));
                break;
                
                default :
                icon = new ImageView(new Image(RESOURCES_ICON_FAIL));
                break;
            }
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

    private ListView<VBox> createListView(ObservableList<VBox> tasks) {
        ListView<VBox> listView = new ListView<VBox>(tasks);
        // override ListView's default height value 400
        listView.prefHeightProperty().bind(Bindings.size(tasks).multiply(66));
        return listView;
    }
    
    private VBox createOngoingEntry(Task task, LocalDateTime today) {
        Label desc = new Label(task.getId() + ". " + task.getDescription());
        HBox details = new HBox();
        details.getStyleClass().add("details");
        
        // add priority icon
        if (task.isImportant()) {
            ImageView icon = new ImageView(new Image(RESOURCES_ICON_PRIORITY));
            details.getChildren().add(icon);
        }
        
        // add date details
        LocalDate now = today.toLocalDate();
        Label startEndDate = createOngoingTaskDates(task, now);
        if (startEndDate != null) {
            startEndDate.getStyleClass().add("details-text");
            details.getChildren().add(startEndDate);
        }
        
        // add categories
        ArrayList<String> categories = task.getCategories();
        for (String cat : categories) {
            Label catName = new Label("#" + cat);
            catName.getStyleClass().add("details-text");
            details.getChildren().add(catName);
        }
        
        VBox entry = new VBox();
        entry.getChildren().addAll(desc, details);
        if (task.isModified()) {
            entry.getStyleClass().add("entry-task-modified");
        } else if (task.isOverdue(today)) {
            entry.getStyleClass().add("entry-task-overdue");
        } else {
            entry.getStyleClass().add("entry-task");
        }
        return entry;
    }
    
    private VBox createCompletedEntry(Task task) {
        Label desc = new Label(task.getDescription());
        HBox details = new HBox();
        details.getStyleClass().add("details");

        // add priority icon
        if (task.isImportant()) {
            ImageView icon = new ImageView(new Image(RESOURCES_ICON_PRIORITY));
            details.getChildren().add(icon);
        }
        
        // add date details
        Label startEndDate = createCompletedTaskDates(task);
        if (startEndDate != null) {
            startEndDate.getStyleClass().add("details-text");
            details.getChildren().add(startEndDate);
        }
        
        // add categories
        ArrayList<String> categories = task.getCategories();
        for (String cat : categories) {
            Label catName = new Label("#" + cat);
            catName.getStyleClass().add("details-text");
            details.getChildren().add(catName);
        }
        
        VBox entry = new VBox();
        entry.getChildren().addAll(desc, details);
        entry.getStyleClass().add("entry-task");
        return entry;
    }

    private Label createOngoingTaskDates(Task task, LocalDate now) {
        Label startEndDate = null;

        if (task.isFloating()) {
            LocalDateTime start = task.getStartDate();
            if (start != null) {
                String startDate = formatDate(now, start);
                startEndDate = new Label(String.format(TASK_DETAILS_DATE_FLOATING, startDate));
            }
        } else if (task.isDeadline()) {
            LocalDateTime end = task.getEndDate();
            String endDate = formatDate(now, end);
            startEndDate = new Label(String.format(TASK_DETAILS_DATE_DEADLINE, endDate));
        } else if (task.isEvent()) {
            LocalDateTime start = task.getStartDate();
            LocalDateTime end = task.getEndDate();
            String startDate = formatDate(now, start);
            String endDate = formatDate(now, end);
            startEndDate = new Label(String.format(TASK_DETAILS_DATE_EVENT, startDate, endDate));
        }
        return startEndDate;
    }

    private String formatDate(LocalDate now, LocalDateTime dateTime) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        if (dateTime.toLocalDate().isEqual(now)) {
            return timeFormatter.format(dateTime);
        } else {
            return dateFormatter.format(dateTime);
        }
    }

    private Label createCompletedTaskDates(Task task) {
        Label startEndDate = null;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        if (task.isFloating()) {
            LocalDateTime start = task.getStartDate();
            if (start != null) {
                String startDate = dateFormatter.format(start);
                startEndDate = new Label(String.format(TASK_DETAILS_DATE_FLOATING, startDate));
            }
        } else if (task.isDeadline()) {
            String endDate = dateFormatter.format(task.getEndDate());
            startEndDate = new Label(String.format(TASK_DETAILS_DATE_DEADLINE, endDate));
        } else if (task.isEvent()) {
            LocalDateTime start = task.getStartDate();
            LocalDateTime end = task.getEndDate();
            String startDate = dateFormatter.format(start);
            String endDate = dateFormatter.format(end);
            startEndDate = new Label(String.format(TASK_DETAILS_DATE_EVENT, startDate, endDate));
        }
        return startEndDate;
    }
    
    private void updateSidebar(ArrayList<Category> categories) {
        catList.getChildren().clear();
        for (int i = 0; i < categories.size(); i++) {
            Category cat = categories.get(i);
            Label name = new Label(cat.getName());
            catList.add(name, 0, i);
            Label num = new Label(Integer.toString(cat.getCount()));
            catList.add(num, 1, i);
        }
    }
    
    private HBox createCategoryEntry(Category cat) {
        Label name = new Label(cat.getName());
        HBox nameBox = new HBox();
        nameBox.getChildren().add(name);
        HBox.setHgrow(nameBox, Priority.ALWAYS);
        
        Label count = new Label(Integer.toString(cat.getCount()));
        count.getStyleClass().add("count");
        
        HBox entry = new HBox();
        entry.getChildren().addAll(nameBox, count);
        entry.getStyleClass().add("entry-cat");
        return entry;
    }
    
}
