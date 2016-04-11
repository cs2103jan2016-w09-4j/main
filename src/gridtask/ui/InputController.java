//@@author A0131507R
package gridtask.ui;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.controlsfx.control.textfield.TextFields;

import gridtask.ui.MainApp;

import org.controlsfx.control.textfield.AutoCompletionBinding;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class InputController extends VBox {

    private static Logger logger = Logger.getLogger("MainApp.InputController");
    private MainApp main;
    @FXML TextField commandBar;

    private static final String FXML_INPUT = "Input.fxml";

    public InputController(MainApp main) {
        this.main = main;
        //initializeLogger();
        loadFXML();
        bindAutoCompletion();
    }

    private void initializeLogger() {
        try {
            Handler fh = new FileHandler("log_ui_input");
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_INPUT));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            logger.severe("failed to load Input");
        }
    }
    
    private void bindAutoCompletion() {
        AutoCompletionBinding<String> binding = TextFields.bindAutoCompletion(commandBar, sr -> {
            return main.getPredictions(commandBar.getText());
        });
    }
    
    @FXML
    private void readInput(ActionEvent event) {
        String input = commandBar.getText();
        logger.info("user entered: " + input);
        commandBar.clear();
        main.handleCommand(input);
    }

}
