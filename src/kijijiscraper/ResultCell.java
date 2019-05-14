package kijijiscraper;

import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class ResultCell extends ListCell<Car>{
    
    private HBox mainWrapper;
    private Text title;
    
    public ResultCell() {
        super();
        mainWrapper = new HBox();
        title = new Text();
    }
    
    @Override
    public void updateItem(Car item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            mainWrapper = new HBox();
            title = new Text();
            mainWrapper.getStylesheets().add("CSS/resultCellStylesheet.css");
            title.setText(item.year + " " + item.make + " " + item.model);
            title.setId("titleText");
            mainWrapper.getChildren().add(title);
            System.out.println("run");
            setGraphic(mainWrapper);
        }
    }
    
}
