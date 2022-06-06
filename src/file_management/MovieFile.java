package file_management;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import static java.util.Objects.isNull;

public final class MovieFile extends File implements Comparable<File>{

    private final String TIME_FORMAT = "^(2[0-3]|[01]?[0-9]):([0-5]?[0-9])$";

    public MovieFile (File file){
        super(file.getAbsolutePath());
    }

    public enum Field {
        THEATER_NUMBER {public String toString() {return "Theater: ";}},
        TITLE{public String toString() {return "Title: ";}},
        DESCRIPTION,
        SESSIONS{public String toString() {return "Sessions: ";}},
        DURATION{public String toString() {return Field.SESSIONS.toString();}},
        POSTER{public String toString() {return "Poster: ";}},
        PRICE{public String toString() {return "Price: ";}}
    }

    public int getTheaterNumber(){
        return Integer.parseInt(getRawDataSubString(Field.THEATER_NUMBER));
    }
    public String getTitle (){
        return getRawDataSubString(Field.TITLE);
    }
    public String getDescription (){
        return getRawDataSubString(Field.DESCRIPTION);
    }
    public int getDuration (){
        return computeMovieDuration(getRawDataSubString(Field.DURATION));
    }
    public String getPoster () {
        return System.getProperty("user.dir") + "\\data\\pics\\" + getRawDataSubString(Field.POSTER);
    }
    public int getPrice (){
        String rawDataString =  getRawDataSubString(Field.PRICE);

        return Integer.parseInt(rawDataString.substring(0, rawDataString.length()-2));
    }
    public ArrayList<LocalTime> getSessionsTimes(){
        return collectSessionsTimes(getRawDataSubString(Field.SESSIONS));
    }
    public ArrayList<LocalTime> collectSessionsTimes(String rawDataLine){

        ArrayList<LocalTime> foundSessionsTimes = new ArrayList<>();

        int nSessions = (rawDataLine.length()+1)/6;

        for (int i = 0; i < nSessions; i++) {
            int offset = i*6;

            if (rawDataLine.substring(offset, 5 + offset).matches(TIME_FORMAT)) {
                int hour = Integer.parseInt(rawDataLine.substring(offset, offset + 2));
                int minute = Integer.parseInt(rawDataLine.substring(offset + 3, offset + 5));

                LocalTime sessionTime = LocalTime.of(hour, minute);

                foundSessionsTimes.add(sessionTime);
            }
        }
        return foundSessionsTimes;
    }

    private int computeMovieDuration(String rawDataLine) {

        if (rawDataLine.substring(0, 5).matches(TIME_FORMAT)
                && rawDataLine.substring(6, 11).matches(TIME_FORMAT)){
            int startHour = Integer.parseInt(rawDataLine.substring(0,2));
            int startMinute = Integer.parseInt(rawDataLine.substring(3,5));

            LocalTime startTime = LocalTime.of(startHour, startMinute);

            int endHour = Integer.parseInt(rawDataLine.substring(6,8));
            int endMinute = Integer.parseInt(rawDataLine.substring(9,11));

            LocalTime endTime = LocalTime.of(endHour, endMinute);

            return (int)startTime.until(endTime, ChronoUnit.MINUTES);

        }
        else throw new RuntimeException("Sessions' times not recognized.");
    }

    private boolean isDescription(String line){
        return !line.startsWith(Field.TITLE.toString())
                && !line.startsWith(Field.THEATER_NUMBER.toString())
                && !line.startsWith(Field.SESSIONS.toString())
                && !line.startsWith(Field.POSTER.toString())
                && !line.startsWith(Field.PRICE.toString());
    }

    private String getRawDataSubString(Field searchedField) {
        String movieFilePath = this.getAbsolutePath();
        try {
            java.io.FileReader fr = new java.io.FileReader(movieFilePath);
            BufferedReader br = new BufferedReader(fr);
            String line;

            while (!isNull(line = br.readLine())) {
                line = line.trim();
                if (!line.isBlank()) {
                    switch (searchedField){
                        case THEATER_NUMBER, TITLE, SESSIONS, DURATION, POSTER, PRICE->{
                            if (line.startsWith(searchedField.toString())){
                                return line.substring(searchedField.toString().length());
                            }
                        }
                        case DESCRIPTION->{
                            if (isDescription(line))
                                return line;
                        }
                    }
                }
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException(searchedField.toString()+" not found in "+movieFilePath);
    }

    @Override
    public int compareTo(File o) {
        if (o instanceof MovieFile){
            return Integer.compare(this.getTheaterNumber(),((MovieFile) o).getTheaterNumber());
        }
        else throw new RuntimeException("should not be compared");
    }
}
