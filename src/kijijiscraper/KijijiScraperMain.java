package kijijiscraper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

public class KijijiScraperMain extends Application implements EventHandler<ActionEvent>{
    
    public static String URL;
    public static String filename = "wagonsunder4k.csv";
    public static ArrayList<String> resultLinks;
    public static ArrayList<Car> carsList;
    public static ObservableList<Car> carsObservableList;
    public static Stage stage;
    public static Scene searchScene, resultsScene;
    public static BorderPane mainBorderPane, resultsBorderPane;
    public static TextField minPriceField, maxPriceField;
    public static ComboBox locationComboBox;
    public static ToggleButton noneButton, usedButton, newButton;
    public static ToggleGroup conditionGroup = new ToggleGroup();
    public static ToggleButton convertibleToggle, coupeToggle, hatchbackToggle, vanToggle, pickupToggle, sedanToggle, suvToggle, wagonToggle, otherbodyToggle;
    public static ToggleGroup bodyTypeGroup = new ToggleGroup();
    public static Button searchButton;
    public static Image bannerImage;
    public static ImageView bannerImageView;
    public static DropShadow dropShadow = new DropShadow(15, Color.BLACK);
    public static ListView resultsListView;
    
    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("KIJIJI Scraper");
        
        makeSearchScene();
        
        stage.setHeight(550);
        stage.setMinHeight(550);
        stage.setWidth(1100);
        stage.setMinWidth(1100);
        
