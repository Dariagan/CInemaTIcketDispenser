package cinema;

import file_management.MovieFile;

import java.io.Serializable;

public final class Movie implements Serializable, Comparable<Movie>{
    private final String TITLE;
    private final String DESCRIPTION;
    private final int DURATION;
    private final String IMAGE;
    private final int THEATER_NUMBER;

    public String getDescription() {return DESCRIPTION;}
    public int getDuration() {return DURATION;}
    //TODO usar la duration
    public String getImage() {return IMAGE;}

    public Movie(MovieFile movieFile) {
        TITLE = movieFile.getTitle();
        DESCRIPTION = movieFile.getDescription();
        DURATION = movieFile.getDuration();
        IMAGE = movieFile.getPoster();
        THEATER_NUMBER = movieFile.getTheaterNumber();
    }

    public String getTitle() {
        return TITLE;
    }

    @Override
    public int compareTo(Movie o) {
        return Integer.compare(this.THEATER_NUMBER, o.THEATER_NUMBER);
    }
}
