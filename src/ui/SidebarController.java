package ui;

import ui.MainApp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SidebarController extends VBox {

    private MainApp main;
    @FXML private VBox categoryContent;
    
    public SidebarController(MainApp main) {
        loadFXML();
        this.main = main;
        initializeSidebar();
    }

    private void loadFXML() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Sidebar.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            System.out.println("INPUT CONTROLLER BROKE");
        }
    }

    private void initializeSidebar() {
        HashMap<String, Integer> categories = main.getCategories();
        updateCategories(categories);
        categoryContent.getStyleClass().add("panel-cat");
        categoryContent.setMinWidth(150);
    }
    
    private void updateCategories(HashMap<String, Integer> categories) {
        ArrayList<HBox> entries = new ArrayList<HBox>();
        Set<Map.Entry<String, Integer>> set = categories.entrySet();
        for (Map.Entry<String, Integer> cat : set) {
            HBox entry = createCategoryEntry(cat.getKey(), cat.getValue());
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
