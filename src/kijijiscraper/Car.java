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
    public String condition;
    public String make;
    public String model;
    public int year;
    public String transmission;
    public String drivetrain;
    public String fuelType;
    public String trim;
    public int kilometers;
    public String body;
    public String color;
    public int seats;
    public String url;
    public boolean soldByDealer;
    public boolean carSold;
    public String otherInfo;
    
    public Car() {
        //do not use
    }
    
    public Car(ArrayList<String> carInfo) {
        condition = carInfo.get(0);
        make = carInfo.get(1);
        model = carInfo.get(2);
        year = Integer.parseInt(carInfo.get(3));
        trim = carInfo.get(4);
        kilometers = stringToInt(carInfo.get(5));
        body = carInfo.get(6);
        color = carInfo.get(7);
        seats = Integer.parseInt(carInfo.get(8));
    }
    
    public Car(String url) throws CarSoldException{
        
        try {
            Document page = Jsoup.connect(url).userAgent("Mozilla/17.0").get();
            try {
                Element attributesBox = page.getElementById("AttributeList");

                Elements attributeLists = attributesBox.getElementsByTag("ul");

                ArrayList<String> carData = new ArrayList();
                this.url = url;

                System.out.println("-------------------------------");

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
                            //carData.add(currentElement.getElementsByTag("td").first().text());
                            System.out.println(attribute + " : " + value);
                        } else {
                            //includes stuff
                            String value;
                            try {
                                value = currentElement.getElementsByTag("span").first().text();
                            } catch (NullPointerException e) {
                                value = "N/A";
                            }
                            System.out.println("INCLUDES : " + value);
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
                
                System.out.println("PRICE : " + price);
                
            } catch (NullPointerException e) {
                System.out.println("PRICE : Could not be determined");
            }
            
            try {
                Element priceCont = page.getElementsByAttributeValue("itemprop", "offers").first();
                soldByDealer = !priceCont.getElementsByClass("additionalTaxes-1130400051").isEmpty();
                System.out.println("SOLD BY DEALER : " + soldByDealer);
            } catch (NullPointerException e) {
                System.out.println("Couldn't determine car seller");
                soldByDealer = false;
            }
            
            failed = false;
        } catch(IOException e) {
            if (e instanceof org.jsoup.HttpStatusException) {
                //error 404, car was sold
                failed = false;
            } else {
                System.out.println("Couldn't connect to website, retrying...");
                failed = true;
                attempts++;
            }
        }
    }
    
    private void assignOverviewArrayData(ArrayList<String> array) {
        condition = array.get(0);
        make = array.get(1);
        model = array.get(2);
        try {
            year = Integer.parseInt(array.get(3));
        } catch (java.lang.NumberFormatException e) {
            year = 0;
        }
        trim = array.get(4);
        try {
            kilometers = stringToInt(array.get(5));
        } catch (java.lang.NumberFormatException e) {
            kilometers = 0;
        }
        
        body = array.get(6);
        color = array.get(7);
        try {
            seats = Integer.parseInt(array.get(8));
        } catch (java.lang.NumberFormatException e) {
            seats = 0;
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
