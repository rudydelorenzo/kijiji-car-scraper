package kijijiscraper;

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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class KijijiScraperMain extends Application{
    
    public static String URL;
    public static String filename = "wagonsunder4k.csv";
    public static ArrayList<String> resultLinks = new ArrayList();
    public static ArrayList<Car> carsList = new ArrayList();
    public static Stage stage;
    public static Scene searchScene;
    public static BorderPane mainBorderPane;
    public static TextField minPriceField, maxPriceField;
    public static ComboBox bodyComboBox;
    public static ToggleButton noneButton, usedButton, newButton;
    public static Image bannerImage;
    public static ImageView bannerImageView;
    public static DropShadow dropShadow = new DropShadow(15, Color.BLACK);
    
    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Kijiji Scraper");
        
        makeSearchScene();
        
        stage.setScene(searchScene);
        stage.setHeight(400);
        stage.setMinHeight(400);
        stage.setWidth(1000);
        
        stage.show();
    }
    
    public static void main(String[] args) {
        //scrape();
        bannerImage = new Image("file:logo/mainBanner.png");
        launch(args);
        
    }
    
    public static void makeSearchScene() {
        mainBorderPane = new BorderPane();
        noneButton = new ToggleButton("NONE");
        newButton = new ToggleButton("NEW");
        usedButton = new ToggleButton("USED");
        HBox conditionButtons = new HBox(0);
        conditionButtons.getChildren().addAll(noneButton, newButton, usedButton);
        conditionButtons.setAlignment(Pos.TOP_CENTER);
        mainBorderPane.setCenter(conditionButtons);
        conditionButtons.setPadding(new Insets(8));
        
        bannerImageView = new ImageView();
        bannerImageView.setImage(bannerImage);
        bannerImageView.setFitHeight(150);
        bannerImageView.setPreserveRatio(true);
        bannerImageView.setFitWidth(900);
        bannerImageView.setEffect(dropShadow);
        mainBorderPane.setTop(bannerImageView);
        
        searchScene = new Scene(mainBorderPane);
        mainBorderPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        
        bannerImageView.fitWidthProperty().bind(stage.widthProperty());
        bannerImageView.fitHeightProperty().bind(stage.heightProperty());
        
    }
    
    public static void scrape() {
        
        URL = buildURL("edmonton-area", "wagon", "used", 0, 4000);
        
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
        
        saveCarsCSV(filename);
        
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
    
    public static String buildURL(String area, String body, String condition, int min, int max) {
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
        
        if (condition != null) {
            switch (condition.toLowerCase()) {
                case "used":
                    condCode = "used";
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