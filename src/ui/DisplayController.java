//@@author Ruoling
package ui;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DisplayController extends HiddenSidesPane {

    private static Logger logger = Logger.getLogger("MainApp.DisplayController");
    private MainApp main;
    private Stage primaryStage;
    
    @FXML private VBox taskPanel, searchPanel, donePanel, helpPanel;
    @FXML private GridPane categoryList, helpList;
    @FXML private VBox todayList, otherList;

    private static final int UNMODIFIED = -1;
    private static final String HEADER_DONE_SINGLE = " completed task";
    private static final String HEADER_DONE_PLURAL = " completed tasks";
    private static final String HEADER_SEARCH_SINGLE = " search result found";
    private static final String HEADER_SEARCH_PLURAL = " search results found";
    private static final String LIST_TODAY_EMPTY = "No tasks due today!";
    private static final String LIST_OTHER_EMPTY = "No other tasks due this week! Type 'search' to view all tasks.";

    private static final String FXML_DISPLAY = "Display.fxml";
    private static final String RESOURCES_ICON_PRIORITY = "/icons/priority.png";

    public DisplayController(MainApp main, Stage primaryStage) {
        this.main = main;
        this.primaryStage = primaryStage;
        this.setFocusTraversable(false);
        //initializeLogger();
        loadFXML();
        initializeTaskPanel();
        initializeHelpPanel();
        initializeSidebar();
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
        ArrayList<Task> tasks = main.getTasks();
        updateTaskPanel(tasks);
        setMainPanelContent(taskPanel);
    }

    private void initializeHelpPanel() {
        String[] actions = { "Add a task",
                             "Edit a task",
                             "Search tasks",
                             "Mark as done",
                             "Search done tasks",
                             "Delete a task",
                             "Undo",
                             "Redo",
                             "Show help guide",
                             "Exit",
                             "Save file",
                             "Load file",
                             "View sidebar" };

        String[] commands = { "add <description> <date/time> #<category>",
                              "edit <task number> <new description> <new date/time> #<new category>",
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
        
        assert (actions.length == commands.length);
        
        // add action and its corresponding command to the next row
        for (int i = 0; i < actions.length; i++) {
            Label action = new Label(actions[i]);
            helpList.add(action, 0, i);
            Label command = new Label(commands[i]);
            helpList.add(command, 1, i);
        }
    }

    private void initializeSidebar() {
        ArrayList<Category> categories = main.getCategories();
        updateSidebar(categories);
        this.setTriggerDistance(50);
    }
    
    /***********************************************
     * PUBLIC APIS TO MANAGE DISPLAY OF MAIN PANEL *
     ***********************************************/
    
    public void toggleSidebar() {
        if (getPinnedSide() == Side.LEFT) {
            // close sidebar
            setPinnedSide(null);
        } else {
            // open sidebar
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
        CommandType command = result.getCommandType();
        assert (command != null);
        ArrayList<Task> tasks = result.getResults();
        switch (command) {
            case INVALID :
                break;

            case SEARCH :
                updateSearchPanel(tasks);
                setMainPanelContent(searchPanel);
                break;
                
            case SEARCHDONE :
                updateDonePanel(tasks);
                setMainPanelContent(donePanel);
                break;
            
            case HELP :
                setMainPanelContent(helpPanel);
                break;
            
            default :
                ArrayList<Category> categories = main.getCategories();
                updateSidebar(categories);
                updateTaskPanel(tasks);
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

    private void updateTaskPanel(ArrayList<Task> tasks) {
        assert (tasks != null);
        createTodayList(tasks);
        createOtherList(tasks);
    }

    private void createTodayList(ArrayList<Task> tasks) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate nowDate = now.toLocalDate();
        ObservableList<VBox> todayTasks = FXCollections.observableArrayList();
        // index of task to scroll to
        int todayModified = UNMODIFIED;
        for (Task task : tasks) {
            VBox entry = createOngoingTask(task, now);
            if (task.isOccurringOn(nowDate)) {
                todayTasks.add(entry);
                if (task.isModified()) {
                    todayModified = todayTasks.size() - 1;
                }
            }
        }
        updateTodayList(todayTasks, todayModified);
    }

    private void updateTodayList(ObservableList<VBox> todayTasks, int todayModified) {
        if (todayTasks.isEmpty()) {
            setEmptyList(todayList, LIST_TODAY_EMPTY);
        } else {
            setList(todayList, todayTasks, todayModified);
        }
    }

    private void createOtherList(ArrayList<Task> tasks) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate nowDate = now.toLocalDate();
        ObservableList<VBox> otherTasks = FXCollections.observableArrayList();
        // index of task to scroll to
        int otherModified = UNMODIFIED;
        for (Task task : tasks) {
            VBox entry = createOngoingTask(task, now);
            if (!task.isOccurringOn(nowDate)) {
                otherTasks.add(entry);
                if (task.isModified()) {
                    otherModified = otherTasks.size() - 1;
                }
            }
        }
        updateOtherList(otherTasks, otherModified);
    }

    private void updateOtherList(ObservableList<VBox> otherTasks, int otherModified) {
        if (otherTasks.isEmpty()) {
            setEmptyList(otherList, LIST_OTHER_EMPTY);
        } else {
            setList(otherList, otherTasks, otherModified);
        }
    }

    private void setEmptyList(VBox list, String prompt) {
        Label info = new Label(prompt);
        info.getStyleClass().add("entry-empty");
        updateChildren(list, info);
    }
    
    private void setList(VBox list, ObservableList<VBox> tasks, int modified) {
        ListView<VBox> listView = createListView(tasks);
        listView.maxHeightProperty().bind(primaryStage.heightProperty().divide(2.5));
        if (modified != UNMODIFIED) {
            listView.scrollTo(modified);
        }
        updateChildren(list, listView);
    }

    private void updateSearchPanel(ArrayList<Task> tasks) {
        assert (tasks != null);
        Label searchHeader = createHeader(tasks.size(), HEADER_SEARCH_SINGLE, HEADER_SEARCH_PLURAL);
        ObservableList<VBox> searchTasks = createSearchTasks(tasks);
        ListView<VBox> searchListView = createListView(searchTasks);
        updateChildren(searchPanel, searchHeader, searchListView);
    }

    private ObservableList<VBox> createSearchTasks(ArrayList<Task> tasks) {
        LocalDateTime todayDateTime = LocalDateTime.now();
        ObservableList<VBox> searchTasks = FXCollections.observableArrayList();
        for (Task task : tasks) {
            VBox entry = createOngoingTask(task, todayDateTime);
            searchTasks.add(entry);
        }
        return searchTasks;
    }
    
    private void updateDonePanel(ArrayList<Task> tasks) {
        assert (tasks != null);
        Label doneHeader = createHeader(tasks.size(), HEADER_DONE_SINGLE, HEADER_DONE_PLURAL);
        ObservableList<VBox> doneTasks = createDoneTasks(tasks);
        ListView<VBox> doneListView = createListView(doneTasks);
        updateChildren(donePanel, doneHeader, doneListView);
    }

    private ObservableList<VBox> createDoneTasks(ArrayList<Task> tasks) {
        ObservableList<VBox> doneTasks = FXCollections.observableArrayList();
        for (Task task : tasks) {
            VBox entry = createDoneEntry(task);
            doneTasks.add(entry);
        }
        return doneTasks;
    }
    
    private void updateChildren(Pane parent, Node... children) {
        parent.getChildren().clear();
        parent.getChildren().addAll(children);
    }
    
    private Label createHeader(int size, String single, String plural) {
        String heading = size + (size == 1 ? single : plural);
        Label header = new Label(heading);
        header.getStyleClass().add("header");
        header.setMinHeight(USE_PREF_SIZE);
        return header;
    }

    private ListView<VBox> createListView(ObservableList<VBox> tasks) {
        ListView<VBox> listView = new ListView<VBox>(tasks);
        // ListView's default height is fixed at 400 regardless of size of list
        // Workaround for resizing ListView by binding to (cell height * number)
        listView.prefHeightProperty().bind(Bindings.size(tasks).multiply(66));
        return listView;
    }
    
    private VBox createOngoingTask(Task task, LocalDateTime now) {
        Label description = formatOngoingTaskDescription(task);
        HBox details = formatOngoingTaskDetails(task, now);
        VBox entry = formatOngoingTaskEntry(task, now, description, details);
        return entry;
    }
    
    private Label formatOngoingTaskDescription(Task task) {
        int id = task.getId();
        String desc = task.getDescription();
        String descriptionString = id + ". " + desc;
        Label description = new Label(descriptionString);
        description.getStyleClass().add("desc");
        return description;
    }

    private HBox formatOngoingTaskDetails(Task task, LocalDateTime now) {
        HBox details = new HBox();
        details.getStyleClass().add("details");
        
        // add priority icon
        if (task.isImportant()) {
            ImageView icon = new ImageView(new Image(RESOURCES_ICON_PRIORITY));
            details.getChildren().add(icon);
        }
        
        // add date details
        LocalDate nowDate = now.toLocalDate();
        String startEndString = task.getRelativeStartEndString(nowDate);
        if (!startEndString.isEmpty()) {
            Label startEndDate = new Label(startEndString);
            details.getChildren().add(startEndDate);
        }
        
        // add categories
        String categoriesString = task.getCategoriesString();
        if (!categoriesString.isEmpty()) {
            Label categories = new Label(categoriesString);
            details.getChildren().add(categories);
        }
        
        return details;
    }

    private VBox formatOngoingTaskEntry(Task task, LocalDateTime now, Label description, HBox details) {
        VBox entry = new VBox();
        entry.getChildren().addAll(description, details);
        if (task.isModified()) {
            entry.getStyleClass().add("entry-task-modified");
        } else if (task.isOverdue(now)) {
            entry.getStyleClass().add("entry-task-overdue");
        } else {
            entry.getStyleClass().add("entry-task");
        }
        return entry;
    }

    private VBox createDoneEntry(Task task) {
        Label description = formatDoneTaskDescription(task);
        HBox details = formatDoneTaskDetails(task);
        VBox entry = formatDoneTaskEntry(task, description, details);
        return entry;
    }
    
    private Label formatDoneTaskDescription(Task task) {
        String descriptionString = task.getDescription();
        Label description = new Label(descriptionString);
        description.getStyleClass().add("desc");
        return description;
    }
    
    private HBox formatDoneTaskDetails(Task task) {
        HBox details = new HBox();
        details.getStyleClass().add("details");
        
        // add priority icon
        if (task.isImportant()) {
            ImageView icon = new ImageView(new Image(RESOURCES_ICON_PRIORITY));
            details.getChildren().add(icon);
        }
        
        // add date details
        String startEndString = task.getStartEndString();
        if (!startEndString.isEmpty()) {
            Label startEndDate = new Label(startEndString);
            details.getChildren().add(startEndDate);
        }
        
        // add categories
        String categoriesString = task.getCategoriesString();
        if (!categoriesString.isEmpty()) {
            Label categories = new Label(categoriesString);
            details.getChildren().add(categories);
        }
        
        return details;
    }

    private VBox formatDoneTaskEntry(Task task, Label description, HBox details) {
        VBox entry = new VBox();
        entry.getChildren().addAll(description, details);
        entry.getStyleClass().add("entry-task");
        return entry;
    }
    
    private void updateSidebar(ArrayList<Category> categories) {
        categoryList.getChildren().clear();
        // add category name and corresponding task count to the next row
        for (int i = 0; i < categories.size(); i++) {
            Category cat = categories.get(i);
            Label name = new Label(cat.getName());
            name.getStyleClass().add("entry-cat");
            categoryList.add(name, 0, i);
            Label count = new Label(Integer.toString(cat.getCount()));
            count.getStyleClass().add("count");
            categoryList.add(count, 1, i);
        }
    }
    
}
