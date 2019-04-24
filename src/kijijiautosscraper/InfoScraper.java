package kijijiautosscraper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class InfoScraper {
    
    public static ArrayList<Car> carsList = new ArrayList();
    
    public static void main(String[] args) {
        
        //args[0] = "links.txt";
        
        //final ArrayList<String> URLs = loadURLFile(args[0]);
        final ArrayList<String> URLs = loadURLFile("links.txt");
        ArrayList<Thread> threadList = new ArrayList();
        
        for (int i = 0; i<URLs.size(); i++) {
            final int f = i;
            threadList.add(i, new Thread() {
                @Override
                public void run() {
                    try {
                        carsList.add(new Car(URLs.get(f)));
                    } catch (CarSoldException e) {
                        System.out.println("Car at " + URLs.get(f) + " was either sold or link pointed to a non-existent page.");
                    }
                }
            });
            threadList.get(i).start();
        }
        
        while (allThreadsDone(threadList)) {
            
        }
        
        saveCarsCSV("cars.csv");
        
    }
    
    public static ArrayList loadURLFile(String filename) {
        String temp = "";
        ArrayList<String> urlsList = new ArrayList();
        try {
            BufferedReader file = new BufferedReader(new FileReader(filename));
            while (file.ready()) {
                urlsList.add(file.readLine());
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        
        return urlsList;
        
    }//end loadFile

    public static void saveCarsCSV(String filename) {
        try {
            PrintWriter file = new PrintWriter(new FileWriter(filename));

            for (int i = 0; i < carsList.size(); i++) {
                //the next lines are customized for whatever data you are getting.
                String toSave = "";
                toSave = carsList.get(i).condition;
                toSave += "," + carsList.get(i).make;
                toSave += "," + carsList.get(i).model;
                toSave += "," + carsList.get(i).year;
                toSave += "," + carsList.get(i).trim;
                toSave += "," + carsList.get(i).kilometers;
                toSave += "," + carsList.get(i).body;
                toSave += "," + carsList.get(i).color;
                toSave += "," + carsList.get(i).seats;
                toSave += "," + carsList.get(i).price;
                
                
                file.println(toSave);

            }
            file.close();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }

    }//end saveFile
    
    /*public static void saveURLs(String filename) {
        try {
            PrintWriter file = new PrintWriter(new FileWriter(filename));

            for (int i = 0; i < URLs.size(); i++) {               
                file.println(URLs.get(i));
            }
            file.close();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }

    }//end saveFile*/
    
    public static boolean allThreadsDone(ArrayList<Thread> list) {
        boolean threadsRunning = false;
        
        for (Thread currentThread : list) {
            if (currentThread.isAlive()) threadsRunning = true;
        }
        return threadsRunning;
    }

}
