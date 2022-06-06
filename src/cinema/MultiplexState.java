package cinema;

import file_management.*;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

import static java.time.LocalDate.now;

public final class MultiplexState implements Serializable {



    private static final String stateFileName = "state.dat";
    private LocalDate creationDate;

    private ArrayList<Theater> theaters = new ArrayList<>();
    private CreditCardManager creditCardManager;

    public static String getFileName() {return stateFileName;}

    public LocalDate getDate() {return creationDate;}
    public ArrayList<Theater> getTheaters() {return theaters;}
    public CreditCardManager getCreditCardManager() {return creditCardManager;}


    private void loadCinemaFiles(){
        TheaterFilesFactory tFactory = new TheaterFilesFactory();
        MovieFilesFactory mFactory = new MovieFilesFactory();

        ArrayList<MovieFile> movieFiles = mFactory.getFiles();

        for (TheaterFile theaterFile : tFactory.getFiles()){
            theaters.add(new Theater(theaterFile, movieFiles.get(theaterFile.getTheaterNumber()-1)));
        }

        Collections.sort(theaters);
    }

    private void loadCreditCards(){
        AssociateFilesFactory aFactory = new AssociateFilesFactory();

        HashSet<Long> allAssociates = new HashSet<>();

        for (AssociateFile associateFile: aFactory.getFiles()){
            allAssociates.addAll(associateFile.getAssociates());
        }

        creditCardManager = new CreditCardManager(allAssociates);
    }

    public MultiplexState() {

        loadCinemaFiles();
        loadCreditCards();
        creationDate = LocalDate.now();
    }
}
