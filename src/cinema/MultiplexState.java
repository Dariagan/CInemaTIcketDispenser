package cinema;

import file_management.*;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public final class MultiplexState implements Serializable {

    private static final String STATE_FILE_NAME = "state.dat";
    private final LocalDate creationDate;
    private final ArrayList<Theater> theaters = new ArrayList<>();
    private final HashSet<Long> allAssociates = new HashSet<>();

    public static String getFileName() {return STATE_FILE_NAME;}
    public LocalDate getDate() {return creationDate;}
    public ArrayList<Theater> getTheaters() {return theaters;}

    public boolean cardHasDiscount(Long card) {
        return allAssociates.contains(card);
    }

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

        for (AssociateFile associateFile: aFactory.getFiles()){
            allAssociates.addAll(associateFile.getAssociates());
        }
    }

    public MultiplexState() {

        loadCinemaFiles();
        loadCreditCards();
        creationDate = LocalDate.now();
    }
}
