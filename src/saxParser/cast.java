package saxParser;

public class cast {
    
    private String first_name;
    private String last_name;
    private String director;
    private String movie;
    
    public cast() {}
    
    public cast(String name) {
        parse(name);
    }
    
    private void parse(String name) {
        String[] split = name.split("\\s+");
        
        if (split.length == 1) {
            first_name = null;
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
    
    // Set first and last name through one name
    public void setName(String name) {
        parse(name);
    }
    
    /**
     * @return the first_name
     */
    public String getFirst_name() {
        return first_name;
    }

    /**
     * @return the last_name
     */
    public String getLast_name() {
        return last_name;
    }

    /**
     * @return the movie
     */
    public String getMovie() {
        return movie;
    }

    /**
     * @param movie the movie to set
     */
    public void setMovie(String movie) {
        this.movie = movie;
    }

    public String toString() {
        String cast = "First name: " + first_name + " "
                    + "Last name: " + last_name + " "
                    + "Movie: " + movie;
        
        return cast;
    }

    /**
     * @return the director
     */
    public String getDirector() {
        return director;
    }

    /**
     * @param director the director to set
     */
    public void setDirector(String director) {
        this.director = director;
    }
}