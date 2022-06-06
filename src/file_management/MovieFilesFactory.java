package file_management;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class MovieFilesFactory extends FilesFactory{

    public MovieFilesFactory() {
        super("movies", ".txt");
    }

    @Override
    boolean extraSelectionConditionIsMet(String fileName) {
        return true;
    }

    public ArrayList<MovieFile> getFiles() {

        ArrayList<MovieFile> movieFiles = new ArrayList<>();
        for (File file : super.getMatchingFiles()){
            movieFiles.add(new MovieFile(file));
        }
        Collections.sort(movieFiles);
        return movieFiles;
    }
}
