package file_management;

import java.io.File;
import java.util.ArrayList;

public final class TheaterFilesFactory extends FilesFactory{

    public TheaterFilesFactory() {
        super("theaters", ".txt", "Theater");
    }

    @Override
    boolean extraSelectionConditionIsMet(String fileName) {
        String fileNameNoun = getFileNameNoun();

        boolean isTheRightLength = fileName.length() == fileNameNoun.length() + 1 + getFileExtension().length();
        boolean startsWithRightNoun = fileName.startsWith(fileNameNoun);
        boolean hasAdequateNumber =
        fileName.substring(fileNameNoun.length(), fileName.length() - getFileExtension().length()).matches("[1-4]");

        return isTheRightLength && startsWithRightNoun && hasAdequateNumber;
    }

    public ArrayList<TheaterFile> getFiles(){
        ArrayList<TheaterFile> theaterFiles = new ArrayList<>();
        for (File file : super.getMatchingFiles()){
            theaterFiles.add(new TheaterFile(file));
        }
        return theaterFiles;
    }
}
