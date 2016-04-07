package ui;

import common.Result;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class Feedback {
    
    Stage primaryStage;
    
    private Popup feedbackPopup;
    private HBox content;
    private ImageView icon;
    private Label message;
    
    private static final String RESOURCES_ICON_SUCCESS = "/icons/success-small.png";
    private static final String RESOURCES_ICON_SUCCESS_DELETE = "/icons/delete-success-small.png";
    private static final String RESOURCES_ICON_SUCCESS_SAVE = "/icons/save-success-small.png";
    private static final String RESOURCES_ICON_FAIL = "/icons/fail-small.png";
    private static final String RESOURCES_ICON_FAIL_DELETE = "/icons/delete-fail-small.png";
    private static final String RESOURCES_ICON_FAIL_SAVE = "/icons/save-fail-small.png";
    
    public Feedback(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeContent();
        initializeFeedbackPopup();
    }

    private void initializeContent() {
        content = new HBox();
        content.getStylesheets().add(getClass().getResource("feedback.css").toExternalForm()); 
        content.setId("popup-box");
        
        message = new Label();
        message.setId("popup-text");
        icon = new ImageView();
        
        content.getChildren().addAll(icon, message);
    }
    
    private void initializeFeedbackPopup() {
        feedbackPopup = new Popup();
        // hide feedback popup when window loses focus
        feedbackPopup.setAutoHide(true);
        // hide feedback popup on any key press
        feedbackPopup.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                feedbackPopup.hide();
            }
        });
        feedbackPopup.getContent().add(content);
    }

    public void displayFeedback(Result result) {
        setFeedback(result);
        if (!isDisplayCommand(result)) {
            showFeedback();
        }
    }
    
    private boolean isDisplayCommand(Result result) {
        switch (result.getCommandType()) {
            case SEARCH :
                // fallthrough
            
            case SEARCHDONE :
                // fallthrough
            
            case HOME :
                // fallthrough
                
            case HELP :
                return true;
            
            default :
                return false;
        }
    }

    private void showFeedback() {
        // align to left of window
        double x = primaryStage.getX() + primaryStage.getScene().getX();
        feedbackPopup.setX(x);
        // align to bottom of window
        double y = primaryStage.getY() + primaryStage.getHeight();
        feedbackPopup.setY(y);
        feedbackPopup.show(primaryStage);
    }

    private void setFeedback(Result result) {
        assert (result != null);
        String msg = result.getMessage();
        message.setText(msg);
        Image img = getIcon(result);
        icon.setImage(img);
    }
    
    private Image getIcon(Result result) {
        String resource;
        switch (result.getCommandType()) {
            case DELETE :
                resource = result.isSuccess() ? RESOURCES_ICON_SUCCESS_DELETE : RESOURCES_ICON_FAIL_DELETE;
                break;
                
            case SAVE :
                // fallthrough
            
            case LOAD :
                resource = result.isSuccess() ? RESOURCES_ICON_SUCCESS_SAVE : RESOURCES_ICON_FAIL_SAVE;
                break;
                
            default :
                resource = result.isSuccess() ? RESOURCES_ICON_SUCCESS : RESOURCES_ICON_FAIL;
                break;
        }
        return new Image(resource);
    }
    
}
