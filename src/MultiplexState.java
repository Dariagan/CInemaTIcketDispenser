import java.io.*;
import java.util.*;

public class MultiplexState implements Serializable {

    private final ArrayList<Theater> theaters = new ArrayList<>();

    public ArrayList<Theater> getTheaters() {
        return theaters;
    }

    public enum FileType {
        THEATERS {public String toString() {return "Theater";}},
        MOVIES {public String toString() {return "Movie";}};

        public String getDir() {
            switch (this) {
                case THEATERS -> {return "\\theaters";}
                case MOVIES -> {return "\\movies";}
                default -> {return null;}
            }
        }

        public String getFileExtension() {
            switch (this) {
                case THEATERS, MOVIES -> {return ".txt";}
                default -> {return null;
                }
            }
        }
    }

    private ArrayList<File> getFiles(FileType fileType) {

        String dirPath = System.getProperty("user.dir") + "\\data" + fileType.getDir();
        File directory = new File(dirPath);

        String noun = fileType.toString();
        String fExt = fileType.getFileExtension();

        File[] readFiles = directory.listFiles();
        ArrayList<File> selectedFiles = new ArrayList<>();

        if (readFiles != null) {
            for (File file : readFiles) {
                String fileName = file.getName();

                if (fileName.length() >= noun.length() + fExt.length() + 1
                    && fileName.startsWith(noun)
                    && fileName.substring(noun.length(), fileName.length() - fExt.length()).matches("[1-9]")
                    && fileName.endsWith(fExt)) {

                    switch (fileType) {
                        case THEATERS -> selectedFiles.add(new TheaterFile(file));
                        case MOVIES ->  selectedFiles.add(new MovieFile(file));
                    }

                } else {
                    String warning = String.format("%s was ignored; doesn't match config file format '%s[1-9]%s'"
                            , fileName, noun, fExt);
                    System.out.println(warning);
                }
            }
        }
        else System.out.println("dar warning de q no se encontraron las files");
        return selectedFiles;
    }


    private void loadState(){
        for (File theatreFile: getFiles(FileType.THEATERS)){
            for (File movieFile: getFiles(FileType.MOVIES)){
                if (((TheaterFile)theatreFile).getTheaterNumber() == ((MovieFile)movieFile).getTheaterNumber()){
                    Theater theater = new Theater((TheaterFile)theatreFile, (MovieFile)movieFile);
                    theaters.add(theater);
                }
            }
        }
        Collections.sort(theaters);
    }

    public MultiplexState() {
        loadState();
    }
}
