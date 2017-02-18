package saxParser;

import java.sql.*;
import java.util.*;
import javafx.util.Pair;

public class main {
    

    
    public static void main(String[] args) throws SQLException {
    		
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "root","password");      

	        // Parse xml files for Lists of objects type Movie, Cast, and Star
	        saxParser movieList = new saxParser("../mains243.xml");
	        saxParser castList = new saxParser("../casts124.xml");
	        saxParser starsList = new saxParser("../actors63.xml");
	        
	        movieList.runParser();
	        castList.runParser();
	        starsList.runParser();
	        
	        // Batch insert movieList Movie objects into movies table
	        Statement stmt = connection.createStatement();
	        
	        insertMovies(movieList, stmt);
	        insertStars(starsList, stmt);
	        insertGenres(movieList, stmt);
	        insertStarsInMovies(castList, stmt);
	        insertGenresInMovies(movieList, stmt);
	        
	        
	        // For testing
	         
	        String sql = "SELECT * FROM stars_in_movies";
	        ResultSet results = stmt.executeQuery(sql);
	        stmt.clearBatch();
	        int count = 1;
	        /*while (results.next()){
	                System.out.println(count + ". star_id = " + results.getString("star_id") 
	                                         + " , movie_id = " + results.getString("movie_id"));
	                ++count;
	        }
	         
	        sql = "SELECT * FROM genres_in_movies";
	        results = stmt.executeQuery(sql);
	        stmt.clearBatch();
	        count = 1;
	        while (results.next()){
	                System.out.println(count + ". genre_id = " + results.getString("genre_id") 
	                                          + " , movie_id = " + results.getString("movie_id"));
	                ++count;
	        }*/
	
	        stmt.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
   
