package file_management;

import java.io.File;
import java.util.ArrayList;

public final class TheaterFilesFactory extends FilesFactory{

    public TheaterFilesFactory() {
        super("theaters", ".txt", "Theater");
    }

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

    public ArrayList<TheaterFile> getFiles(){
        ArrayList<TheaterFile> theaterFiles = new ArrayList<>();
        for (File file : super.getMatchingFiles()){
            theaterFiles.add(new TheaterFile(file));
        }
        return theaterFiles;
    }
}
