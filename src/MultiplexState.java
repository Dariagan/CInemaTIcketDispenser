import java.io.*;
import java.util.*;

public final class MultiplexState implements Serializable {

    private final ArrayList<Theater> theaters = new ArrayList<>();
    private CreditCardManager creditCardManager;

    public ArrayList<Theater> getTheaters() {return theaters;}
    public CreditCardManager getCreditCardManager() {return creditCardManager;}

    public enum FileType {
        THEATERS {public String toString() {return "Theater";}},
        MOVIES {public String toString() {return "Movie";}},
        ASSOCIATES {public String toString() {return "Associate";}};

        public String getDir() {
            switch (this) {
                case THEATERS -> {return "\\theaters";}
                case MOVIES -> {return "\\movies";}
                case ASSOCIATES -> {return "\\associates";}
                default -> {return null;}
            }
        }
        public String getFileExtension() {
            switch (this) {
                case THEATERS, MOVIES, ASSOCIATES -> {return ".txt";}
                default -> {return null;}
            }
        }
    }

    private ArrayList<File> getFiles(FileType fileType) {//todo volver polimórfico en vez de usar switches de enums

        String dirPath = System.getProperty("user.dir") + "\\data" + fileType.getDir();
        File dataFolder = new File(dirPath);

        String noun = fileType.toString();
        String fExt = fileType.getFileExtension();

        File[] readFiles = dataFolder.listFiles();
        ArrayList<File> selectedFiles = new ArrayList<>();

        if (readFiles != null) {
            for (File file : readFiles) {
                String fileName = file.getName();

                if (fileName.endsWith(fExt)) {
                    switch (fileType) {
                        case THEATERS -> {
                            if (fileName.length() >= noun.length() + fExt.length() + 1
                                && fileName.startsWith(noun)
                                && fileName.substring(noun.length(), fileName.length() - fExt.length()).matches("[1-9]"))
                                selectedFiles.add(new TheaterFile(file));
                            else {
                                String warning =
                                        String.format("%s was not loaded; doesn't match config file format '%s[1-9]%s'",
                                        fileName, noun, fExt);
                                System.out.println(warning);
                            }
                        }
                        case MOVIES ->  selectedFiles.add(new MovieFile(file));
                        case ASSOCIATES -> selectedFiles.add(new AssociateFile(file));
                    }
                }
            }
        }
        else {
            String exceptionText = String.format("%s files were not found at %s", noun, dataFolder);
            throw new RuntimeException(exceptionText);
        }
        return selectedFiles;
    }

    private void loadAllFiles(){
        for (File theatreFile: getFiles(FileType.THEATERS))
            for (File movieFile: getFiles(FileType.MOVIES))
                if (((TheaterFile)theatreFile).getTheaterNumber() == ((MovieFile)movieFile).getTheaterNumber()){
                    Theater theater = new Theater((TheaterFile)theatreFile, (MovieFile)movieFile);
                    theaters.add(theater);
                }
        Collections.sort(theaters);

        loadCreditCards();

    }

    private void loadCreditCards(){
        int arraySize = 0;
        for (File associateFile : getFiles(FileType.ASSOCIATES)){
            arraySize += ((AssociateFile)associateFile).getAssociateArray().length;
        }

        long[] allAssociatesArray = new long[arraySize];

        int i = 0;
        for (File associateFile : getFiles(FileType.ASSOCIATES)){
            for (Long cardNumber : ((AssociateFile)associateFile).getAssociateArray()){
                allAssociatesArray[i] = cardNumber;
                i++;
            }
        }
        creditCardManager = new CreditCardManager(allAssociatesArray);
    }

    public MultiplexState() {
        loadAllFiles();

    }
}
