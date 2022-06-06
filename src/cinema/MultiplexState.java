package cinema;

import file_management.*;

import java.io.*;
import java.util.*;

public final class MultiplexState implements Serializable {

    private final ArrayList<Theater> theaters = new ArrayList<>();
    private CreditCardManager creditCardManager;

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

    }
}
