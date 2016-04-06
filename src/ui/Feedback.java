package ui;

import java.util.logging.Level;

import common.Command;
import common.Result;
import common.Command.CommandType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class Feedback {
    
    Stage primaryStage;
    
    private Popup popup;
    
    private static final String RESOURCES_ICON_SUCCESS = "/icons/success-small.png";
    private static final String RESOURCES_ICON_SUCCESS_DELETE = "/icons/delete-success-small.png";
    private static final String RESOURCES_ICON_SUCCESS_SAVE = "/icons/save-success-small.png";
    private static final String RESOURCES_ICON_FAIL = "/icons/fail-small.png";
    private static final String RESOURCES_ICON_FAIL_DELETE = "/icons/delete-fail-small.png";
    private static final String RESOURCES_ICON_FAIL_SAVE = "/icons/save-fail-small.png";
    
    public Feedback(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.popup = new Popup();
    }
    
    public void displayFeedback(Result result) {
        HBox feedback = createFeedback(result);
        if (!isDisplayCommand(result)) {
            showFeedback(feedback);
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
                // fallthrough
                
            case INVALID :
                return true;
            
            default :
                return false;
        }
    }

    private void showFeedback(HBox feedback) {
        popup.getContent().clear();
        popup.getContent().add(feedback);
        double x = primaryStage.getX() + 10;
        double y = primaryStage.getY() + primaryStage.getHeight();
        popup.setX(x);
        popup.setY(y);
        popup.show(primaryStage);
    }

    private HBox createFeedback(Result result) {
        HBox box = new HBox();
        box.getStylesheets().add(getClass().getResource("feedback.css").toExternalForm()); 
        Text message = new Text(result.getMessage());
        ImageView icon = getIcon(result);
        
        box.setId("popup");
        message.setId("popup-text");
        box.getChildren().addAll(icon, message);
        return box;
    }
    
    private ImageView getIcon(Result result) {
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
        return new ImageView(resource);
    }
    
}