    // Batch inserts movies that do not already exist in the database
    public static void insertMovies(saxParser movieList, Statement statement) {
    	try {
	        HashSet<Pair<String,String>> dbMoviesSet = new HashSet<Pair<String,String>>();
	        
	        // Load db names to not insert stars that already exist
	        String query = "SELECT id, title, director FROM movies";
	        ResultSet results = statement.executeQuery(query);
	        statement.clearBatch();
	        while (results.next()) {
	            dbMoviesSet.add(new Pair(results.getString("title"), results.getString("director")));
	        }
	        
	        // Insert into db if there are no duplicates
	        String batchInsertQuery = "INSERT INTO movies (title, year, director) VALUES ";
	        Iterator it = movieList.elements.iterator();
	        int count = 0;
	        while (it.hasNext()) {
	            movie tempMov = (movie)it.next();
	            // Check for valid input and add to batch insert
	            // banner and trailer url do not appear in xml files
	            if ((dbMoviesSet.isEmpty() || !dbMoviesSet.contains(new Pair(tempMov.getTitle(), tempMov.getDirector())))
	                && tempMov.getTitle().matches("[a-zA-Z.]+") 
	                && tempMov.getDirector() != null && tempMov.getDirector().matches("[a-zA-Z.]+")) {
	            	
	            	String batchQuery = "insert into movies (title, year, director) values ";
	            	
	            	batchQuery += " ('" + tempMov.getTitle() + "', " 
	                                          + tempMov.getYear() + ", '" 
	                                          + tempMov.getDirector() + "')";
	            	statement.addBatch(batchQuery);
	                ++count;
	            }
	        }
	        
	        if (count != 0) {
	        	statement.executeBatch();
	        }
    	} catch (SQLException ex){ 
	    	while (ex != null) 
	        {
	            System.out.println("SQL Exception:  " + ex.getMessage());
	            ex = ex.getNextException();
	        }
    	}
    	catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Batch inserts star names that do not already exist in the database
    public static void insertStars(saxParser starsList, Statement statement) throws SQLException {
        // HashSets to insure no duplicate star names are input or overwritten
        HashSet<Pair<String, String>> xmlStarSet = new HashSet<Pair<String, String>>();
        HashSet<Pair<String, String>> dbStarSet = new HashSet<Pair<String, String>>();
        
        // Load xmlNames
        Iterator it = starsList.elements.iterator();
        while (it.hasNext()) {
            star tempStar = (star)it.next();
            xmlStarSet.add(new Pair(tempStar.getFirst_name(), tempStar.getLast_name()));
        }
        
        // Load db names to not insert stars that already exist
        String query = "SELECT first_name, last_name FROM stars";
        ResultSet starNames = statement.executeQuery(query);
        statement.clearBatch();
        while (starNames.next()) {
            dbStarSet.add(new Pair(starNames.getString("first_name"), starNames.getString("last_name")));
        }
        
        String batchInsertQuery = "INSERT INTO stars (first_name, last_name) VALUES ";
        it = xmlStarSet.iterator();
        int count = 0;
        while (it.hasNext()) {
            Pair<String,String> tempName = (Pair)it.next();
            
            // Check to not insert if exists
            if (!dbStarSet.contains(tempName)) {
                // Check for valid input and add to batch insert
                if ((tempName.getKey().equals("") || tempName.getKey().matches("[a-zA-Z.]+")) &&
                tempName.getValue().matches("[a-zA-Z.]+")) {
                batchInsertQuery += " ('" + tempName.getKey() + "', '" 
                                          + tempName.getValue()+ "'),";
                ++count;
                }
            }
        }
        // Replace trailing "," with ";" and insert
        batchInsertQuery = batchInsertQuery.substring(0, batchInsertQuery.length() -1) + ";";
        
        // In case there are no entries to update
        if (count != 0) {
            statement.executeUpdate(batchInsertQuery);
            statement.clearBatch();
        }
    }
    
    // Batch inserts genre names that do not already exist in the database
    public static void insertGenres(saxParser movieList, Statement statement) throws SQLException {
        HashSet<String> xmlGenreSet = new HashSet<String>();
        HashSet<String> dbgenreSet = new HashSet<String>();
        
        // Load genre list from movies and add to set to prevent duplicates
        Iterator it = movieList.elements.iterator();
        while (it.hasNext()) {
            movie tempMov = (movie)it.next();
            ArrayList<genre> genres = tempMov.getGenres();
            Iterator itGenres = genres.iterator();
            while (itGenres.hasNext()) {
                genre tempGen = (genre)itGenres.next();
                xmlGenreSet.add(tempGen.getName());
            }   
        }
        // Remove possible bad input
        if (xmlGenreSet.contains(null)) {
            xmlGenreSet.remove(null);
        }
        
        // Load genre names from db to prevent inserting pre-existing names
        // Load db names
        String query = "SELECT name FROM genres";
        ResultSet results = statement.executeQuery(query);
        statement.clearBatch();
        while (results.next()) {
            dbgenreSet.add(results.getString("name"));
        }
        
        // Batch insert into genres if name does not already exist
        String batchInsertQuery = "INSERT INTO genres (name) VALUES ";
        it = xmlGenreSet.iterator();
        int count = 0;
        while (it.hasNext()) {
            String name = (String)it.next();
            if (!dbgenreSet.contains(name)) {
                batchInsertQuery += " ('" + name + "'),";
                ++count;
            }
        }
        // Replace trailing "," with ";" and insert
        batchInsertQuery = batchInsertQuery.substring(0, batchInsertQuery.length() -1) + ";";
        
        // Execute insert only if genres were added
        if (count != 0) {
            statement.executeUpdate(batchInsertQuery);
            statement.clearBatch();
        }
    }
    
    public static void insertGenresInMovies(saxParser movieList, Statement statement) throws SQLException {
        HashMap<String, Integer> genresMap = new HashMap<String, Integer>();
        HashMap<Pair<String, String>, Integer> moviesMap = new HashMap<Pair<String, String>, Integer>();
        HashSet<Pair<Integer, Integer>> checkID = new HashSet<Pair<Integer, Integer>>();
        
        
        String query = "SELECT id, name FROM genres";
        ResultSet results = statement.executeQuery(query);
        statement.clearBatch();
        while (results.next()) {
            genresMap.put(results.getString("name"), results.getInt("id"));
        }
        
        query = "SELECT id, title, director FROM movies";
        results = statement.executeQuery(query);
        statement.clearBatch();
        while (results.next()) {
            moviesMap.put(new Pair(results.getString("title"), results.getString("director")), results.getInt("id"));
        }
        
        // Load genres_from_movies into checkID
        query = "SELECT genre_id, movie_id from genres_in_movies";
        results = statement.executeQuery(query);
        statement.clearBatch();
        while (results.next()) {
            checkID.add(new Pair(results.getInt("genre_id"), results.getInt("movie_id")));
        }      
        
        // Batch insert into genres_in_movies
        // title, director and name Strings added for readability
        String batchInsertQuery = "INSERT IGNORE INTO genres_in_movies (genre_id, movie_id) VALUES ";
        Iterator it = movieList.elements.iterator();
        int count = 0;
        while (it.hasNext()) {
            movie tempMov = (movie) it.next();
            String title = tempMov.getTitle();
            String director = tempMov.getDirector();
            ArrayList<genre> genreList = tempMov.getGenres();
            Iterator itGenre = genreList.iterator();
            while (itGenre.hasNext()) {
                genre tempGenre = (genre) itGenre.next();
                String name = tempGenre.getName();
                if (moviesMap.get(new Pair(title, director)) != null && genresMap.get(name) != null
                        && (checkID.isEmpty() || !checkID.contains(new Pair(genresMap.get(name), moviesMap.get(new Pair(title, director)))))) {
                    batchInsertQuery += " ('" + genresMap.get(name) + "','"
                                              + moviesMap.get(new Pair(title, director)) + "'),";
                    ++count;
                }
            }
        }
        // Replace trailing "," with ";" and insert
        batchInsertQuery = batchInsertQuery.substring(0, batchInsertQuery.length() -1) + ";";
        
        // Execute insert only if elements were added
        if (count != 0) {
            statement.executeUpdate(batchInsertQuery);
            statement.clearBatch();
        }
        
    }
    
    // Assumes no duplicate stars due to it being impossible to check stars without DOB
    public static void insertStarsInMovies(saxParser castList, Statement statement) throws SQLException {
        HashSet<Pair<Integer, Integer>> checkID = new HashSet<Pair<Integer, Integer>>();
        HashMap<Pair<String, String>, Integer> starNamesToID = new HashMap<Pair<String, String>, Integer>();
        HashMap<Pair<String, String>, Integer> movieNamesToID = new HashMap<Pair<String, String>, Integer>();
        HashSet<cast> castSet = new HashSet<cast>();
        
        // Load movieNamesToID map from movies table
        String query = "SELECT id, title, director FROM movies";
        ResultSet results = statement.executeQuery(query);
        statement.clearBatch();
        while (results.next()) {
            movieNamesToID.put(new Pair(results.getString("title"), results.getString("director")), results.getInt("id"));
        }
        
        // Load starNamesToID map from stars table
        query = "SELECT id, first_name, last_name FROM stars";
        results = statement.executeQuery(query);
        statement.clearBatch();
        while (results.next()) {
            starNamesToID.put(new Pair(results.getString("first_name"), results.getString("last_name")), results.getInt("id"));
        }
        
        // Load checkID set from stars_in_movies to prevent duplicate insertions
        query = "SELECT star_id, movie_id FROM stars_in_movies";
        results = statement.executeQuery(query);
        statement.clearBatch();
        while (results.next()) {
            checkID.add(new Pair(results.getString("star_id"), results.getString("movie_id")));
        }
        
        // Remove any duplicate castList entries
        Iterator it = castList.elements.iterator();
        while (it.hasNext()) {
            castSet.add((cast) it.next());
        }
        
        // Insert into database using Cast variables as ID's
        String batchInsertQuery = "INSERT IGNORE INTO stars_in_movies (star_id, movie_id) VALUES ";
        it = castSet.iterator();
        int count = 0;
        while (it.hasNext()) {
            cast tempCast = (cast) it.next();
            if (movieNamesToID.containsKey(new Pair(tempCast.getMovie(), tempCast.getDirector())) 
                    && tempCast.getFirst_name() != null && starNamesToID.containsKey(new Pair(tempCast.getFirst_name(), tempCast.getLast_name()))
                    && !checkID.contains(new Pair(starNamesToID.get(new Pair(tempCast.getFirst_name(), tempCast.getLast_name())),movieNamesToID.get(new Pair(tempCast.getMovie(), tempCast.getDirector()))))) {
                batchInsertQuery += " ('" + starNamesToID.get(new Pair(tempCast.getFirst_name(), tempCast.getLast_name())) + "','"
                                          + movieNamesToID.get(new Pair(tempCast.getMovie(), tempCast.getDirector())) + "'),";
                ++count;
            }
        }
        // Replace trailing "," with ";" and insert
        batchInsertQuery = batchInsertQuery.substring(0, batchInsertQuery.length() -1) + ";";
        
        // Execute insert only if elements were added
        if (count != 0) {
            statement.executeUpdate(batchInsertQuery);
            statement.clearBatch();
        }
       
    }
}