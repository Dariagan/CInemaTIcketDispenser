package cinema;

import file_management.MovieFile;
import file_management.TheaterFile;
import java.io.*;
import java.time.LocalTime;
import java.util.*;

public final class Theater implements Serializable, Comparable<Theater>{


    private final int NUMBER;
    private final int PRICE;
    private final Movie movie;
    private int maxRows;
    private int maxCols;
    private TreeSet<Seat> seatSet;
    private final ArrayList<Session> sessionList = new ArrayList<>();

    public Theater(TheaterFile theaterFile, MovieFile movieFile){

        this.NUMBER = theaterFile.getTheaterNumber();
        this.movie = new Movie(movieFile);
        this.PRICE = movieFile.getPrice();

        loadSeats(theaterFile);
        loadSessions(movieFile);
    }

    public ArrayList<Session> getSessionList() {
        return sessionList;
    }

    public int getNUMBER() {return NUMBER;}
    public int getPRICE() {return PRICE;}
    public Movie getMovie(){return movie;}
    public int getMaxRows() {return maxRows;}
    public int getMaxCols() {return maxCols;}
    public boolean hasSeat(Seat seat){return seatSet.contains(seat);}

    private void loadSeats(TheaterFile theaterFile){
        int[] maxRows = {0};
        int[] maxCols = {0};

        seatSet = theaterFile.getSeats(theaterFile, maxRows, maxCols);

        this.maxRows = maxRows[0];
        this.maxCols = maxCols[0];
    }
    private void loadSessions(MovieFile movieFile){
        for (LocalTime sessionTime: movieFile.getSessionsTimes()){
            sessionList.add(new Session(sessionTime));
        }
        Collections.sort(sessionList);
    }

    public String toString() {
        return String.format("%s, %d€", movie.toString(), this.getPRICE());
    }

    @Override
    public int compareTo(Theater o) {
        return Integer.compare(this.getNUMBER(), o.getNUMBER());
    }
}
