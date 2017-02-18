package saxParser;

import java.sql.Date;
import java.util.ArrayList;

public class star {
	private int id;
	private String first_name;
	private String last_name;
	//private Date dob;
        private String dob;
	private String photo_url;
	
	private ArrayList<movie> movies;
        
        public star() {
        }
        
        public star(String name) {
            parse(name);
        }
        
//	public Star(int id, String first_name, String last_name, Date dob, String photo_url) {
//		super();
//		this.id = id;
//		this.first_name = first_name;
//		this.last_name = last_name;
//		this.dob = dob;
//		this.movies = new ArrayList<Movie>();
//		
//		setPhoto_url(photo_url);
//	}

        
        private void parse(String name) {
            String[] split = name.split("\\s+");
        
            if (split.length == 1) {
                first_name = "";
                last_name = split[0];
            
            } else if (split.length == 2) {
                first_name = split[0];
                last_name = split[1];
            
            } else {
                first_name = split[0] + " ";
            
                int i;
                for (i = 1; i < split.length - 1; ++i) {
                    first_name += split[i] + " ";
                }           
                last_name = split[i];
            }     
        }
        
        public void setName(String name) {
            parse(name);
        }
        
	public void addMovie(movie movie)
	{
		movies.add(movie);
	}
	
	public void clearMovies()
	{
		movies.clear();
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the first_name
	 */
	public String getFirst_name() {
		return first_name;
	}
	/**
	 * @param first_name the first_name to set
	 */
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	/**
	 * @return the last_name
	 */
	public String getLast_name() {
		return last_name;
	}
	/**
	 * @param last_name the last_name to set
	 */
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	/**
	 * @return the dob
	 */
	public String getDob() {
		return dob;
	}
	/**
	 * @param dob the dob to set
	 */
	public void setDob(String dob) {
		this.dob = dob;
	}
        
        
        
//        public Date getDob() {
//		return dob;
//	}
//	/**
//	 * @param dob the dob to set
//	 */
//	public void setDob(Date dob) {
//		this.dob = dob;
//	}
        
	/**
	 * @return the photo_url
	 */
	public String getPhoto_url() {
		return photo_url;
	}
	/**
	 * @param photo_url the photo_url to set
	 */
	public void setPhoto_url(String photo_url) {
		if (!photo_url.toLowerCase().matches("^\\w+://.*")) {
			photo_url = "http://" + photo_url;
		}
		this.photo_url = photo_url;
	}
	
	public ArrayList<movie> getMovies() {
		return movies;
	}
        
        public String toString() {
        String star = "First name: " + first_name + " "
                    + "Last name: " + last_name + " "
                    + "DOB: " + dob;
        
        return star;
    }
        
}