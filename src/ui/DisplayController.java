package ui;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class DisplayController extends GridPane {

    private static Logger logger = Logger.getLogger("MainApp.DisplayController");
    private MainApp main;
    private Stage primaryStage;
    
    @FXML private HiddenSidesPane mainPanel;
    private VBox taskPanel, searchPanel;
    private SidebarController sidebar;
    
    private static final String DISPLAY_FXML = "Display.fxml";
    private static final String RESOURCES_ICON_SUCCESS = "file:main/src/resources/icons/success-smaller.png";
    private static final String RESOURCES_ICONS_FAIL = "file:main/src/resources/icons/fail-smaller.png";

    public DisplayController(MainApp main, Stage primaryStage) {
        this.main = main;
        this.primaryStage = primaryStage;
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
        loadFXML();
        initializeTaskPanel();
        initializeSidebarContent();
        initializeSearchPanel();
        handleUserInteractions();
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
        ArrayList<Task> allTasks = main.getTasks();
        ArrayList<VBox> todayTasks = new ArrayList<VBox>();
        ArrayList<VBox> otherTasks = new ArrayList<VBox>();
        int index = 0;
        for (Task task : allTasks) {
            if (task.isToday()) {
                todayTasks.add(createToday(task, ++index));
            } else {
                otherTasks.add(createOther(task, ++index));
            }
        }
        taskPanel = new VBox();
        taskPanel.getStyleClass().add("panel-task");
        updateTaskPanel(todayTasks, otherTasks);
        mainPanel.setContent(taskPanel);
    }

    private void initializeSidebarContent() {
        sidebar = new SidebarController(main);
        mainPanel.setLeft(sidebar);
        mainPanel.setTriggerDistance(30);
    }
    
    private void initializeSearchPanel() {
        searchPanel = new VBox();
        searchPanel.getStyleClass().add("panel-search");
    }

    private void handleUserInteractions() {
        this.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            
            public void handle(KeyEvent event) {
                if (event.getCode()==KeyCode.M && event.isControlDown()) {
                    if (mainPanel.getPinnedSide()==Side.LEFT) {
                        mainPanel.setPinnedSide(null);
                    } else {
                        mainPanel.setPinnedSide(Side.LEFT);
                    }
                } else if (event.getCode()==KeyCode.PAGE_DOWN) {
                    logger.log(Level.INFO, "user pressed page down");
                } else if (event.getCode()==KeyCode.PAGE_UP) {
                    logger.log(Level.INFO, "user pressed page up");
                } else if (event.getCode()==KeyCode.HOME) {
                    logger.log(Level.INFO, "user pressed home");
                    mainPanel.setContent(taskPanel);
                }
            }
            
        });
    }
    
    private void updateTaskPanel(ArrayList<VBox> todayTasks, ArrayList<VBox> otherTasks) {
        // build today panel
        VBox todayPanel = new VBox();
        
        Label todayHeader = createHeader("Today");
        VBox todayContent = new VBox();
        if (todayTasks.isEmpty()) {
            Label empty = new Label("No tasks today");
            todayContent.getChildren().add(empty);
        } else {
            todayContent.getChildren().addAll(todayTasks);
        }
        ScrollPane todayScroll = new ScrollPane();
        VBox.setVgrow(todayScroll, Priority.ALWAYS);
        todayScroll.setContent(todayContent);
        
        todayPanel.getChildren().addAll(todayHeader, todayScroll);
        //VBox.setVgrow(todayPanel, Priority.ALWAYS);
        
        // build other panel
        VBox otherPanel = new VBox();

        Label otherHeader = createHeader("Others");
        VBox otherContent = new VBox();
        if (otherTasks.isEmpty()) {
            Label empty = new Label("No tasks");
            otherContent.getChildren().add(empty);
        } else {
            otherContent.getChildren().addAll(otherTasks);
        }
        ScrollPane otherScroll= new ScrollPane();
        otherScroll.setContent(otherContent);
        VBox.setVgrow(otherScroll, Priority.ALWAYS);

        otherPanel.getChildren().addAll(otherHeader, otherScroll);        
        //VBox.setVgrow(otherPanel, Priority.ALWAYS);
        
        // add to task panel
        taskPanel.getChildren().clear();
        taskPanel.getChildren().addAll(todayPanel, otherPanel);
    }
    
    private Label createHeader(String heading) {
        Label header = new Label(heading);
        header.getStyleClass().add("header");
        header.setMinHeight(USE_PREF_SIZE);
        return header;
    }

    private void updateSearchPanel(ArrayList<Task> results) {
        searchPanel.getChildren().clear();
        Label searchHeader = createHeader(results.size() + " search results found");
        VBox searchContent = new VBox();
        ArrayList<VBox> searchTasks = new ArrayList<VBox>();
        int index = 0;
        for (Task result : results) {
            searchTasks.add(createOther(result, ++index));
        }
        searchContent.getChildren().addAll(searchTasks);
        searchPanel.getChildren().addAll(searchHeader, searchContent);
    }
    
    public void displayResult(Result result) {
        CommandType cmd = result.getCommandType();
        
        if (cmd == CommandType.SEARCH) {
            updateSearchPanel(result.getResults());
            mainPanel.setContent(searchPanel);
        } else {
            ArrayList<Task> allTasks = result.getResults();
            ArrayList<VBox> todayTasks = new ArrayList<VBox>();
            ArrayList<VBox> otherTasks = new ArrayList<VBox>();
            int index = 0;
            for (Task task : allTasks) {
                if (task.isToday()) {
                    todayTasks.add(createToday(task, ++index));
                } else {
                    otherTasks.add(createOther(task, ++index));
                }
            }
            updateTaskPanel(todayTasks, otherTasks);
            sidebar.update();
            showFeedback(cmd, result.isSuccess());
            mainPanel.setContent(taskPanel);
        }
    }
    
    private void showFeedback(CommandType cmd, boolean isSuccess) {
        // TODO: refactor this mess!!!!
        logger.log(Level.INFO, String.format("showing feedback for %1s, %2s", cmd, isSuccess));

        final Popup window = new Popup();
        window.setAutoHide(true);

        HBox box = createFeedback(cmd, isSuccess);
        window.getContent().add(box);
        double x = primaryStage.getX() + 10;
        double y = primaryStage.getY() + primaryStage.getHeight();
        window.setX(x);
        window.setY(y);
        window.show(primaryStage);
    }
    
    private HBox createFeedback(CommandType commandType, boolean isSuccess) {
        HBox box = new HBox();
        box.getStylesheets().add(getClass().getResource("feedback.css").toExternalForm()); 
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
            
            case UNDO :
                message = new Text("Last command undone");
                break;
            
            case REDO :
                message = new Text("Undo undone command");
                break;
            
            case SAVE :
                message = new Text("Saved");
                break;
            
            case LOAD :
                message = new Text("Loaded");
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
    
    private VBox createToday(Task task, int index) {
        VBox entry = new VBox();
        Label desc = new Label(index + ". " + task.getDescription());
        
        HBox details = new HBox();
        Date date;
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mma");
        String startDate = null, endDate = null;
        Label startEndDate = null;
        
        if ((date = task.getStartDate()) != null) {
            startDate = "from " + dateFormat.format(date);
        }
        if ((date = task.getEndDate()) != null) {
            endDate = "until " + dateFormat.format(date);
        }
        
        if (startDate!=null && endDate!=null) {
            String dates = startDate + " " + endDate;
            dates = dates.substring(0, 1).toLowerCase() + dates.substring(1);
            startEndDate = new Label(dates);
        } else if (startDate!=null) {
            String dates = startDate;
            dates = dates.substring(0, 1).toLowerCase() + dates.substring(1);
            startEndDate = new Label(dates);
        } else {
            String dates = endDate;
            dates = dates.substring(0, 1).toLowerCase() + dates.substring(1);
            startEndDate = new Label(dates);
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
        Date date;
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy h:mma");
        String startDate = null, endDate = null;
        Label startEndDate = null;
        
        if ((date = task.getStartDate()) != null) {
            startDate = "from " + dateFormat.format(date);
        }
        if ((date = task.getEndDate()) != null) {
            endDate = "until " + dateFormat.format(date);
           
        }
        
        if (startDate!=null && endDate!=null) {
            String dates = startDate + " " + endDate;
            dates = dates.substring(0, 1).toLowerCase() + dates.substring(1);
            startEndDate = new Label(dates);
        } else if (startDate!=null) {
            String dates = startDate;
            dates = dates.substring(0, 1).toLowerCase() + dates.substring(1);
            startEndDate = new Label(dates);
        } else if (endDate!=null){
            String dates = endDate;
            dates = dates.substring(0, 1).toLowerCase() + dates.substring(1);
            startEndDate = new Label(dates);
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