package file_management;

import java.io.File;
import java.util.ArrayList;

public final class TheaterFilesFactory extends FilesFactory{

    public TheaterFilesFactory() {
        super("theaters", ".txt", "Theater");
    }

    /**
     * Checks if the file name for the read theater file is adequate.
     * @param fileName file name to inspect
     * @return <code>true</code> if the selection condition is met in this factory
     */
    @Override
    boolean extraSelectionConditionIsMet(String fileName) {
        String fileNameNoun = getFILENAME_NOUN();

        boolean isTheRightLength = fileName.length() == fileNameNoun.length() + 1 + getFILE_EXTENSION().length();

        if (!isTheRightLength)
            return false;

        boolean startsWithRightNoun = fileName.startsWith(fileNameNoun);

        if (!startsWithRightNoun)
            return false;

        boolean hasAdequateNumber =
        fileName.substring(fileNameNoun.length(), fileName.length() - getFILE_EXTENSION().length()).matches("[1-4]");

        if (!hasAdequateNumber)
            return false;

        return true;
    }

    /**
     * Gets all the matching files from the parent factory's <code>getMatchingFiles()</code> method, and constructs an
     * instance of <code>TheaterFile</code> for each file said method returned.
     * @return list of selected theater files
     */
    public ArrayList<TheaterFile> getFiles(){
        ArrayList<TheaterFile> theaterFiles = new ArrayList<>();

        for (File file : super.getMatchingFiles()){
            theaterFiles.add(new TheaterFile(file));
        }

        return theaterFiles;
    }
}
