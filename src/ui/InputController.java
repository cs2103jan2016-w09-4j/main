package ui;

import java.io.IOException;

import org.controlsfx.control.textfield.TextFields;

import ui.MainApp;
import ui.LogicStub;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

public class InputController extends VBox {

    private MainApp main;
    @FXML TextField commandBar;
    
    public InputController(MainApp main) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Input.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            System.out.println("INPUT CONTROLLER BROKE");;
        }
        this.main = main;
        commandBar.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                KeyCode key = event.getCode();
                if (key == KeyCode.PAGE_DOWN) {
                    System.out.println("User tried to scroll down");
                } else if (key == KeyCode.PAGE_UP) {
                    System.out.println("User tried to scroll up");
                } else if (key == KeyCode.ENTER) {
                    String input = commandBar.getText();
                    System.out.println("User typed in: " + input);
                    commandBar.clear();
                    main.handleCommand(input);
                } else if (key.isLetterKey()) {
                    System.out.println("Show auto-complete");
                }
            }
        });
        TextFields.bindAutoCompletion(commandBar, sr -> {
            return main.getPredictions(commandBar.getText());
        });
    }

}
