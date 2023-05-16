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
    public LocalDate getCreationDate() {return creationDate;}
    public ArrayList<Theater> getTheaters() {return theaters;}

    public boolean cardHasDiscount(Long card) {
        return allAssociates.contains(card);
    }

    /**
     * Instantiates a theater files factory, and a movie files factory, and extracts the files from both factories
     * to construct the theaters with their movie.
     */
    private void loadCinemaFiles(){
        TheaterFilesFactory tFactory = new TheaterFilesFactory();
        MovieFilesFactory mFactory = new MovieFilesFactory();

        ArrayList<MovieFile> movieFiles = mFactory.getMatchingFiles();

        for (TheaterFile theaterFile : tFactory.getMatchingFiles()){

            theaters.add(new Theater(theaterFile, movieFiles.get(theaterFile.getTheaterNumber() - 1)));
        }

        Collections.sort(theaters);
    }

    /**
     * Instantiates an associates files factory, and from each gotten file from it, it adds all the read numbers
     * to this multiplex state's own set of associate credit card numbers.
     */
    private void loadAssociatesCreditCards(){
        AssociateFilesFactory aFactory = new AssociateFilesFactory();

        for (AssociateFile associateFile: aFactory.getMatchingFiles()){
            allAssociates.addAll(associateFile.getAssociates());
        }
    }

    public MultiplexState() {

        loadCinemaFiles();
        loadAssociatesCreditCards();
        creationDate = LocalDate.now();
    }
}
