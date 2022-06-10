package file_management;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public final class MovieFilesFactory extends FilesFactory{

    public MovieFilesFactory() {
        super("movies", ".txt");
    }

    @Override
    boolean extraSelectionConditionIsMet(String fileName) {
        return true;
    }

    /**
     * Gets all the matching files from the parent factory's <code>getMatchingFiles()</code> method, and constructs an
     * instance of <code>MovieFile</code> for each file said method returned.
     * @return list of movie files which have the correct file extension
     */
    public ArrayList<MovieFile> getFiles() {

        ArrayList<MovieFile> movieFiles = new ArrayList<>();
        for (File file : super.getMatchingFiles()){
            movieFiles.add(new MovieFile(file));
        }
        Collections.sort(movieFiles);
        return movieFiles;
    }
}