        stage.show();
    }
    
    public static void main(String[] args) {
        //scrape();
        bannerImage = new Image("file:logo/mainBanner.png");
        launch(args);
        
    }
    
    @Override
    public  void handle(ActionEvent event) {
        if (event.getSource() == searchButton) scrape(locationComboBox.getSelectionModel(), bodyTypeGroup.getSelectedToggle(), conditionGroup.getSelectedToggle(), minPriceField.getText(), maxPriceField.getText());
    }
    
    public void makeSearchScene() {
        mainBorderPane = new BorderPane();
        mainBorderPane.getStylesheets().add("/CSS/searchStylesheet.css");
        noneButton = new ToggleButton("ANY");
        noneButton.setId("anyButton");
        newButton = new ToggleButton("NEW");
        newButton.setId("newButton");
        usedButton = new ToggleButton("USED");
        usedButton.setId("usedButton");
        conditionGroup.getToggles().addAll(noneButton, newButton, usedButton);
        HBox conditionButtons = new HBox(0);
        conditionButtons.getChildren().addAll(noneButton, newButton, usedButton);
        conditionButtons.setAlignment(Pos.TOP_CENTER);
        conditionButtons.setPadding(new Insets(8));
        
        HBox priceSelect = new HBox(8);
        minPriceField = new TextField();
        minPriceField.setPromptText("MIN.");
        minPriceField.setId("priceBox");
        Label toLabel = new Label("to");
        toLabel.setAlignment(Pos.CENTER);
        toLabel.setId("toLabel");
        maxPriceField = new TextField();
        maxPriceField.setPromptText("MAX.");
        maxPriceField.setId("priceBox");
        priceSelect.getChildren().addAll(minPriceField, toLabel, maxPriceField);
        priceSelect.setAlignment(Pos.CENTER);
        
        VBox leftColumn = new VBox(12);
        leftColumn.getChildren().addAll(conditionButtons, priceSelect);
        leftColumn.setAlignment(Pos.CENTER);
        
        Separator searchSeparator = new Separator(Orientation.VERTICAL);
        searchSeparator.setPadding(new Insets(8));
        
        locationComboBox = new ComboBox();
        locationComboBox.setEditable(true);
        locationComboBox.getItems().addAll("Edmonton Area", "Edmonton");
        locationComboBox.setId("locationBox");
        
        convertibleToggle = new ToggleButton("CONVERTIBLE");
        coupeToggle = new ToggleButton("COUPE");
        hatchbackToggle = new ToggleButton("HATCHBACK");
        vanToggle = new ToggleButton("VAN");
        pickupToggle = new ToggleButton("PICKUP");
        sedanToggle = new ToggleButton("SEDAN");
        suvToggle = new ToggleButton("SUV");
        wagonToggle = new ToggleButton("WAGON");
        otherbodyToggle = new ToggleButton("OTHER");
        bodyTypeGroup.getToggles().addAll(convertibleToggle, coupeToggle, hatchbackToggle, vanToggle, pickupToggle, sedanToggle, suvToggle, wagonToggle, otherbodyToggle);
        
        HBox topBodyButtons = new HBox(0);
        HBox middleBodyButtons = new HBox(0);
        HBox bottomBodyButtons = new HBox(0);
        for (int i = 0; i<bodyTypeGroup.getToggles().size(); i++) {
            ((ToggleButton) bodyTypeGroup.getToggles().get(i)).setId("bodyButton");
            if (i <= 2) {
                if (i == 0) ((ToggleButton) bodyTypeGroup.getToggles().get(i)).setId("bodyButtonTL");
                else if (i == 2) ((ToggleButton) bodyTypeGroup.getToggles().get(i)).setId("bodyButtonTR");
                topBodyButtons.getChildren().addAll((ToggleButton) bodyTypeGroup.getToggles().get(i));
            } else if (i <= 5){
                middleBodyButtons.getChildren().addAll((ToggleButton) bodyTypeGroup.getToggles().get(i));
            } else {
                if (i == 6) ((ToggleButton) bodyTypeGroup.getToggles().get(i)).setId("bodyButtonBL");
                else if (i == 8) ((ToggleButton) bodyTypeGroup.getToggles().get(i)).setId("bodyButtonBR");
                bottomBodyButtons.getChildren().addAll((ToggleButton) bodyTypeGroup.getToggles().get(i));
            }
        }
        VBox bodyButtons = new VBox(0);
        bodyButtons.getChildren().addAll(topBodyButtons, middleBodyButtons, bottomBodyButtons);
        
        VBox rightColumn = new VBox(12);
        rightColumn.getChildren().addAll(locationComboBox, bodyButtons);
        rightColumn.setAlignment(Pos.CENTER);
        
        HBox centerTop = new HBox(25);
        centerTop.getChildren().addAll(leftColumn, searchSeparator, rightColumn);
        centerTop.setAlignment(Pos.CENTER);
        
        searchButton = new Button("SEARCH");
        searchButton.setId("searchButton");
        searchButton.setOnAction(this);
        
        HBox centerBottom = new HBox(0);
        centerBottom.setPadding(new Insets(20,0,0,0));
        centerBottom.getChildren().addAll(searchButton);
        centerBottom.setAlignment(Pos.CENTER);
        
        VBox centerWrapper = new VBox();
        centerWrapper.getChildren().addAll(centerTop, centerBottom);
        centerWrapper.setAlignment(Pos.CENTER);
        
        mainBorderPane.setCenter(centerWrapper);
        
        bannerImageView = new ImageView();
        bannerImageView.setImage(bannerImage);
        bannerImageView.setFitHeight(150);
        bannerImageView.setPreserveRatio(true);
        bannerImageView.setFitWidth(900);
        
        StackPane imageContainer = new StackPane(bannerImageView);
        imageContainer.setAlignment(Pos.CENTER);
        imageContainer.setStyle("-fx-background-color: #373373;");
        mainBorderPane.setTop(imageContainer);
        
        searchScene = new Scene(mainBorderPane);
        mainBorderPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        
        bannerImageView.fitWidthProperty().bind(stage.widthProperty());
        bannerImageView.fitHeightProperty().bind(stage.heightProperty());
        
        stage.setScene(searchScene);
        
    }
    
    public static void makeResultsScene() {
        resultsBorderPane = new BorderPane();
        VBox resultsWrapper = new VBox(8);
        resultsWrapper.setPadding(new Insets(8));
        carsObservableList = FXCollections.observableArrayList(carsList);
        resultsListView = new ListView();
        resultsListView.setItems(carsObservableList);
        resultsListView.setCellFactory(new Callback<ListView<Car>, ListCell<Car>>() {

            @Override
            public ListCell<Car> call(ListView<Car> param) {
                ListCell<Car> cell = new ListCell<Car>() {

                    @Override
                    protected void updateItem(Car item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                           setText(item.model);
                        }
                    }
                };
                return cell;
            }
        });
        
        resultsWrapper.getChildren().addAll(resultsListView);
        
        resultsBorderPane.setCenter(resultsWrapper);
        
        resultsScene = new Scene(resultsBorderPane);
        stage.setScene(resultsScene);
    }
    
    public static void scrape(SingleSelectionModel area, Toggle btype, Toggle cond, String min, String max) {
        carsList = new ArrayList();
        resultLinks = new ArrayList();
        
        System.out.println("Starting scrape...");
        
        String condString ;
        if (cond == null) {
            condString = "any";
        } else {
            condString = cond.toString().substring(cond.toString().indexOf("'")+1, cond.toString().indexOf("'", cond.toString().indexOf("'")+1)).toLowerCase();
        }
        
        if (btype == null) {
            URL = buildURL(area.selectedItemProperty().getValue().toString(), "none", condString, min, max);
        } else {
            URL = buildURL(area.selectedItemProperty().getValue().toString(), btype.toString().substring(btype.toString().indexOf("'")+1, btype.toString().indexOf("'", btype.toString().indexOf("'")+1)), condString, min, max);
        }
        
        getLinksFromURL(URL);
        
        final ArrayList<String> finalURLs = resultLinks;
        
        ArrayList<Thread> threadList = new ArrayList();
        
        for (int i = 0; i<finalURLs.size(); i++) {
            final int f = i;
            threadList.add(i, new Thread() {
                @Override
                public void run() {
                    try {
                        carsList.add(new Car(finalURLs.get(f)));
                    } catch (CarSoldException e) {
                        System.out.println("Car at " + finalURLs.get(f) + " was either sold or link pointed to a non-existent page.");
                    }
                }
            });
            threadList.get(i).start();
        }
        
        //holds up on saving until all threads are done :)
        while (allThreadsDone(threadList)) {}
                
        makeResultsScene();
        
        /*FileChooser saveFileChooser = new FileChooser();
        saveFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        saveFileChooser.setTitle("Save CSV car info...");
        saveFileChooser.setInitialFileName("name.csv");
        
        File saveFile = saveFileChooser.showSaveDialog(stage);
        try {
            filename = saveFile.getPath();
            saveCarsCSV(filename);
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Saving finished");
            alert.setHeaderText("Saving successful!");
            alert.showAndWait();
        } catch (NullPointerException e) {}*/
        
    }
    
    public static void saveCarsCSV(String filename) {
        try {
            PrintWriter file = new PrintWriter(new FileWriter(filename));

            for (int i = 0; i < carsList.size(); i++) {
                //the next lines are customized for whatever data you are getting.
                //add CSVSAFE method to any strings
                String toSave = "";
                toSave = "" + carsList.get(i).year;
                toSave += "," + csvSafe(carsList.get(i).condition);
                toSave += "," + csvSafe(carsList.get(i).make);
                toSave += "," + csvSafe(carsList.get(i).model);
                toSave += "," + csvSafe(carsList.get(i).trim);
                toSave += "," + csvSafe(carsList.get(i).body);
                toSave += "," + csvSafe(carsList.get(i).color);
                toSave += "," + carsList.get(i).seats;
                toSave += "," + carsList.get(i).kilometers;
                toSave += "," + csvSafe(carsList.get(i).transmission);
                toSave += "," + csvSafe(carsList.get(i).fuelType);
                toSave += "," + carsList.get(i).price;
                toSave += "," + carsList.get(i).soldByDealer;
                toSave += "," + csvSafe(carsList.get(i).otherInfo);
                toSave += "," + csvSafe(carsList.get(i).description);
                toSave += "," + csvSafe(carsList.get(i).url);
                
                file.println(toSave);

            }
            file.close();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }

    }//end saveFile
    
    public static String csvSafe(String input) {
        
        if (input.contains(",")) {
            input = "\"" + input + "\"";
        }
        //if input contans no commas, what's the point in continuing?, just return the input
        
        return input;
    }
    
    public static void getLinksFromURL(String url) {
        try {
            String protocol = new URL(URL).getProtocol();
            String hostname = new URL(URL).getHost();
            try {
                Document page = Jsoup.connect(url).userAgent("Mozilla/17.0").get();

                Element resultsContainer = page.getElementsByClass("container-results large-images").first();

                Elements resultDivs = resultsContainer.getElementsByTag("div");
                for (int i = 0; i<resultDivs.size(); i++) {
                    if (!resultDivs.get(i).className().contains("regular-ad") || !resultDivs.get(i).className().contains("search-item")) {
                        resultDivs.remove(i);
                        i--;
                    }
                }
                for (Element currentResult : resultDivs) {
                    if (!currentResult.attr("data-vip-url").contains("kijijiautos.")) {
                        //filter out kijijiautos links for now
                        resultLinks.add((protocol + "://" + hostname + currentResult.attr("data-vip-url")));
                    }
                }
                
                //if there is a next, click it and scrape web
                Element pagination = page.getElementsByClass("bottom-bar").first().getElementsByClass("pagination").first();
                Element nextLink = pagination.getElementsByAttributeValue("title", "Next").first();
                
                if (nextLink != null) {
                    String nextPage = nextLink.attr("href");
                    nextPage = protocol + "://" + hostname + nextPage;
                    //System.out.println(nextPage);
                    getLinksFromURL(nextPage);
                }
                
                
            } catch (IOException e) {
                System.out.println("An error occurred, most likely page didn't connect.");
            }
            
        } catch (MalformedURLException e) {
            System.out.println("Bad URL.");
        }
    }
    
    public static String buildURL(String area, String body, String condition, String minString, String maxString) {
        
        int min = 0;
        try {
            min = Integer.parseInt(minString);
        } catch (NumberFormatException e){}
        
        int max = 0;
        try {
            max = Integer.parseInt(maxString);
        } catch (NumberFormatException e) {}
        
        String url;
        String areaCode = "edmonton"; //must have a default value
        String bodyCode = null;
        String condCode = null;
        String categoryCode = "c174l1700202";
        String price = "";
        
        url = "https://www.kijiji.ca/b-cars-trucks";
        
        if (area != null) {
            switch (area.toLowerCase()) {
                case "edmonton area":
                    areaCode = "edmonton-area";
                case "edmonton":
                    areaCode = "edmonton";
            }
        }
        
        if (body != null) {
            switch (body.toLowerCase()) {
                case "wagon":
                    bodyCode = "wagon";
                    categoryCode += "a138";
            }
        }
        
        if (!condition.equals("any")) {
            switch (condition) {
                case "used":
                    condCode = "used";
                    categoryCode += "a49";
                case "new":
                    condCode = "new";
                    categoryCode += "a49";
            }
        }
        
        String bodyCondCombo = null;
        if (condCode != null && bodyCode != null) {
            bodyCondCombo = bodyCode + "-" + condCode;
        } else if (condCode != null) {
            bodyCondCombo = condCode;
        } else if (bodyCode != null) {
            bodyCondCombo = bodyCode;
        }
        
        //price stuffs
        if (min > 0 || max > 0) {
            if ((min < max) || (min > max && max <= 0)) {
                price = "?price=";
                if (min > 0) {
                    price += min;
                }
                price += "__";
                if (max > 0) {
                    price += max;
                }
            }
        }
        
        url += "/" + areaCode;
        if (bodyCondCombo != null) {
            url += "/" + bodyCondCombo;
        }
        url += "/" + categoryCode;
        url += price;
        
        return url;
    }
    
    public static boolean allThreadsDone(ArrayList<Thread> list) {
        boolean threadsRunning = false;
        
        for (Thread currentThread : list) {
            if (currentThread.isAlive()) threadsRunning = true;
        }
        return threadsRunning;
    }
}