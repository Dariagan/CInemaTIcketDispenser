/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.time.LocalTime;
import java.util.*;

public final class Theater implements Serializable, Comparable<Theater>{


    private final int number;
    private int price;
    private int maxRows;
    private int maxCols;
    private final Movie movie;
    private final TreeSet<Seat> seatSet = new TreeSet<>();
    private final ArrayList<Session> sessionList = new ArrayList<>();

    //Hice que se le pasen las files como argumentos y no el fileName (String) porque no me parece bien que a un
    //constructor público se le pueda pasar cualquier String como argumento.
    public Theater(TheaterFile theaterFile, MovieFile movieFile){

        this.number = theaterFile.getTheaterNumber();
        this.movie = new Movie(movieFile);

        loadSeats(theaterFile);
        loadSessions(movieFile);
    }

    public ArrayList<Session> getSessionList() {
        return sessionList;
    }

    public int getNumber() {return number;}
    public Movie getMovie(){return movie;}
    public int getPrice() {return price;}
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
        return movie.getTitle();
    }

    @Override
    public int compareTo(Theater o) {
        return Integer.compare(this.getNumber(), o.getNumber());
    }
}
