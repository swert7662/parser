package saxParser;

import java.util.ArrayList;
import java.util.Iterator;

public class movie {
    private String title;
    private int year;
    private String director;
    private ArrayList<genre> genres;
    private String banner_url;
    private String trailer_url;
    
    public movie(){
        this.genres = new ArrayList<genre>();
    }


    public ArrayList<genre> getGenres() {
        return genres;
    }
    
    public void addGenre(genre genre){
	genres.add(genre);
    }
    
    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(int year) {
        this.year = year;
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

    /**
     * @return the banner_url
     */
    public String getBanner_url() {
        return banner_url;
    }

    /**
     * @param banner_url the banner_url to set
     */
    public void setBanner_url(String banner_url) {
        this.banner_url = banner_url;
    }

    /**
     * @return the trailer_url
     */
    public String getTrailer_url() {
        return trailer_url;
    }

    /**
     * @param trailer_url the trailer_url to set
     */
    public void setTrailer_url(String trailer_url) {
        this.trailer_url = trailer_url;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
	sb.append("Movie Details - ");
	sb.append("Title: " + getTitle());
	sb.append(", ");
	sb.append("Year: " + getYear());
	sb.append(", ");
	sb.append("Director: " + getDirector());
        sb.append(", ");
        sb.append("Genres: ");
        Iterator it = genres.iterator();
        while(it.hasNext()) {
            sb.append(it.next().toString() + ", ");
        }
	sb.append(".");

	return sb.toString();
    }
    
}