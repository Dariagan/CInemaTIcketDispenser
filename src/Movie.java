import java.io.Serializable;

public final class Movie implements Serializable{
    private final String TITLE;
    private final String DESCRIPTION;
    private final int DURATION;
    private final String IMAGE;

    public String getDESCRIPTION() {return DESCRIPTION;}
    public int getDURATION() {return DURATION;}
    //TODO usar la duration
    public String getIMAGE() {return IMAGE;}

    public Movie(MovieFile movieFile) {
        TITLE = movieFile.getTitle();
        DESCRIPTION = movieFile.getDescription();
        DURATION = movieFile.getDuration();
        IMAGE = movieFile.getPoster();
    }

    public String getTITLE() {
        return TITLE;
    }
}
