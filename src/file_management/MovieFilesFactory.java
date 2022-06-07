package file_management;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public final class MovieFilesFactory extends FilesFactory{

    public MovieFilesFactory() {
        super("movies", ".txt");
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
