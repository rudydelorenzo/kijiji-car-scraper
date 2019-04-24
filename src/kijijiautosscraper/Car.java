package kijijiautosscraper;

import java.io.IOException;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.jsoup.HttpStatusException;

public class Car {
    
    private int attempts = 0;
    private boolean failed = false;
    
    public int price;
    public String condition;
    public String make;
    public String model;
    public int year;
    public String trim;
    public int kilometers;
    public String body;
    public String color;
    public int seats;
    
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

            Elements boxes = page.select("table._33axAgiVBzJUijJwQ638BO");
            Element priceContainer = page.select("div._1CI6wnk8u0QiMxa1hH-Kll").first();
            //first box is Overview, second is Mecanical, third is Equipment

            Elements overviewData = boxes.get(0).getElementsByTag("tr");
            Elements mechData = boxes.get(1).getElementsByTag("tr");
            if (boxes.size() > 2) {
                Elements equipmentData = boxes.get(2).getElementsByTag("tr");
            }

            ArrayList<String> carData = new ArrayList();
            for (Element currentElement : overviewData) {
                carData.add(currentElement.getElementsByTag("td").first().text());
            }

            assignOverviewArrayData(carData);

            price = stringToInt(priceContainer.getElementsContainingText("$").first().text());

            failed = false;
        } catch(IOException e) {
            if (e instanceof org.jsoup.HttpStatusException) {
                //error 404, car was sold
                failed = false;
                throw new CarSoldException("Car was sold");
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
