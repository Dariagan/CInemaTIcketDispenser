package file_management;

import java.io.File;
import java.util.ArrayList;

public abstract class FilesFactory {

    private String filesFolder;
    private String fileExtension;
    private String fileNameNoun;

    String getFileExtension() {return fileExtension;}
    String getFileNameNoun() {return fileNameNoun;}

    public FilesFactory(String filesFolder, String fileExtension) {
        this.filesFolder = filesFolder;
        this.fileExtension = fileExtension;
    }
    public FilesFactory(String filesFolder, String fileExtension, String fileNameNoun) {
        this.filesFolder = filesFolder;
        this.fileExtension = fileExtension;
        this.fileNameNoun = fileNameNoun;
    }

    ArrayList<File> getMatchingFiles() {

        String dirPath = System.getProperty("user.dir") + "\\data\\" + filesFolder;
        File dataFolder = new File(dirPath);

        File[] readFiles = dataFolder.listFiles();
        ArrayList<File> foundFiles = new ArrayList<>();

        if (readFiles != null) {
            for (File file : readFiles) {
                String fileName = file.getName();
                if (fileName.endsWith(fileExtension) && extraSelectionConditionIsMet(fileName)) {
                    foundFiles.add(file);
                    System.out.println(fileName+" loaded");
                }else {
                    String warning =
                            String.format("%s was not loaded; doesn't match file-name format of %s",
                                    fileName, getClass().getSimpleName());
                    System.out.println(warning);
                }
            }
        }
        else {
            String exceptionText = String.format("No files were found at %s", dirPath);
            throw new RuntimeException(exceptionText);
        }
        return foundFiles;
    }

    boolean extraSelectionConditionIsMet(String fileName){return true;}
}
