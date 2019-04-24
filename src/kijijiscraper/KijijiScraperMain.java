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

public class KijijiScraperMain {
    
    public static String URL = "https://www.kijiji.ca/b-cars-trucks/edmonton/wagon-used/c174l1700203a138a49?price=__3000";
    public static ArrayList<String> resultLinks = new ArrayList();
    public static String filename = "linkskijiji.txt";
            
    public static void main(String[] args) {
        try {
            String protocol = new URL(URL).getProtocol();
            String hostname = new URL(URL).getHost();
            
            try {
                Document page = Jsoup.connect(URL).userAgent("Mozilla/17.0").get();

                Element resultsContainer = page.getElementsByClass("container-results large-images").first();

                Elements resultDivs = resultsContainer.getElementsByTag("div");
                for (int i = 0; i<resultDivs.size(); i++) {
                    if (!resultDivs.get(i).className().contains("regular-ad") || !resultDivs.get(i).className().contains("search-item")) {
                        resultDivs.remove(i);
                        i--;
                    }
                }
                for (Element currentResult : resultDivs) {
                    resultLinks.add((protocol + "://" + hostname + currentResult.attr("data-vip-url")));
                }
                
                saveToTextfile(resultLinks);
                
                String[] arguments = {filename};
                
                InfoScraper.main(arguments);
                
                //System.out.println(resultDivs.size());
            } catch (IOException e) {
                System.out.println("An error occurred, most likely page didn't connect.");
            }
            
        } catch (MalformedURLException e) {
            System.out.println("Bad URL.");
        }
            
    }
    
    public static void saveToTextfile(ArrayList<String> links) {
        try {
            PrintWriter file = new PrintWriter(new FileWriter(filename));

            for (int i = 0; i < links.size(); i++) {
                file.println(links.get(i));
            }
            
            file.close();
        } catch (IOException ex) {
        System.out.println(ex.toString());
        }

    }
    
}
