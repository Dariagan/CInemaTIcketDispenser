/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.time.LocalTime;
import java.util.*;

public final class Theater implements Serializable, Comparable<Theater>{


    private final int NUMBER;
    private final int PRICE;
    private int maxRows;
    private int maxCols;
    private final Movie MOVIE;
    private final TreeSet<Seat> seatSet = new TreeSet<>();
    private final ArrayList<Session> sessionList = new ArrayList<>();

    public Theater(TheaterFile theaterFile, MovieFile movieFile){

        this.NUMBER = theaterFile.getTheaterNumber();
        this.MOVIE = new Movie(movieFile);
        this.PRICE = movieFile.getPrice();

        loadSeats(theaterFile);
        loadSessions(movieFile);
    }

    public ArrayList<Session> getSessionList() {
        return sessionList;
    }

    public int getNUMBER() {return NUMBER;}
    public Movie getMOVIE(){return MOVIE;}
    public int getPRICE() {return PRICE;}
    public int getMaxRows() {return maxRows;}
    public int getMaxCols() {return maxCols;}
    public TreeSet<Seat> getSeatSet() {return seatSet;}

    private void loadSeats(TheaterFile theaterFile){
        int[] maxRows = {0};
        int[] maxCols = {0};

        seatSet.addAll(theaterFile.getSeats(theaterFile, maxRows, maxCols));

        this.maxRows = maxRows[0];
        this.maxCols = maxCols[0];
    }
    private void loadSessions(MovieFile movieFile){
        for (LocalTime sessionTime: movieFile.getSessionsTime()){
            sessionList.add(new Session(sessionTime));
        }
        Collections.sort(sessionList);
    }

    public String toString() {
        return MOVIE.getTITLE();
    }

    @Override
    public int compareTo(Theater o) {
        return Integer.compare(this.getNUMBER(), o.getNUMBER());
    }
}
