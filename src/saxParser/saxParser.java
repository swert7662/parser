package saxParser;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.*;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;

import org.xml.sax.helpers.DefaultHandler;

public class saxParser extends DefaultHandler{
    
    List elements;
    private String xmlFile;
    private String tempVal;
    private String director;
    private String tag = null;  
    private movie tempMov;
    private star tempStar;
    private cast tempCast;
    Connection db;
    
    // Map movieCategories of <code, category> for genre codes -> names
    // Populated in instantiation
    public static HashMap<String, String> movieCategories;

    
    // to maintain context
    
    
    public saxParser(String file){
        populateCategories();
        this.xmlFile = file;
        elements = new ArrayList();
    }
    
    public void runParser(){
        parse();
        printData();
    }
    
    public void parse(){
        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            javax.xml.parsers.SAXParser sp = spf.newSAXParser();
            
            //parse the file and also register this class for call backs
            InputSource input = new InputSource(xmlFile);
            input.setEncoding("ISO-8859-1");
            sp.parse(input, this);
            
        }catch (Exception e) {
                e.printStackTrace();
        } 
    }
    
    // For testing
    private void printData(){
        System.out.println("No of elements '" + elements.size() + "'.");
		
        Iterator it = elements.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }
    
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("film")) {
            // Create a new Movie instance
            tempMov = new movie();
            
        } else if (qName.equalsIgnoreCase("actor")) {
            // Create a new Star instance
            tempStar = new star();
            
        } else if (qName.equalsIgnoreCase("is")) {
            // Create a new Star instance
            tempCast = new cast();
        }
    }  
    
    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch,start,length);
    }
    
    public void endElement(String uri, String localName, String qName) throws SAXException {

        // In case of movie (title may be for Cast or Movie object)
	if (qName.equalsIgnoreCase("film")) {
            //Add to list if not a repeat and set to null
            elements.add(tempMov);
	}else if (qName.equalsIgnoreCase("t")){
            if (tempCast == null) {
                tempMov.setTitle(tempVal); 
            }
            else {
                tempCast.setMovie(tempVal);
            }
             
        } else if (qName.equalsIgnoreCase("year")){
            //tempMov.setYear(1234);
            tempMov.setYear(Integer.parseInt(tempVal));
        } else if (qName.equalsIgnoreCase("dirn")){
            tempMov.setDirector(tempVal);
        } else if (qName.equalsIgnoreCase("cat")){
            tempMov.addGenre(new genre(movieCategories.get(tempVal)));            
        }
        
        // In case of cast
        else if (qName.equalsIgnoreCase("is")) {
            director = tempVal;
        }else if (qName.equalsIgnoreCase("a")) {
            // parses to first_name and last_name
            tempCast.setDirector(director);
            tempCast.setName(tempVal);
        } else if (qName.equalsIgnoreCase("m")) {
            elements.add(tempCast);
        }
        
        // In case of actors
        else if (qName.equalsIgnoreCase("stagename")) {
            // parses to first_name and last_name
            tempStar.setName(tempVal);
        }else if (qName.equalsIgnoreCase("actor")) {
            elements.add(tempStar); 
        }
                // Removed due to invalid DOB format on xml being only year
//        else if (qName.equalsIgnoreCase("dob")) {
//            tempStar.setDob(tempVal);
//        } 
    }
    
    // populate genre categories
    public static void populateCategories () {
        movieCategories = new HashMap();
        movieCategories.put("Ctxx", "Uncategorized");
        movieCategories.put("Camp", "now - camp");
        movieCategories.put("Disa", "Disaster");
        movieCategories.put("Epic", "Epic");
        movieCategories.put("Faml", "Family");
        movieCategories.put("Surl", "Surreal");
        movieCategories.put("AvGa", "Avant Garde");
        movieCategories.put("Susp", "Thriller");
        movieCategories.put("susp", "Thriller");
        movieCategories.put("Fant", "Fantasy");
        movieCategories.put("Fant ", "Fantasy");
        movieCategories.put("Cart", "Cartoon");
        movieCategories.put("Actn", "Action");
        movieCategories.put("CnR", "Cops and Robbers");
        movieCategories.put("Dram", "Drama");
        movieCategories.put("Hist", "Historical");
        movieCategories.put("West", "Western");
        movieCategories.put("Myst", "Mystery");
        movieCategories.put("S.F.", "Science Fiction");
        movieCategories.put("ScFi", "Science Fiction");
        movieCategories.put("Advt", "Adventure");
        movieCategories.put("Horr", "Horror");
        movieCategories.put("Romt", "Romantic");
        movieCategories.put("Comd", "Comedy");
        movieCategories.put("Musc", "Musical");
        movieCategories.put("Docu", "Documentary");
        movieCategories.put("Porn", "Pornography");
        movieCategories.put("Noir", "Film Noir");
        movieCategories.put("BioP", "Biographical Picture");
        movieCategories.put("TV", "TV Show");
        movieCategories.put("TVs", "TV Series");
        movieCategories.put("TVm", "TV Miniseries");
    }
    
}