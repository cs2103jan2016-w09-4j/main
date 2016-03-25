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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SidebarController extends VBox {

    private Logger logger = Logger.getLogger("MainApp.SidebarController");
    private MainApp main;
    @FXML private VBox categoryContent;
    
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
        categoryContent.getStyleClass().add("panel-cat");
        categoryContent.setMinWidth(150);
    }
    
    public void update() {
        logger.log(Level.INFO, "updating sidebar");
        ArrayList<Category> categories = main.getCategories();
        updateCategories(categories);
    }
    
    private void updateCategories(ArrayList<Category> categories) {
        logger.log(Level.INFO, "updating categories");
        ArrayList<HBox> entries = new ArrayList<HBox>();
        for (Category cat : categories) {
            HBox entry = createCategoryEntry(cat.getName(), cat.getCount());
            entries.add(entry);
        }
        
        categoryContent.getChildren().clear();
        categoryContent.getChildren().addAll(entries);
    }

    private HBox createCategoryEntry(String key, Integer value) {
        HBox entry = new HBox();
        HBox name = new HBox();
        Label nameLab = new Label(key);
        name.getChildren().add(nameLab);
        HBox.setHgrow(name, Priority.ALWAYS);
        HBox count = new HBox();
        Label countLab = new Label(value.toString());
        countLab.getStyleClass().add("count");
        count.getChildren().add(countLab);
        count.getStyleClass().add("sticker");
        count.setMaxHeight(25);
        
        entry.getChildren().addAll(name, count);
        entry.getStyleClass().add("entry-cat");
        return entry;
    }
    
}
