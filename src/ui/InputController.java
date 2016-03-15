package ui;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.controlsfx.control.textfield.TextFields;

import ui.MainApp;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

public class InputController extends VBox {

    private static Logger logger = Logger.getLogger("MainApp.InputController");
    private MainApp main;
    @FXML TextField commandBar;
    
    public InputController(MainApp main) {
        this.main = main;
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
        loadFXML();
        handleUserInteractions();
    }

    private void loadFXML() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Input.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            logger.severe("failed to load Input");
        }
    }
    
    private void handleUserInteractions() {
        commandBar.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                KeyCode key = event.getCode();
                if (key == KeyCode.PAGE_DOWN) {
                    logger.info("user pressed page down");
                } else if (key == KeyCode.PAGE_UP) {
                    logger.info("user pressed page up");
                } else if (key == KeyCode.ENTER) {
                    String input = commandBar.getText();
                    logger.info("user entered: " + input);
                    commandBar.clear();
                    main.handleCommand(input);
                }
            }
        });
        
        TextFields.bindAutoCompletion(commandBar, sr -> {
            return main.getPredictions(commandBar.getText());
        });
    }

}
