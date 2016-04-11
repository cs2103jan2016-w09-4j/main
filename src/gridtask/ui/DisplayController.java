//@@author A0131507R
package gridtask.ui;

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

import gridtask.common.Category;
import gridtask.common.Command.CommandType;
import gridtask.common.Result;
import gridtask.common.Task;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class DisplayController extends HiddenSidesPane {

    private static Logger logger = Logger.getLogger("MainApp.DisplayController");
    private MainApp main;
    
    @FXML private VBox taskPanel, searchPanel, donePanel, helpPanel;
    @FXML private GridPane categoryList, helpList;
    @FXML private VBox todayList, otherList;
    
    private static final int CELL_MIN_HEIGHT = 67;
    private static final int UNMODIFIED = -1;
    
    private static final String HEADER_DONE_SINGLE = " completed task";
    private static final String HEADER_DONE_PLURAL = " completed tasks";
    private static final String HEADER_SEARCH_SINGLE = " search result found";
    private static final String HEADER_SEARCH_PLURAL = " search results found";
    private static final String MESSAGE_EMPTY_LIST_TODAY = "No tasks due today!";
    private static final String MESSAGE_EMPTY_LIST_OTHER = "No other tasks due this week! "
                                                           + "Type 'search' to view all tasks.";

    private static final String FXML_DISPLAY = "Display.fxml";
    private static final String RESOURCES_ICON_PRIORITY = "/icons/priority.png";

    public DisplayController(MainApp main) {
        this.main = main;
        this.setFocusTraversable(false);
        initializeLogger();
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
        populateTaskPanel(tasks);
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
                             "Show tasks",
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
                              "home",
                              "exit",
                              "save <directory> <filename>",
                              "load <directory> <filename>",
                              "CTRL+M" };
        
        assert (actions.length == commands.length);
        
        // add action and its corresponding command to the next row
        for (int row = 0; row < actions.length; row++) {
            Label action = new Label(actions[row]);
            helpList.add(action, 0, row);
            Label command = new Label(commands[row]);
            helpList.add(command, 1, row);
        }
    }

    private void initializeSidebar() {
        ArrayList<Category> categories = main.getCategories();
        populateSidebar(categories);
        this.setTriggerDistance(50);
    }
    
    /*******************************************
     * METHODS TO MANAGE DISPLAY OF MAIN PANEL *
     *******************************************/
    
    /**
     * Toggles the sidebar.
     * Opens the sidebar if it is closed and closes the sidebar if it is open.
     */
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
     * Displays the task results of the command to the user.
     * 
     * Invalid           no changes to main panel
     * Search            main panel shows search results
     * Searchdone        main panel shows completed tasks
     * Help              main panel shows help guide
     * Other commands    main panel shows tasks
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
                populateSearchPanel(tasks);
                setMainPanelContent(searchPanel);
                break;
                
            case SEARCHDONE :
                populateDonePanel(tasks);
                setMainPanelContent(donePanel);
                break;
            
            case HELP :
                setMainPanelContent(helpPanel);
                break;

            default :
                ArrayList<Category> categories = main.getCategories();
                populateSidebar(categories);
                populateTaskPanel(tasks);
                setMainPanelContent(taskPanel);
                break;
        }
    }

    /**
     * Sets the specified node to be shown on the main panel.
     */
    private void setMainPanelContent(Node node) {
        this.setContent(node);
    }
    
    /********************************
     * METHODS TO UPDATE TASK PANEL *
     ********************************/

    /**
     * Populate the task panel with the specified tasks
     */
    private void populateTaskPanel(ArrayList<Task> tasks) {
        assert (tasks != null);
        populateTodayList(tasks);
        populateOtherList(tasks);
    }

    private void populateTodayList(ArrayList<Task> tasks) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate nowDate = now.toLocalDate();
        ObservableList<VBox> todayTasks = FXCollections.observableArrayList();
        // index of task to scroll to
        int todayModified = UNMODIFIED;
        for (Task task : tasks) {
            VBox entry = createOngoingTaskEntry(task, now);
            if (task.isOccurringOn(nowDate)) {
                todayTasks.add(entry);
                if (task.isModified()) {
                    todayModified = todayTasks.size() - 1;
                }
            }
        }
        
        if (todayTasks.isEmpty()) {
            setEmptyMessageInList(todayList, MESSAGE_EMPTY_LIST_TODAY);
        } else {
            ListView<VBox> todayListView = createListView(todayTasks);
            if (!todayTasks.isEmpty()) {
                int numVisibleTasks = Math.min(todayTasks.size(), 3);
                todayListView.setMinHeight(CELL_MIN_HEIGHT * numVisibleTasks);
            }
            if (todayModified != UNMODIFIED) {
                todayListView.scrollTo(todayModified);
            }
            updateChildren(todayList, todayListView);
        }
    }

    private void populateOtherList(ArrayList<Task> tasks) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate nowDate = now.toLocalDate();
        ObservableList<VBox> otherTasks = FXCollections.observableArrayList();
        // index of task to scroll to
        int otherModified = UNMODIFIED;
        for (Task task : tasks) {
            VBox entry = createOngoingTaskEntry(task, now);
            if (!task.isOccurringOn(nowDate)) {
                otherTasks.add(entry);
                if (task.isModified()) {
                    otherModified = otherTasks.size() - 1;
                }
            }
        }
        
        if (otherTasks.isEmpty()) {
            setEmptyMessageInList(otherList, MESSAGE_EMPTY_LIST_OTHER);
        } else {
            ListView<VBox> otherListView = createListView(otherTasks);
            if (!otherTasks.isEmpty()) {
                int numVisibleTasks = Math.min(otherTasks.size(), 2);
                otherListView.setMinHeight(CELL_MIN_HEIGHT * numVisibleTasks);
            }
            if (otherModified != UNMODIFIED) {
                otherListView.scrollTo(otherModified);
            }
            updateChildren(otherList, otherListView);
        }
    }

    private void setEmptyMessageInList(VBox list, String prompt) {
        Label info = new Label(prompt);
        info.getStyleClass().add("entry-empty");
        info.setMinHeight(USE_PREF_SIZE);
        updateChildren(list, info);
    }
    
    /****************************
     * METHOD TO UPDATE SIDEBAR *
     ****************************/
    
    /**
     * Populate the sidebar with the specified categories
     */
    private void populateSidebar(ArrayList<Category> categories) {
        categoryList.getChildren().clear();
        // add category name and corresponding task count to the next row
        for (int row = 0; row < categories.size(); row++) {
            Category cat = categories.get(row);
            Label name = new Label(cat.getName());
            name.getStyleClass().add("entry-cat");
            categoryList.add(name, 0, row);
            Label count = new Label(Integer.toString(cat.getCount()));
            count.getStyleClass().add("count");
            categoryList.add(count, 1, row);
        }
    }
    
    /**********************************
     * METHODS TO UPDATE SEARCH PANEL *
     **********************************/

    /**
     * Populate the search panel with the given search results
     */
    private void populateSearchPanel(ArrayList<Task> searchResults) {
        assert (searchResults != null);
        Label searchHeader = createHeader(searchResults.size(), HEADER_SEARCH_SINGLE, HEADER_SEARCH_PLURAL);
        ObservableList<VBox> searchTasks = createSearchTasks(searchResults);
        ListView<VBox> searchListView = createListView(searchTasks);
        updateChildren(searchPanel, searchHeader, searchListView);
    }
    
    private ObservableList<VBox> createSearchTasks(ArrayList<Task> tasks) {
        LocalDateTime todayDateTime = LocalDateTime.now();
        ObservableList<VBox> searchTasks = FXCollections.observableArrayList();
        for (Task task : tasks) {
            VBox entry = createOngoingTaskEntry(task, todayDateTime);
            searchTasks.add(entry);
        }
        return searchTasks;
    }
    
    /********************************
     * METHODS TO UPDATE DONE PANEL *
     ********************************/

    /**
     * Populate the done panel with the specified list of completed tasks
     */
    private void populateDonePanel(ArrayList<Task> tasks) {
        assert (tasks != null);
        Label doneHeader = createHeader(tasks.size(), HEADER_DONE_SINGLE, HEADER_DONE_PLURAL);
        ObservableList<VBox> doneTasks = createDoneTasks(tasks);
        ListView<VBox> doneListView = createListView(doneTasks);
        updateChildren(donePanel, doneHeader, doneListView);
    }

    private ObservableList<VBox> createDoneTasks(ArrayList<Task> tasks) {
        ObservableList<VBox> doneTasks = FXCollections.observableArrayList();
        for (Task task : tasks) {
            VBox entry = createDoneTaskEntry(task);
            doneTasks.add(entry);
        }
        return doneTasks;
    }
    
    /******************
     * HELPER METHODS *
     ******************/

    private void updateChildren(Pane parent, Node... children) {
        parent.getChildren().clear();
        parent.getChildren().addAll(children);
    }
    
    private Label createHeader(int size, String single, String plural) {
        String headerText = size + (size == 1 ? single : plural);
        Label header = new Label(headerText);
        header.getStyleClass().add("header");
        return header;
    }

    private ListView<VBox> createListView(ObservableList<VBox> tasks) {
        ListView<VBox> listView = new ListView<VBox>(tasks);
        // ListView's default height is fixed at 400 regardless of size of list
        // Workaround for resizing ListView by binding to (cell height * number)
        listView.maxHeightProperty().bind(Bindings.size(tasks).multiply(CELL_MIN_HEIGHT));
        VBox.setVgrow(listView, Priority.SOMETIMES);
        return listView;
    }
    
    private VBox createOngoingTaskEntry(Task task, LocalDateTime now) {
        Label description = formatOngoingTaskDescription(task);
        HBox details = formatOngoingTaskDetails(task, now);
        VBox entry = formatOngoingTaskEntry(task, now, description, details);
        return entry;
    }
    
    private Label formatOngoingTaskDescription(Task task) {
        int id = task.getId();
        String desc = task.getDescription();
        String descriptionText = id + ". " + desc;
        Label description = new Label(descriptionText);
        description.getStyleClass().add("desc");
        return description;
    }

    private HBox formatOngoingTaskDetails(Task task, LocalDateTime now) {
        HBox details = new HBox();
        details.getStyleClass().add("details");
        
        // add priority icon if task is important
        if (task.isImportant()) {
            ImageView icon = new ImageView(new Image(RESOURCES_ICON_PRIORITY));
            details.getChildren().add(icon);
        }
        
        // add date details if available
        LocalDate nowDate = now.toLocalDate();
        String startEndString = task.getRelativeStartEndString(nowDate);
        if (!startEndString.isEmpty()) {
            Label startEndDate = new Label(startEndString);
            details.getChildren().add(startEndDate);
        }
        
        // add categories if available
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

    private VBox createDoneTaskEntry(Task task) {
        Label description = formatDoneTaskDescription(task);
        HBox details = formatDoneTaskDetails(task);
        VBox entry = formatDoneTaskEntry(task, description, details);
        return entry;
    }
    
    private Label formatDoneTaskDescription(Task task) {
        String descriptionText = task.getDescription();
        Label description = new Label(descriptionText);
        description.getStyleClass().add("desc");
        return description;
    }
    
    private HBox formatDoneTaskDetails(Task task) {
        HBox details = new HBox();
        details.getStyleClass().add("details");
        
        // add priority icon if task is important
        if (task.isImportant()) {
            ImageView icon = new ImageView(new Image(RESOURCES_ICON_PRIORITY));
            details.getChildren().add(icon);
        }
        
        // add date details if available
        String startEndString = task.getStartEndString();
        if (!startEndString.isEmpty()) {
            Label startEndDate = new Label(startEndString);
            details.getChildren().add(startEndDate);
        }
        
        // add categories if available
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

}
