package kijijiscraper;

import javafx.scene.control.ListCell;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class ResultCell extends ListCell<Car>{
    
    private HBox centerTopWrapper;
    private HBox centerBottomWrapper;
    private VBox centerWrapper;
    private Text title, kms, condition, transmission, bodyType;
    private BorderPane mainWrapper;
    
    public ResultCell() {
        super();
        createCellItems();
    }
    
    @Override
    public void updateItem(Car item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            createCellItems();
            mainWrapper.getStylesheets().add("CSS/resultCellStylesheet.css");
            title.setText(item.year + " " + item.make + " " + item.model);
            title.setFont(KijijiScraperMain.productSansReg);
            title.setId("titleText");
            centerTopWrapper.getChildren().add(title);
            
            title.setText(item.year + " " + item.make + " " + item.model);
            title.setFont(KijijiScraperMain.productSansReg);
            title.setId("titleText");
            kms.setText(item.year + " " + item.make + " " + item.model);
            kms.setFont(KijijiScraperMain.productSansReg);
            kms.setId("smallText");title.setText(item.year + " " + item.make + " " + item.model);
            condition.setFont(KijijiScraperMain.productSansReg);
            condition.setId("smallText");title.setText(item.year + " " + item.make + " " + item.model);
            condition.setFont(KijijiScraperMain.productSansReg);
            transmission.setId("smallText");title.setText(item.year + " " + item.make + " " + item.model);
            transmission.setFont(KijijiScraperMain.productSansReg);
            transmission.setId("smallText");
            bodyType.setId("smallText");title.setText(item.year + " " + item.make + " " + item.model);
            bodyType.setFont(KijijiScraperMain.productSansReg);
            bodyType.setId("smallText");
            centerBottomWrapper.getChildren().addAll(kms, condition, transmission, bodyType);
            
            centerWrapper.getChildren().addAll(centerTopWrapper, centerBottomWrapper);
            
            mainWrapper.setCenter(centerWrapper);
            
            setGraphic(mainWrapper);
        }
    }
    
    public void createCellItems() {
        centerTopWrapper = new HBox(8);
        title = new Text();
        mainWrapper = new BorderPane();
        centerWrapper = new VBox(8);
        centerBottomWrapper = new HBox(8);
        kms = new Text();
        condition = new Text();
        transmission = new Text();
        bodyType = new Text();
    }
    
}
