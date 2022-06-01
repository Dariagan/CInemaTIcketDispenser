import java.io.Serializable;

public class Movie implements Serializable{
    private final String name;
    private final String description;
    private final int duration;

    public String getTitle() {return name;}
    public String getDescription() {return description;}

    public Movie(MovieFile movieFile) {
        name = movieFile.getTitle();
        description = movieFile.getDescription();
        duration = movieFile.getDuration();
    }

}
