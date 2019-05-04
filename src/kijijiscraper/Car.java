package kijijiscraper;

import java.io.IOException;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class Car {
    
    private int attempts = 0;
    private boolean failed = false;
    
    public int price;
    public String condition = "N/A";
    public String description = "N/A";
    public String make = "N/A";
    public String model = "N/A";
    public int year;
    public String transmission = "N/A";
    public String drivetrain = "N/A";
    public String fuelType = "N/A";
    public String trim = "N/A";
    public int kilometers;
    public String body = "N/A";
    public String color = "N/A";
    public int seats;
    public String url = "N/A";
    public boolean soldByDealer;
    public boolean carSold = false;
    public String otherInfo = "None";
    public String diagnosticMsgs = "";
    
    public Car() {
        //do not use
    }
    
    public Car(String url) throws CarSoldException{
        do {
            try {
                Document page = Jsoup.connect(url).userAgent("Mozilla/17.0").get();
                try {
                    Element attributesBox = page.getElementById("AttributeList");

                    Elements attributeLists = attributesBox.getElementsByTag("ul");

                    ArrayList<String> carData = new ArrayList();
                    this.url = url;

                    //System.out.println("-------------------------------");

                    for (Element currentList : attributeLists) {
                        for (Element currentElement : currentList.getElementsByTag("li")) {
                            if (currentElement.getElementsByTag("span").isEmpty()) {
                                String attribute;
                                String value;
                                try {
                                    attribute = currentElement.getElementsByTag("dt").first().text();
                                } catch (NullPointerException e) {
                                    attribute = "N/A";
                                }
                                try {
                                    value = currentElement.getElementsByTag("dd").first().text();
                                } catch (NullPointerException e) {
                                    value = "N/A";
                                }
                                assignData(attribute, value);
                                //System.out.println(attribute + " : " + value);
                            } else {
                                //includes stuff
                                String value;
                                try {
                                    value = currentElement.getElementsByTag("span").first().text();
                                } catch (NullPointerException e) {
                                    value = "N/A";
                                }
                                assignData("includes", value);
                                //System.out.println("INCLUDES : " + value);
                            }
                        }

                    }
                } catch (NullPointerException e) {
                    if (page.select("div.expired-ad-container") != null) {
                        carSold = true;
                        System.out.println("Car at " + url + " was sold!");
                    }
                }
                //assignOverviewArrayData(carData); 

                //gotta change up how values are assigned. attribute and value will get fed into a method with a ton of if statements.

                try {

                    Element priceContainer = page.getElementsByAttributeValue("itemprop", "price").first();

                    price = stringToInt(priceContainer.getElementsContainingText("$").first().text());

                    //System.out.println("PRICE : " + price);

                } catch (NullPointerException e) {
                    System.out.println("PRICE : Could not be determined");
                }

                try {
                    Element priceCont = page.getElementsByAttributeValue("itemprop", "offers").first();
                    soldByDealer = !priceCont.getElementsByClass("additionalTaxes-1130400051").isEmpty();
                    //System.out.println("SOLD BY DEALER : " + soldByDealer);
                } catch (NullPointerException e) {
                    System.out.println("Couldn't determine car seller" + e.getStackTrace().toString());
                    soldByDealer = false;
                }
                
                //get description
                try {
                    Element descriptionContent = page.getElementsByClass("descriptionContainer-3544745383").first();
                    description = descriptionContent.getElementsByAttributeValue("itemprop", "description").text();
                    //System.out.println(description);
                } catch (NullPointerException e) {
                    System.out.println("Couldn't find description");
                }

                failed = false;
            } catch(IOException e) {
                if (e instanceof org.jsoup.HttpStatusException) {
                    //error 404, car was sold
                    failed = false;
                } else {
                    System.out.println("(" + attempts + ")Couldn't connect to website " + url + ", retrying...");
                    failed = true;
                    attempts++;
                }
            }
        } while(attempts < 3 && failed);
    }
    
    private void assignData(String attribute, String value) {
        switch (attribute.toLowerCase()) {
            case "N/A":
                break;
            case "condition":
                condition = value;
                break;
            case "make":
                make = value;
                break;
            case "model":
                model = value;
                break;
            case "year":
                try {
                    year = stringToInt(value);
                } catch (java.lang.NumberFormatException e) {
                    System.out.println("Year failed to convert");
                    year = 0;
                }
                break;
            case "trim":
                trim = value;
                break;
            case "kilometers":
                try {
                    kilometers = stringToInt(value);
                } catch (java.lang.NumberFormatException e) {
                    System.out.println("KMs failed to convert");
                    kilometers = 0;
                }
                break;
            case "body type":
                body = value;
                break;
            case "colour":
                color = value;
                break;
            case "no. of seats":
                try {
                    seats = stringToInt(value);
                } catch (java.lang.NumberFormatException e) {
                    System.out.println("No. of seats failed to convert");
                    seats = 0;
                }
                break;
            case "transmission":
                transmission = value;
                break;
            case "drivetrain":
                drivetrain = value;
                break;
            case "fuel type":
                fuelType = value;
                break;
            case "includes":
                if (!otherInfo.contains("Includes")) {
                    otherInfo = "Includes: " + value;
                } else {
                    otherInfo += (", " + value);
                }
                break;
        }
        
        
    }
    
    private int stringToInt(String string) throws java.lang.NumberFormatException {
        //remove decimals
        if (string.contains(".")) string = string.substring(0, string.indexOf("."));
        //string to arraylist
        ArrayList<Character> chars = new ArrayList();
        for (char currentChar:string.toCharArray()) {
            chars.add(currentChar);
        }
        //cleanup arraylist
        for (int i = 0; i<chars.size(); i++) {
            if ((int)chars.get(i) > 57 || (int)chars.get(i) < 48) {
                chars.remove(i);
                i--;
            }
        }
        //back to array and convert to string
        char[] finalArray = new char[chars.size()];
        for (int i = 0; i<finalArray.length; i++) {
            finalArray[i] = chars.get(i);
        }
        
        return Integer.parseInt(new String(finalArray));
    }
    
}
