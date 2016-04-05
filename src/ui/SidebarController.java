//@@author Ruoling
package ui;

import ui.MainApp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import common.Category;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SidebarController extends VBox {

    private Logger logger = Logger.getLogger("MainApp.SidebarController");
    private MainApp main;
    @FXML private ScrollPane categoryPanel;
    
    public SidebarController(MainApp main) {
        this.main = main;
        //initializeLogger();
        loadFXML();
        initializeSidebar();
    }

    private void initializeLogger() {
        try {
            Handler fh = new FileHandler("log_ui_sidebar");
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Sidebar.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "failed to load Sidebar", e);
        }
    }

    private void initializeSidebar() {
        logger.log(Level.INFO, "initializing sidebar");
        ArrayList<Category> categories = main.getCategories();
        updateCategories(categories);
        categoryPanel.getStyleClass().add("panel-cat");
        categoryPanel.setMinWidth(180);
        categoryPanel.setFocusTraversable(true);
    }
    
    public void update() {
        logger.log(Level.INFO, "updating sidebar");
        ArrayList<Category> categories = main.getCategories();
        updateCategories(categories);
    }
    
    private void updateCategories(ArrayList<Category> categories) {
        logger.log(Level.INFO, "updating categories");
        ArrayList<HBox> catEntries = new ArrayList<HBox>();
        for (Category cat : categories) {
            HBox entry = createCategoryEntry(cat);
            catEntries.add(entry);
        }
        VBox categoryContent = new VBox();
        categoryContent.getChildren().addAll(catEntries);
        categoryPanel.setContent(categoryContent);
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
